package grupo1.services;

import grupo1.dtos.CityDTO;
import grupo1.entities.City;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.ICityRepository;
import grupo1.services.impl.CityServiceImpl;
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
class CityServiceTest {

    @InjectMocks
    private CityServiceImpl cityService;

    @Mock
    private ICityRepository cityRepository;

    private City existingCity;

    @BeforeEach
    void setUp() {
        existingCity = new City();
        existingCity.setId(1);
        existingCity.setNome("Existing City");
        existingCity.setPais("Existing Country");

        when(cityRepository.findById(1)).thenReturn(Optional.of(existingCity));
        when(cityRepository.findById(2)).thenReturn(Optional.empty());
        when(cityRepository.findCidadeByNomeContainingIgnoreCase(any(), any())).thenReturn(Page.empty());
    }

    @Test
    void testSaveCity() {
        CityDTO cityDTO = new CityDTO();
        cityDTO.setNome("New City");
        cityDTO.setPais("New Country");

        City savedCity = new City();
        savedCity.setId(2);
        savedCity.setNome(cityDTO.getNome());
        savedCity.setPais(cityDTO.getPais());

        when(cityRepository.save(any(City.class))).thenReturn(savedCity);
        cityService.save(cityDTO);
        verify(cityRepository).save(any(City.class));
    }

    @Test
    void testFindAllCities() {
        Page<City> cityPage = Page.empty();
        Pageable pageable = Pageable.unpaged();

        when(cityRepository.findAll(pageable)).thenReturn(cityPage);
        Page<CityDTO> resultPage = cityService.findAll(pageable);

        assertTrue(resultPage.isEmpty());
    }

    @Test
    void testFindCityByIdExisting() {
        Optional<CityDTO> result = cityService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(existingCity.getNome(), result.get().getNome());
        assertEquals(existingCity.getPais(), result.get().getPais());
    }

    @Test
    void testFindCityByIdNonExisting() {
        assertThrows(NotFoundException.class, () -> cityService.findById(2));
    }

    @Test
    void testFindCitiesByNameNotFound() {
        assertThrows(NotFoundException.class, () -> cityService.findByName(Pageable.unpaged(), "Non-existing City"));
    }

    @Test
    void testUpdateExistingCity() {
        CityDTO cityDTO = new CityDTO();
        cityDTO.setNome("Updated City");
        cityDTO.setPais("Updated Country");

        when(cityRepository.save(any(City.class))).thenReturn(existingCity);
        CityDTO updatedCityDTO = cityService.update(1, cityDTO);

        assertEquals(cityDTO.getNome(), updatedCityDTO.getNome());
        assertEquals(cityDTO.getPais(), updatedCityDTO.getPais());
    }

    @Test
    void testUpdateNonExistingCity() {
        CityDTO cityDTO = new CityDTO();

        assertThrows(NotFoundException.class, () -> cityService.update(2, cityDTO));
    }

    @Test
    void testDeleteExistingCity() {
        assertDoesNotThrow(() -> cityService.delete(1));
    }

    @Test
    void testDeleteNonExistingCity() {
        assertThrows(NotFoundException.class, () -> cityService.delete(2));
    }

    @Test
    void testDeleteWithIntegrityViolation() {
        doThrow(DataIntegrityViolationException.class).when(cityRepository).deleteById(1);
        assertThrows(DatabaseException.class, () -> cityService.delete(1));
    }
}
