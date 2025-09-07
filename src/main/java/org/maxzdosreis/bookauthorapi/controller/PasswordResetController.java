package org.maxzdosreis.bookauthorapi.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.maxzdosreis.bookauthorapi.controller.docs.PasswordResetControllerDocs;
import org.maxzdosreis.bookauthorapi.data.dto.PasswordResetConfirmDTO;
import org.maxzdosreis.bookauthorapi.data.dto.PasswordResetRequestDTO;
import org.maxzdosreis.bookauthorapi.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.TooManyListenersException;
import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping(value = "/forgot-password",
        produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody PasswordResetRequestDTO request) throws TooManyListenersException {
        log.info("Solicitação de reset de senha enviada para email: {}", request.getEmail());

        passwordResetService.initiatePasswordReset(request.getEmail());

        return ResponseEntity.ok("Se o email existir em nossa base, um link de reset foi enviado.");
    }

    @GetMapping(value = "/reset-password",
            produces = {"application/json", "application/xml", "application/x-yaml"})
    public ResponseEntity<String> validateResetToken(@RequestParam String token) {
        log.info("Validando token de reset: {}", token);

        passwordResetService.validateToken(token);

        return ResponseEntity.ok("Token válido. Você pode redefinir sua senha.");
    }

    @PostMapping(value = "/reset-password",
         produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetConfirmDTO request) throws TooManyListenersException {
        log.info("Confirmação de reset de senha recebida com token: {}", request.getToken());

        passwordResetService.confirmPasswordReset(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok("Senha alterada com sucesso");
    }
}
