package ru.kos.someApp.web.creditOffer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kos.someApp.entity.Bank;
import ru.kos.someApp.entity.Client;
import ru.kos.someApp.entity.CreditOffer;
import ru.kos.someApp.entity.Payment;
import ru.kos.someApp.service.BankService;
import ru.kos.someApp.service.ClientService;
import ru.kos.someApp.service.CreditOfferService;
import ru.kos.someApp.service.PaymentService;
import ru.kos.someApp.web.MainScreen;
import ru.kos.someApp.web.payment.PaymentDialog;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.List;

import static ru.kos.someApp.web.configs.AppConfig.*;

@Route(value = "creditOffers", layout = MainScreen.class)
public class CreditOfferBrowser extends AppLayout {

    private final Grid<CreditOffer> grid;
    private CreditOffer creditOffer;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BankService bankService;

    @Autowired
    private CreditOfferService creditOfferService;

    @Autowired
    private PaymentService paymentService;

    public CreditOfferBrowser() {
        VerticalLayout layout = new VerticalLayout();
        grid = new Grid<>();
        RouterLink linkCreate = new RouterLink(resourceBundle.getString("createCreditOffer"),
                CreditOfferEditor.class, 0);
        layout.add(linkCreate);
        layout.add(grid);
        setContent(layout);
    }

    @PostConstruct
    private void fillGrid() {

        List<CreditOffer> creditOffers = creditOfferService.getAll();
        if (!creditOffers.isEmpty()) {
            grid.addColumn(creditOffer ->
                    creditOffer.getClient().getLastName()).setHeader(resourceBundle.getString("lastName"));
            grid.addColumn(creditOffer ->
                    creditOffer.getClient().getFirstName()).setHeader(resourceBundle.getString("firstName"));
            grid.addColumn(creditOffer ->
                    creditOffer.getCredit().getTitle()).setHeader(resourceBundle.getString("credit"));
            grid.addColumn(creditOffer ->
                    creditOffer.getCredit().getBank().getName()).setHeader(resourceBundle.getString("bank"));
            grid.addColumn(column -> new SimpleDateFormat("dd MMMM yyyy").format(column.getStartDate())).
                    setHeader(resourceBundle.getString("startDate"));
            grid.addColumn(CreditOffer::getSumValue).setHeader(resourceBundle.getString("sum"));
            grid.addColumn(CreditOffer::getInitialFee).setHeader(resourceBundle.getString("initialFee"));

            grid.addColumn(new NativeButtonRenderer<>(resourceBundle.getString("edit"), creditOffer -> {
                UI.getCurrent().navigate(CreditOfferEditor.class, creditOffer.getId());
            }));

            grid.addColumn(new NativeButtonRenderer<>(resourceBundle.getString("delete"), creditOffer -> {
                Dialog dialog = createDeleteDialog(creditOffer);
                dialog.open();
            }));

            grid.addColumn(new NativeButtonRenderer<>(resourceBundle.getString("paymentSchedule"), e -> {
                creditOffer = creditOfferService.getById(e.getId()).orElse(null);
                List<Payment> paymentList = paymentService.getByCreditOfferId(e.getId());
                creditOffer.setPaymentList(paymentList);
                assert creditOffer != null;
                PaymentDialog dialog = new PaymentDialog(creditOffer);
                dialog.open();
            }));

            grid.setItems(creditOffers);

            grid.addItemDoubleClickListener(e -> {
                UI.getCurrent().navigate(CreditOfferEditor.class, e.getItem().getId());
            });
        }
    }

    private Dialog createDeleteDialog(CreditOffer creditOffer) {
        Dialog dialog = new Dialog();
        Button confirm = new Button(resourceBundle.getString("delete"));
        Button cancel = new Button(resourceBundle.getString("cancel"));
        dialog.add(resourceBundle.getString("reallyDeleteCreditOffer"));
        dialog.add(confirm);
        dialog.add(cancel);

        confirm.addClickListener(clickEvent -> {
            deleteCreditOffer(creditOffer);
            creditOfferService.delete(creditOffer);
            dialog.close();
            Notification notification = new Notification(resourceBundle.getString("deleteCreditOffer"),
                    DURATION_OF_NOTIFICATION_SHORT);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.open();

            grid.setItems(creditOfferService.getAll());
        });

        cancel.addClickListener(clickEvent -> {
            dialog.close();
        });
        return dialog;
    }

    private void deleteCreditOffer(CreditOffer creditOffer) {
        Bank bank = creditOffer.getCredit().getBank();
        Client client = creditOffer.getClient();
        bank.removeClient(client);
        clientService.add(client);
        bankService.add(bank);
    }
}
