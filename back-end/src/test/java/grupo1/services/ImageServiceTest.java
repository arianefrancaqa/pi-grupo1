package grupo1.services;

import grupo1.dtos.ImageDTO;
import grupo1.entities.Image;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.IImageRepository;
import grupo1.services.impl.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private IImageRepository imageRepository;

    private Image existingImage;

    @BeforeEach
    void setUp() {
        existingImage = new Image();
        existingImage.setId(1);
        existingImage.setUrl("existing-image-url");
        existingImage.setTitulo("existing-title");

        when(imageRepository.findById(1)).thenReturn(Optional.of(existingImage));
        when(imageRepository.findById(2)).thenReturn(Optional.empty());
    }

    @Test
    void testSaveImage() {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setUrl("new-image-url");

        Image savedImage = new Image();
        savedImage.setId(2);
        savedImage.setUrl(imageDTO.getUrl());

        when(imageRepository.save(any(Image.class))).thenReturn(savedImage);
        imageService.save(imageDTO);
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void testFindAllImages() {
        Page<Image> imagePage = Page.empty();
        Pageable pageable = Pageable.unpaged();

        when(imageRepository.findAll(pageable)).thenReturn(imagePage);

        Page<ImageDTO> resultPage = imageService.findAll(pageable);
        assertTrue(resultPage.isEmpty());
    }

    @Test
    void testFindImageByIdExisting() {
        Optional<ImageDTO> result = imageService.findById(1);
        assertTrue(result.isPresent());
        assertEquals(existingImage.getUrl(), result.get().getUrl());
    }

    @Test
    void testFindImageByIdNonExisting() {
        assertThrows(DatabaseException.class, () -> imageService.findById(2));
    }

    @Test
    void testUpdateExistingImage() {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setUrl("updated-image-url");
        imageDTO.setTitulo("updated-title");

        when(imageRepository.save(any(Image.class))).thenReturn(existingImage);

        ImageDTO updatedImageDTO = imageService.update(existingImage.getId(), imageDTO);
        assertEquals(imageDTO.getTitulo(), updatedImageDTO.getTitulo());
    }

    @Test
    void testUpdateNonExistingImage() {
        ImageDTO imageDTO = new ImageDTO();
        assertThrows(NotFoundException.class, () -> imageService.update(2, imageDTO));
    }

    @Test
    void testDeleteExistingImage() {
        assertDoesNotThrow(() -> imageService.delete(1));
    }

    @Test
    void testDeleteNonExistingImage() {
        assertThrows(NotFoundException.class, () -> imageService.delete(2));
    }

    @Test
    void testDeleteWithIntegrityViolation() {
        doThrow(DataIntegrityViolationException.class).when(imageRepository).deleteById(1);
        assertThrows(DatabaseException.class, () -> imageService.delete(1));
    }
}
