package grupo1.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import grupo1.dtos.RoleDTO;
import grupo1.entities.Role;
import grupo1.entities.User;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.IRoleRepository;
import grupo1.repositories.IUserRepository;
import grupo1.services.IRoleService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class RoleServiceImpl implements IRoleService {

    private static final Logger logger = Logger.getLogger(RoleServiceImpl.class);
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    @Autowired
    public RoleServiceImpl(IRoleRepository roleRepository, IUserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RoleDTO save(RoleDTO dto) {
        Optional<Role> roleDB = roleRepository.findRoleByNome(dto.getNome());
        if(roleDB.isEmpty()) {
            Role role = new Role();
            role.setNome(dto.getNome());
            roleRepository.save(role);
            logger.info("Função salva com sucesso.");
            return new RoleDTO(role);
        }
        throw new DatabaseException("Função com o nome " + dto.getNome() + " já existente!");
    }

    @Override
    public Page<RoleDTO> findAll(Pageable pageable) {
        Page<Role> roles = roleRepository.findAll(pageable);
        logger.info("Lista de funções encontrada com sucesso.");
        return roles.map(RoleDTO::new);
    }

    @Override
    public Optional<RoleDTO> findById(Integer id) {
        Optional<Role> role = roleRepository.findById(id);
        ObjectMapper mapper = new ObjectMapper();
        if(role.isEmpty()) {
            logger.warn("Função não encontrada." + role);
            throw new NotFoundException("Nenhuma função encontrada para o ID: " + id);
        }
        logger.info("Função encotrada com sucesso." + role);
        return role.map(r -> mapper.convertValue(r, RoleDTO.class));
    }

    @Override
    public RoleDTO update(Integer id, RoleDTO dto) {
        dto.setId(id);
        Role role = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Função não encontrada"));
        role.setNome(dto.getNome());
        role.setId(dto.getId());
        role = roleRepository.save(role);
        logger.info("Função atualizada com sucesso." + role);
        return new RoleDTO(role);
    }

    @Override
    public void delete(Integer id) {
        Optional<Role> role = roleRepository.findById(id);
        List<User> users = userRepository.findAll();
        if(role.isEmpty()) {
            logger.warn("Função não encontrada pelo ID: " + id);
            throw new NotFoundException("Id não encontrado: " + id);
        }
        try {
            for(User user : users) {
                for (Role funcao : user.getFuncoes()) {
                    if(Objects.equals(funcao,role.get())) {
                        user.removeRole(role.get());
                    }
                }
                userRepository.save(user);
            }
            roleRepository.deleteById(id);
            logger.info("Função deletada com sucesso: " + id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Erro ao buscar função pelo: " + id);
            throw new NotFoundException("Id não encontrado: " + id);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Violação de integridade.");
            throw new DatabaseException("Violação de integridade.");
        }
    }
}
