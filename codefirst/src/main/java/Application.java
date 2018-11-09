import shampoo_company.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Application {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence
                .createEntityManagerFactory("code_first_tables");

        EntityManager entityManager = entityManagerFactory.createEntityManager();


        entityManager.getTransaction().begin();

//        BasicIngredient am = new AmmoniumChloride();
//        BasicIngredient mint = new Mint();
//        BasicIngredient nettle = new Nettle();
//
//        BasicLabel label = new BasicLabel("Fresh Nuke Shampoo", "Contains mint and nettle");
//        BasicShampoo shampoo = new FreshNuke(label);
//        shampoo.getIngredients().add(mint);
//        shampoo.getIngredients().add(nettle);
//        shampoo.getIngredients().add(am);
//        entityManager.persist(shampoo);

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
