package ru.kos.someApp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kos.someApp.entity.Client;
import ru.kos.someApp.repository.ClientRepository;
import ru.kos.someApp.service.ClientService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository repository;

    @Override
    public Client add(Client client) {
        return repository.save(client);
    }

    @Override
    public void delete(Client client) {
        repository.delete(client);
    }

    @Override
    public List<Client> getAllByBankId(Integer id) {
        return repository.findAllByBankId(id);
    }

    @Override
    public Optional<Client> getById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Client> getAll() {
        return repository.findAll();
    }
}
