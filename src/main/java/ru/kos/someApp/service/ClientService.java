package ru.kos.someApp.service;

import ru.kos.someApp.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    Client add(Client client);
    void delete(Client client);
    List<Client> getAllByBankId(Integer id);
    Optional<Client> getById(Integer id);
    List<Client> getAll();
}
