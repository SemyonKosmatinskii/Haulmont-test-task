package ru.kos.someApp.web.payment;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import ru.kos.someApp.entity.CreditOffer;
import ru.kos.someApp.entity.Payment;

import java.text.SimpleDateFormat;
import java.util.List;

public class PaymentDialog extends Dialog {

    public PaymentDialog(CreditOffer creditOffer) {

        setWidth("1024px");
        setHeight("768px");

        List<Payment> paymentList = creditOffer.getPaymentList();


        double resultPayment = creditOffer.getResultPayment();
        double resultPercent = creditOffer.getResultPercent();

        Grid<Payment> paymentGrid = new Grid<>();

        paymentGrid.addColumn(column ->
                new SimpleDateFormat("dd MMMM yyyy").format(column.getStartDate())).setHeader("Дата");
        paymentGrid.addColumn(column -> String.format("%1$,.2f", column.getPayment()))
                .setHeader("Платеж")
                .setFooter("Всего: " + String.format("%1$,.2f", resultPayment));
        paymentGrid.addColumn(column -> String.format("%1$,.2f", column.getPercent()))
                .setHeader("Гашение процентов")
                .setFooter("Переплата: " + String.format("%1$,.2f", resultPercent));
        paymentGrid.addColumn(column -> String.format("%1$,.2f", column.getCapital()))
                .setHeader("Гашение тела");
        paymentGrid.addColumn(column -> String.format("%1$,.2f", column.getResidue()))
                .setHeader("Остаток");
        paymentGrid.setSizeFull();
        paymentGrid.setItems(paymentList);
        add(paymentGrid);
    }
}
