package ru.kos.someApp.repository;


import org.springframework.data.repository.CrudRepository;
import ru.kos.someApp.entity.Bank;
import ru.kos.someApp.entity.Credit;

import java.util.List;

public interface CreditRepository extends CrudRepository<Credit, Integer> {

    List<Credit> findAll();

    List<Credit> findAllByBank(Bank bank);
}
