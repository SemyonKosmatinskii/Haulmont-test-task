package ru.kos.someApp.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kos.someApp.entity.Bank;

import java.util.List;

public interface BankRepository extends CrudRepository<Bank, Integer> {

    List<Bank> findAll();
    Bank getByName(String name);
}
