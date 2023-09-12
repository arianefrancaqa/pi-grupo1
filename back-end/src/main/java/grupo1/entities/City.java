package grupo1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cidades")
public class City extends AbstractEntity {
    private String nome;
    private String pais;

    @OneToMany(mappedBy = "cidade", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Product> produtos = new HashSet<>();
}
