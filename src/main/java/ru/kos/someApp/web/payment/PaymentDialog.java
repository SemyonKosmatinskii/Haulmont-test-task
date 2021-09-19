package ru.kos.someApp.web.payment;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import ru.kos.someApp.entity.CreditOffer;
import ru.kos.someApp.entity.Payment;

import java.text.SimpleDateFormat;
import java.util.List;

import static ru.kos.someApp.web.configs.AppConfig.resourceBundle;

public class PaymentDialog extends Dialog {

    public PaymentDialog(CreditOffer creditOffer) {

        setWidth("1024px");
        setHeight("768px");

        List<Payment> paymentList = creditOffer.getPaymentList();

        double resultPayment = creditOffer.getResultPayment();
        double resultPercent = creditOffer.getResultPercent();

        Grid<Payment> paymentGrid = new Grid<>();

        paymentGrid.addColumn(column -> new SimpleDateFormat("dd MMMM yyyy").format(column.getStartDate()))
                .setHeader(resourceBundle.getString("date"));
        paymentGrid.addColumn(column -> String.format("%1$,.2f", column.getPayment()))
                .setHeader(resourceBundle.getString("payment"))
                .setFooter(resourceBundle.getString("resultSum") + ": " + String.format("%1$,.2f", resultPayment));
        paymentGrid.addColumn(column -> String.format("%1$,.2f", column.getPercent()))
                .setHeader(resourceBundle.getString("percents"))
                .setFooter(resourceBundle.getString("overSum") + ": " + String.format("%1$,.2f", resultPercent));
        paymentGrid.addColumn(column -> String.format("%1$,.2f", column.getCapital()))
                .setHeader(resourceBundle.getString("capital"));
        paymentGrid.addColumn(column -> String.format("%1$,.2f", column.getResidue()))
                .setHeader(resourceBundle.getString("residue"));
        paymentGrid.setSizeFull();
        paymentGrid.setItems(paymentList);
        add(paymentGrid);
    }
}
