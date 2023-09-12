package grupo1.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import grupo1.dtos.ProductDTO;
import grupo1.dtos.ReservationDTO;
import grupo1.dtos.ReservationDTOResponse;
import grupo1.dtos.UserDTO;
import grupo1.entities.Product;
import grupo1.entities.Reservation;
import grupo1.entities.User;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.IProductRepository;
import grupo1.repositories.IReservationRepository;
import grupo1.repositories.IUserRepository;
import grupo1.services.IReservationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements IReservationService {

    private static final org.apache.log4j.Logger logger = Logger.getLogger(ProductServiceImpl.class);
    private final IReservationRepository reservationRepository;
    private final IProductRepository productRepository;
    private final IUserRepository userRepository;

    @Autowired
    public ReservationServiceImpl(IReservationRepository reservationRepository, IProductRepository productRepository, IUserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReservationDTO save(ReservationDTO dto) {
        Integer contadorReservas = reservationRepository.findReservavionBetweenDates(dto.getProductId(),dto.getDataInicioReserva(), dto.getDataFimReserva());
        if(contadorReservas > 0){
            throw new DatabaseException("Já existe uma reserva para este produto nessas datas");
        }
        logger.info("Produto livre nas datas selecionadas");
        Reservation reservation = new Reservation();
        copyDtoToEntity(dto, reservation);
        System.out.println(reservation);
        reservation = reservationRepository.save(reservation);
        logger.info("Reserva salva com sucesso: " + dto);
        return new ReservationDTO(reservation);
    }

    @Override
    public Page<ReservationDTO> findAll(Pageable pageable) {
        Page<Reservation> reservations = reservationRepository.findAll(pageable);
        if (reservations.isEmpty()) {
            logger.info("Nenhuma reserva encontrada.");
        } else {
            logger.info("Reserva(s) encontrada(s) com sucesso.");
        }
        return reservations.map(ReservationDTO::new);
    }

    @Override
    public Optional<ReservationDTOResponse> findById(Integer id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        ReservationDTOResponse reservationResponse = new ReservationDTOResponse();
        if (reservation.isEmpty()) {
            logger.info("Reserva não encontrada: " + id);
        } else {
            logger.info("Reserva encontrada com sucesso: " + id);
            copyEntityToDto(reservation.get(),reservationResponse);
        }
        return Optional.of(reservationResponse);
    }

    @Override
    public  Page<ReservationDTO> findReservationsByCustomerId(Integer id) {
        List<Reservation> reservations = reservationRepository.findReservationsByCustomerId(id);
        if (reservations.isEmpty()) {
            logger.info("Nenhuma reserva encontrada.");
        } else {
            logger.info("Reserva(s) encontrada(s) com sucesso.");
        }
        Page<Reservation> pagedReservations = new PageImpl<>(reservations);
        return pagedReservations.map(ReservationDTO::new);
    }

    @Override
    public ReservationDTO update(Integer id, ReservationDTO dto) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
        dto.setId(id);
        copyDtoToEntity(dto, reservation);
        reservation = reservationRepository.save(reservation);
        logger.info("Produto atualizado com sucesso: " + dto);
        return new ReservationDTO(reservation);
    }

    @Override
    public void delete(Integer id) {
        try {
            //APAGANDO DE PRODUTO:
            Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
            List<User> users = userRepository.findAll();
            reservationRepository.deleteById(id);
            logger.info("Reserva deletada com sucesso: " + id);
        }
        catch (EmptyResultDataAccessException e) {
            logger.warn("Erro ao buscar a reserva pelo: " + id);
            throw new NotFoundException("Id não encontrado: " + id);
        }
        catch (DataIntegrityViolationException e) {
            logger.warn("Violação de integridade.");
            throw new DatabaseException("Violação de integridade.");
        }
    }

    private void copyDtoToEntity(ReservationDTO dto, Reservation entity){
        entity.setId(dto.getId());
        entity.setDataInicioReserva(dto.getDataInicioReserva());
        entity.setDataFimReserva(dto.getDataFimReserva());
        entity.setHoraInicioReserva(dto.getHoraInicioReserva());

        Optional<Product> product = productRepository.findById(dto.getProductId());
        if(product.isPresent()) {
            entity.setProduct(product.get());
        } else {
            throw new NotFoundException("Produto não encontrado!");
        }

        Optional<User> customer = userRepository.findById(dto.getCustomerId());
        if(customer.isPresent()) {
            entity.setCustomer(customer.get());
        } else {
            throw new NotFoundException("Cliente não encontrado!");
        }
    }

    private void copyEntityToDto(Reservation entity, ReservationDTOResponse dto){
        dto.setId(entity.getId());
        dto.setDataInicioReserva(entity.getDataInicioReserva());
        dto.setDataFimReserva(entity.getDataFimReserva());
        dto.setHoraInicioReserva(entity.getHoraInicioReserva());
        dto.setProduct(new ProductDTO(entity.getProduct()));
        dto.setCustomer(new UserDTO(entity.getCustomer()));
    }

}
