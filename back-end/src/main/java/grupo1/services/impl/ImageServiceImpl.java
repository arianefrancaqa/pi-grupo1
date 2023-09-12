package grupo1.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import grupo1.dtos.ImageDTO;
import grupo1.entities.Image;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.IImageRepository;
import grupo1.services.IImageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageServiceImpl implements IImageService {

    private static final org.apache.log4j.Logger logger = Logger.getLogger(ImageServiceImpl.class);
    private final IImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(IImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public ImageDTO save(ImageDTO dto) {
        Optional<Image> imageDB = imageRepository.findImageByTitulo(dto.getTitulo());
        if(imageDB.isPresent()) {
            throw new DatabaseException("Imagem com o título " + dto.getTitulo() + " já existente!");
        }
        Image image = new Image();
        copyDtoToEntity(image, dto);
        imageRepository.save(image);
        logger.info("Imagem salva com sucesso: " + dto);
        return new ImageDTO(image);
    }

    @Override
    public Page<ImageDTO> findAll(Pageable pageable) {
        Page<Image> categories = imageRepository.findAll(pageable);
        logger.info("Lista de imagens encontrada com sucesso.");
        return categories.map(ImageDTO::new);
    }

    @Override
    public Optional<ImageDTO> findById(Integer id) {
        Optional<Image> image = imageRepository.findById(id);
        ObjectMapper mapper = new ObjectMapper();
        if(image.isEmpty()) {
            logger.warn("Imagem não encontrada." + image);
            throw new DatabaseException("Imagem com o id " + id + " não encontrada");
        } else {
            logger.info("Imagem encontrada com sucesso." + image);
        }
        return image.map(image1 -> mapper.convertValue(image1, ImageDTO.class));
    }

    @Override
    public ImageDTO update(Integer id, ImageDTO dto) {
        dto.setId(id);
        Optional<Image> imageDb = imageRepository.findById(id);
        if(imageDb.isEmpty()) {
            logger.warn("Imagem não encontrada: " + id);
            throw new NotFoundException("Imagem não econtrada para o ID: " + id);
        }
        Image image = new Image();
        copyDtoToEntity(imageDb.get(), dto);
        image = imageRepository.save(imageDb.get());
        return new ImageDTO(image);
    }

    @Override
    public void delete(Integer id) {
        Optional<Image> image = imageRepository.findById(id);
        if(image.isEmpty()) {
            logger.warn("Imagem não encontrada: " + id);
            throw new NotFoundException("Imagem não econtrada para o ID: " + id);
        }
        try {
            imageRepository.deleteById(id);
            logger.info("Imagem deletada com sucesso: " + id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Erro ao buscar a imagem pelo: " + id);
            throw new NotFoundException("Id não encontrado: " + id);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Violação de integridade.");
            throw new DatabaseException("Violação de integridade.");
        }
    }

    private void copyDtoToEntity(Image entity, ImageDTO dto) {
        entity.setId(dto.getId());
        entity.setUrl(dto.getUrl());
        entity.setTitulo(dto.getTitulo());
    }

}
