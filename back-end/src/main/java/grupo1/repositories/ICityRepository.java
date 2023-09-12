package grupo1.repositories;

import grupo1.entities.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICityRepository extends JpaRepository<City, Integer> {
    Page<City> findCidadeByNomeContainingIgnoreCase(Pageable pageable, String nome);

    Optional<City> findCityByNomeAndPais(String name, String country);
}
