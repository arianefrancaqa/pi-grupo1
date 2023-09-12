package grupo1.services;

import grupo1.dtos.ImageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IImageService {

    ImageDTO save(ImageDTO dto);
    Page<ImageDTO> findAll(Pageable pageable);
    Optional<ImageDTO> findById(Integer id);
    ImageDTO update(Integer id, ImageDTO request);
    void delete(Integer id);

}
