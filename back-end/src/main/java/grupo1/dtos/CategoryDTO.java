package grupo1.dtos;

import grupo1.entities.Category;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CategoryDTO {

    private Integer id;
    private String qualification;
    private String description;
    private String urlImg;

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.qualification = category.getQualification();
        this.description = category.getDescription();
        this.urlImg = category.getUrlImg();
    }
}
