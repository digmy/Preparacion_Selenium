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

public class ExperiencePage extends BasePage {

    // Buttton de cada card: puede ser button (no seleccionado) o span (seleccionado/preseleccionado)
    private final By experienceButton = By.cssSelector("button[automation-id^='cabin-experience-selection-button-']," +
            "span[automation-id^='cabin-experience-selection-button-']");

    // Card contenedor (más fiable para leer el texto: BELLA/FANTASTICA/AUREA)
    private final By experienceCard = By.cssSelector("[automation-id^='cabin-experience-section-']");

    // Estado preseleccionado típico cuando es 1 sola: span.button.checked
    private final By preselectedCTA = By.cssSelector("span.button.checked[automation-id^='cabin-experience-selection-button-']");

    //Selector para controlar que llegue a la pagina del formulario de pasajeros
    private final By firstNamePassenger1 = By.cssSelector("#FirstName_1_1"); //nombre del pasajero 1

    //constructor Experience
    public ExperiencePage() {
    }

    //implemento metodo abstracto de clase BasePage abstracta
    @Override
    public boolean isAt() {
        return !driver.findElements(experienceCard).isEmpty() || !driver.findElements(experienceButton).isEmpty();
    }

    public NextStep selectExperience() {

        out.println("I'm inside Experience step");

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {
            out.println("I'm inside experience");

            List<WebElement> cards = Waits.waitForVisibilityOfElements(experienceCard);
            Assert.assertFalse(cards.isEmpty(), "ERROR: No experience cards found");

            // elegir card (si hay 1, esa; si hay varias, random)
            WebElement card = (cards.size() == 1)
                    ? cards.get(0)
                    : cards.get(ActionHelpers.randomInt(0, cards.size() - 1));

            scrollToElementSmoothly(card);

            String cardText = card.getText().toLowerCase();
            boolean isBella = cardText.contains("bella");

            out.println("Selected card text: " + cardText);
            out.println("Is Bella? " + isBella);

            // CTA: primero busca button, si no hay, span checked (preseleccionado)
            List<WebElement> ctas = card.findElements(experienceButton);
            Assert.assertFalse(ctas.isEmpty(), "ERROR: No experience CTA found inside card");

            //Elegir CTA: si hay 1, ese; si hay varios, random
            WebElement cta = (ctas.size() == 1)
                    ? ctas.get(0)
                    : ctas.get(ActionHelpers.randomInt(0, ctas.size() - 1));

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

            if (isBella) {
                // Bella a veces salta directo a Passenger, otras requiere un NEXT más
                if (!verifyPassengerSection(firstNamePassenger1)) {
                    out.println("Bella selected but Passenger not reached -> clicking NEXT again");
                    clickNextRobust();
                }
                return NextStep.PASSENGER_FORM;
            }

            return NextStep.CABIN_POSITION;

        } catch (Exception e) {
            out.println("ERROR: Experience selection failed - " + e.getMessage());
            Assert.fail("Experience selection failed: " + e.getMessage());
            return NextStep.CABIN_POSITION;

        }
    }
}

