package grupo1.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import grupo1.dtos.CityDTO;
import grupo1.entities.City;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.ICityRepository;
import grupo1.services.ICityService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CityServiceImpl implements ICityService {

    private static final org.apache.log4j.Logger logger = Logger.getLogger(CityServiceImpl.class);
    private final ICityRepository cityRepository;

    @Autowired
    public CityServiceImpl(ICityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public CityDTO save(CityDTO dto) {
        Optional<City> cityDB = cityRepository.findCityByNomeAndPais(dto.getNome(), dto.getPais());
        if(cityDB.isPresent()) {
            throw new DatabaseException("Cidade com o nome " + dto.getNome() + " no " + dto.getPais() + " já existente!");
        }
        City city = new City();
        copyDtoToEntity(dto, city);
        cityRepository.save(city);
        logger.info("Cidade salva com sucesso: " + dto);
        return new CityDTO(city);
    }

    @Override
    public Page<CityDTO> findAll(Pageable pageable) {
        Page<City> cities = cityRepository.findAll(pageable);
        if (cities.isEmpty()) {
            logger.info("Nenhuma cidade encontrada.");
        } else {
            logger.info("Cidades(s) encontrada(s) com sucesso.");
        }
        return cities.map(CityDTO::new);
    }

    @Override
    public Optional<CityDTO> findById(Integer id) {
        Optional<City> city = cityRepository.findById(id);
        ObjectMapper mapper = new ObjectMapper();
        if(city.isEmpty()) {
            logger.info("Cidade não encontrada: " + id);
            throw new NotFoundException("Cidade não encontrada: " + id);
        } else {
            logger.info("Cidade encontrada com sucesso." + city);
        }
        return city.map(city1 -> mapper.convertValue(city1, CityDTO.class));
    }

    @Override
    public Page<CityDTO> findByName(Pageable pageable, String name) {
        Page<City> cidade = cityRepository.findCidadeByNomeContainingIgnoreCase(pageable, name);
        if(cidade.isEmpty()) {
            logger.warn("Cidade não encontrada: " + name);
            throw new NotFoundException("Nenhuma cidade encontrada com o nome: " + name);
        }
        logger.info("Cidade encontrada com sucesso." + cidade);
        return cidade.map(CityDTO::new);
    }

    @Override
    public CityDTO update(Integer id, CityDTO dto) {
        dto.setId(id);
        City city = cityRepository.findById(id).orElseThrow(() -> new NotFoundException("Cidade não encontrada: " + id));
        copyDtoToEntity(dto, city);
        city = cityRepository.save(city);
        logger.info("Cidade atualizada com sucesso: " + dto);
        return new CityDTO(city);
    }

    @Override
    public void delete(Integer id) {
        Optional<City> city = cityRepository.findById(id);
        if(city.isEmpty()) {
            logger.warn("Cidade não encontrada: " + id);
            throw new NotFoundException("Cidade não encontrada: " + id);
        }
        try {
            cityRepository.deleteById(id);
            logger.info("Cidade deletada com sucesso: " + id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Erro ao buscar a cidade pelo: " + id);
            throw new NotFoundException("Id não encontrado: " + id);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Violação de integridade.");
            throw new DatabaseException("Violação de integridade.");
        }
    }

    private void copyDtoToEntity(CityDTO dto, City entity) {
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setPais(dto.getPais());
    }
}
