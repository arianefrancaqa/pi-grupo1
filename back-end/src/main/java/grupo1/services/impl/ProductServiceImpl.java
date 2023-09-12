package grupo1.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import grupo1.dtos.DateSearchDTO;
import grupo1.dtos.ProductDTO;
import grupo1.entities.*;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.*;
import grupo1.services.IProductService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    private static final org.apache.log4j.Logger logger = Logger.getLogger(ProductServiceImpl.class);
    private final IProductRepository productRepository;
    private final IFeatureRepository featureRepository;
    private final ICityRepository cityRepository;
    private final IImageRepository imageRepository;
    private final ICategoryRepository categoryRepository;
    private final IReservationRepository reservationRepository;

    @Autowired
    public ProductServiceImpl(
            IProductRepository productRepository,
            IFeatureRepository featureRepository,
            ICityRepository cityRepository,
            IImageRepository imageRepository,
            ICategoryRepository categoryRepository,
            IReservationRepository reservationRepository
    ) {
        this.productRepository = productRepository;
        this.featureRepository = featureRepository;
        this.cityRepository = cityRepository;
        this.imageRepository = imageRepository;
        this.categoryRepository = categoryRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public ProductDTO save(ProductDTO dto) {
        Optional<Product> productDB = productRepository.findProductByNome(dto.getNome());
        if(productDB.isPresent()) {
            throw new DatabaseException("Produto com o nome " + dto.getNome() + " já existente!");
        }
        Product product = new Product();
        copyDtoToEntity(dto, product);
        System.out.println(product);
        productRepository.save(product);
        logger.info("Produto salvo com sucesso: " + dto);
        return new ProductDTO(product);
    }

    @Override
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> categories = productRepository.findAll(pageable);
        logger.info("Lista de produtos encontrada com sucesso.");
        return categories.map(ProductDTO::new);
    }

    @Override
    public Optional<ProductDTO> findById(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        ObjectMapper mapper = new ObjectMapper();
        if (product.isEmpty()) {
            logger.info("Produto não encontrado." + product);
            throw new NotFoundException("Produto com o id " + id + " não encontrado");
        } else {
            logger.info("Produto encontrado com sucesso." + product);
        }
        return product.map(product1 -> mapper.convertValue(product1, ProductDTO.class));
    }

    @Override
    public Page<ProductDTO> findByName(Pageable pageable, String name) {
        Page<Product> products = productRepository.findProductByNomeContainingIgnoreCase(pageable, name);
        if (products.isEmpty()) {
            logger.info("Nenhum produto encontrado com o nome:" + name);
        } else {
            logger.info("Produto(s) encontrado(s) com sucesso.");
        }
        return products.map(ProductDTO::new);
    }

    @Override
    public Page<ProductDTO> findByCategory(Pageable pageable, Integer idCategoria) {
        Page<Product> products = productRepository.findAll(pageable);
        List<Product> filteredProductsList = products
                .stream()
                .filter(produto -> Objects.equals(produto.getCategoria().getId(), idCategoria))
                .toList();
        Page<Product> filteredProductsPage = new PageImpl<>(filteredProductsList);
        if (filteredProductsPage.isEmpty()) {
            logger.info("Nenhum produto encontrado para a categoria: " + idCategoria);
        } else {
            logger.info("Produto(s) encontrado(s) com sucesso.");
        }
        return filteredProductsPage.map(ProductDTO::new);
    }

    @Override
    public Page<ProductDTO> findByCity(Pageable pageable, Integer idCidade) {
        Page<Product> products = productRepository.findAll(pageable);
        List<Product> filteredProductsList = products
                .stream()
                .filter(produto -> Objects.equals(produto.getCidade().getId(), idCidade))
                .toList();
        Page<Product> filteredProductsPage = new PageImpl<>(filteredProductsList);
        if (filteredProductsPage.isEmpty()) {
            logger.info("Nenhum produto encontrado para a cidade: " + idCidade);
        } else {
            logger.info("Produto(s) encontrado(s) com sucesso.");
        }
        return filteredProductsPage.map(ProductDTO::new);
    }

    @Override
    public Page<ProductDTO> findByDates(Pageable pageable, DateSearchDTO dateSearchDTO) {
        List<Product> products = productRepository
                .findProductsThatDoNotHaveReservationInAGivenPeriodOfTime(
                        dateSearchDTO.getDataInicioPesquisa(),
                        dateSearchDTO.getDataFimPesquisa()
                );
        Page<Product> productsPage = new PageImpl<>(products);
        if (productsPage.isEmpty()) {
            logger.info("Nenhum produto encontrado na data.");
        } else {
            logger.info("Produto(s) encontrado(s) com sucesso.");
        }
        return productsPage.map(ProductDTO::new);
    }

    @Override
    public Page<ProductDTO> findByDatesAndCity(Pageable pageable, Integer idCidade, DateSearchDTO dateSearchDTO) {
        Page<ProductDTO> productsByDates = findByDates(pageable, dateSearchDTO);
        List<ProductDTO> productsByDatesAndCity = productsByDates
                .stream()
                .filter(produto -> Objects.equals(produto.getCidade().getId(), idCidade))
                .toList();
        Page<ProductDTO> filteredProductsPage = new PageImpl<>(productsByDatesAndCity);
        if (filteredProductsPage.isEmpty()) {
            logger.info("Nenhum produto encontrado na data para a cidade: " + idCidade);
        } else {
            logger.info("Produto(s) encontrado(s) com sucesso.");
        }
        return filteredProductsPage;
    }

    @Override
    public ProductDTO update(Integer id, ProductDTO dto) {
        dto.setId(id);
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado."));
        copyDtoToEntity(dto, product);
        product = productRepository.save(product);
        logger.info("Produto atualizado com sucesso: " + dto.getNome());
        return new ProductDTO(product);
    }

    @Override
    public void delete(Integer id) {
        try {
            //APAGANDO CIDADE:
            Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Produto não encontrado."));
            Set<Product> productsCity = product.getCidade().getProdutos();
            Set<Product> productsCityRevised = productsCity
                    .stream()
                    .filter(produto -> !Objects.equals(produto.getId(), id))
                    .collect(Collectors.toSet());

            City city = cityRepository.findById(product.getCidade().getId()).orElseThrow(() -> new NotFoundException("Cidade não encontrada."));
            city.setProdutos(productsCityRevised);
            cityRepository.save(city);

            //APAGANDO CATEGORIA:
            Set<Product> productsCategory = product.getCategoria().getProdutos();
            Set<Product> productsCategoryRevised = productsCategory
                    .stream()
                    .filter(produto -> !Objects.equals(produto.getId(), id))
                    .collect(Collectors.toSet());

            Category category = categoryRepository.findById(product.getCategoria().getId()).orElseThrow(() -> new NotFoundException("Categoria não encontrada."));
            category.setProdutos(productsCategoryRevised);
            categoryRepository.save(category);

            productRepository.deleteById(id);
            logger.info("Produto deletado com sucesso: " + id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Erro ao buscar a produto pelo: " + id);
            throw new NotFoundException("Id não encontrado: " + id);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Violação de integridade.");
            throw new DatabaseException("Violação de integridade.");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.getCaracteristicas().clear();
        dto.getCaracteristicas().forEach(featureDTO -> {
            Optional<Feature> feature = featureRepository.findById(featureDTO.getId());
            if(feature.isPresent()) {
                entity.getCaracteristicas().add(feature.get());
            } else {
                throw new NotFoundException("Característica não encontrada!");
            }
        });

        entity.getImagens().clear();
        dto.getImagens().forEach(imageDTO -> {
            Optional<Image> image = imageRepository.findById(imageDTO.getId());
            if(image.isPresent()) {
                entity.getImagens().add(image.get());
            } else {
                throw new NotFoundException("Imagem não encontrada!");
            }
        });

        Optional<Category> category = categoryRepository.findById(dto.getCategoria().getId());
        if(category.isPresent()) {
            entity.setCategoria(category.get());
        } else {
            throw new NotFoundException("Categoria não encontrada!");
        }
        Optional<City> city = cityRepository.findById(dto.getCidade().getId());
        if(city.isPresent()) {
            entity.setCidade(city.get());
        } else {
            throw new NotFoundException("Cidade não encontrada!");
        }
    }
}
