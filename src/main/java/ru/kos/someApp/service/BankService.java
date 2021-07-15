package ru.kos.someApp.service;

import ru.kos.someApp.entity.Bank;

import java.util.List;
import java.util.Optional;

public interface BankService {

    Bank add(Bank bank);
    void delete(Bank bank);
    List<Bank> getAll();
    Optional<Bank> getById(Integer id);
    Bank getByName(String name);
}
