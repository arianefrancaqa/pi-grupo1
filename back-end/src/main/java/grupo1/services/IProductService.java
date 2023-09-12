package grupo1.services;

import grupo1.dtos.DateSearchDTO;
import grupo1.dtos.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IProductService {

    ProductDTO save(ProductDTO dto);
    Page<ProductDTO> findAll(Pageable pageable);
    Optional<ProductDTO> findById(Integer id);
    Page<ProductDTO> findByName(Pageable pageable, String name);
    Page<ProductDTO> findByCategory(Pageable pageable, Integer category);
    Page<ProductDTO> findByCity(Pageable pageable, Integer category);
    Page<ProductDTO> findByDates(Pageable pageable, DateSearchDTO dateSearchDTO);
    Page<ProductDTO> findByDatesAndCity(Pageable pageable, Integer idCidade, DateSearchDTO dateSearchDTO);
    ProductDTO update(Integer id, ProductDTO request);
    void delete(Integer id);

}
