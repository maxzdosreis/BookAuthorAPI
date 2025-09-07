package org.maxzdosreis.bookauthorapi.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.maxzdosreis.bookauthorapi.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenCleanupScheduler {

    @Autowired
    private PasswordResetService passwordResetService;

    // Executa a limpeza de tokens expirados todos os dias à meia-noite
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredTokensDaily() {
        log.info("Iniciando limpeza diária de tokens expirados");
        try {
            passwordResetService.cleanExpiredTokens();
            log.info("Limpeza diária de tokens expirados concluída com sucesso");
        } catch (Exception e) {
            log.error("Erro durante limpeza diária de tokens expirados: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    public void cleanupExpiredTokensFrequent() {
        log.debug("Executando limpeza frequente de tokens expirados");
        try {
            passwordResetService.cleanExpiredTokens();
        } catch (Exception e) {
            log.error("Erro durante limpeza frequente de tokens: {}", e.getMessage());
        }
    }
}
