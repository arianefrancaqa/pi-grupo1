package grupo1.services;

import grupo1.dtos.UserCompleteDTO;
import grupo1.dtos.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IUserService {
    UserDTO save(UserCompleteDTO dto);
    Page<UserDTO> findAll(Pageable pageable);
    Optional<UserDTO> findById(Integer id);
    Optional<UserDTO> findByEmail(String email);
    UserDTO update(Integer id, UserCompleteDTO request);
    void delete(Integer id);
}
