package ru.kos.someApp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.kos.someApp.entity.Client;

import java.util.List;

public interface ClientRepository extends CrudRepository<Client, Integer> {

    List<Client> findAll();

    @Query("SELECT DISTINCT c FROM Client c JOIN c.banks b WHERE b.id = :id")
    List<Client> findAllByBankId(@Param("id") Integer id);
}
