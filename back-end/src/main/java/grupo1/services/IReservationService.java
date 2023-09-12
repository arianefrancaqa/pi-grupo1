package grupo1.services;

import grupo1.dtos.ReservationDTO;
import grupo1.dtos.ReservationDTOResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IReservationService {
    ReservationDTO save(ReservationDTO dto);
    Page<ReservationDTO> findAll(Pageable pageable);
    Optional<ReservationDTOResponse> findById(Integer id);
    Page<ReservationDTO> findReservationsByCustomerId(Integer id);
    ReservationDTO update(Integer id, ReservationDTO request);
    void delete(Integer id);
}
