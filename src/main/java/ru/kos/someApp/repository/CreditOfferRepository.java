package ru.kos.someApp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.kos.someApp.entity.Client;
import ru.kos.someApp.entity.CreditOffer;
import ru.kos.someApp.entity.Payment;

import java.util.List;

public interface CreditOfferRepository extends CrudRepository<CreditOffer, Integer> {

    List<CreditOffer> findAll();

    Client findClientById(Integer id);

    @Query("SELECT p FROM Payment p JOIN FETCH p.creditOffer c WHERE c.id = :id")
    List<Payment> findPaymentListById(@Param("id") Integer id);
}
