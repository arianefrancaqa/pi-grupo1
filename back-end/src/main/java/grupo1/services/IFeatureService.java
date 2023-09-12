package grupo1.services;

import grupo1.dtos.FeatureDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IFeatureService {

    FeatureDTO save(FeatureDTO dto);
    Page<FeatureDTO> findAll(Pageable pageable);
    Optional<FeatureDTO> findById(Integer id);
    FeatureDTO update(Integer id, FeatureDTO request);
    void delete(Integer id);

}
