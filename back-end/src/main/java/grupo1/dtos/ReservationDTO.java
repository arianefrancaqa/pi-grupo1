package grupo1.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import grupo1.entities.Reservation;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationDTO {
    private Integer id;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dataInicioReserva;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dataFimReserva;
    @JsonFormat(pattern="HH:mm")
    private LocalTime horaInicioReserva;
    private Integer productId;
    private Integer customerId;

    public ReservationDTO(Reservation reservation) {
        this.id = reservation.getId();
        this.dataInicioReserva = reservation.getDataInicioReserva();
        this.dataFimReserva = reservation.getDataFimReserva();
        this.horaInicioReserva = reservation.getHoraInicioReserva();
        this.productId = reservation.getProduct().getId();
        this.customerId = reservation.getCustomer().getId();
    }
}
