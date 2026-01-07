package pages;

import core.BasePage;
import dom.CommonDOM;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utils.ActionHelpers;
import utils.NextStep;
import utils.Waits;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class CabinTypePage extends BasePage {

    // Para "estar en la página" puede servir que exista button o span
    private final By cabinTypeAny = By.cssSelector(
            "button[automation-id^='cabin-type-selection-button-'], " +
                    "span[automation-id^='cabin-type-selection-button-']"
    );

    // ✅ Para CLICAR, usa solo buttons
    private final By cabinTypeButtons = By.cssSelector(
            "button[automation-id^='cabin-type-selection-button-']"
    );

    public CabinTypePage() { super(); }

    @Override
    public boolean isAt() {
        return !driver.findElements(cabinTypeAny).isEmpty();
    }

    public NextStep selectCabin() {

        out.println("I'm inside Cabin Type step");

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {

            // Si NEXT ya está habilitado, ya hay selección -> avanzar
            if (isNextEnabled()) {
                out.println("Cabin already selected (NEXT enabled), clicking NEXT");
                clickNextRobust();
                return NextStep.EXPERIENCE;
            }

            //Traer SOLO buttons clicables
            List<WebElement> allButtons = Waits.waitForVisibilityOfElements(cabinTypeButtons);
            Assert.assertFalse(allButtons.isEmpty(), "ERROR: No cabin BUTTON options found");

            out.println(allButtons.size() + " cabin BUTTON CTA(s) found");

            // Si hay 1 solo, click y avanzar
            if (allButtons.size() == 1) {
                out.println("Only one cabin BUTTON available");

                WebElement only = allButtons.get(0);
                scrollToElementSmoothly(only);
                clickIfButton(only);

                waitUntilNextEnabled();
                Assert.assertTrue(isNextEnabled(), "NEXT is disabled after selecting the only cabin");

                clickNextRobust();
                return NextStep.EXPERIENCE;
            }

            // Excluir la última (anti-yacht)
            List<WebElement> candidates = new ArrayList<>(allButtons);
            candidates.remove(candidates.size() - 1);

            Assert.assertFalse(candidates.isEmpty(),
                    "ERROR: No candidates left after excluding last cabin");

            int attempts = Math.min(3, candidates.size());

            for (int i = 0; i < attempts; i++) {

                closeGenericPopup();
                Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

                if (isNextEnabled()) {
                    out.println("NEXT enabled during attempts -> clicking NEXT");
                    clickNextRobust();
                    return NextStep.EXPERIENCE;
                }

                int index = ActionHelpers.randomInt(0, candidates.size() - 1);
                WebElement chosen = candidates.get(index);

                out.println("Attempt " + (i + 1) + ": selecting cabin index " + index);

                scrollToElementSmoothly(chosen);

                //ahora siempre será button -> clickIfButton sí clickea
                clickIfButton(chosen);

                closeGenericPopup();
                Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

                //espera sin sleep a que NEXT se habilite
                waitUntilNextEnabled();

                if (isNextEnabled()) {
                    out.println("NEXT enabled -> clicking NEXT");
                    clickNextRobust();
                    return NextStep.EXPERIENCE;
                }

                out.println("NEXT still disabled -> trying another cabin");
                candidates.remove(index);
                if (candidates.isEmpty()) break;
            }

            Assert.fail("Could not enable NEXT after trying cabin selections (last excluded)");
            return NextStep.EXPERIENCE;

        } catch (Exception e) {
            out.println("ERROR: Cabin selection failed - " + e.getMessage());
            Assert.fail("Cabin selection failed: " + e.getMessage());
            return NextStep.EXPERIENCE;
        }
    }


}
