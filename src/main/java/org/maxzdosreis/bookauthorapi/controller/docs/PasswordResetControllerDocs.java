package org.maxzdosreis.bookauthorapi.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.maxzdosreis.bookauthorapi.data.dto.PasswordResetConfirmDTO;
import org.maxzdosreis.bookauthorapi.data.dto.PasswordResetRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.TooManyListenersException;

@Tag(name = "Password Reset", description = "Endpoints for reset password via email")
public interface PasswordResetControllerDocs {

    @Operation(
            summary = "Solicitar reset de senha",
            description = "Envia um email com link para redefinir a senha do usuário",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Too Many Requests", responseCode = "429", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<String> forgotPassword(@Valid @RequestBody PasswordResetRequestDTO request) throws TooManyListenersException;

    @Operation(
            summary = "Validar token de reset",
            description = "Verifica se o token é válido para redefinição de senha",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<String> validateResetToken(@RequestParam String token) throws TooManyListenersException;

    @Operation(
            summary = "Confirmar reset de senha",
            description = "Define nova senha usando token válido",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetConfirmDTO request) throws TooManyListenersException;
}
