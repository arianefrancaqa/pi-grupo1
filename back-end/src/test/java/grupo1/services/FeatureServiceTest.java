package grupo1.services;

import grupo1.dtos.FeatureDTO;
import grupo1.entities.Feature;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.IFeatureRepository;
import grupo1.services.impl.FeatureServiceImpl;
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
class FeatureServiceTest {

    @InjectMocks
    private FeatureServiceImpl featureService;

    @Mock
    private IFeatureRepository featureRepository;

    private Feature existingFeature;

    @BeforeEach
    void setUp() {
        existingFeature = new Feature();
        existingFeature.setId(1);
        existingFeature.setNome("Existing Feature");
        existingFeature.setIcone("Existing Icone");

        when(featureRepository.findById(1)).thenReturn(Optional.of(existingFeature));
        when(featureRepository.findById(2)).thenReturn(Optional.empty());
    }

    @Test
    void testSaveFeature() {
        FeatureDTO featureDTO = new FeatureDTO();
        featureDTO.setNome("New Feature");

        Feature savedFeature = new Feature();
        savedFeature.setId(2);
        savedFeature.setNome("New Feature");

        when(featureRepository.save(any(Feature.class))).thenReturn(savedFeature);

        featureService.save(featureDTO);
        verify(featureRepository).save(any(Feature.class));
    }

    @Test
    void testFindAllFeatures() {
        Page<Feature> featurePage = Page.empty();
        Pageable pageable = Pageable.unpaged();

        when(featureRepository.findAll(pageable)).thenReturn(featurePage);

        Page<FeatureDTO> resultPage = featureService.findAll(pageable);
        assertTrue(resultPage.isEmpty());
    }

    @Test
    void testFindFeatureByIdExisting() {
        Optional<FeatureDTO> result = featureService.findById(1);
        assertTrue(result.isPresent());
        assertEquals(existingFeature.getNome(), result.get().getNome());
    }

    @Test
    void testFindFeatureByIdNonExisting() {
        assertThrows(NotFoundException.class, () -> featureService.findById(2));
    }

    @Test
    void testUpdateExistingFeature() {
        FeatureDTO featureDTO = new FeatureDTO();
        featureDTO.setNome("Updated Feature");
        featureDTO.setIcone("Updated Icone");
        FeatureDTO updatedFeatureDTO = featureService.update(existingFeature.getId(), featureDTO);

        assertEquals(featureDTO.getNome(), updatedFeatureDTO.getNome());
    }
    @Test
    void testUpdateNonExistingFeature() {
        FeatureDTO featureDTO = new FeatureDTO();
        assertThrows(NotFoundException.class, () -> featureService.update(2, featureDTO));
    }

    @Test
    void testDeleteExistingFeature() {
        assertDoesNotThrow(() -> featureService.delete(1));
    }

    @Test
    void testDeleteNonExistingFeature() {
        assertThrows(NotFoundException.class, () -> featureService.delete(2));
    }

    @Test
    void testDeleteWithIntegrityViolation() {
        doThrow(DataIntegrityViolationException.class).when(featureRepository).deleteById(1);
        assertThrows(DatabaseException.class, () -> featureService.delete(1));
    }
}
