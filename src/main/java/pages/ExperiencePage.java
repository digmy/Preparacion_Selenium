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

    private final By experienceButton = By.cssSelector(
            "button[automation-id^='cabin-experience-selection-button-'], " +
                    "span[automation-id^='cabin-experience-selection-button-']"
    );

    private final By experienceCard = By.cssSelector("[automation-id^='cabin-experience-section-']");

    //detecta el badge “selezionato” dentro del card
    private final By selectedBadge = By.cssSelector(".checked, [class*='checked'], [class*='selected'], [class*='selezionato']");

    private final By firstNamePassenger1 = By.cssSelector("#FirstName_1_1");

    public ExperiencePage() {}

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

            //elegir card (si hay 1, esa; si hay varias, random)
            WebElement card = (cards.size() == 1)
                    ? cards.get(0)
                    : cards.get(ActionHelpers.randomInt(0, cards.size() - 1));

            scrollToElementSmoothly(card);

            //Seleccionar CTA dentro de la card
            List<WebElement> ctas = card.findElements(experienceButton);
            Assert.assertFalse(ctas.isEmpty(), "ERROR: No experience CTA found inside card");

            WebElement cta = (ctas.size() == 1)
                    ? ctas.get(0)
                    : ctas.get(ActionHelpers.randomInt(0, ctas.size() - 1));

            scrollToElementSmoothly(cta);

            clickIfButton(cta); //usa tu método BasePage (button->click / span->skip)

            //Ahora leemos cuál experiencia está seleccionada de verdad
            String selectedExperience = getSelectedExperienceName(cards);

            out.println("Selected experience detected: " + selectedExperience);

            clickNextRobust();

            boolean isBella = selectedExperience.contains("bella");

            if (isBella) {
                //Bella suele ir a Passenger
                if (!verifyPassengerSection(firstNamePassenger1)) {
                    out.println("Bella selected but Passenger not reached -> clicking NEXT again");
                    clickNextRobust();
                }
                return NextStep.PASSENGER_FORM;
            }

            //no-bella => cabin position
            return NextStep.CABIN_POSITION;

        } catch (Exception e) {
            out.println("ERROR: Experience selection failed - " + e.getMessage());
            Assert.fail("Experience selection failed: " + e.getMessage());
            return NextStep.CABIN_POSITION;
        }
    }

    //Busca el card que está seleccionado (checked/selezionato) y devuelve su texto de forma segura.
    private String getSelectedExperienceName(List<WebElement> cards) {

        // primero: intentar encontrar el seleccionado por clases/badge
        for (WebElement c : cards) {
            String text = c.getText().toLowerCase();

            // criterio simple: si el card tiene la palabra "selezionato"
            // (en tu log aparece literal)
            if (text.contains("selezionato")) {

                if (text.contains("bella")) return "bella";
                if (text.contains("fantastica")) return "fantastica";
                if (text.contains("aurea")) return "aurea";

                return text; // fallback
            }
        }

        //si ninguno tiene “selezionato”, fallback: devuelve el texto del primer card
        return cards.get(0).getText().toLowerCase();
    }
}
