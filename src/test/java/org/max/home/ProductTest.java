package org.max.home;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.persistence.Entity;
import javax.persistence.PersistenceException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
public class ProductTest extends AbstractTest {
    @Test
    void getAllValidProductsSQLlite() throws SQLException {
        //given
        String sql = "SELECT * FROM products";
        Statement stmt = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        //then
        Assertions.assertEquals(10, countTableSize);
    }
    @Test
    void getOnlySalads() {
        //given
        String sql = "SELECT * FROM products WHERE menu_name LIKE '%SALAD%'";
        //when
        final Query query = getSession().createSQLQuery(sql).addEntity(ProductsEntity.class);
        //then
        Assertions.assertEquals(2, query.list().size());
    }

    @ParameterizedTest
    @CsvSource({"PIZZA, 125.0", "RASPBERRY LEMONADE, 100.0", "CHOCOLATE CAKE, 150.0"})
    void addNewProduct(String product, String price) {
        //given
        Session session = getSession();
        String sql = "SELECT MAX(product_id) FROM products";
        final Query maxid = session.createSQLQuery(sql);
        Integer nextid = (Integer) maxid.uniqueResult() + 1;

        ProductsEntity productsEntity = new ProductsEntity();
        productsEntity.setProductId(nextid.shortValue());
        productsEntity.setMenuName(product);
        productsEntity.setPrice(price);

        session.beginTransaction();
        session.persist(productsEntity);
        session.getTransaction().commit();
        //when
        final Query allProducts = session.createQuery("FROM ProductsEntity");

        final Query newProduct = session.createSQLQuery("SELECT * FROM products WHERE product_id=" + nextid)
                .addEntity(ProductsEntity.class);
        Optional<ProductsEntity> queryEntity = newProduct.uniqueResultOptional();
        //then


        Assertions.assertTrue(queryEntity.isPresent());
        Assertions.assertEquals(product, queryEntity.get().getMenuName());
        Assertions.assertEquals(price, queryEntity.get().getPrice());
    }

}
