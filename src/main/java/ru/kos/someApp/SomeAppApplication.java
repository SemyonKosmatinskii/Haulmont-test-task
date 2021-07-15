package ru.kos.someApp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.kos.someApp.entity.Bank;
import ru.kos.someApp.entity.Client;
import ru.kos.someApp.entity.Credit;
import ru.kos.someApp.repository.BankRepository;
import ru.kos.someApp.repository.ClientRepository;
import ru.kos.someApp.repository.CreditRepository;

@SpringBootApplication
public class SomeAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SomeAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(ClientRepository clientRepository,
									  CreditRepository creditRepository,
									  BankRepository bankRepository) {
		return (args) -> {
			Client semyon = new Client();
			semyon.setFirstName("Semyon");
			semyon.setLastName("Kosmatinskii");
			semyon.setPatronymic("Sergeevich");
			semyon.setEmail("kos@domain.com");
			semyon.setPhoneNumber("89990001122");
			semyon.setPassportData("3622563801");
			clientRepository.save(semyon);

			Client andrej = new Client();
			andrej.setFirstName("Andrej");
			andrej.setLastName("Vorobjev");
			andrej.setEmail("vorob@domain.com");
			andrej.setPhoneNumber("89990001123");
			andrej.setPassportData("3699007333");
			clientRepository.save(andrej);

			Client oleg = new Client();
			oleg.setFirstName("Oleg");
			oleg.setLastName("Petrov");
			oleg.setPatronymic("Artemievich");
			oleg.setPassportData("3600777001");
			clientRepository.save(oleg);

			Bank alphaBank = new Bank();
			alphaBank.setName("Alpha");
			bankRepository.save(alphaBank);

			Credit littleCredit = new Credit();
			littleCredit.setTitle("Little");
			littleCredit.setInterestRate(12d);
			littleCredit.setLimitSum(200000d);
			littleCredit.setTerm(12);
			littleCredit.setBank(alphaBank);
			creditRepository.save(littleCredit);

			Credit mediumCredit = new Credit();
			mediumCredit.setTitle("Medium");
			mediumCredit.setInterestRate(9.8d);
			mediumCredit.setLimitSum(600000d);
			mediumCredit.setTerm(24);
			mediumCredit.setBank(alphaBank);
			creditRepository.save(mediumCredit);

			Credit bigCredit = new Credit();
			bigCredit.setTitle("Big");
			bigCredit.setInterestRate(8.6d);
			bigCredit.setLimitSum(1200000d);
			bigCredit.setTerm(36);
			bigCredit.setBank(alphaBank);
			creditRepository.save(bigCredit);
		};
	}
}
