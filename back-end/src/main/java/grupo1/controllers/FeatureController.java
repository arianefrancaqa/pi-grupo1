package grupo1.controllers;

import grupo1.dtos.FeatureDTO;
import grupo1.services.impl.FeatureServiceImpl;
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
@RequestMapping("/caracteristicas")
public class FeatureController {

    final static Logger log = Logger.getLogger(FeatureController.class);

    private final FeatureServiceImpl featureServiceImpl;

    @Autowired
    public FeatureController(FeatureServiceImpl featureServiceImpl) {
        this.featureServiceImpl = featureServiceImpl;
    }

    @PostMapping
    public ResponseEntity<FeatureDTO> save(@RequestBody FeatureDTO featureDTO) {
        log.debug("Salvando nova caracteristica");
        FeatureDTO feature = featureServiceImpl.save(featureDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(feature.getId()).toUri();
        return ResponseEntity.created(uri).body(feature);
    }

    @GetMapping
    public ResponseEntity<Page<FeatureDTO>> findAll(Pageable pageable) {
        log.debug("Buscando todas as caracteristicas");
        Page<FeatureDTO> caracteristicas = featureServiceImpl.findAll(pageable);
        return ResponseEntity.ok(caracteristicas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<FeatureDTO>> findById(@PathVariable Integer id) {
        log.debug("Buscando a caracteristica com id: " + id);
        return ResponseEntity.ok(featureServiceImpl.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeature(@PathVariable Integer id) {
        log.debug("Excluindo a caracteristica com id: " + id);
        featureServiceImpl.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeatureDTO> update(@PathVariable Integer id, @RequestBody FeatureDTO featureDTO) {
        log.debug("Atualizando a caracteristica: " + featureDTO.toString());
        return ResponseEntity.status(HttpStatus.OK).body(featureServiceImpl.update(id, featureDTO));
    }

}