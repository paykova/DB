package shampoo_company;

import java.math.BigDecimal;

public class Lavender extends BasicIngredient{

    private static final String NAME = "Lavender";
    private static final BigDecimal PRICE = new BigDecimal("2");

    public Lavender() {
        super(NAME, PRICE);
    }
}
