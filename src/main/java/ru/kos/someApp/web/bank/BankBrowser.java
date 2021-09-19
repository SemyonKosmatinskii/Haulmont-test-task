package ru.kos.someApp.web.bank;

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
import ru.kos.someApp.entity.Credit;
import ru.kos.someApp.service.BankService;
import ru.kos.someApp.service.ClientService;
import ru.kos.someApp.service.CreditService;
import ru.kos.someApp.web.MainScreen;

import javax.annotation.PostConstruct;
import java.util.List;

import static ru.kos.someApp.web.configs.AppConfig.*;

@Route(value = "banks", layout = MainScreen.class)
public class BankBrowser extends AppLayout {

    private final Grid<Bank> bankGrid;

    @Autowired
    private BankService bankService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private ClientService clientService;

    public BankBrowser() {
        VerticalLayout layout = new VerticalLayout();
        bankGrid = new Grid<>();

        RouterLink linkCreateBank = new RouterLink(
                resourceBundle.getString("createBank"), BankEditor.class, 0);
        layout.add(linkCreateBank, bankGrid);
        setContent(layout);
    }

    @PostConstruct
    private void fillGrid() {
        List<Bank> banks = bankService.getAll();
        if (!banks.isEmpty()) {
            bankGrid.addColumn(Bank::getName).setHeader(resourceBundle.getString("title"));

            bankGrid.addColumn(new NativeButtonRenderer<>(resourceBundle.getString("edit"), bank -> {
                UI.getCurrent().navigate(BankEditor.class, bank.getId());
            }));

            bankGrid.addColumn(new NativeButtonRenderer<>(resourceBundle.getString("delete"), bank -> {
                Dialog dialog = createDeleteDialog(bank);
                dialog.open();
            }));

            bankGrid.addColumn(new NativeButtonRenderer<>(resourceBundle.getString("clientsBank"), bank -> {
                Dialog dialog = createClientsDialog(bank);
                dialog.open();
            }));

            bankGrid.addColumn(new NativeButtonRenderer<>(resourceBundle.getString("creditsBank"), bank -> {
                Dialog dialog = createCreditsDialog(bank);
                dialog.open();
            }));

            bankGrid.setItems(banks);

            bankGrid.addItemDoubleClickListener(e -> {
                UI.getCurrent().navigate(BankEditor.class, e.getItem().getId());
            });
        }
    }

    private Dialog createDeleteDialog(Bank bank) {
        Dialog dialog = new Dialog();
        Button confirm = new Button(resourceBundle.getString("delete"));
        Button cancel = new Button(resourceBundle.getString("cancel"));
        dialog.add(resourceBundle.getString("reallyDeleteBank"));
        dialog.add(confirm);
        dialog.add(cancel);

        Dialog innerDialog = new Dialog();
        Button innerConfirm = new Button(resourceBundle.getString("delete"));
        Button innerCancel = new Button(resourceBundle.getString("cancel"));
        innerDialog.add(resourceBundle.getString("warningBankCredit"));
        innerDialog.add(innerConfirm);
        innerDialog.add(innerCancel);


        confirm.addClickListener(buttonClickEvent -> {
            dialog.close();
            innerDialog.open();
        });

        innerConfirm.addClickListener(clickEvent -> {
            createDeleteInnerDialog(bank, innerDialog);
        });

        innerCancel.addClickListener(clickEvent -> {
            dialog.close();
            innerDialog.close();
        });

        cancel.addClickListener(clickEvent -> {
            dialog.close();
        });
        return dialog;
    }

    private Dialog createCreditsDialog(Bank bank) {
        Dialog dialog = new Dialog();
        Grid<Credit> grid = new Grid<>();
        List<Credit> creditList = creditService.getAllByBank(bank);

        dialog.setWidth("800px");
        dialog.setHeight("600px");
        grid.addColumn(Credit::getTitle).setHeader(resourceBundle.getString("title"));
        grid.addColumn(Credit::getInterestRate).setHeader(resourceBundle.getString("rate"));
        grid.addColumn(Credit::getLimitSum).setHeader(resourceBundle.getString("maxSum"));
        grid.addColumn(Credit::getTerm).setHeader(resourceBundle.getString("term"));
        grid.setSizeFull();
        grid.setItems(creditList);
        dialog.add(grid);
        return dialog;
    }

    private Dialog createClientsDialog(Bank bank) {
        Dialog dialog = new Dialog();
        Grid<Client> grid = new Grid<>();
        List<Client> clientList = clientService.getAllByBankId(bank.getId());

        dialog.setWidth("1100px");
        dialog.setHeight("600px");
        grid.addColumn(Client::getLastName).setHeader(resourceBundle.getString("lastName"));
        grid.addColumn(Client::getFirstName).setHeader(resourceBundle.getString("firstName"));
        grid.addColumn(Client::getPatronymic).setHeader(resourceBundle.getString("pat"));
        grid.addColumn(Client::getPhoneNumber).setHeader(resourceBundle.getString("phone"));
        grid.addColumn(Client::getEmail).setHeader(resourceBundle.getString("email"));
        grid.addColumn(Client::getPassportData).setHeader(resourceBundle.getString("passport"));
        grid.setSizeFull();
        grid.setItems(clientList);
        dialog.add(grid);
        return dialog;
    }

    private void createDeleteInnerDialog(Bank bank, Dialog innerDialog) {
        try {
            bankService.delete(bank);
        } catch (Exception e) {
            Notification notification = new Notification(
                    resourceBundle.getString("warningCreditOffer"),
                    DURATION_OF_NOTIFICATION_LONG);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.open();
            innerDialog.close();
            return;
        }
        innerDialog.close();
        Notification notification = new Notification(resourceBundle.getString("deleteBank"),
                DURATION_OF_NOTIFICATION_SHORT);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();

        bankGrid.setItems(bankService.getAll());
    }
}
