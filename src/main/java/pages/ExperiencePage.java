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

    // CTA marcado como seleccionado (suele ser span.button.checked o button.checked)
    private final By selectedCtaInsideCard = By.cssSelector(
            "span.button.checked[automation-id^='cabin-experience-selection-button-'], " +
                    "button.checked[automation-id^='cabin-experience-selection-button-'], " +
                    "button.button.checked[automation-id^='cabin-experience-selection-button-']"
    );

    // Locator para detectar Passenger
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

            // elegir card (si hay 1, esa; si hay varias, random)
            WebElement card = (cards.size() == 1)
                    ? cards.get(0)
                    : cards.get(ActionHelpers.randomInt(0, cards.size() - 1));

            scrollToElementSmoothly(card);

            // CTA dentro de la card elegida
            List<WebElement> ctas = card.findElements(experienceButton);
            Assert.assertFalse(ctas.isEmpty(), "ERROR: No experience CTA found inside card");

            WebElement cta = (ctas.size() == 1)
                    ? ctas.get(0)
                    : ctas.get(ActionHelpers.randomInt(0, ctas.size() - 1));

            scrollToElementSmoothly(cta);

            // click solo si es button y no está checked (tu método ya lo hace)
            clickIfButton(cta);

            // Detectar experiencia REAL seleccionada (sin caer en "bella+")
            String selectedExperience = getSelectedExperienceName(cards);
            out.println("Selected experience detected: " + selectedExperience);

            clickNextRobust();

            // NO asumir que bella siempre va a Passenger. Si Passenger está visible => PASSENGER_FORM; si no => CABIN_POSITION
            if (isVisible(firstNamePassenger1)) {
                out.println("Passenger form detected after NEXT");
                return NextStep.PASSENGER_FORM;
            }

            out.println("Passenger form NOT detected -> going to Cabin Position");
            return NextStep.CABIN_POSITION;

        } catch (Exception e) {
            out.println("ERROR: Experience selection failed - " + e.getMessage());
            Assert.fail("Experience selection failed: " + e.getMessage());
            return NextStep.CABIN_POSITION;
        }
    }

    // Encuentra el card seleccionado y extrae el nombre EXACTO (bella/fantastica/aurea)
    private String getSelectedExperienceName(List<WebElement> cards) {

        WebElement selectedCard = null;

        // 1) Preferencia: card que tenga un CTA checked dentro
        for (WebElement c : cards) {
            if (!c.findElements(selectedCtaInsideCard).isEmpty()) {
                selectedCard = c;
                break;
            }
        }

        //Fallback: card que contenga "selezionato" en su texto (pero ojo: NO leer bella por contains)
        if (selectedCard == null) {
            for (WebElement c : cards) {
                String txt = c.getText().toLowerCase();
                if (txt.contains("selezionato")) {
                    selectedCard = c;
                    break;
                }
            }
        }

        // 3) Último fallback: primer card
        if (selectedCard == null) {
            selectedCard = cards.get(0);
        }

        String text = selectedCard.getText().toLowerCase();

        //Heurística simple y buena: el nombre suele venir como línea sola
        for (String line : text.split("\\R")) {
            String l = line.trim();
            if (l.equals("bella")) return "bella";
            if (l.equals("fantastica")) return "fantastica";
            if (l.equals("aurea")) return "aurea";
        }

        // fallback suave
        if (text.contains("fantastica")) return "fantastica";
        if (text.contains("aurea")) return "aurea";
        if (text.contains("bella")) return "bella";

        return text;
    }
}
