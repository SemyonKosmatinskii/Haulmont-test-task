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

import static ru.kos.someApp.web.configs.AppConfig.*;

@Route(value = "clients", layout = MainScreen.class)
public class ClientBrowser extends AppLayout {

    private final Grid<Client> grid;

    @Autowired
    private ClientService clientService;

    public ClientBrowser() {
        VerticalLayout layout = new VerticalLayout();
        grid = new Grid<>();

        RouterLink linkCreate = new RouterLink(
                resourceBundle.getString("createClient"), ClientEditor.class, 0);
        layout.add(linkCreate);
        layout.add(grid);
        setContent(layout);
    }

    @PostConstruct
    private void fillGrid() {
        List<Client> clients = clientService.getAll();
        if (!clients.isEmpty()) {
            grid.addColumn(Client::getLastName).setHeader(resourceBundle.getString("family"));
            grid.addColumn(Client::getFirstName).setHeader(resourceBundle.getString("name"));
            grid.addColumn(Client::getPatronymic).setHeader(resourceBundle.getString("pat"));
            grid.addColumn(Client::getPhoneNumber).setHeader(resourceBundle.getString("phone"));
            grid.addColumn(Client::getEmail).setHeader(resourceBundle.getString("email"));
            grid.addColumn(Client::getPassportData).setHeader(resourceBundle.getString("passport"));

            grid.addColumn(new NativeButtonRenderer<>(resourceBundle.getString("edit"), client -> {
                UI.getCurrent().navigate(ClientEditor.class, client.getId());
            }));

            grid.addColumn(new NativeButtonRenderer<>(resourceBundle.getString("delete"), client -> {
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
        Button confirm = new Button(resourceBundle.getString("delete"));
        Button cancel = new Button(resourceBundle.getString("cancel"));
        dialog.add(resourceBundle.getString("reallyDelete"));
        dialog.add(confirm);
        dialog.add(cancel);

        confirm.addClickListener(clickEvent -> {
            try {
               clientService.delete(client);
            } catch (Exception e) {
                Notification notification = new Notification(
                        resourceBundle.getString("haveCreditOffer"), DURATION_OF_NOTIFICATION_LONG);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.open();
                dialog.close();
                return;
            }
            dialog.close();
            Notification notification = new Notification(resourceBundle.getString("deleteClientOk"),
                    DURATION_OF_NOTIFICATION_SHORT);
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
