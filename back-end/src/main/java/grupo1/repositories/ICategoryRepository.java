package grupo1.repositories;

import grupo1.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Integer> {
    Page<Category> findCategoryByDescriptionContainingIgnoreCase(Pageable pageable, String nome);
   Optional<Category> findCategoryByDescription(String description);
}
