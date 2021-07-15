package ru.kos.someApp.service;

import ru.kos.someApp.entity.Bank;
import ru.kos.someApp.entity.Credit;

import java.util.List;
import java.util.Optional;

public interface CreditService {

    Credit add(Credit credit);
    void delete(Credit credit);
    Optional<Credit> getById(Integer id);
    List<Credit> getAllByBank(Bank bank);
    List<Credit> getAll();
}
