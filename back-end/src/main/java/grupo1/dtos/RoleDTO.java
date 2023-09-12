package grupo1.dtos;

import grupo1.entities.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class RoleDTO {

    public Integer id;
    public String nome;

    public RoleDTO(Role role) {
        this.id = role.getId();
        this.nome = role.getNome();
    }
}
