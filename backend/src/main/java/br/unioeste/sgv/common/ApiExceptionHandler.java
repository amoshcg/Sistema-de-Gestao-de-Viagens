package br.unioeste.sgv.common;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduz falhas de validacao em uma resposta JSON com os erros por campo,
 * consumida pelo formulario do frontend.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.putIfAbsent(erro.getField(), erro.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(corpo(HttpStatus.BAD_REQUEST, "Dados invalidos", erros));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> tratarCorpoInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(corpo(HttpStatus.BAD_REQUEST, "Requisicao invalida", Map.of()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Map<String, Object>> tratarConflito(ConflitoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpo(HttpStatus.CONFLICT, ex.getMessage(), Map.of()));
    }

    private Map<String, Object> corpo(HttpStatus status, String mensagem, Map<String, String> erros) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", OffsetDateTime.now().toString());
        corpo.put("status", status.value());
        corpo.put("mensagem", mensagem);
        corpo.put("erros", erros);
        return corpo;
    }
}
