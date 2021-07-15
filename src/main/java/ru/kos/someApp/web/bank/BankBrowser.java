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

@Route(value = "banks", layout = MainScreen.class)
public class BankBrowser extends AppLayout {

    private final Grid<Bank> bankGrid;

    @Autowired
    BankService bankService;

    @Autowired
    CreditService creditService;

    @Autowired
    ClientService clientService;

    public BankBrowser() {
        VerticalLayout layout = new VerticalLayout();
        bankGrid = new Grid<>();

        RouterLink linkCreateBank = new RouterLink("Создать банк", BankEditor.class, 0);
        layout.add(linkCreateBank, bankGrid);
        setContent(layout);
    }

    @PostConstruct
    public void fillGrid() {
        List<Bank> banks = bankService.getAll();
        if (!banks.isEmpty()) {
            bankGrid.addColumn(Bank::getName).setHeader("Название");

            bankGrid.addColumn(new NativeButtonRenderer<>("Редактировать", bank -> {
                UI.getCurrent().navigate(BankEditor.class, bank.getId());
            }));

            bankGrid.addColumn(new NativeButtonRenderer<>("Удалить", bank -> {
                Dialog dialog = new Dialog();
                Button confirm = new Button("Удалить");
                Button cancel = new Button("Отмена");
                dialog.add("Вы уверены что хотите удалить банк?");
                dialog.add(confirm);
                dialog.add(cancel);

                Dialog innerDialog = new Dialog();
                Button innerConfirm = new Button("Удалить");
                Button innerCancel = new Button("Отмена");
                innerDialog.add("Вместе с банком будут удалены все его кредиты");
                innerDialog.add(innerConfirm);
                innerDialog.add(innerCancel);


                confirm.addClickListener(buttonClickEvent -> {
                    dialog.close();
                    innerDialog.open();
                });
                innerConfirm.addClickListener(clickEvent -> {
                    try {
                        bankService.delete(bank);
                    } catch (Exception e) {
                        Notification notification = new Notification(
                                "Кредиты этого банка фигурируют в существующих кредитных предложениях", 3000);
                        notification.setPosition(Notification.Position.MIDDLE);
                        notification.open();
                        innerDialog.close();
                        return;
                    }
                    innerDialog.close();
                    Notification notification = new Notification("Банк удален", 1000);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.open();

                    bankGrid.setItems(bankService.getAll());

                });

                innerCancel.addClickListener(clickEvent -> {
                    dialog.close();
                    innerDialog.close();
                });

                cancel.addClickListener(clickEvent -> {
                    dialog.close();
                });

                dialog.open();

            }));

            bankGrid.addColumn(new NativeButtonRenderer<>("Клиенты банка", bank -> {
                Dialog dialog = new Dialog();
                Grid<Client> grid = new Grid<>();
                List<Client> clientList = clientService.getAllByBankId(bank.getId());

                dialog.setWidth("1100px");
                dialog.setHeight("600px");
                grid.addColumn(Client::getLastName).setHeader("Фамилия");
                grid.addColumn(Client::getFirstName).setHeader("Имя");
                grid.addColumn(Client::getPatronymic).setHeader("Отчество");
                grid.addColumn(Client::getPhoneNumber).setHeader("Номер телефона");
                grid.addColumn(Client::getEmail).setHeader("email");
                grid.addColumn(Client::getPassportData).setHeader("№ паспорта");
                grid.setSizeFull();
                grid.setItems(clientList);
                dialog.add(grid);
                dialog.open();
            }));

            bankGrid.addColumn(new NativeButtonRenderer<>("Кредиты банка", bank -> {

                Dialog dialog = new Dialog();
                Grid<Credit> grid = new Grid<>();
                List<Credit> creditList = creditService.getAllByBank(bank);

                dialog.setWidth("800px");
                dialog.setHeight("600px");
                Grid.Column<Credit> columnTitle = grid.addColumn(Credit::getTitle).setHeader("Название");
                grid.addColumn(Credit::getInterestRate).setHeader("Процентная ставка");
                grid.addColumn(Credit::getLimitSum).setHeader("Максимальная сумма");
                grid.addColumn(Credit::getTerm).setHeader("Срок (в мес.)");
                grid.setSizeFull();
                grid.setItems(creditList);
                dialog.add(grid);
                dialog.open();
            }));

            bankGrid.setItems(banks);

            bankGrid.addItemDoubleClickListener(e -> {
                UI.getCurrent().navigate(BankEditor.class, e.getItem().getId());
            });
        }
    }
}
