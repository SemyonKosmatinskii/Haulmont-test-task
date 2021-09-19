package ru.kos.someApp.web.client;

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
import ru.kos.someApp.entity.Client;
import ru.kos.someApp.service.ClientService;
import ru.kos.someApp.web.MainScreen;

import javax.annotation.PostConstruct;
import java.util.List;

@Route(value = "clients", layout = MainScreen.class)
public class ClientBrowser extends AppLayout {

    private static final int DURATION_OF_NOTIFICATION_LONG = 3000;
    private static final int DURATION_OF_NOTIFICATION_SHORT = 1000;
    private final Grid<Client> grid;

    @Autowired
    private ClientService clientService;

    public ClientBrowser() {
        VerticalLayout layout = new VerticalLayout();
        grid = new Grid<>();
        RouterLink linkCreate = new RouterLink("Создать клиента", ClientEditor.class, 0);
        layout.add(linkCreate);
        layout.add(grid);
        setContent(layout);
    }

    @PostConstruct
    private void fillGrid() {
        List<Client> clients = clientService.getAll();
        if (!clients.isEmpty()) {
            grid.addColumn(Client::getLastName).setHeader("Фамилия");
            grid.addColumn(Client::getFirstName).setHeader("Имя");
            grid.addColumn(Client::getPatronymic).setHeader("Отчество");
            grid.addColumn(Client::getPhoneNumber).setHeader("Номер телефона");
            grid.addColumn(Client::getEmail).setHeader("email");
            grid.addColumn(Client::getPassportData).setHeader("№ паспорта");

            grid.addColumn(new NativeButtonRenderer<>("Редактировать", client -> {
                UI.getCurrent().navigate(ClientEditor.class, client.getId());
            }));

            grid.addColumn(new NativeButtonRenderer<>("Удалить", client -> {
                Dialog dialog = createDeleteDialog(client);
                dialog.open();
            }));

            grid.setItems(clients);

            grid.addItemDoubleClickListener(e -> {
                UI.getCurrent().navigate(ClientEditor.class, e.getItem().getId());
            });
        }
    }

    private Dialog createDeleteDialog(Client client) {
        Dialog dialog = new Dialog();
        Button confirm = new Button("Удалить");
        Button cancel = new Button("Отмена");
        dialog.add("Вы уверены что хотите удалить клиента?");
        dialog.add(confirm);
        dialog.add(cancel);

        confirm.addClickListener(clickEvent -> {
            try {
               clientService.delete(client);
            } catch (Exception e) {
                Notification notification = new Notification(
                        "У клиента имеются открытые кредитные предложения", DURATION_OF_NOTIFICATION_LONG);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.open();
                dialog.close();
                return;
            }
            dialog.close();
            Notification notification = new Notification("Клиент удален", DURATION_OF_NOTIFICATION_SHORT);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.open();

            grid.setItems(clientService.getAll());

        });

        cancel.addClickListener(clickEvent -> {
            dialog.close();
        });
        return dialog;
    }
}
