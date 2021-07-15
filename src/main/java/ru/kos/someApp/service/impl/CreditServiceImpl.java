package ru.kos.someApp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kos.someApp.entity.Bank;
import ru.kos.someApp.entity.Credit;
import ru.kos.someApp.repository.CreditRepository;
import ru.kos.someApp.service.CreditService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CreditServiceImpl implements CreditService {

    @Autowired
    CreditRepository repository;

    @Override
    public Credit add(Credit credit) {
        return repository.save(credit);
    }

    @Override
    public void delete(Credit credit) {
        repository.delete(credit);
    }

    @Override
    public Optional<Credit> getById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Credit> getAllByBank(Bank bank) {
        return repository.findAllByBank(bank);
    }

    @Override
    public List<Credit> getAll() {
        return repository.findAll();
    }
}
