package grupo1.controllers;

import grupo1.dtos.DateSearchDTO;
import grupo1.dtos.ProductDTO;
import grupo1.services.impl.ProductServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:5173/", maxAge = 3600)
@RestController
@RequestMapping("/produtos")
public class ProductController {

    final static Logger log = Logger.getLogger(ProductController.class);

    private final ProductServiceImpl productServiceImpl;
    @Autowired
    public ProductController(ProductServiceImpl productServiceImpl) {
        this.productServiceImpl = productServiceImpl;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> save(@RequestBody ProductDTO productDTO){
        log.debug("Salvando novo produto");
        ProductDTO product = productServiceImpl.save(productDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).body(product);
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(Pageable pageable) {
        log.debug("Buscando todas as produtos");
        Page<ProductDTO> produtos = productServiceImpl.findAll(pageable);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ProductDTO>> findById(@PathVariable Integer id) {
        log.debug("Buscando a produto com id: " + id);
        return ResponseEntity.ok(productServiceImpl.findById(id));
    }

    @GetMapping("/descricao/{description}")
    public ResponseEntity<Page<ProductDTO>> findByName(Pageable pageable, @PathVariable String description){
        log.debug("Buscando o produto: " + description);
        Page<ProductDTO> responses = productServiceImpl.findByName(pageable, description);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<Page<ProductDTO>> findByCategory(Pageable pageable, @PathVariable Integer idCategoria){
        log.debug("Buscando todos os produtos com a categoria: " + idCategoria);
        Page<ProductDTO> responses = productServiceImpl.findByCategory(pageable, idCategoria);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/cidade/{idCidade}")
    public ResponseEntity<Page<ProductDTO>> findByCity(Pageable pageable, @PathVariable Integer idCidade){
        log.debug("Buscando todos os produtos com a cidade: " + idCidade);
        Page<ProductDTO> responses = productServiceImpl.findByCity(pageable, idCidade);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/datas")
    public ResponseEntity<Page<ProductDTO>> findByDates(Pageable pageable, @RequestParam LocalDate dataInicioPesquisa, @RequestParam LocalDate dataFimPesquisa){
        DateSearchDTO dateSearchDTO = new DateSearchDTO(dataInicioPesquisa,dataFimPesquisa);
        log.debug("Buscando todos os produtos disponíveis nas datas: " + dateSearchDTO);
        Page<ProductDTO> responses = productServiceImpl.findByDates(pageable, dateSearchDTO);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/cidade/{idCidade}/datas")
    public ResponseEntity<Page<ProductDTO>> findByCityAndDates(Pageable pageable, @PathVariable Integer idCidade, @RequestParam LocalDate dataInicioPesquisa, @RequestParam LocalDate dataFimPesquisa){
        DateSearchDTO dateSearchDTO = new DateSearchDTO(dataInicioPesquisa,dataFimPesquisa);
        log.debug("Buscando todos os produtos disponíveis nas datas: " + dateSearchDTO);
        Page<ProductDTO> responses = productServiceImpl.findByDatesAndCity(pageable, idCidade, dateSearchDTO);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        log.debug("Excluindo a produto com id: " + id);
        productServiceImpl.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Integer id, @RequestBody ProductDTO productDTO) {
        log.debug("Atualizando a produto: " + productDTO.toString());
        return ResponseEntity.status(HttpStatus.OK).body(productServiceImpl.update(id, productDTO));
    }

}
