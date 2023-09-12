package grupo1.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DateSearchDTO {
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dataInicioPesquisa;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dataFimPesquisa;
}
