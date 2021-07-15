package ru.kos.someApp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.kos.someApp.entity.Payment;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Integer> {

    @Query("SELECT p FROM Payment p JOIN p.creditOffer c WHERE c.id = :id")
    List<Payment> findByCreditOfferId(@Param("id") Integer id);
}
