package shampoo_company;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "shampoos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "shampoo_type", discriminatorType = DiscriminatorType.STRING)
public class BasicShampoo implements Shampoo{

    @Id
    private long id;

    @Basic
    private BigDecimal price;

    @Basic
    private String brand;

    @Enumerated
    private Size size;

    @OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "label", referencedColumnName = "id")
    private BasicLabel label;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "shampoos_ingredients", joinColumns = @JoinColumn(name = "shampoo_id", referencedColumnName = "id"))
    private Set<BasicIngredient> ingredients;




    protected BasicShampoo() {
        this.setIngredients(new HashSet<BasicIngredient>());
    }


    public BasicShampoo(BigDecimal price, String brand, Size size, BasicLabel basicLabel) {
        this.price = price;
        this.brand = brand;
        this.size = size;
        this.label = basicLabel;
    }

    public Long getId() {
        return null;
    }

    public void setId(Long id) {

    }

    public String getBrand() {
        return null;
    }

    public void setBrand(String brand) {

    }

    public ProductionBatch getBatch() {
        return null;
    }

    public void setBatch(ProductionBatch productionBatch) {

    }

    public BigDecimal getPrice() {
        return null;
    }

    public void setPrice(BigDecimal price) {

    }

    public Size getSize() {
        return null;
    }

    public void setSize(Size size) {

    }

    public BasicLabel getLabel() {
        return null;
    }

    public void setLabel(BasicLabel label) {

    }

    public Set<BasicIngredient> getIngredients() {
        return null;
    }

    public void setIngredients(Set<BasicIngredient> ingredients) {

    }
}
