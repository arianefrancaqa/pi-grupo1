package grupo1.exceptions;

import lombok.*;

import java.time.Instant;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ErrorEntity {

    private Instant timestamp = Instant.now();
    private Integer status;
    private String error;
    private String message;
    private String path;
}
