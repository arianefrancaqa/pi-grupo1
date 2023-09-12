package grupo1.services;

import grupo1.dtos.CityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ICityService {

    CityDTO save(CityDTO dto);
    Page<CityDTO> findAll(Pageable pageable);
    Optional<CityDTO> findById(Integer id);
    Page<CityDTO> findByName(Pageable pageable, String name);
    CityDTO update(Integer id, CityDTO request);
    void delete(Integer id);

}
