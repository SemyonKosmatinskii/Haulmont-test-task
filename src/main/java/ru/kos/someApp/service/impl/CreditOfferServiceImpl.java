package ru.kos.someApp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kos.someApp.entity.CreditOffer;
import ru.kos.someApp.entity.Payment;
import ru.kos.someApp.repository.CreditOfferRepository;
import ru.kos.someApp.service.CreditOfferService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CreditOfferServiceImpl implements CreditOfferService {

    @Autowired
    CreditOfferRepository repository;

    @Override
    public CreditOffer add(CreditOffer creditOffer) {
        return repository.save(creditOffer);
    }

    @Override
    public void delete(CreditOffer creditOffer) {
       repository.delete(creditOffer);
    }

    @Override
    public List<CreditOffer> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<CreditOffer> getById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Payment> getPaymentListById(Integer id) {
        return repository.findPaymentListById(id);
    }

}
