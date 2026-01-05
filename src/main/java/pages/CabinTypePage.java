package pages;

import core.BasePage;
import dom.CommonDOM;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.ActionHelpers;
import utils.NextStep;
import utils.Waits;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class CabinTypePage extends BasePage {

    private final By cabinTypeButton = By.cssSelector(
            "button[automation-id^='cabin-type-selection-button-'], " +
                    "span[automation-id^='cabin-type-selection-button-']"
    );

    public CabinTypePage() { super(); }

    @Override
    public boolean isAt() {
        return !driver.findElements(cabinTypeButton).isEmpty();
    }

    public NextStep selectCabin() {

        out.println("I'm inside Cabin Type step (NO-LAST / CHECK-NEXT mode)");

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {

            //Si el NEXT ya está habilitado, ya hay selección -> avanzar
            if (isNextEnabled()) {
                out.println("Cabin already selected (NEXT enabled) -> clicking NEXT");
                clickNextRobust();
                return NextStep.EXPERIENCE;
            }

            //Traer CTAs
            List<WebElement> allCtas = Waits.waitForVisibilityOfElements(cabinTypeButton);
            Assert.assertFalse(allCtas.isEmpty(), "ERROR: No cabin options found");

            out.println(allCtas.size() + " cabin CTA(s) found");

            //Si hay 1 solo, intentamos click si es button y avanzar
            if (allCtas.size() == 1) {
                out.println("Only one cabin CTA available");

                clickIfButton(allCtas.get(0));

                Assert.assertTrue(isNextEnabled(), "NEXT is disabled after selecting the only cabin");
                clickNextRobust();
                return NextStep.EXPERIENCE;
            }

            //Excluir la última (anti-yacht)
            List<WebElement> candidates = new ArrayList<>(allCtas);
            candidates.remove(candidates.size() - 1);

            Assert.assertFalse(candidates.isEmpty(),
                    "ERROR: No candidates left after excluding last cabin");

            //Intentar hasta 3 opciones diferentes
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

                // Usa tu método ya existente
                clickIfButton(chosen);

                // popups pueden salir después del click
                closeGenericPopup();
                Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

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
