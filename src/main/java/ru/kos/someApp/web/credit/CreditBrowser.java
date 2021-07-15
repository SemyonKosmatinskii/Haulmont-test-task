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

@Route(value = "credits", layout = MainScreen.class)
public class CreditBrowser extends AppLayout {

    private final Grid<Credit> grid;

    @Autowired
    CreditService creditService;

    public CreditBrowser() {
        VerticalLayout layout = new VerticalLayout();
        grid = new Grid<>();
        RouterLink linkCreate = new RouterLink("Создать кредит", CreditEditor.class, 0);
        layout.add(linkCreate);
        layout.add(grid);
        setContent(layout);
    }

    @PostConstruct
    public void fillGrid() {

        List<Credit> credits = creditService.getAll();
        if (!credits.isEmpty()) {
            grid.addColumn(Credit::getTitle).setHeader("Название");
            grid.addColumn(Credit::getInterestRate).setHeader("Процентная ставка");
            grid.addColumn(Credit::getLimitSum).setHeader("Максимальная сумма");
            grid.addColumn(Credit::getTerm).setHeader("Срок (в мес.)");
            grid.addColumn(credit -> credit.getBank().getName()).setHeader("Банк");

            grid.addColumn(new NativeButtonRenderer<>("Редактировать", credit -> {
                UI.getCurrent().navigate(CreditEditor.class, credit.getId());
            }));

            grid.addColumn(new NativeButtonRenderer<>("Удалить", credit -> {
                Dialog dialog = new Dialog();
                Button confirm = new Button("Удалить");
                Button cancel = new Button("Отмена");
                dialog.add("Вы уверены что хотите удалить кредит?");
                dialog.add(confirm);
                dialog.add(cancel);

                confirm.addClickListener(clickEvent -> {
                    try {
                        creditService.delete(credit);
                    } catch (Exception e) {
                        Notification notification = new Notification(
                                "У кредита имеются открытые кредитные предложения", 3000);
                        notification.setPosition(Notification.Position.MIDDLE);
                        notification.open();
                        dialog.close();
                        return;
                    }
                    dialog.close();
                    Notification notification = new Notification("Кредит удален", 1000);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.open();

                    grid.setItems(creditService.getAll());

                });

                cancel.addClickListener(clickEvent -> {
                    dialog.close();
                });

                dialog.open();
            }));

            grid.setItems(credits);

            grid.addItemDoubleClickListener(e -> {
                UI.getCurrent().navigate(CreditEditor.class, e.getItem().getId());
            });
        }
    }
}
