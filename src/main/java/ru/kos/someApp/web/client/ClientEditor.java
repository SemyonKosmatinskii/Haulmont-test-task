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
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kos.someApp.entity.Client;
import ru.kos.someApp.service.ClientService;

import static ru.kos.someApp.web.configs.AppConfig.*;

@Route("client")
public class ClientEditor extends AppLayout implements HasUrlParameter<Integer> {


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

        firstNameField = new TextField(resourceBundle.getString("firstName"));
        lastNameField = new TextField(resourceBundle.getString("lastName"));
        patronymicField = new TextField(resourceBundle.getString("pat"));
        numberPhoneField = new TextField(resourceBundle.getString("phone"));
        emailField = new EmailField(resourceBundle.getString("email"));
        passportDataField = new TextField(resourceBundle.getString("passport"));
        passportDataField.setHelperText(resourceBundle.getString("emailHelp"));

        saveBtn = new Button(resourceBundle.getString("save"));
        cancelBtn = new Button(resourceBundle.getString("cancel"));

        clientForm.add(firstNameField, lastNameField, patronymicField, numberPhoneField,
                emailField, passportDataField, new Div(saveBtn, cancelBtn));
        setContent(clientForm);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer clientId) {
        client = clientService.getById(clientId).orElse(null);
        if (client != null) {
            addToNavbar(new H3(resourceBundle.getString("edit")));
        } else {
            client = new Client();
            addToNavbar(new H3(resourceBundle.getString("createClient")));
        }
        fillForm();
        configureBinding();
        configureListeners();
    }

    private void fillForm() {
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
                .asRequired(resourceBundle.getString("requiredField"))
                .bind(Client::getFirstName, Client::setFirstName);
        binder.forField(lastNameField)
                .asRequired(resourceBundle.getString("requiredField"))
                .bind(Client::getLastName, Client::setLastName);
        binder.forField(numberPhoneField)
                .asRequired(resourceBundle.getString("requiredField"))
                .withValidator(new RegexpValidator(resourceBundle.getString("warningPhone"),
                        "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$"))
                .bind(Client::getPhoneNumber, Client::setPhoneNumber);
        binder.forField(emailField)
                .withValidator((Validator<String>) (e, valueContext) -> {
                    if (emailField.isInvalid())
                        return ValidationResult.error("");
                    else
                        return ValidationResult.ok();
                })
                .bind(Client::getEmail, Client::setEmail);
        binder.forField(passportDataField)
                .asRequired(resourceBundle.getString("requiredField"))
                .withValidator(correctlyNumber -> correctlyNumber.length() >= 6 && correctlyNumber.length() <= 12,
                        resourceBundle.getString("emailHelp"))
                .bind(Client::getPassportData, Client::setPassportData);
        binder.setBean(client);
    }

    private void configureListeners() {
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
                        isNew ? resourceBundle.getString("createClientOk") :
                                resourceBundle.getString("createCreditOk"),
                        DURATION_OF_NOTIFICATION_SHORT
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
