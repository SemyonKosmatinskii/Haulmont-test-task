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
import ru.kos.someApp.entity.Credit;
import ru.kos.someApp.service.BankService;
import ru.kos.someApp.service.CreditService;

import static ru.kos.someApp.web.configs.AppConfig.*;


@Route("credit")
public class CreditEditor extends AppLayout implements HasUrlParameter<Integer> {

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

        titleField = new TextField(resourceBundle.getString("title"));
        interestRateField = new NumberField(resourceBundle.getString("rate"));
        interestRateField.setPrefixComponent(new Icon(VaadinIcon.BOOK_PERCENT));
        limitSumField = new NumberField(resourceBundle.getString("maxSum"));
        termField = new IntegerField(resourceBundle.getString("term"));
        termField.setHasControls(true);
        termField.setMin(0);
        bankComboBox = new ComboBox<>(resourceBundle.getString("bank"));

        saveBtn = new Button(resourceBundle.getString("save"));
        cancelBtn = new Button(resourceBundle.getString("cancel"));

        creditForm.add(titleField, interestRateField, limitSumField, termField, bankComboBox,
                new Div(saveBtn, cancelBtn));
        setContent(creditForm);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer creditId) {
        credit = creditService.getById(creditId).orElse(null);
        if (credit != null) {
            addToNavbar(new H3(resourceBundle.getString("edit")));
        } else {
            credit = new Credit();
            addToNavbar(new H3(resourceBundle.getString("createCredit")));
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
            bankComboBox.setHelperText(resourceBundle.getString("bankHelp"));
        }
    }

    private void configureBinding() {
        binder.forField(titleField)
                .asRequired(resourceBundle.getString("requiredField"))
                .bind(Credit::getTitle, Credit::setTitle);
        binder.forField(interestRateField)
                .asRequired(resourceBundle.getString("requiredField"))
                .withValidator(e -> e <= 500, resourceBundle.getString("rateHelp"))
                .withValidator(e -> e >= 0d, resourceBundle.getString("warningPositive"))
                .bind(Credit::getInterestRate, Credit::setInterestRate);
        binder.forField(limitSumField)
                .asRequired(resourceBundle.getString("requiredField"))
                .withValidator(e -> e >= 0d, resourceBundle.getString("warningPositive"))
                .bind(Credit::getInterestRate, Credit::setInterestRate);
        binder.forField(termField)
                .asRequired(resourceBundle.getString("requiredField"))
                .withValidator(e -> e >= 0, resourceBundle.getString("warningPositive"))
                .bind(Credit::getTerm, Credit::setTerm);
        binder.forField(bankComboBox)
                .asRequired(resourceBundle.getString("requiredField"))
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
                        isNew ? resourceBundle.getString("createCreditOk") :
                                resourceBundle.getString("editCreditOk"),
                        DURATION_OF_NOTIFICATION_SHORT
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

