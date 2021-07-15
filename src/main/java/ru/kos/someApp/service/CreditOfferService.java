package ru.kos.someApp.service;

import ru.kos.someApp.entity.CreditOffer;
import ru.kos.someApp.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface CreditOfferService {

    CreditOffer add(CreditOffer creditOffer);
    void delete(CreditOffer creditOffer);
    List<CreditOffer> getAll();
    Optional<CreditOffer> getById(Integer id);
    List<Payment> getPaymentListById(Integer id);
}
