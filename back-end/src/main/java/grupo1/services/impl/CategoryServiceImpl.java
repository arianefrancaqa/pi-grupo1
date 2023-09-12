package grupo1.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import grupo1.dtos.CategoryDTO;
import grupo1.entities.Category;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.ICategoryRepository;
import grupo1.services.ICategoryService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements ICategoryService {

    private static final org.apache.log4j.Logger logger = Logger.getLogger(CategoryServiceImpl.class);
    private final ICategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDTO save(CategoryDTO dto) {
        Optional<Category> categoryDB = categoryRepository.findCategoryByDescription(dto.getDescription());
        if(categoryDB.isPresent()) {
            throw new DatabaseException("Categoría chamada " + dto.getDescription() + " já existente!");
        }
        Category category = new Category();
        copyDtoToEntity(dto, category);
        categoryRepository.save(category);
        logger.info("Categoria salva com sucesso: " + dto);
        return new CategoryDTO(category);
    }

    @Override
    public Page<CategoryDTO> findAll(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        logger.info("Lista de categorias encontrada com sucesso.");
        return categories.map(CategoryDTO::new);
    }

    @Override
    public Optional<CategoryDTO> findById(Integer id) {
        Optional<Category> category = categoryRepository.findById(id);
        ObjectMapper mapper = new ObjectMapper();
        if(category.isEmpty()) {
            logger.warn("Categoria não encontrada.");
            throw new NotFoundException("Nenhuma categoria encontrada para o ID: " + id);
        }
        logger.info("Categoria encontrada com sucesso." + category);
        return category.map(category1 -> mapper.convertValue(category1, CategoryDTO.class));
    }

    @Override
    public Page<CategoryDTO> findByName(Pageable pageable, String name) {
        Page<Category> categories = categoryRepository.findCategoryByDescriptionContainingIgnoreCase(pageable, name);
        if(categories.isEmpty()) {
            logger.warn("Nenhuma categoria encontrada para a descrição: " + name);
            throw new NotFoundException("Nenhuma categoria encontrada para a descrição: " + name);
        }
        logger.info("Categorias encontradas com sucesso.");
        return categories.map(CategoryDTO::new);
    }

    @Override
    public CategoryDTO update(Integer id, CategoryDTO dto) {
        dto.setId(id);
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        copyDtoToEntity(dto, category);
        category = categoryRepository.save(category);
        logger.info("Categoria atualizada com sucesso: " + dto);
        return new CategoryDTO(category);
    }

    @Override
    public void delete(Integer id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()) {
            logger.warn("Categoria não encontrada pelo ID: " + id);
            throw new NotFoundException("Id não encontrado: " + id);
        }
        try {
            categoryRepository.deleteById(id);
            logger.info("Categoria deletada com sucesso: " + id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Erro ao buscar a categoria pelo: " + id);
            throw new NotFoundException("Id não encontrado: " + id);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Violação de integridade.");
            throw new DatabaseException("Violação de integridade.");
        }
    }

    private void copyDtoToEntity(CategoryDTO dto, Category entity) {
        entity.setId(dto.getId());
        entity.setDescription(dto.getDescription());
        entity.setQualification(dto.getQualification());
        entity.setUrlImg(dto.getUrlImg());
    }
}
