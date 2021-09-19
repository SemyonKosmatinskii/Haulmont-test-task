package ru.kos.someApp.web;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import ru.kos.someApp.web.bank.BankBrowser;
import ru.kos.someApp.web.client.ClientBrowser;
import ru.kos.someApp.web.credit.CreditBrowser;
import ru.kos.someApp.web.creditOffer.CreditOfferBrowser;

import java.util.ResourceBundle;

import static ru.kos.someApp.web.configs.AppConfig.resourceBundle;

@Route("")
public class MainScreen extends AppLayout {

    public MainScreen() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle());

        header.setDefaultVerticalComponentAlignment(
                FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassName("header");


        addToNavbar(header);

    }

    private void createDrawer() {
        RouterLink clientLink = new RouterLink(resourceBundle.getString("mainLayoutClients"), ClientBrowser.class);
        RouterLink creditLink = new RouterLink(resourceBundle.getString("mainLayoutCredits"), CreditBrowser.class);
        RouterLink bankLink = new RouterLink(resourceBundle.getString("mainLayoutBanks"), BankBrowser.class);
        RouterLink creditOfferLink = new RouterLink(
                resourceBundle.getString("mainLayoutCreditOffers"), CreditOfferBrowser.class);
        clientLink.setHighlightCondition(HighlightConditions.sameLocation());
        creditLink.setHighlightCondition(HighlightConditions.sameLocation());
        bankLink.setHighlightCondition(HighlightConditions.sameLocation());
        creditOfferLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(clientLink, creditLink, bankLink, creditOfferLink));
    }
}
