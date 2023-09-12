package grupo1.services;

import grupo1.configs.auth.AuthRequest;
import grupo1.configs.auth.AuthService;
import grupo1.configs.auth.AuthenticationResponse;
import grupo1.configs.auth.RegisterRequest;
import grupo1.configs.jwt.JwtService;
import grupo1.entities.Role;
import grupo1.entities.User;
import grupo1.repositories.IUserRepository;
import grupo1.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserServiceImpl userService;
    @Mock
    private JwtService jwtService;

    @Mock
    private IUserRepository userRepository;

    private User existingUser;
    private Role existingRole;

    @Test
    void testRegisterUser() {
        Set<String> roles = new HashSet<>();
        roles.add(existingRole.getNome());

        RegisterRequest request = new RegisterRequest();
        request.setNome("Jane");
        request.setSobrenome("Doe");
        request.setEmail("jane.doe@example.com");
        request.setSenha("password");
        request.setFuncoes(roles);

        existingUser = new User();
        existingUser.setId(1);
        existingUser.setNome("John");
        existingUser.setSobrenome("Doe");
        existingUser.setEmail("john.doe@example.com");
        existingUser.setSenha("password");
        existingUser.addRole(existingRole);


        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));
        AuthenticationResponse response = authService.register(request);
        assertFalse(response.getToken().isEmpty());
    }

    @Test
    void testAuthUser() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("jane.doe@example.com");
        authRequest.setSenha("password");

        AuthenticationResponse response = authService.auth(authRequest);
        assertFalse(response.getToken().isEmpty());
    }

}
