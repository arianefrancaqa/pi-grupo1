package grupo1.services.impl;

import grupo1.dtos.UserCompleteDTO;
import grupo1.dtos.UserDTO;
import grupo1.entities.Role;
import grupo1.entities.User;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.IRoleRepository;
import grupo1.repositories.IUserRepository;
import grupo1.services.IUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserServiceImpl(IUserRepository userRepository, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDTO save(UserCompleteDTO dto) {
        Optional<User> userRequest = userRepository.findByEmail(dto.getEmail());
        if (userRequest.isPresent()) {
            logger.warn("Usuário com mesmo e-mail encontrado!");
            throw new DatabaseException("E-mail já cadastrado!");
        }
        User user = new User();
        copyDtoToEntity(user, dto);
        user.setSenha(passwordEncoder.encode(dto.getSenha()));
        user = userRepository.save(user);
        if(Objects.equals(user.getNome(), dto.getNome())) {
            logger.info("Usuário salvo com sucesso.");
            return new UserDTO(user);
        }
        throw new RuntimeException("Erro ao salvar usuário");
    }

    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        logger.info("Lista de usuários encontrada com sucesso.");
        return users.map(UserDTO::new);
    }

    @Override
    public Optional<UserDTO> findById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        UserDTO userDTO = new UserDTO();
        if (user.isPresent()) {
            logger.info("Usuário encotrado com sucesso.");
            copyEntityToDto(user.get(),userDTO);
            return  Optional.of(userDTO);
        } else {
            logger.warn("Usuário não encontrado.");
            throw new NotFoundException("Nenhum usuário encontrado para o ID: " + id);
        }
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        UserDTO userDTO = new UserDTO();
        if (user.isPresent()) {
            logger.info("Usuário encotrado com sucesso.");
            copyEntityToDto(user.get(),userDTO);
            return  Optional.of(userDTO);
        } else {
            logger.warn("Usuário não encontrado.");
            throw new NotFoundException("Nenhum usuário encontrado para o e-mail: " + email);
        }
    }

    @Override
    public UserDTO update(Integer id, UserCompleteDTO dto) {
        dto.setId(id);
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        copyDtoToEntity(user, dto);
        user.setSenha(passwordEncoder.encode(dto.getSenha()));
        user = userRepository.save(user);
        logger.info("Usuário atualizado com sucesso. ID: " + id);
        return new UserDTO(user);
    }

    @Override
    public void delete(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {
            logger.warn("Usuário não encontrado pelo ID: " + id);
            throw new NotFoundException("Id não encontrado: " + id);
        }
        try {
            userRepository.deleteById(id);
            logger.info("Usuário deletado com sucesso: " + id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Erro ao buscar o usuário pelo: " + id);
            throw new NotFoundException("Id não encontrado: " + id);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Violação de integridade.");
            throw new DatabaseException("Violação de integridade.");
        }
    }

    private void copyDtoToEntity(User entity, UserCompleteDTO dto) {
        entity.setNome(dto.getNome());
        entity.setSobrenome(dto.getSobrenome());
        entity.setEmail(dto.getEmail());
        dto.getFuncoes().forEach(role -> {
            Optional<Role> roleDB = roleRepository.findRoleByNome(role);
            if(roleDB.isEmpty()) {
                logger.info("Função não encontrado pelo nome: " + role);
                logger.info("Criando nova função...");
                Role newRole = new Role();
                newRole.setNome(role);
                newRole = roleRepository.save(newRole);
                logger.info("Adicionando função ao usuário...");
                entity.addRole(newRole);
            } else {
                logger.info("Adicionando função ao usuário...");
                entity.addRole(roleDB.get());
            }
        });
    }

    private void copyEntityToDto(User entity, UserDTO dto) {
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setSobrenome(entity.getSobrenome());
        dto.setEmail(entity.getEmail());
        entity.getFuncoes().forEach(role -> {
            Optional<Role> roleDB = roleRepository.findRoleByNome(role.getNome());
            if(roleDB.isEmpty()) {
                logger.warn("Função não encontrado pelo nome: " + role.getNome());
                throw new NotFoundException("Função não encontrada: " + role.getNome());
            }
            dto.addRole(role.getNome());
        });
    }
}
