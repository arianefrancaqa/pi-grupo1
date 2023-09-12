package grupo1.services;

import grupo1.dtos.RoleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IRoleService {

    RoleDTO save(RoleDTO dto);
    Page<RoleDTO> findAll(Pageable pageable);
    Optional<RoleDTO> findById(Integer id);
    RoleDTO update(Integer id, RoleDTO request);
    void delete(Integer id);
}
