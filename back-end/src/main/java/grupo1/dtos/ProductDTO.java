package grupo1.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import grupo1.entities.Product;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {

    private Integer id;
    private String nome;
    private String descricao;
    private Set<FeatureDTO> caracteristicas = new HashSet<>();
    private Set<ImageDTO> imagens = new HashSet<>();
    private CategoryDTO categoria;
    private CityDTO cidade;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.nome = product.getNome();
        this.descricao = product.getDescricao();
        product.getCaracteristicas().forEach(caracteristica -> {
            FeatureDTO featureDTO = new FeatureDTO();
            featureDTO.setId(caracteristica.getId());
            featureDTO.setNome(caracteristica.getNome());
            featureDTO.setIcone(caracteristica.getIcone());
            addFeature(featureDTO);
        });
        product.getImagens().forEach(imagem -> {
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setId(imagem.getId());
            imageDTO.setUrl(imagem.getUrl());
            imageDTO.setTitulo(imagem.getTitulo());
            addImage(imageDTO);
        });
        this.categoria = new CategoryDTO(product.getCategoria());
        this.cidade = new CityDTO(product.getCidade());
    }

    public void addFeature(FeatureDTO featureDTO) {
        this.caracteristicas.add(featureDTO);
    }

    public void addImage(ImageDTO imageDTO) {
        this.imagens.add(imageDTO);
    }
}
