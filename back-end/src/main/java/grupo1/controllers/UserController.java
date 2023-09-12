package grupo1.controllers;

import grupo1.dtos.UserCompleteDTO;
import grupo1.dtos.UserDTO;
import grupo1.services.impl.UserServiceImpl;
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
@RequestMapping("/usuarios")
public class UserController {

    final static Logger log = Logger.getLogger(UserController.class);

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) { this.userService = userService; }


    @PostMapping
    public ResponseEntity<UserDTO> save(@RequestBody UserCompleteDTO userDTO){
        log.debug("Salvando novo usuário");
        UserDTO user = userService.save(userDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
        log.debug("Buscando todos os usuários");
        Page<UserDTO> usuarios = userService.findAll(pageable);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<UserDTO>> findById(@PathVariable Integer id) {
        log.debug("Buscando o usuário com id: " + id);
        return ResponseEntity.ok(userService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        log.debug("Excluindo o usuário com id: " + id);
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Integer id, @RequestBody UserCompleteDTO userDTO) {
        log.debug("Atualizando o usuário com ID: " + id);
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, userDTO));
    }
}

