package grupo1.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Entity
@Table(name = "reservas")
public class Reservation extends AbstractEntity {
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dataInicioReserva;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dataFimReserva;
    @JsonFormat(pattern="HH:mm")
    private LocalTime horaInicioReserva;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User customer;

}
