package ru.kos.someApp.web.credit;

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
import ru.kos.someApp.entity.Credit;
import ru.kos.someApp.service.CreditService;
import ru.kos.someApp.web.MainScreen;

import javax.annotation.PostConstruct;
import java.util.List;

import static ru.kos.someApp.web.configs.AppConfig.*;

@Route(value = "credits", layout = MainScreen.class)
public class CreditBrowser extends AppLayout {

    private final Grid<Credit> grid;

    @Autowired
    private CreditService creditService;

    public CreditBrowser() {
        VerticalLayout layout = new VerticalLayout();
        grid = new Grid<>();
        RouterLink linkCreate = new RouterLink(resourceBundle.getString("createCredit"),
                CreditEditor.class, 0);
        layout.add(linkCreate);
        layout.add(grid);
        setContent(layout);
    }

    @PostConstruct
    private void fillGrid() {

        List<Credit> credits = creditService.getAll();
        if (!credits.isEmpty()) {
            grid.addColumn(Credit::getTitle).setHeader(resourceBundle.getString("title"));
            grid.addColumn(Credit::getInterestRate).setHeader(resourceBundle.getString("rate"));
            grid.addColumn(Credit::getLimitSum).setHeader(resourceBundle.getString("maxSum"));
            grid.addColumn(Credit::getTerm).setHeader(resourceBundle.getString("term"));
            grid.addColumn(credit -> credit.getBank().getName()).setHeader(resourceBundle.getString("bank"));

            grid.addColumn(new NativeButtonRenderer<>(resourceBundle.getString("edit"), credit -> {
                UI.getCurrent().navigate(CreditEditor.class, credit.getId());
            }));

            grid.addColumn(new NativeButtonRenderer<>(resourceBundle.getString("delete"), credit -> {
                Dialog dialog = createDeleteDialog(credit);
                dialog.open();
            }));

            grid.setItems(credits);

            grid.addItemDoubleClickListener(e -> {
                UI.getCurrent().navigate(CreditEditor.class, e.getItem().getId());
            });
        }
    }

    private Dialog createDeleteDialog(Credit credit) {
        Dialog dialog = new Dialog();
        Button confirm = new Button(resourceBundle.getString("delete"));
        Button cancel = new Button(resourceBundle.getString("cancel"));
        dialog.add(resourceBundle.getString("reallyDeleteCredit"));
        dialog.add(confirm);
        dialog.add(cancel);

        confirm.addClickListener(clickEvent -> {
            try {
                creditService.delete(credit);
            } catch (Exception e) {
                Notification notification = new Notification(
                        resourceBundle.getString("haveCreditOffer"), DURATION_OF_NOTIFICATION_LONG);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.open();
                dialog.close();
                return;
            }
            dialog.close();
            Notification notification = new Notification(resourceBundle.getString("deleteCredit"),
                    DURATION_OF_NOTIFICATION_SHORT);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.open();

            grid.setItems(creditService.getAll());

        });

        cancel.addClickListener(clickEvent -> {
            dialog.close();
        });
        return dialog;
    }
}
