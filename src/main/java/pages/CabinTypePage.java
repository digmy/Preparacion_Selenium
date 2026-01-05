package pages;

import core.BasePage;
import dom.CommonDOM;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.ActionHelpers;
import utils.NextStep;


import utils.Waits;import java.util.List;

import static java.lang.System.out;

public class CabinTypePage extends BasePage {

    // CTA puede ser button o span (preseleccionado)
    private final By cabinTypeButton = By.cssSelector("button[automation-id^='cabin-type-selection-button-'], " +
                    "span[automation-id^='cabin-type-selection-button-']"
    );

    public CabinTypePage() { super(); }

    @Override
    public boolean isAt() {
        return !driver.findElements(cabinTypeButton).isEmpty();
    }

    public NextStep selectCabin() {

        out.println("I'm inside Cabin Type step (SKIP-LAST mode)");

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {
            List<WebElement> ctas = Waits.waitForVisibilityOfElements(cabinTypeButton);
            Assert.assertFalse(ctas.isEmpty(), "ERROR: No cabin options found");

            int size = ctas.size();
            out.println(size + " cabin CTA(s) found");

            // Si solo hay 1, ya está seleccionado normalmente → NEXT
            if (size == 1) {
                out.println("Only one cabin type available -> clicking NEXT");
                clickNextRobust();
                return NextStep.EXPERIENCE; // asumiendo no-yacht por regla negocio, si aquí puede ser yacht avísame
            }

            //nunca seleccionar la última opción que casi siempre es yatch
            int maxIndex = size - 2; // última excluida
            int index = (maxIndex == 0) ? 0 : ActionHelpers.randomInt(0, maxIndex);

            out.println("Selecting cabin index: " + index + " (last excluded: " + (size - 1) + ")");

            WebElement chosen = ctas.get(index);
            scrollToElementSmoothly(chosen);

            // Click solo si es button y no está checked
            String tag = chosen.getTagName().toLowerCase();
            String clazz = chosen.getAttribute("class");
            boolean alreadySelected = (clazz != null && clazz.contains("checked")) || "span".equals(tag);

            if ("button".equals(tag) && !alreadySelected) {
                clickWithActions(chosen);
                out.println("Cabin clicked");
            } else {
                out.println("Cabin already selected (span/checked) -> skipping click");
            }

            clickNextRobust();
            return NextStep.EXPERIENCE;

        } catch (Exception e) {
            out.println("ERROR: Cabin selection failed - " + e.getMessage());
            Assert.fail("Cabin selection failed: " + e.getMessage());
            return NextStep.EXPERIENCE;
        }
    }
}
