package ru.kos.someApp.web.credit;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kos.someApp.entity.Bank;
import ru.kos.someApp.entity.Client;
import ru.kos.someApp.entity.Credit;
import ru.kos.someApp.service.BankService;
import ru.kos.someApp.service.ClientService;
import ru.kos.someApp.service.CreditService;

import java.util.List;
import java.util.Optional;

@Route("credit")
public class CreditEditor extends AppLayout implements HasUrlParameter<Integer> {

    private static final int DURATION_OF_NOTIFICATION_SHORT = 1000;
    private final FormLayout creditForm;
    private final TextField titleField;
    private final NumberField interestRateField;
    private final NumberField limitSumField;
    private final IntegerField termField;
    private final ComboBox<Bank> bankComboBox;
    private final Button saveBtn;
    private final Button cancelBtn;
    private final Binder<Credit> binder;
    private Credit credit;

    @Autowired
    private CreditService creditService;

    @Autowired
    private BankService bankService;

    public CreditEditor() {

        binder = new Binder<>(Credit.class);

        creditForm = new FormLayout();

        titleField = new TextField("Название");
        interestRateField = new NumberField("Процентная ставка");
        interestRateField.setPrefixComponent(new Icon(VaadinIcon.BOOK_PERCENT));
        limitSumField = new NumberField("Максимальная сумма");
        termField = new IntegerField("Срок (в мес.)");
        termField.setHasControls(true);
        termField.setMin(0);
        bankComboBox = new ComboBox<>("Банк");

        saveBtn = new Button("Сохранить");
        cancelBtn = new Button("Отменить");

        creditForm.add(titleField, interestRateField, limitSumField, termField, bankComboBox,
                new Div(saveBtn, cancelBtn));
        setContent(creditForm);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer creditId) {
        credit = creditService.getById(creditId).orElse(null);
        if (credit != null) {
            addToNavbar(new H3("Редактирование кредита"));
        } else {
            credit = new Credit();
            addToNavbar(new H3("Создание кредита"));
        }
        fillForm();
        configureBinding();
        configureListeners();
    }

    private void fillForm() {

        bankComboBox.setItems(bankService.getAll());
        bankComboBox.setItemLabelGenerator(Bank::getName);

        if (credit.getId() != null) {
            titleField.setValue(credit.getTitle());
            interestRateField.setValue(credit.getInterestRate());
            limitSumField.setValue(credit.getLimitSum());
            termField.setValue(credit.getTerm());
            bankComboBox.setValue(credit.getBank());
            bankComboBox.setReadOnly(true);
            bankComboBox.setHelperText("У существующего кредита нельзя менять банк");
        }
    }

    private void configureBinding() {
        binder.forField(titleField)
                .asRequired("Это обязательное поле")
                .bind(Credit::getTitle, Credit::setTitle);
        binder.forField(interestRateField)
                .asRequired("Это обязательное поле")
                .withValidator(e -> e <= 500, "Установленно ограничение в 500%")
                .withValidator(e -> e >= 0d, "Значение не может быть отрицательным")
                .bind(Credit::getInterestRate, Credit::setInterestRate);
        binder.forField(limitSumField)
                .asRequired("Это обязательное поле")
                .withValidator(e -> e >= 0d, "Значение не может быть отрицательным")
                .bind(Credit::getInterestRate, Credit::setInterestRate);
        binder.forField(termField)
                .asRequired("Это обязательное поле")
                .withValidator(e -> e >= 0, "Значение не может быть отрицательным")
                .bind(Credit::getTerm, Credit::setTerm);
        binder.forField(bankComboBox)
                .asRequired("Это обязательное поле")
                .bind(Credit::getBank, Credit::setBank);
        binder.setBean(credit);
    }

    private void configureListeners() {
        saveBtn.addClickListener(clickEvent -> {
            if (binder.validate().isOk()) {

                boolean isNew = credit.getId() == null;
                credit.setTitle(titleField.getValue());
                credit.setInterestRate(interestRateField.getValue());
                credit.setLimitSum(limitSumField.getValue());
                credit.setTerm(termField.getValue());
                credit.setBank(bankComboBox.getValue());
                creditService.add(credit);

                Notification notification = new Notification(
                        isNew ? "Кредит успешно создан" : "Кредит был изменен", DURATION_OF_NOTIFICATION_SHORT
                );
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addDetachListener(detachEvent -> {
                    UI.getCurrent().navigate(CreditBrowser.class);
                });
                creditForm.setEnabled(false);
                notification.open();
            }
        });

        cancelBtn.addClickListener(e -> {
            UI.getCurrent().navigate(CreditBrowser.class);
        });
    }
}

