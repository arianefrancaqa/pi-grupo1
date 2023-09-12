package grupo1.dtos;

import grupo1.entities.Feature;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class FeatureDTO {

    private Integer id;
    private String nome;
    private String icone;

    public FeatureDTO(Feature entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.icone = entity.getIcone();
    }
}
