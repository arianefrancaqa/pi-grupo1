package grupo1.services;

import grupo1.dtos.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ICategoryService {

    CategoryDTO save(CategoryDTO dto);
    Page<CategoryDTO> findAll(Pageable pageable);
    Optional<CategoryDTO> findById(Integer id);
    Page<CategoryDTO> findByName(Pageable pageable, String name);
    CategoryDTO update(Integer id, CategoryDTO request);
    void delete(Integer id);

}
