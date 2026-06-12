package br.com.marquesautodetail.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException exception) {
        return erro(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException exception) {
        String mensagem = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Dados inválidos");
        return erro(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> unreadableBody() {
        return erro(HttpStatus.BAD_REQUEST, "JSON inválido ou em codificação incorreta");
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Map<String, String>> invalidParameter(Exception exception) {
        return erro(HttpStatus.BAD_REQUEST, "Parâmetro obrigatório ausente ou inválido");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> unauthorized() {
        return erro(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> forbidden(AccessDeniedException exception) {
        return erro(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> notFound() {
        return erro(HttpStatus.NOT_FOUND, "Recurso não encontrado");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> conflict(DataIntegrityViolationException exception) {
        log.warn("Violação de integridade no banco", exception);
        return erro(HttpStatus.CONFLICT, "A operação conflita com dados já cadastrados");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> internalError(Exception exception) {
        log.error("Erro não tratado na API", exception);
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao processar a solicitação");
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}
