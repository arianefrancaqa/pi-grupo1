package grupo1.repositories;

import grupo1.entities.Reservation;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Integer> {
    @Transactional
    @Query(value = "select count(*) from reservas r where r.product_id = :id and r.data_inicio_reserva >= :dataInicioReserva and r.data_fim_reserva <= :dataFimReserva", nativeQuery = true)
    Integer findReservavionBetweenDates (
            @Param("id") Integer id,
            @Param("dataInicioReserva") LocalDate dataInicioReserva,
            @Param("dataFimReserva") LocalDate dataFimReserva);

    @Transactional
    @Query(value = "select * from reservas r where r.customer_id = :id", nativeQuery = true)
    List<Reservation> findReservationsByCustomerId(@Param("id") Integer id);
}
