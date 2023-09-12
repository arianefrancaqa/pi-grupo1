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
public class ReservationDTOResponse {
    private Integer id;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dataInicioReserva;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dataFimReserva;
    @JsonFormat(pattern="HH:mm")
    private LocalTime horaInicioReserva;
    private ProductDTO product;
    private UserDTO customer;

    public ReservationDTOResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.dataInicioReserva = reservation.getDataInicioReserva();
        this.dataFimReserva = reservation.getDataFimReserva();
        this.horaInicioReserva = reservation.getHoraInicioReserva();
        this.product = new ProductDTO(reservation.getProduct());
        this.customer = new UserDTO(reservation.getCustomer());
    }
}
