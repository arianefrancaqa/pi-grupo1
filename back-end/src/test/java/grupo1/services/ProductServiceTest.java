package grupo1.services;

import grupo1.dtos.*;
import grupo1.entities.*;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.*;
import grupo1.services.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @InjectMocks
    private FeatureServiceImpl featureService;

    @InjectMocks
    private CityServiceImpl cityService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private IProductRepository productRepository;
    @Mock
    private IFeatureRepository featureRepository;
    @Mock
    private ICityRepository cityRepository;
    @Mock
    private ICategoryRepository categoryRepository;
    @Mock
    private IImageRepository imageRepository;

    private Product existingProduct;

    @BeforeEach
    void setUp() {
        existingProduct = new Product();
        existingProduct.setId(1);
        existingProduct.setDescricao("Existing Description");
        existingProduct.setNome("Existing Product");
        Set<Image> images = new HashSet<>();
        existingProduct.setImagens(images);
        Category category = new Category();
        existingProduct.setCategoria(category);
        City city = new City();
        existingProduct.setCidade(city);
        Set<Feature> features = new HashSet<>();
        existingProduct.setCaracteristicas(features);

        when(productRepository.findById(1)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findById(2)).thenReturn(Optional.empty());
    }

    @Test
    void testSaveProduct() {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setTitulo("Image Title");
        imageDTO.setUrl("Image URL");
        imageService.save(imageDTO);
        Set<ImageDTO> images = new HashSet<>();
        images.add(imageDTO);

        FeatureDTO featureDTO = new FeatureDTO();
        featureDTO.setNome("Feature Name");
        featureDTO.setIcone("Feature Icone");
        featureService.save(featureDTO);
        Set<FeatureDTO> features = new HashSet<>();
        features.add(featureDTO);

        CityDTO cityDTO = new CityDTO();
        cityDTO.setNome("City Name");
        cityDTO.setPais("City Country");
        cityService.save(cityDTO);
        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setDescription("Category Descriptiom");
        categoryDTO.setQualification("Category Qualification");
        categoryDTO.setUrlImg("Category URLImage");
        categoryService.save(categoryDTO);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setImagens(images);
        productDTO.setCaracteristicas(features);
        productDTO.setCategoria(categoryDTO);
        productDTO.setCidade(cityDTO);
        productDTO.setNome("New Name");
        productDTO.setDescricao("New Description");

        productService.save(productDTO);
        Optional<Product> product = productRepository.findProductByNome(productDTO.getNome());
        assertTrue(product.isPresent());
    }

    @Test
    void testFindAllProducts() {
        Page<Product> productPage = Page.empty();
        Pageable pageable = Pageable.unpaged();

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        Page<ProductDTO> resultPage = productService.findAll(pageable);

        assertTrue(resultPage.isEmpty());
    }

    @Test
    void testFindProductByIdExisting() {
        Optional<ProductDTO> result = productService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(existingProduct.getNome(), result.get().getNome());
    }

    @Test
    void testFindProductByIdNonExisting() {
        assertThrows(NotFoundException.class, () -> productService.findById(2) );
    }
}
