package grupo1.controllers;

import grupo1.dtos.CityDTO;
import grupo1.services.impl.CityServiceImpl;
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
@RequestMapping("/cidades")
public class CityController {

    final static Logger log = Logger.getLogger(CityController.class);

    private final CityServiceImpl cityServiceImpl;
    @Autowired
    public CityController(CityServiceImpl cityServiceImpl) {
        this.cityServiceImpl = cityServiceImpl;
    }

    @PostMapping
    public ResponseEntity<CityDTO> save(@RequestBody CityDTO cityDTO){
        log.debug("Salvando nova cidade");
        CityDTO city = cityServiceImpl.save(cityDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(city.getId()).toUri();
        return ResponseEntity.created(uri).body(city);
    }

    @GetMapping
    public ResponseEntity<Page<CityDTO>> findAll(Pageable pageable) {
        log.debug("Buscando todas as cidades");
        Page<CityDTO> cidades = cityServiceImpl.findAll(pageable);
        return ResponseEntity.ok(cidades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<CityDTO>> findById(@PathVariable Integer id) {
        log.debug("Buscando a cidade com id: " + id);
        return ResponseEntity.ok(cityServiceImpl.findById(id));
    }

    @GetMapping("/descricao/{description}")
    public ResponseEntity<Page<CityDTO>> findByName(Pageable pageable, @PathVariable String description){
        log.debug("Buscando a cidade: " + description);
        Page<CityDTO> responses = cityServiceImpl.findByName(pageable, description);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable Integer id) {
        log.debug("Excluindo a cidade com id: " + id);
        cityServiceImpl.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CityDTO> update(@PathVariable Integer id, @RequestBody CityDTO cityDTO) {
        log.debug("Atualizando a cidade: " + cityDTO.toString());
        return ResponseEntity.status(HttpStatus.OK).body(cityServiceImpl.update(id, cityDTO));
    }

}
