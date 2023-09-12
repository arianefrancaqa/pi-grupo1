package grupo1.dtos;


import grupo1.entities.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserDTO {

    private Integer id;
    private String nome;
    private String sobrenome;
    private String email;
    @Setter(AccessLevel.NONE)
    @OneToMany(fetch = FetchType.EAGER)
    private Set<String> funcoes = new HashSet<>();

    public UserDTO(User user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.sobrenome = user.getSobrenome();
        this.email = user.getEmail();
        user.getFuncoes().forEach(role -> this.addRole(role.getNome()));
    }

    public void addRole(String role) {
        this.funcoes.add(role);
    }

    public void removeRole(String role) {
        this.funcoes.remove(role);
    }
}
