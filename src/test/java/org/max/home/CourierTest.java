package org.max.home;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.persistence.PersistenceException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;


public class CourierTest extends AbstractTest{

    //у Вас тесты зависимые, поэтому лучше задать порядок через @Order()
    @Test
    void getCouriers_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM courier_info WHERE delivery_type='car'";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery("SELECT * FROM courier_info").addEntity(CourierInfoEntity.class);
        //then
        Assertions.assertEquals(3, countTableSize);
        Assertions.assertEquals(4, query.list().size());
    }


    @ParameterizedTest
    @CsvSource({"John, Rython", "Kate, Looran", "Bob, Kolaris"})
    void getCourierById_whenValid_shouldReturn(String name, String lastName) throws SQLException {
        //given
        String sql = "SELECT * FROM courier_info WHERE first_name='" + name + "'";
        Statement stmt  = getConnection().createStatement();
        String nameString = "";
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            nameString = rs.getString(3);
        }
        //then
        Assertions.assertEquals(lastName, nameString);
    }

    @Test
    void addCourier_whenValid_shouldSave() {
        //given
        CourierInfoEntity entity = new CourierInfoEntity();
        entity.setFirstName("Michael");
        entity.setLastName("Peterson");
        entity.setPhoneNumber("+7 913 800 5078");
        entity.setDeliveryType("car");
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id="+5).addEntity(CourierInfoEntity.class);
        //у Вас переменная entity уже используется на строке 55.
        CourierInfoEntity entity = (CourierInfoEntity) query.uniqueResult();
        //then
        Assertions.assertNotNull(entity);
        Assertions.assertEquals("car", entity.getDeliveryType());
    }

    @Test
    void deleteCourier_whenValid_shouldDelete() {
        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + 5).addEntity(CourierInfoEntity.class);
        Optional<CourierInfoEntity> courierInfoEntity = (Optional<CourierInfoEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(courierInfoEntity.isPresent());
        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(courierInfoEntity.get());
        session.getTransaction().commit();
        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM courier_info WHERE courier_id=" + 5).addEntity(CourierInfoEntity.class);
        Optional<CourierInfoEntity> courierInfoEntityAfterDelete = (Optional<CourierInfoEntity>) queryAfterDelete.uniqueResultOptional();
        Assertions.assertFalse(courierInfoEntityAfterDelete.isPresent());
    }




}
