package grupo1.repositories;

import grupo1.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IImageRepository extends JpaRepository<Image, Integer> {
    Optional<Image> findImageByTitulo(String titulo);
}
