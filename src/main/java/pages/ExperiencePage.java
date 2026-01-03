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
    private final By experienceButton = By.cssSelector("[automation-id^='cabin-experience-selection-button-']");

    // Card contenedor (más fiable para leer el texto: BELLA/FANTASTICA/AUREA)
    private final By experienceCard = By.cssSelector("[automation-id^='cabin-experience-section-']");

    // Estado preseleccionado típico cuando es 1 sola: span.button.checked (opcional si lo quieres)
    private final By preselectedCTA = By.cssSelector("span.button.checked");

    //Selector para controlar que llegue a la pagina del formulario de pasajeros
    private final By firstNamePassenger1 = By.cssSelector("#FirstName_1_1"); //nombre del pasajero 1

    //constructor Experience
    public ExperiencePage() {}

    //implemento metodo abstracto de clase BasePage abstracta
    @Override
    public boolean isAt() {
        return !driver.findElements(experienceButton).isEmpty();
    }

    public NextStep selectExperience() {

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {
            out.println("I'm inside experience");

            //traer todos los CTA (button o span)
            List<WebElement> ctas = Waits.waitForVisibilityOfElements(experienceButton);

            Assert.assertFalse(ActionHelpers.isEmpty(ctas), "ERROR: No experience CTA found");

            out.println(ctas.size() + " experience CTA(s) found");

            //elegir button (si hay 1, ese; si hay varios, random)
            WebElement cta = (ctas.size() == 1)
                    ? ctas.get(0)
                    : ctas.get(ActionHelpers.randomInt(0, ctas.size() - 1));

            scrollToElementSmoothly(cta);

            //subir al card padre y leer texto (NO dependes de button/span)
            // Ajusta la clase si tu card tiene otra
            WebElement card = cta.findElement(By.xpath("./ancestor::div[contains(@class,'section--experiences__experience')]"));

            String cardText = card.getText().toLowerCase();

            boolean isBella = cardText.contains("bella");

            out.println("Selected card text: " + cardText);

            out.println("Is Bella? " + isBella);

            //click en CTA solo si es button
            //si es span.button.checked normalmente está preseleccionado
            boolean isButton = cta.getTagName().equalsIgnoreCase("button");

            if (isButton) {
                try {
                    clickWithActions(cta);

                    out.println("CTA clicked (button)");

                } catch (Exception e) {
                    out.println("CTA Actions failed -> JS click");

                    jsClickByElement(cta);
                }
            } else {
                out.println("CTA is not a button (likely preselected span) -> skipping click");
            }

            //next
            closeGenericPopup();

            jsClickByLocator(CommonDOM.nextButton);

            //Bella: validar llegada a Passenger; si no llega, un 2do Next y basta
            if (isBella) {
                if (!verifyPassengerSection(firstNamePassenger1)) {

                    out.println("Bella selected but Passenger section not reached -> clicking NEXT again");

                    closeGenericPopup();

                    jsClickByLocator(CommonDOM.nextButton);
                }
                return NextStep.PASSENGER_FORM;
            }

            //no Bella: sigue flujo normal
            return NextStep.CABIN_POSITION;

        } catch (Exception e) {
            out.println("ERROR: Experience selection failed - " + e.getMessage());

            Assert.fail("Experience selection failed: " + e.getMessage());

            return NextStep.CABIN_POSITION;
        }
    }
}

