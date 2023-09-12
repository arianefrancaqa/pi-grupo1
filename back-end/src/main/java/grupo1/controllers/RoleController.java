package grupo1.controllers;

import grupo1.dtos.RoleDTO;
import grupo1.services.impl.RoleServiceImpl;
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
@RequestMapping("/funcoes")
public class RoleController {

    final static Logger log = Logger.getLogger(RoleController.class);

    private final RoleServiceImpl roleService;
    @Autowired
    public RoleController(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleDTO> save(@RequestBody RoleDTO roleDTO) {
        log.debug("Salvando nova função");
        RoleDTO role = roleService.save(roleDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(role.getId()).toUri();
        return ResponseEntity.created(uri).body(role);
    }

    @GetMapping
    public ResponseEntity<Page<RoleDTO>> findAll(Pageable pageable) {
        log.debug("Buscando todas funções");
        Page<RoleDTO> funcoes = roleService.findAll(pageable);
        return ResponseEntity.ok(funcoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<RoleDTO>> findById(@PathVariable Integer id) {
        log.debug("Buscando função com id: " + id);
        return ResponseEntity.ok(roleService.findById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id) {
        log.debug("Excluindo função com id: " + id);
        roleService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable Integer id, @RequestBody RoleDTO userDTO) {
        log.debug("Atualizando função: " + userDTO.toString());
        return ResponseEntity.status(HttpStatus.OK).body(roleService.update(id, userDTO));
    }
}
