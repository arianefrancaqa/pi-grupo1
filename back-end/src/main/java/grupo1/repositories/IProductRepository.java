package grupo1.repositories;

import grupo1.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findProductByNomeContainingIgnoreCase(Pageable pageable, String nome);
    Optional<Product> findProductByNome(String nome);
    @Query(value = "SELECT p.* FROM produtos p LEFT JOIN reservas r ON p.id = r.product_id WHERE (r.data_inicio_reserva IS NULL OR r.data_inicio_reserva > :dataFimReserva) OR (r.data_fim_reserva IS NULL OR r.data_fim_reserva < :dataInicioReserva)", nativeQuery = true)
    List<Product> findProductsThatDoNotHaveReservationInAGivenPeriodOfTime(
            @Param("dataInicioReserva") LocalDate dataInicioReserva,
            @Param("dataFimReserva") LocalDate dataFimReserva
    );
}
