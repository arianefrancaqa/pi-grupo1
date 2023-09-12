package grupo1.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorEntity> entityNotFound(NotFoundException e, HttpServletRequest req) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorEntity error = new ErrorEntity();
        error.setStatus(status.value());
        error.setError("Entidade não encontrada!");
        error.setMessage(e.getMessage());
        error.setPath(req.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorEntity> userNotFound(UsernameNotFoundException e, HttpServletRequest req) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorEntity error = new ErrorEntity();
        error.setStatus(status.value());
        error.setError("Usuário não encontrado!");
        error.setMessage(e.getMessage());
        error.setPath(req.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorEntity> databaseIntegrityViolation(DatabaseException e, HttpServletRequest req) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorEntity error = new ErrorEntity();
        error.setStatus(status.value());
        error.setError("Violação de integridade!");
        error.setMessage(e.getMessage());
        error.setPath(req.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}
