package grupo1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class User extends AbstractEntity implements UserDetails, Serializable {

    private String nome;
    private String sobrenome;

    @Column(unique = true)
    private String email;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String senha;

    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarios_funcoes", joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name="id_role"))
    private Set<Role> funcoes = new HashSet<>();

    @OneToMany(mappedBy = "customer")
    private Set<Reservation> reservas = new HashSet<>();

    public void addRole(Role role) {
        this.funcoes.add(role);
    }
    public void removeRole(Role role) {
        this.funcoes.remove(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return funcoes.stream().map(role -> new SimpleGrantedAuthority(role.getNome())).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
