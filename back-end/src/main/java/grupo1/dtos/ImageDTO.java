package grupo1.dtos;

import grupo1.entities.Image;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ImageDTO {
    public Integer id;
    private String titulo;
    private String url;

    public ImageDTO(Image image) {
        this.id = image.getId();
        this.titulo = image.getTitulo();
        this.url = image.getUrl();
    }
}
