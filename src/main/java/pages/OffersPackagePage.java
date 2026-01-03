package pages;

import core.BasePage;
import dom.CommonDOM;
import org.testng.Assert;
import utils.NextStep;
import utils.Waits;

import static java.lang.System.out;

public class OffersPackagePage extends BasePage {

    //constructor
    public OffersPackagePage(){}

    @Override
    public boolean isAt() {
        // isAt debe ser rápido y no lanzar excepción
        return false;
    }
    public NextStep continueNext() {

        out.println("I'm inside Insurance step -> just continue");

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {
            // Validación mínima antes de avanzar (opcional pero útil)
            Assert.assertTrue(isAt(), "ERROR: Not on Insurance page");

            clickNextRobust();
            return NextStep.CHECKOUT;

        } catch (Exception e) {
            Assert.fail("Insurance NEXT failed: " + e.getMessage());
            return NextStep.CHECKOUT;
        }
    }
}
