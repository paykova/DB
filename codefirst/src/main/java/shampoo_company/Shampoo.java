package shampoo_company;

import java.math.BigDecimal;
import java.util.Set;

public interface Shampoo {

    Long getId();
    void setId(Long id);

    String getBrand();
    void setBrand(String brand);

    ProductionBatch getBatch();
    void setBatch(ProductionBatch productionBatch);

    BigDecimal getPrice();
    void setPrice(BigDecimal price);

    Size getSize();
    void setSize(Size size);

    BasicLabel getLabel();
    void setLabel(BasicLabel label);

    Set<BasicIngredient> getIngredients();
    void setIngredients(Set<BasicIngredient> ingredients);
}
