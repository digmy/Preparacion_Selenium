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

public class CabinPositionPage extends BasePage {

    //para la posición ya marcada
    private final By selectCabinPositionChecked = By.cssSelector("span[automation-id^='cabin-type-selection-button-']");

    //para seleccionar las demás opciones disponibles
    private final By cabinPositionButton = By.cssSelector("button[automation-id^='cabin-type-selection-button-']," +
            "span[automation-id^='cabin-type-selection-button-']");


    public CabinPositionPage(){}

    //implemento metodo abstracto de clase BasePage abstracta
    @Override
    public boolean isAt() {
        return !driver.findElements(cabinPositionButton).isEmpty();
    }
    public NextStep selectCabinPosition() {

        out.println("I'm inside Cabin Position step");

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {
            //obtengo la lista de cabinas
            List<WebElement> cabinPositionCtasList = Waits.waitForVisibilityOfElements(cabinPositionButton);

            Assert.assertFalse(ActionHelpers.isEmpty(cabinPositionCtasList), "ERROR: No cabin positions options found");

            out.println(cabinPositionCtasList.size() + " cabin(s) position found");

            //Elegir CTA: si hay 1, ese; si hay varios, random
            WebElement cta = (cabinPositionCtasList.size() == 1)
                    ? cabinPositionCtasList.get(0)
                    : cabinPositionCtasList.get(ActionHelpers.randomInt(0, cabinPositionCtasList.size() - 1));

            scrollToElementSmoothly(cta);

            //Lógica button/span + checked
            String tag = cta.getTagName().toLowerCase();
            String clazz = cta.getAttribute("class");
            boolean alreadySelected = (clazz != null && clazz.contains("checked"));

            if ("button".equals(tag) && !alreadySelected) {
                clickWithActions(cta);
                out.println("CTA clicked (button)");
            } else {
                out.println("CTA preseleccionado (span.checked o ya checked), no clickeo");
                // opcional: validar que efectivamente hay preselected
                // Assert.assertFalse(card.findElements(preselectedCTA).isEmpty(), "No preselected CTA found");
            }

            clickNextRobust();

            return NextStep.CABIN_NUMBER;
        } catch (Exception e) {
            out.println("ERROR: No cabin position found -" + e.getMessage());

            Assert.fail("Cabin position selection failed:" + e.getMessage());

            return NextStep.CABIN_NUMBER;
        }

    }
}
