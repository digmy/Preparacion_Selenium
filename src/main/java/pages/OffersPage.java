package pages;

import core.BasePage;
import dom.CommonDOM;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
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
    private final By selectOfferButton = By.cssSelector("button[automation-id^='price-type-selection-button-']");

    //constructor Offers
    public OffersPage (){}

    //implemento metodo abstracto de clase BasePage abstracta
    @Override
    public boolean isAt() {
        return !driver.findElements(selectOfferButton).isEmpty();
    }

    //selecciona oferta
    public CabinTypePage selectOffer() {

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {
            out.println("I'm inside selectOffer");

            List<WebElement> offers = Waits.waitForVisibilityOfElements(selectOfferButton);
            Assert.assertFalse(offers.isEmpty(), "ERROR: No offers found");

            if (offers.size() == 1) {
                out.println("Only one offer available");

                WebElement onlyOffer = offers.get(0);
                scrollToElementSmoothly(onlyOffer);

                // Intentar click (no obligatorio si ya está preseleccionada)
                try {
                    clickWithActions(onlyOffer);
                    out.println("Offer clicked (single option)");
                } catch (Exception e) {
                    out.println("Offer click skipped (probably preselected or intercepted)");
                }

            } else {
                out.println("Multiple offers available: " + offers.size());

                // Selección aleatoria usando tu helper
                clickRandom(selectOfferButton);
                out.println("Random offer selected");
            }

            // Siempre: cerrar popups y esperar loader antes de NEXT
            closeGenericPopup();
            Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

            // NEXT (más robusto: intenta normal → actions → JS)
            clickNextRobust();

            // Validación mínima opcional (si tienes locator de la CabinTypePage, mejor)
            return new CabinTypePage();

        } catch (Exception e) {
            out.println("ERROR: Offer selection failed - " + e.getMessage());
            Assert.fail("Offer selection failed: " + e.getMessage());
            return null;
        }
    }
    private void clickNextRobust() {
        try {
            WebElement next = Waits.waitForClickableByLocator(CommonDOM.nextButton);
            scrollToElementSmoothly(next);
            clickWithActions(next);
            out.println("Clicked NEXT with Actions");
        } catch (Exception e) {
            out.println("Actions click failed, trying JS NEXT");
            jsClickByLocator(CommonDOM.nextButton);
        }
    }

}

