package grupo1.configs.auth;

import grupo1.configs.jwt.JwtService;
import grupo1.entities.Role;
import grupo1.entities.User;
import grupo1.repositories.IRoleRepository;
import grupo1.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    final static Logger log = Logger.getLogger(AuthService.class);
   public AuthenticationResponse register(RegisterRequest request) {
       Set<Role> roles = new HashSet<>();
       request.getFuncoes().forEach((r) -> {
                   Optional<Role> roleDB = roleRepository.findRoleByNome(r);
                   Role role = new Role();
                   if (roleDB.isEmpty()) {
                       log.info("No role found. Saving role...");
                       role.setNome(r);
                       var newRole = roleRepository.save(role);
                       roles.add(newRole);
                   } else {
                       log.info("Role " + r + " found...");
                       roles.add(roleDB.get());
                   }
               }
       );
       var user = User.builder()
               .nome(request.getNome())
               .sobrenome(request.getSobrenome())
               .email(request.getEmail())
               .senha(passwordEncoder.encode(request.getSenha()))
               .funcoes(roles)
               .build();
       Optional<User> userDB = userRepository.findByEmail(request.getEmail());
       if(userDB.isEmpty()) {
           log.info("Usuário não encontrado. Salvando...");
           userRepository.save(user);
       } else {
           log.warn("Usuário já existente. E-mail: " + request.getEmail());
           throw new DataIntegrityViolationException("User already exists. E-mail: " + request.getEmail());
       }
       var jwtTOken = jwtService.generateToken(user);
       return AuthenticationResponse.builder().token(jwtTOken).build();
    }

    public AuthenticationResponse auth(AuthRequest request) {
        var user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );
        var jwtTOken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtTOken)
                .build();
    }
}
