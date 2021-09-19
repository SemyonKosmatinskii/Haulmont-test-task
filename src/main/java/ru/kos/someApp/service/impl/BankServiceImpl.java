package ru.kos.someApp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kos.someApp.entity.Bank;
import ru.kos.someApp.repository.BankRepository;
import ru.kos.someApp.service.BankService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BankServiceImpl implements BankService {

    @Autowired
    private BankRepository repository;

    @Override
    public Bank add(Bank bank) {
        return repository.save(bank);
    }

    @Override
    public void delete(Bank bank) {
        repository.delete(bank);
    }

    @Override
    public List<Bank> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Bank> getById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Bank getByName(String name) {
        return repository.getByName(name);
    }

}
