package ru.kos.someApp.service;

import ru.kos.someApp.entity.Credit;
import ru.kos.someApp.entity.CreditOffer;
import ru.kos.someApp.entity.Payment;

import java.util.Date;
import java.util.List;

public interface PaymentService {

    Payment add(Payment payment);
    List<Payment> addListByCreditOffer(List<Payment> paymentList, CreditOffer creditOffer);
    List<Payment> getByCreditOfferId(Integer id);

    List<Payment> calculatePayments(Date startDate, double sum, Credit credit);

    double calculateResultPayment(List<Payment> paymentList, double initialFee);

    double calculateResultPercent(List<Payment> paymentList);
}
