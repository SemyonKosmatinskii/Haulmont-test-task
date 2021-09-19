package ru.kos.someApp.web.client;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kos.someApp.entity.Client;
import ru.kos.someApp.service.ClientService;

@Route("client")
public class ClientEditor extends AppLayout implements HasUrlParameter<Integer> {

    private static final int DURATION_OF_NOTIFICATION_SHORT = 1000;
    private final FormLayout clientForm;
    private final TextField firstNameField;
    private final TextField lastNameField;
    private final TextField patronymicField;
    private final TextField numberPhoneField;
    private final EmailField emailField;
    private final TextField passportDataField;
    private final Button saveBtn;
    private final Button cancelBtn;
    private final Binder<Client> binder;
    private Client client;

    @Autowired
    private ClientService clientService;

    public ClientEditor() {

        binder = new Binder<>(Client.class);

        clientForm = new FormLayout();

        firstNameField = new TextField("Имя");
        lastNameField = new TextField("Фамилия");
        patronymicField = new TextField("Отчество");
        numberPhoneField = new TextField("Номер телефона");
        emailField = new EmailField("Электронная почта");
        passportDataField = new TextField("Номер паспорта");
        passportDataField.setHelperText("Должен состоять из 10 символов");

        saveBtn = new Button("Сохранить");
        cancelBtn = new Button("Отменить");

        clientForm.add(firstNameField, lastNameField, patronymicField, numberPhoneField,
                emailField, passportDataField, new Div(saveBtn, cancelBtn));
        setContent(clientForm);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer clientId) {
        client = clientService.getById(clientId).orElse(null);
        if (client != null) {
            addToNavbar(new H3("Редактирование клиента"));
        } else {
            client = new Client();
            addToNavbar(new H3("Создание клиента"));
        }
        fillForm();
        configureBinding();
        configureListeners();
    }

    public void fillForm() {
        if (client.getId() != null) {
            firstNameField.setValue(client.getFirstName());
            lastNameField.setValue(client.getLastName());
            if (client.getPatronymic() != null) patronymicField.setValue(client.getPatronymic());
            if (client.getPhoneNumber() != null) numberPhoneField.setValue(client.getPhoneNumber());
            if (client.getEmail() != null) emailField.setValue(client.getEmail());
            passportDataField.setValue(client.getPassportData());
        }
    }

    private void configureBinding() {
        binder.forField(firstNameField)
                .asRequired("Это обязательное поле")
                .bind(Client::getFirstName, Client::setFirstName);
        binder.forField(lastNameField)
                .asRequired("Это обязательное поле")
                .bind(Client::getLastName, Client::setLastName);
//        binder.forField(numberPhoneField)
//                .withValidator()
        binder.forField(passportDataField)
                .asRequired("Это обязательное поле")
                .bind(Client::getPassportData, Client::setPassportData);
        binder.setBean(client);
    }

    public void configureListeners() {
        saveBtn.addClickListener(clickEvent -> {
            if (binder.validate().isOk()) {
                boolean isNew = client.getId() == null;
                client.setFirstName(firstNameField.getValue());
                client.setLastName(lastNameField.getValue());
                client.setPatronymic(patronymicField.getValue());
                client.setEmail(emailField.getValue());
                client.setPhoneNumber(numberPhoneField.getValue());
                client.setPassportData(passportDataField.getValue());
                clientService.add(client);

                Notification notification = new Notification(
                        isNew ? "Клиент успешно создан" : "Клиент был изменен", DURATION_OF_NOTIFICATION_SHORT
                );
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addDetachListener(detachEvent -> {
                    UI.getCurrent().navigate(ClientBrowser.class);
                });
                clientForm.setEnabled(false);
                notification.open();
            }
        });

        cancelBtn.addClickListener(e -> {
            UI.getCurrent().navigate(ClientBrowser.class);
        });
    }
}
