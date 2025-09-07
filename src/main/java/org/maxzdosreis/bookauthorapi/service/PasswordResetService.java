package org.maxzdosreis.bookauthorapi.service;

import org.maxzdosreis.bookauthorapi.exception.EmailServiceException;
import org.maxzdosreis.bookauthorapi.exception.ExpiredTokenException;
import org.maxzdosreis.bookauthorapi.exception.InvalidTokenException;
import org.maxzdosreis.bookauthorapi.exception.TokenAlreadyUsedException;
import org.maxzdosreis.bookauthorapi.model.PasswordResetToken;
import org.maxzdosreis.bookauthorapi.model.User;
import org.maxzdosreis.bookauthorapi.repository.PasswordResetTokenRepository;
import org.maxzdosreis.bookauthorapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PasswordResetService {

    Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.password-reset.token-expiry-hours:1}")
    private int tokenExpiryHours;

    @Value("${app.password-reset.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.password-reset.max-attempts-per-hour:3}")
    private int maxAttempsPerHour;

    @Transactional
    public void initiatePasswordReset(String email) throws TooManyListenersException {
        logger.info("Iniciando reset de senha para email: {}", email);

        // verifica o limite de tentativas
        checkResetAttemptLimit(email);

        User user = userRepository.findByEmail(email);
        if(user == null) {
            logger.warn("Tentativa de reset para email inexistente: {}", email);
            return;
        }

        try {
            // Remove os tokens anteriores para este email
            tokenRepository.deleteByEmail(email);

            // Cria novo token
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setEmail(email);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(tokenExpiryHours));
            resetToken.setUsed(false);

            // salva o novo token
            tokenRepository.save(resetToken);

            // Envia email assíncrono
            sendResetEmailAsync(email, token, user.getFullName());

            logger.info("Token de reset criado com sucesso para: {}", email);
        } catch (Exception e) {
            logger.error("Error ao criar token de reset para {}: {}", email, e.getMessage());
            throw new EmailServiceException("Erro ao processar solicitação de reset", e);
        }
    }

    // verifica o limite de tentativas por hora
    private void checkResetAttemptLimit(String email) throws TooManyListenersException {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentAttemps = tokenRepository.countRecentTokenByEmail(email, oneHourAgo);

        if(recentAttemps >= maxAttempsPerHour) {
            logger.warn("Muitas tentativas de reset para email: {} ({})", email, recentAttemps);
            throw new TooManyListenersException();
        }
    }

    // Envia email de reset de forma assíncrona
    @Async("emailTaskExecutor")
    public void sendResetEmailAsync(String email, String token, String fullName) {
        try {
            sendResetEmail(email, token, fullName);
        } catch (Exception e) {
            logger.error("Erro ao enviar email de reset para {}: {}", email, e.getMessage());
            throw new EmailServiceException("Erro ao enviar email de reset", e);
        }
    }

    // Envia o email com o link de reset
    private void sendResetEmail(String email, String token, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Reset de Senha - BookAuthor API");

        String resetLink = baseUrl + "/auth/api/v1/reset-password?token=" + token;

        String emailBody = "Olá" + (fullName != null ? " " + fullName : "") + "!\n\n" +
                "Você solicitou a redefinição da sua senha na BookAuthor API.\n\n" +
                "Para redefinir sua senha, clique no link abaixo:\n" +
                resetLink + "\n\n" +
                "Este link expira em " + tokenExpiryHours + " hora(s).\n\n" +
                "Se você não solicitou este reset, ignore este email.\n\n" +
                "Atenciosamente,\n" +
                "Equipe BookAuthor API";

        message.setText(emailBody);

        mailSender.send(message);
        logger.info("Email de reset enviado com sucesso para: {}", email);
    }

    // Valida se o token é válido
    public void validateToken(String token) {
        logger.info("Validando token: {}", token);

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        // verifica se o token está vazio
        if(tokenOpt.isEmpty()) {
            logger.warn("Token não encontrado: {}", token);
            throw new InvalidTokenException();
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Verifica se o token já foi usado
        if(resetToken.isUsed()) {
            logger.warn("Token já utilizado: {}", token);
            throw new TokenAlreadyUsedException();
        }

        // Verifica se o token não está expirado
        if(resetToken.isExpired()) {
            logger.warn("Token expirado: {}", token);
            throw new ExpiredTokenException();
        }

        logger.info("Token válido: {}", token);
    }

    // Confirma o reset de senha usando o mesmo padrão de hase do AuthService
    @Transactional
    public void confirmPasswordReset(String token, String newPassword) {
        logger.info("Confirmando reset de senha com token: {}", token);

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        // verifica se o token está vazio
        if(tokenOpt.isEmpty()) {
            logger.warn("Token não encontrado: {}", token);
            throw new InvalidTokenException();
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Verifica se o token já foi usado
        if(resetToken.isUsed()) {
            logger.warn("Token já utilizado: {}", token);
            throw new TokenAlreadyUsedException();
        }

        // Verifica se o token não está expirado
        if(resetToken.isExpired()) {
            logger.warn("Token expirado: {}", token);
            throw new ExpiredTokenException();
        }

        User user = userRepository.findByEmail(resetToken.getEmail());
        if(user == null) {
            logger.error("Usuário não encontrando para email: {}", resetToken.getEmail());
            throw new InvalidTokenException("Usuário não encontrado");
        }

        try {
            // Atualiza a senha do usuário usando o mesmo padrão do AuthService
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Marcar token como usado
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);

            logger.info("Senha alterad com sucesso para usuário: {}", user.getFullName());
        } catch (Exception e) {
            logger.error("Erro ao alterar senha: {}", e.getMessage());
            throw new RuntimeException("Erro interno ao alterar senha", e);
        }
    }

    /**
    // Gera hash da senha usando o mesmo padrão do AuthService
    private String generateHashedPassword(String password) {
        PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder(
                "", 8, 18500,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("pbkdf2", pbkdf2Encoder);
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);

        passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
        return passwordEncoder.encode(password);
    }
    **/

    // Faz a limpeza automática de token expirados
    @Transactional
    public void cleanExpiredTokens() {
        try {
            tokenRepository.deleteExpiredTokens(LocalDateTime.now());
            logger.info("Token expirados removidos");
        } catch (Exception e) {
            logger.error("Erro ao remover token expirados: {}", e.getMessage());
        }
    }
}
