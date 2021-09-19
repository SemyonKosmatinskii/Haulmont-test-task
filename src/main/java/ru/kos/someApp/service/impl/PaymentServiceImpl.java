package ru.kos.someApp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kos.someApp.entity.Credit;
import ru.kos.someApp.entity.CreditOffer;
import ru.kos.someApp.entity.Payment;
import ru.kos.someApp.repository.PaymentRepository;
import ru.kos.someApp.service.PaymentService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Override
    public Payment add(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> addListByCreditOffer(List<Payment> paymentList, CreditOffer creditOffer) {
        for (Payment p : paymentList) {
            p.setCreditOffer(creditOffer);
            paymentRepository.save(p);
        }
        return paymentList;
    }

    @Override
    public List<Payment> getByCreditOfferId(Integer id) {
        return paymentRepository.findByCreditOfferId(id);
    }

    @Override
    public List<Payment> calculatePayments(Date startDate, double sum, Credit credit) {

        List<Payment> result = new ArrayList<>();

        double annualInterestRate = credit.getInterestRate();
        double monthlyInterestRate = annualInterestRate / (12 * 100);
        int period = credit.getTerm();

        double paymentValue = (((monthlyInterestRate * (Math.pow((1 + monthlyInterestRate), period)))
                / (Math.pow((1 + monthlyInterestRate), period) - 1)) * sum);
        double residue = (sum - (paymentValue - (sum * monthlyInterestRate)));
        double percents = (sum * monthlyInterestRate);
        double capital = paymentValue - percents;

        for (int i = 1; i <= period; i++) {

            Payment payment = new Payment();
            payment.setStartDate(startDate);
            payment.setPayment(paymentValue);
            payment.setPercent(percents);
            payment.setCapital(capital);
            payment.setResidue(residue);
            result.add(payment);

            LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            localStartDate = localStartDate.plusMonths(1);
            startDate = Date.from(localStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            percents = (residue * (monthlyInterestRate));
            capital = paymentValue - percents;
            residue = residue - capital;
        }

        return result;
    }

    @Override
    public double calculateResultPayment(List<Payment> paymentList, double initialFee) {
        return paymentList.stream().mapToDouble(Payment::getPayment).reduce(initialFee, Double::sum);
    }

    @Override
    public double calculateResultPercent(List<Payment> paymentList) {
        return paymentList.stream().mapToDouble(Payment::getPayment).sum();
    }


}
