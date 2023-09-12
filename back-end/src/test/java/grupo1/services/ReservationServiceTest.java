package grupo1.services;

import grupo1.dtos.ReservationDTO;
import grupo1.dtos.ReservationDTOResponse;
import grupo1.entities.Product;
import grupo1.entities.Reservation;
import grupo1.entities.User;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.IProductRepository;
import grupo1.repositories.IReservationRepository;
import grupo1.repositories.IUserRepository;
import grupo1.services.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(SpringExtension.class)
public class ReservationServiceTest {

    @InjectMocks
    private ReservationServiceImpl reservationService;
    @Mock
    private IReservationRepository reservationRepository;
    @Mock
    private IProductRepository productRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private Reservation existingReservation;
    @Mock
    private User existingUser;
    @BeforeEach
    public void setUp() {
        existingUser.setNome("João");
        existingUser.setSobrenome("Menezes");
        existingUser.setEmail("joaomenezes@email.com");
        existingUser.setSenha("brasil123");
        userRepository.save(existingUser);

        existingReservation = new Reservation();
        existingReservation.setId(1);
        existingReservation.setCustomer(existingUser);
        reservationRepository.save(existingReservation);

        when(reservationRepository.findById(1)).thenReturn(Optional.of(existingReservation));
        when(reservationRepository.findById(2)).thenReturn(Optional.empty());
    }

    @Test
    public void testSaveReservation() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setProductId(1);
        reservationDTO.setCustomerId(existingUser.getId());
        reservationDTO.setDataInicioReserva(LocalDate.parse("2023-08-26"));
        reservationDTO.setHoraInicioReserva(LocalTime.parse("10:00"));
        reservationDTO.setDataFimReserva(LocalDate.parse("2023-08-30"));

        ReservationDTO savedReservation = reservationService.save(reservationDTO);
        assertNotNull(savedReservation);
    }

    @Test
    public void testFindAllReservations() {
        List<Reservation> reservationList = new ArrayList<>();
        when(reservationRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(reservationList));
        Page<ReservationDTO> reservations = reservationService.findAll(Pageable.unpaged());
        verify(reservationRepository, times(1)).findAll(any(Pageable.class));
        assertNotNull(reservations);
    }

    @Test
    public void testFindReservationById() {
        when(reservationRepository.findById(existingReservation.getId())).thenReturn(Optional.of(new Reservation()));
        Optional<ReservationDTOResponse> reservationDTOResponse = reservationService.findById(existingReservation.getId());
        verify(reservationRepository, times(1)).findById(existingReservation.getId());
        assertTrue(reservationDTOResponse.isPresent());
    }

    @Test
    public void testUpdateReservation() {
        int reservationId = 1;
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setProductId(1);
        reservationDTO.setDataInicioReserva(LocalDate.parse("2023-08-26"));
        reservationDTO.setDataFimReserva(LocalDate.parse("2023-08-30"));
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(new Reservation()));
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(new Product()));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        ReservationDTO updatedReservation = reservationService.update(reservationId, reservationDTO);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        assertNotNull(updatedReservation);
    }

    @Test
    public void testDeleteReservation() {
        int reservationId = 1;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(new Reservation()));
        doNothing().when(reservationRepository).deleteById(reservationId);
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        assertDoesNotThrow(() -> reservationService.delete(reservationId));
        verify(reservationRepository, times(1)).deleteById(reservationId);
    }

    @Test
    public void testDeleteReservationNotFoundException() {
        int reservationId = 1;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> reservationService.delete(reservationId));
        verify(reservationRepository, never()).deleteById(reservationId);
    }

    @Test
    public void testDeleteReservationDataIntegrityViolationException() {
        int reservationId = 1;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(new Reservation()));
        doThrow(DataIntegrityViolationException.class).when(reservationRepository).deleteById(reservationId);
        assertThrows(DatabaseException.class, () -> reservationService.delete(reservationId));
        verify(reservationRepository, times(1)).deleteById(reservationId);
    }
}

