package grupo1.repositories;

import grupo1.entities.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IFeatureRepository extends JpaRepository<Feature, Integer> {
    Optional<Feature> findFeatureByNome(String nome);
}
