package grupo1.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import grupo1.dtos.FeatureDTO;
import grupo1.entities.Feature;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.IFeatureRepository;
import grupo1.services.IFeatureService;
import jakarta.transaction.Transactional;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FeatureServiceImpl implements IFeatureService {

    private static final org.apache.log4j.Logger logger = Logger.getLogger(FeatureServiceImpl.class);
    private final IFeatureRepository featureRepository;

    @Autowired
    public FeatureServiceImpl(IFeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    @Override
    @Transactional
    public FeatureDTO save(FeatureDTO dto) {
        Optional<Feature> featureDB = featureRepository.findFeatureByNome(dto.getNome());
        if(featureDB.isPresent()) {
            throw new DatabaseException("Característica com o nome " + dto.getNome() + " já existente!");
        }
        Feature feature = new Feature();
        feature.setNome(dto.getNome());
        feature.setIcone(dto.getIcone());
        Feature savedFeature = featureRepository.save(feature);
        logger.info("caracteristica salva com sucesso: " + dto);
        return new FeatureDTO(savedFeature);
    }

    @Override
    public Page<FeatureDTO> findAll(Pageable pageable) {
        Page<Feature> categories = featureRepository.findAll(pageable);
        logger.info("Lista de caracteristicas encontrada com sucesso.");
        return categories.map(FeatureDTO::new);
    }

    @Override
    public Optional<FeatureDTO> findById(Integer id) {
        Optional<Feature> feature = featureRepository.findById(id);
        if(feature.isEmpty()) {
            logger.info("caracteristica não encontrada: " + id);
            throw new NotFoundException("caracteristica não encontrada: " + id);
        }
        ObjectMapper mapper = new ObjectMapper();
        logger.info("caracteristica encontrada com sucesso: " + feature.get().getNome());
        return feature.map(feature1 -> mapper.convertValue(feature1, FeatureDTO.class));
    }

    @Override
    public FeatureDTO update(Integer id, FeatureDTO dto) {
        dto.setId(id);
        Optional<Feature> featureDb = featureRepository.findById(id);
        if(featureDb.isEmpty()) {
            logger.warn("Característica não encontrada: " + id);
            throw new NotFoundException("Característica não encontrada para o ID: " + id);
        }
        Feature feature = new Feature();
        copyDtoToEntity(feature, dto);
        feature = featureRepository.save(feature);
        return new FeatureDTO(feature);
    }

    @Override
    public void delete(Integer id) {
        Optional<Feature> featureDb = featureRepository.findById(id);
        if(featureDb.isEmpty()) {
            logger.warn("Característica não encontrada: " + id);
            throw new NotFoundException("Característica não encontrada para o ID: " + id);
        }
        try {
            featureRepository.deleteById(id);
            logger.info("Caracteristica deletada com sucesso: " + id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Erro ao buscar a caracteristica pelo: " + id);
            throw new NotFoundException("Id não encontrado: " + id);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Violação de integridade.");
            throw new DatabaseException("Violação de integridade.");
        }
    }

    private void copyDtoToEntity(Feature entity, FeatureDTO dto) {
        entity.setNome(dto.getNome());
        entity.setIcone(dto.getIcone());
    }
}
