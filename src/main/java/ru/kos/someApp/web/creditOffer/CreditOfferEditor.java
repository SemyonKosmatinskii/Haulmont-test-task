package ru.kos.someApp.web.creditOffer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kos.someApp.entity.*;
import ru.kos.someApp.service.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.kos.someApp.web.configs.AppConfig.*;

@Route("credit_offer")
public class CreditOfferEditor extends AppLayout implements HasUrlParameter<Integer> {

    private final FormLayout creditOfferForm;
    private final ComboBox<Client> clientComboBox;
    private final ComboBox<Credit> creditComboBox;
    private final NumberField interestRateField;
    private final NumberField limitSumField;
    private final IntegerField termField;
    private final DatePicker startDateField;
    private final NumberField sumField;
    private final NumberField initialFeeField;
    private final Button calculateBtn;
    private final Button saveBtn;
    private final Button cancelBtn;
    private List<Payment> paymentList;
    private final TextField resultPaymentField;
    private final TextField resultPercentField;
    private final Binder<CreditOffer> binder;
    private final Grid<Payment> paymentGrid;
    private CreditOffer creditOffer;
    private Client client;
    private Bank bank;
    private boolean isPaymentsCalculated;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private BankService bankService;

    @Autowired
    private CreditOfferService creditOfferService;

    @Autowired
    private PaymentService paymentService;

    public CreditOfferEditor() {
        isPaymentsCalculated = true;

        binder = new Binder<>(CreditOffer.class);

        creditOfferForm = new FormLayout();

        clientComboBox = new ComboBox<>(resourceBundle.getString("client"));

        creditComboBox = new ComboBox<>(resourceBundle.getString("credit"));

        interestRateField = new NumberField(resourceBundle.getString("rate"));

        limitSumField = new NumberField(resourceBundle.getString("limitSum"));

        termField = new IntegerField(resourceBundle.getString("term"));

        startDateField = new DatePicker(resourceBundle.getString("startDate"));

        sumField = new NumberField(resourceBundle.getString("sum"));
        sumField.setPlaceholder(resourceBundle.getString("warningLimit"));

        initialFeeField = new NumberField(resourceBundle.getString("initialFee"));
        initialFeeField.setValue(0d);

        calculateBtn = new Button(resourceBundle.getString("calculated"));

        saveBtn = new Button(resourceBundle.getString("save"));
        cancelBtn = new Button(resourceBundle.getString("cancel"));

        paymentGrid = new Grid<>();

        resultPaymentField = new TextField(resourceBundle.getString("resultSum"));

        resultPercentField = new TextField(resourceBundle.getString("overSum"));

        creditOfferForm.setResponsiveSteps(new FormLayout.ResponsiveStep("30em", 3));
        creditOfferForm.add(clientComboBox,
                creditComboBox,
                interestRateField,
                limitSumField,
                termField,
                startDateField,
                sumField,
                initialFeeField,
                calculateBtn,
                paymentGrid,
                new Div(resultPaymentField, resultPercentField),
                new Div(saveBtn, cancelBtn));
        creditOfferForm.setColspan(clientComboBox, 3);
        creditOfferForm.setColspan(creditComboBox, 3);
        creditOfferForm.setColspan(calculateBtn, 3);
        creditOfferForm.setColspan(paymentGrid, 3);
        setContent(creditOfferForm);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer creditOfferId) {
        creditOffer = creditOfferService.getById(creditOfferId).orElse(null);
        if (creditOffer != null) {
            List<Payment> paymentList = paymentService.getByCreditOfferId(creditOffer.getId());
            creditOffer.setPaymentList(paymentList);
            addToNavbar(new H3(resourceBundle.getString("edit")));
        } else {
            creditOffer = new CreditOffer();
            addToNavbar(new H3(resourceBundle.getString("createCreditOffer")));
        }

        fillForm();
        configureBinding();
        configureListeners();
    }

    private void fillForm() {
        paymentGrid.addColumn(column -> new SimpleDateFormat("dd MMMM yyyy").format(column.getStartDate())).
                setHeader(resourceBundle.getString("date"));
        paymentGrid.addColumn(column ->
                String.format("%1$,.2f", column.getPayment())).setHeader(resourceBundle.getString("payment"));
        paymentGrid.addColumn(column ->
                String.format("%1$,.2f", column.getPercent())).setHeader(resourceBundle.getString("percents"));
        paymentGrid.addColumn(column ->
                String.format("%1$,.2f", column.getCapital())).setHeader(resourceBundle.getString("capital"));
        paymentGrid.addColumn(column ->
                String.format("%1$,.2f", column.getResidue())).setHeader(resourceBundle.getString("residue"));
        interestRateField.setReadOnly(true);
        limitSumField.setReadOnly(true);
        termField.setReadOnly(true);
        startDateField.setRequired(true);
        resultPaymentField.setReadOnly(true);
        resultPercentField.setReadOnly(true);

        clientComboBox.setItems(clientService.getAll());
        clientComboBox.setItemLabelGenerator(client -> client.getLastName() + " " + client.getFirstName());

        creditComboBox.setItems(creditService.getAll());
        creditComboBox.setItemLabelGenerator(credit -> credit.getTitle() + " (" + credit.getBank().getName() + ")");

        creditComboBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                interestRateField.setValue(e.getValue().getInterestRate());
                limitSumField.setValue(e.getValue().getLimitSum());
                termField.setValue(e.getValue().getTerm());
            } else {
                interestRateField.setValue(null);
                limitSumField.setValue(null);
                termField.setValue(null);
            }
        });

        if (creditOffer.getId() != null) {
            List<Payment> paymentList = creditOffer.getPaymentList();

            LocalDate startDateTime = creditOffer.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            startDateField.setValue(startDateTime);
            sumField.setValue(creditOffer.getSumValue());
            initialFeeField.setValue(creditOffer.getInitialFee());
            clientComboBox.setValue(creditOffer.getClient());
            creditComboBox.setValue(creditOffer.getCredit());
            client = creditOffer.getClient();
            bank = creditOffer.getCredit().getBank();
            resultPaymentField.setValue(String.format("%1$,.2f", creditOffer.getResultPayment()));
            resultPercentField.setValue(String.format("%1$,.2f", creditOffer.getResultPercent()));

            paymentGrid.setItems(paymentList);
        }
    }

    private void configureBinding() {

        binder.forField(clientComboBox)
                .asRequired(resourceBundle.getString("requiredField"))
                .bind(CreditOffer::getClient, CreditOffer::setClient);
        binder.forField(creditComboBox)
                .asRequired(resourceBundle.getString("requiredField"))
                .bind(CreditOffer::getCredit, CreditOffer::setCredit);
        binder.forField(startDateField)
                .asRequired(resourceBundle.getString("requiredField"))
                .bind(creditOffer -> {
                            Date startDate = creditOffer.getStartDate();
                            if (startDate != null) {
                                return startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            }
                            return LocalDate.now();
                        },
                        (creditOffer, value) ->
                                creditOffer.setStartDate(Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                );
        binder.forField(sumField)
                .asRequired(resourceBundle.getString("requiredField"))
                .withValidator(e -> {
                    Double limitSumFieldValue = limitSumField.getValue();
                    return limitSumFieldValue != null && e <= limitSumFieldValue;
                }, resourceBundle.getString("warningLimit"))
                .withValidator(e -> e >= 0d, resourceBundle.getString("warningPositive"))
                .bind(CreditOffer::getSumValue, CreditOffer::setSumValue);
        binder.forField(initialFeeField)
                .asRequired(resourceBundle.getString("requiredField"))
                .withValidator(e -> {
                            Double sumFieldValue = sumField.getValue();
                            Double limitSumFieldValue = limitSumField.getValue();
                            return sumFieldValue != null
                                    && limitSumFieldValue != null
                                    && e <= sumFieldValue
                                    && e <= limitSumFieldValue;
                        },
                        resourceBundle.getString("warningSum"))
                .withValidator(e -> e >= 0d, resourceBundle.getString("warningPositive"))
                .bind(CreditOffer::getInitialFee, CreditOffer::setInitialFee);
        binder.setBean(creditOffer);
    }

    private void configureListeners() {

        List<Payment> cleanList = new ArrayList<>();

        binder.addValueChangeListener(e -> {
            isPaymentsCalculated = false;
            paymentGrid.setItems(cleanList);
        });

        calculateBtn.addClickListener(e -> {
            if (binder.validate().isOk()) {
                isPaymentsCalculated = true;
                LocalDate startDateFieldValue = startDateField.getValue();
                Date startDateUtil = Date.from(startDateFieldValue.atStartOfDay(ZoneId.systemDefault()).toInstant());
                paymentList = paymentService.calculatePayments(startDateUtil,
                        sumField.getValue() - initialFeeField.getValue(), creditComboBox.getValue());
                int i = 0;
                for (Payment payment : paymentList) {
                    payment.setId(i++ + 1);
                }
                double resultPayment = paymentService.calculateResultPayment(paymentList, initialFeeField.getValue());
                double resultPercent = paymentService.calculateResultPercent(paymentList);

                resultPaymentField.setValue(String.format("%1$,.2f", resultPayment));
                resultPercentField.setValue(String.format("%1$,.2f", resultPercent));
                paymentGrid.setItems(paymentList);
                creditOffer.setPaymentList(paymentList);
                creditOffer.setResultPayment(resultPayment);
                creditOffer.setResultPercent(resultPercent);
            }
        });

        saveBtn.addClickListener(clickEvent -> {
            if (binder.validate().isOk()) {
                if (isPaymentsCalculated) {
                    boolean isNew = false;
                    if (creditOffer.getId() != null) {
                        bank.removeClient(client);
                        bankService.add(bank);
                        clientService.add(client);
                    } else {
                        isNew = true;
                    }
                    LocalDate startDateFieldValue = startDateField.getValue();
                    Date startDateUtil = Date.from(startDateFieldValue.atStartOfDay(ZoneId.systemDefault()).toInstant());

                    creditOffer.setStartDate(startDateUtil);
                    creditOffer.setSumValue(sumField.getValue());
                    creditOffer.setInitialFee(initialFeeField.getValue());
                    creditOffer.setClient(clientComboBox.getValue());
                    creditOffer.setCredit(creditComboBox.getValue());

                    bank = creditComboBox.getValue().getBank();
                    client = clientComboBox.getValue();
                    bank.addClient(client);

                    bankService.add(bank);
                    clientService.add(client);

                    for (Payment payment : creditOffer.getPaymentList()) {
                        payment.setId(null);
                    }
                    paymentGrid.setItems(cleanList);
                    creditOfferService.add(creditOffer);


                    Notification notification = new Notification(
                            isNew ? resourceBundle.getString("createCreditOfferOk") :
                                    resourceBundle.getString("editCreditOfferOk"),
                            DURATION_OF_NOTIFICATION_SHORT
                    );
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.addDetachListener(detachEvent -> {
                        UI.getCurrent().navigate(CreditOfferBrowser.class);
                    });
                    creditOfferForm.setEnabled(false);
                    notification.open();
                } else {
                    Notification notification = new Notification(resourceBundle.getString("needCalculated"),
                            DURATION_OF_NOTIFICATION_LONG);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.open();
                }
            }
        });

        cancelBtn.addClickListener(e -> {
            UI.getCurrent().navigate(CreditOfferBrowser.class);
        });
    }
}

