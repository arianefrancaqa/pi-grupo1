package grupo1.controllers;

import grupo1.dtos.ImageDTO;
import grupo1.services.impl.ImageServiceImpl;
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
@RequestMapping("/imagens")
public class ImageController {

    final static Logger log = Logger.getLogger(ImageController.class);

    private final ImageServiceImpl imageServiceImpl;
    @Autowired
    public ImageController(ImageServiceImpl imageServiceImpl) {
        this.imageServiceImpl = imageServiceImpl;
    }

    @PostMapping
    public ResponseEntity<ImageDTO> save(@RequestBody ImageDTO imageDTO){
        log.debug("Salvando nova imagem");
        ImageDTO image = imageServiceImpl.save(imageDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(image.getId()).toUri();
        return ResponseEntity.created(uri).body(image);
    }

    @GetMapping
    public ResponseEntity<Page<ImageDTO>> findAll(Pageable pageable) {
        log.debug("Buscando todas as imagems");
        Page<ImageDTO> imagems = imageServiceImpl.findAll(pageable);
        return ResponseEntity.ok(imagems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ImageDTO>> findById(@PathVariable Integer id) {
        log.debug("Buscando a imagem com id: " + id);
        return ResponseEntity.ok(imageServiceImpl.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer id) {
        log.debug("Excluindo a imagem com id: " + id);
        imageServiceImpl.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImageDTO> update(@PathVariable Integer id, @RequestBody ImageDTO imageDTO) {
        log.debug("Atualizando a imagem: " + imageDTO.toString());
        return ResponseEntity.status(HttpStatus.OK).body(imageServiceImpl.update(id, imageDTO));
    }

}
