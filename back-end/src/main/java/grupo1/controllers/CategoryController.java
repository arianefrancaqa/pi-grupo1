package grupo1.controllers;

import grupo1.dtos.CategoryDTO;
import grupo1.services.impl.CategoryServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173/", maxAge = 3600)
@RestController
@RequestMapping("/categorias")
public class CategoryController {

    final static Logger log = Logger.getLogger(CategoryController.class);

    private final CategoryServiceImpl categoryServiceImpl;
    @Autowired
    public CategoryController(CategoryServiceImpl categoryServiceImpl) {
        this.categoryServiceImpl = categoryServiceImpl;
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> save(@RequestBody CategoryDTO categoryDTO){
        log.debug("Salvando nova categoria");
        CategoryDTO category = categoryServiceImpl.save(categoryDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(category.getId()).toUri();
        return ResponseEntity.created(uri).body(category);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> findAll(Pageable pageable) {
        log.debug("Buscando todas as categorias");
        Page<CategoryDTO> categorias = categoryServiceImpl.findAll(pageable);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<CategoryDTO>> findById(@PathVariable Integer id) {
        log.debug("Buscando a categoria com id: " + id);
        Optional<CategoryDTO> category = categoryServiceImpl.findById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/descricao/{description}")
    public ResponseEntity<Page<CategoryDTO>> findByName(Pageable pageable, @PathVariable String description){
        log.debug("Buscando categoria: " + description);
        Page<CategoryDTO> categories = categoryServiceImpl.findByName(pageable, description);
        return ResponseEntity.ok(categories);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        log.debug("Excluindo a categoria com id: " + id);
        categoryServiceImpl.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(@PathVariable Integer id, @RequestBody CategoryDTO categoryDTO) {
        log.debug("Atualizando a categoria: " + categoryDTO.toString());
        return ResponseEntity.status(HttpStatus.OK).body(categoryServiceImpl.update(id, categoryDTO));
    }

}
