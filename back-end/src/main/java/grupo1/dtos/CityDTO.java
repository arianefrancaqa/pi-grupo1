package grupo1.dtos;

import grupo1.entities.City;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CityDTO {

    public Integer id;
    public String nome;
    public String pais;

    public CityDTO(City city) {
        this.id = city.getId();
        this.nome = city.getNome();
        this.pais = city.getPais();
    }
}
