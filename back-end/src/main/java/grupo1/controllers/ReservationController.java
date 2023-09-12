package grupo1.controllers;

import grupo1.dtos.ReservationDTO;
import grupo1.dtos.ReservationDTOResponse;
import grupo1.services.impl.ReservationServiceImpl;
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

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "http://localhost:5173/", maxAge = 3600)
public class ReservationController {

    final static Logger log = Logger.getLogger(ReservationController.class);

    private final ReservationServiceImpl reservationServiceImpl;

    @Autowired
    public ReservationController(ReservationServiceImpl reservationServiceImpl) {
        this.reservationServiceImpl = reservationServiceImpl;
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> save(@RequestBody ReservationDTO reservationDTO) {
        log.debug("Salvando nova reserva");
        ReservationDTO reservation = reservationServiceImpl.save(reservationDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(reservation.getId()).toUri();
        return ResponseEntity.created(uri).body(reservation);
    }

    @GetMapping
    public ResponseEntity<Page<ReservationDTO>> findAll(Pageable pageable) {
        log.debug("Buscando todas as reserva");
        Page<ReservationDTO> reservationms = reservationServiceImpl.findAll(pageable);
        return ResponseEntity.ok(reservationms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ReservationDTOResponse>> findById(@PathVariable Integer id) {
        log.debug("Buscando a reserva com id: " + id);
        return ResponseEntity.ok(reservationServiceImpl.findById(id));
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Page<ReservationDTO>> findReservationsByCustomerId(@PathVariable Integer id) {
        log.debug("Buscando a reserva com id: " + id);
        return ResponseEntity.ok(reservationServiceImpl.findReservationsByCustomerId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        log.debug("Excluindo a r7eserva com id: " + id);
        reservationServiceImpl.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> update(@PathVariable Integer id, @RequestBody ReservationDTO reservationDTO) {
        log.debug("Atualizando a reserva: " + reservationDTO.toString());
        return ResponseEntity.status(HttpStatus.OK).body(reservationServiceImpl.update(id, reservationDTO));
    }
}