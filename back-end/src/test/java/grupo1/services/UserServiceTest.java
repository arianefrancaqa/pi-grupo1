package grupo1.services;

import grupo1.configs.jwt.JwtService;
import grupo1.dtos.UserCompleteDTO;
import grupo1.dtos.UserDTO;
import grupo1.entities.Role;
import grupo1.entities.User;
import grupo1.exceptions.DatabaseException;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.IRoleRepository;
import grupo1.repositories.IUserRepository;
import grupo1.services.impl.UserServiceImpl;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Log4j
@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserServiceImpl userServiceImpl;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private IRoleRepository roleRepository;
    private User existingUser;
    private Role existingRole;

    @BeforeEach
    void setUp() {
        existingRole = new Role();
        existingRole.setId(1);
        existingRole.setNome("ROLE_USER");

        existingUser = new User();
        existingUser.setId(1);
        existingUser.setNome("John");
        existingUser.setSobrenome("Doe");
        existingUser.setEmail("john.doe@example.com");
        existingUser.setSenha("password");
        existingUser.addRole(existingRole);

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        when(roleRepository.findRoleByNome("ROLE_USER")).thenReturn(Optional.of(existingRole));
        when(roleRepository.findRoleByNome("ROLE_ADMIN")).thenReturn(null);
    }

    @Test
    void testSaveUser() {
        UserCompleteDTO userDTO = new UserCompleteDTO();
        userDTO.setNome("Jane");
        userDTO.setSobrenome("Doe");
        userDTO.setEmail("jane.doe@example.com");
        userDTO.setSenha("password");
        userDTO.addRole(existingRole.getNome());

        User savedUser = new User();
        savedUser.setId(2);
        savedUser.setNome(userDTO.getNome());
        savedUser.setSobrenome(userDTO.getSobrenome());
        savedUser.setEmail(userDTO.getEmail());
        savedUser.setSenha(userDTO.getSenha());
        savedUser.addRole(existingRole);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        UserDTO savedUserDTO = userService.save(userDTO);

        assertEquals(savedUserDTO.getId(), savedUser.getId());
        assertEquals(savedUserDTO.getNome(), savedUser.getNome());
        assertEquals(savedUserDTO.getSobrenome(), savedUser.getSobrenome());
        assertEquals(savedUserDTO.getEmail(), savedUser.getEmail());
        assertTrue(savedUserDTO.getFuncoes().contains("ROLE_USER"));
    }

    @Test
    void testSaveUserWithExistingEmail() {
        UserCompleteDTO userDTO = new UserCompleteDTO();
        userDTO.setEmail(existingUser.getEmail());

        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.ofNullable(existingUser));
        assertThrows(DatabaseException.class, () -> userService.save(userDTO));
    }

    @Test
    void testFindAllUsers() {
        Page<User> userPage = Page.empty();
        Pageable pageable = Pageable.unpaged();

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        Page<UserDTO> resultPage = userService.findAll(pageable);
        assertTrue(resultPage.isEmpty());
    }

    @Test
    void testFindUserByIdExisting() {
        Optional<UserDTO> result = userService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(existingUser.getNome(), result.get().getNome());
        assertEquals(existingUser.getSobrenome(), result.get().getSobrenome());
        assertEquals(existingUser.getEmail(), result.get().getEmail());
        assertTrue(result.get().getFuncoes().contains("ROLE_USER"));
    }

    @Test
    void testFindUserByIdNonExisting() {
        assertThrows(NotFoundException.class, () -> userService.findById(2));
    }

    @Test
    void testUpdateExistingUser() {
        UserCompleteDTO userDTO = new UserCompleteDTO();
        userDTO.setNome("Updated Jane");
        userDTO.setSobrenome("Updated Doe");
        userDTO.setEmail("updated.jane.doe@example.com");
        userDTO.setSenha("updated_password");
        userDTO.addRole(existingRole.getNome());

        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        UserDTO updatedUserDTO = userService.update(1, userDTO);

        assertEquals(userDTO.getNome(), updatedUserDTO.getNome());
        assertEquals(userDTO.getSobrenome(), updatedUserDTO.getSobrenome());
        assertEquals(userDTO.getEmail(), updatedUserDTO.getEmail());
    }

    @Test
    void testUpdateNonExistingUser() {
        UserCompleteDTO userDTO = new UserCompleteDTO();
        assertThrows(NotFoundException.class, () -> userService.update(2, userDTO));
    }

    @Test
    void testDeleteExistingUser() {
        assertDoesNotThrow(() -> userService.delete(1));
    }

    @Test
    void testDeleteNonExistingUser() {
        assertThrows(NotFoundException.class, () -> userService.delete(2));
    }

    @Test
    void testDeleteWithIntegrityViolation() {
        doThrow(DataIntegrityViolationException.class).when(userRepository).deleteById(1);
        assertThrows(DatabaseException.class, () -> userService.delete(1));
    }
}
