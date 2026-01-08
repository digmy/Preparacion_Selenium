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

    private final By cabinTypeAny = By.cssSelector(
            "button[automation-id^='cabin-type-selection-button-'], " +
                    "span[automation-id^='cabin-type-selection-button-']"
    );

    private final By cabinTypeButtons = By.cssSelector(
            "button[automation-id^='cabin-type-selection-button-']"
    );

    public CabinTypePage() { super(); }

    @Override
    public boolean isAt() {
        return !driver.findElements(cabinTypeAny).isEmpty();
    }

    //NO asumo Experience: detecta a qué página llegaste
    private NextStep detectNextAfterCabinType() {
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        if (new ExperiencePage().isAt()) {
            out.println("Reached Experience page");
            return NextStep.EXPERIENCE;
        }

        if (new CabinPositionPage().isAt()) {
            out.println("Reached Cabin Position page");
            return NextStep.CABIN_POSITION;
        }

        Assert.fail("After NEXT from Cabin Type, did not reach Experience nor Cabin Position");
        return NextStep.EXPERIENCE;
    }

    public NextStep selectCabin() {

        out.println("I'm inside Cabin Type step");

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {

            // Traer SOLO buttons clicables
            List<WebElement> allButtons = Waits.waitForVisibilityOfElements(cabinTypeButtons);
            Assert.assertFalse(allButtons.isEmpty(), "ERROR: No cabin BUTTON options found");

            //Filtrar Yacht por texto (en lower)
            List<WebElement> candidates = new ArrayList<>();
            for (WebElement b : allButtons) {
                String t = (b.getText() == null ? "" : b.getText()).toLowerCase();

                if (t.contains("yacht")) {
                    out.println("Skipping Yacht option: " + t);
                    continue;
                }
                candidates.add(b);
            }
            Assert.assertFalse(
                    candidates.isEmpty(),
                    "ERROR: All cabin options were filtered out (only Yacht available)"
            );

            // Si NEXT ya está habilitado, validar que el preseleccionado NO sea yacht
            if (isNextEnabled()) {
                out.println("NEXT enabled on entry, verifying selected cabin is not yacht");

                boolean selectedIsYacht = allButtons.stream().anyMatch(b -> {
                    String cls = b.getAttribute("class");
                    String txt = (b.getText() == null ? "" : b.getText()).toLowerCase();
                    return cls != null && cls.contains("checked") && txt.contains("yacht");
                });

                if (!selectedIsYacht) {
                    out.println("Preselected cabin is NOT Yacht, clicking NEXT");
                    clickNextRobust();
                    return detectNextAfterCabinType();
                }

                out.println("Preselected cabin looks like Yacht, forcing a non-yacht selection");
                // sigue abajo para seleccionar manualmente
            }

            // Si solo queda 1 candidata (no-yacht)
            if (candidates.size() == 1) {
                WebElement only = candidates.get(0);
                scrollToElementSmoothly(only);
                clickIfButton(only);

                waitUntilNextEnabled(); // tu método en BasePage
                Assert.assertTrue(isNextEnabled(), "NEXT is disabled after selecting the only (no-yacht) cabin");

                clickNextRobust();
                return detectNextAfterCabinType();
            }

            int attempts = Math.min(3, candidates.size());

            for (int i = 0; i < attempts; i++) {

                closeGenericPopup();
                Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

                int index = ActionHelpers.randomInt(0, candidates.size() - 1);
                WebElement chosen = candidates.get(index);

                out.println("Attempt " + (i + 1) + ": selecting cabin index " + index);

                scrollToElementSmoothly(chosen);
                clickIfButton(chosen);

                closeGenericPopup();
                Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

                waitUntilNextEnabled();

                if (isNextEnabled()) {
                    out.println("NEXT enabled, clicking NEXT");
                    clickNextRobust();
                    return detectNextAfterCabinType();
                }

                out.println("NEXT still disabled, trying another cabin");
                candidates.remove(index);
                if (candidates.isEmpty()) break;
            }

            Assert.fail("Could not enable NEXT after trying cabin selections (yacht excluded)");
            return NextStep.EXPERIENCE;

        } catch (Exception e) {
            out.println("ERROR: Cabin selection failed - " + e.getMessage());
            Assert.fail("Cabin selection failed: " + e.getMessage());
            return NextStep.EXPERIENCE;
        }
    }
}
