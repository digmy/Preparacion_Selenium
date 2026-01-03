package pages;

import core.BasePage;
import dom.CommonDOM;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.ActionHelpers;
import utils.NextStep;
import utils.Waits;

import java.util.List;

import static java.lang.System.out;

public class OffersPage extends BasePage {

    //selectores para agregar pasajero
    private final By addPassenger = By.cssSelector("div[automation-id='cabin-configuration-icon']"); //botón + para agregar pasajero
    private final By addAdult = By.cssSelector("a[automation-id='cabin-1-occupancy-passengers-a-add-button']");
    private final By addChildren = By.cssSelector("a[automation-id='cabin-1-occupancy-passengers-c-add-button']");
    private final By addKids = By.cssSelector("a[automation-id='cabin-1-occupancy-passengers-j-add-button']");
    private final By addBaby = By.cssSelector("a[automation-id='cabin-1-occupancy-passengers-i-add-button']");

    //agregar pasajero con mobilidad reducida
    private final By mobilityCheckbox = By.cssSelector("input[automation-id='cabin-1-occupancy-accessible-cabin-checkbox']");

    //botones dentro la ventana de pasajeros
    private final By confirmButton = By.cssSelector("button[automation-id='cabin-confirm-button']");
    private final By cancelButton = By.cssSelector("button[automation-id='cabin-cancel-button']");

    //boton dentro de la ventana para agregar la voyager card
    private final By vcButton = By.cssSelector("button[automation-id='msc-voyagers-club-member-button']");

    //boton para seleccionar la oferta
    private final By selectOfferButton = By.cssSelector("button[automation-id^='price-type-selection-button-']," +
            "span[automation-id^='price-type-selection-button-']");

    // Estado preseleccionado típico cuando es 1 sola: span.button.checked (opcional si lo quieres)
    private final By preselectedCTA = By.cssSelector("span.button.checked[automation-id^='price-type-selection-button-']");

    //constructor Offers
    public OffersPage (){}

    //implemento metodo abstracto de clase BasePage abstracta
    @Override
    public boolean isAt() {
        return !driver.findElements(selectOfferButton).isEmpty();
    }

    //selecciona oferta
    public NextStep selectOffer() {

        out.println("I'm inside Offers step");

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {
            out.println("I'm inside selectOffer");

            List<WebElement> offersCtasList = Waits.waitForVisibilityOfElements(selectOfferButton);
            Assert.assertFalse(offersCtasList.isEmpty(), "ERROR: No offers found");

            //Elegir CTA: si hay 1, ese; si hay varios, random
            WebElement cta = (offersCtasList.size() == 1)
                    ? offersCtasList.get(0)
                    : offersCtasList.get(ActionHelpers.randomInt(0, offersCtasList.size() - 1));

            scrollToElementSmoothly(cta);

            //Lógica button/span + checked
            String tag = cta.getTagName().toLowerCase();
            String clazz = cta.getAttribute("class");
            boolean alreadySelected = (clazz != null && clazz.contains("checked"));

            if ("button".equals(tag) && !alreadySelected) {
                clickWithActions(cta);
                out.println("CTA clicked (button)");
            } else {
                out.println("CTA preseleccionado (span.checked o ya checked) -> no clickeo");
                // opcional: validar que efectivamente hay preselected
                // Assert.assertFalse(card.findElements(preselectedCTA).isEmpty(), "No preselected CTA found");
            }

            clickNextRobust();

            return NextStep.CABIN_TYPE;
        } catch (Exception e) {
            out.println("ERROR: No offers found -" + e.getMessage());

            Assert.fail("Offers selection failed:" + e.getMessage());

            return NextStep.CABIN_TYPE;
        }

    }

}

