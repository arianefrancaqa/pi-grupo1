package grupo1.services;

import grupo1.dtos.CategoryDTO;
import grupo1.entities.Category;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.ICategoryRepository;
import grupo1.services.impl.CategoryServiceImpl;
import lombok.extern.log4j.Log4j;
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
@Log4j
@ExtendWith(SpringExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private ICategoryRepository categoryRepository;

    private Category existingCategory;

    @BeforeEach
    void setUp() {
        existingCategory = new Category();
        existingCategory.setId(1);
        existingCategory.setDescription("Category Description");
        existingCategory.setQualification("Category Qualification");
        existingCategory.setUrlImg("category_image.jpg");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findById(2)).thenReturn(Optional.empty());
        when(categoryRepository.findCategoryByDescriptionContainingIgnoreCase(any(), any())).thenReturn(Page.empty());
    }

    @Test
    void testSaveNewCategory() {
        log.info("TEST START - SAVE NEW CATEGORY");
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setDescription("New Category");
        categoryDTO.setQualification("New Qualification");
        categoryDTO.setUrlImg("new_category_image.jpg");

        Category savedCategory = new Category();
        savedCategory.setId(2);
        savedCategory.setDescription(categoryDTO.getDescription());
        savedCategory.setQualification(categoryDTO.getQualification());
        savedCategory.setUrlImg(categoryDTO.getUrlImg());

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        categoryService.save(categoryDTO);
        verify(categoryRepository).save(any(Category.class));
        log.info("TEST FINISH - SAVE NEW CATEGORY");
    }

    @Test
    void testFindAllCategories() {
        log.info("TEST START - FIND ALL CATEGORIES");
        Page<Category> categoryPage = Page.empty();
        Pageable pageable = Pageable.unpaged();

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Page<CategoryDTO> resultPage = categoryService.findAll(pageable);
        assertTrue(resultPage.isEmpty());
        log.info("TEST FINISH - FIND ALL CATEGORIES");
    }

    @Test
    void testFindCategoryByIdExisting() {
        log.info("TEST START - FIND CATEGORY BY EXISTING ID");
        Optional<CategoryDTO> result = categoryService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(existingCategory.getDescription(), result.get().getDescription());
        assertEquals(existingCategory.getQualification(), result.get().getQualification());
        assertEquals(existingCategory.getUrlImg(), result.get().getUrlImg());
        log.info("TEST FINISH - FIND CATEGORY BY EXISTING ID");
    }

    @Test
    void testFindCategoryByIdNonExisting() {
        log.info("TEST START - THROW EXCPETION WHEN TRYING TO FIND ROLE BY NON-EXISTENT ID");
        assertThrows(NotFoundException.class, () -> categoryService.findById(2));
        log.info("TEST FINISH - THROW EXCPETION WHEN TRYING TO FIND ROLE BY NON-EXISTENT ID");
    }

    @Test
    void testFindCategoriesByNameNotFound() {
        log.info("TEST START - THROW EXCPETION WHEN TRYING TO FIND CATEGORY BY NON-EXISTENT NAME");
        assertThrows(NotFoundException.class, () -> categoryService.findByName(Pageable.unpaged(), "Non-existent Category"));
        log.info("TEST FINISH - THROW EXCPETION WHEN TRYING TO FIND CATEGORY BY NON-EXISTENT NAME");
    }

    @Test
    void testUpdateExistingCategory() {
        log.info("TEST START - UPDATE CATEGORY");
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setDescription("Updated Category");
        categoryDTO.setQualification("Updated Qualification");
        categoryDTO.setUrlImg("updated_category_image.jpg");

        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        CategoryDTO updatedCategoryDTO = categoryService.update(1, categoryDTO);

        assertEquals(categoryDTO.getDescription(), updatedCategoryDTO.getDescription());
        assertEquals(categoryDTO.getQualification(), updatedCategoryDTO.getQualification());
        assertEquals(categoryDTO.getUrlImg(), updatedCategoryDTO.getUrlImg());
        log.info("TEST FINISH - UPDATE CATEGORY");
    }

    @Test
    void testUpdateNonExistingCategory() {
        log.info("TEST START - THROW EXCPETION WHEN TRYING TO FIND NON-EXISTENT CATEGORY");
        CategoryDTO categoryDTO = new CategoryDTO();

        assertThrows(NotFoundException.class, () -> categoryService.update(2, categoryDTO));
        log.info("TEST FINISH - THROW EXCPETION WHEN TRYING TO FIND NON-EXISTENT CATEGORY");
    }

    @Test
    void testDeleteExistingCategory() {
        log.info("TEST START - DELETE CATEGORY");
        assertDoesNotThrow(() -> categoryService.delete(1));
        log.info("TEST START - DELETE CATEGORY");
    }

    @Test
    void testDeleteNonExistingCategory() {
        log.info("TEST START - THROW EXCPETION WHEN TRYING TO DELETE NON-EXISTENT CATEGORY");

        assertThrows(NotFoundException.class, () -> categoryService.delete(2));
        log.info("TEST START - THROW EXCPETION WHEN TRYING TO DELETE NON-EXISTENT CATEGORY");
    }

    @Test
    void testDeleteWithIntegrityViolation() {
        log.info("TEST START - THROW EXCPETION WHEN TRYING TO FIND ROLE BY NON-EXISTENT ID");
        doThrow(DataIntegrityViolationException.class).when(categoryRepository).deleteById(1);

        assertThrows(DatabaseException.class, () -> categoryService.delete(1));
        log.info("TEST START - THROW EXCPETION WHEN TRYING TO FIND ROLE BY NON-EXISTENT ID");
    }
}