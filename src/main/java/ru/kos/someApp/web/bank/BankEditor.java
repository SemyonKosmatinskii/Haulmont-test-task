package ru.kos.someApp.web.bank;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kos.someApp.entity.Bank;
import ru.kos.someApp.service.BankService;

import static ru.kos.someApp.web.configs.AppConfig.DURATION_OF_NOTIFICATION_SHORT;

@Route("bank")
public class BankEditor extends AppLayout implements HasUrlParameter<Integer> {

    private final TextField nameField;
    private final FormLayout bankForm;
    private final Button saveBtn;
    private final Button cancelBtn;
    private final Binder<Bank> binder;
    private Bank bank;



    @Autowired
    private BankService bankService;

    public BankEditor() {

        binder = new Binder<>(Bank.class);

        bankForm = new FormLayout();
        nameField = new TextField("Название банка");

        saveBtn = new Button("Сохранить");
        cancelBtn = new Button("Отменить");

        bankForm.add(nameField, new Div(saveBtn, cancelBtn));

        setContent(bankForm);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer bankId) {
        bank = bankService.getById(bankId).orElse(null);
        if (bank != null) {
            addToNavbar(new H3("Редактирование банка"));
        } else {
            bank = new Bank();
            addToNavbar(new H3("Создание банка"));
        }
        fillForm();
        configureBinding();
        configureListeners();
    }

    private void fillForm() {
        if (bank.getId() != null) {
            nameField.setValue(bank.getName());
        }
    }

    private void configureBinding() {

        binder.forField(nameField)
                .asRequired("Это обязательное поле")
                .bind(Bank::getName, Bank::setName);
        binder.setBean(bank);
    }

    private void configureListeners() {

        saveBtn.addClickListener(clickEvent -> {
            if (binder.validate().isOk()) {
                try {
                    boolean isNew = bank.getId() == null;
                    bank.setName(nameField.getValue());
                    bankService.add(bank);

                    Notification notification = new Notification(
                            isNew ? "Банк успешно создан" : "Банк был изменен", DURATION_OF_NOTIFICATION_SHORT
                    );
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.addDetachListener(detachEvent -> {
                        UI.getCurrent().navigate(BankBrowser.class);
                    });
                    bankForm.setEnabled(false);
                    notification.open();
                } catch (Exception e) {
                    Dialog dialog = new Dialog();
                    Button okBtn = new Button("ОК");
                    dialog.add(
                            "Проверьте правильность введенных данных, возможно, банк с таким названием уже существует"
                    );
                    dialog.add(okBtn);
                    okBtn.addClickListener(buttonClickEvent -> dialog.close());
                    dialog.open();
                }
            }
        });

        cancelBtn.addClickListener(e -> {
            UI.getCurrent().navigate(BankBrowser.class);
        });
    }
}
