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

public class InsurancePage extends BasePage {

    //selectores que utilizarè porque en el sitio de italia aparece la insurance preselccionada
    private final By mandatoryInsuranceSection = By.cssSelector("div.charges.insurance--mandatory");
    private final By mandatoryLabel = By.cssSelector(".insurance-card-mandatory-insurance-label");

    public InsurancePage(){}

    @Override
    public boolean isAt() {
        // isAt debe ser rápido y no lanzar excepción
        return !driver.findElements(mandatoryInsuranceSection).isEmpty()
                || !driver.findElements(mandatoryLabel).isEmpty();
    }

    public NextStep continueWithMandatoryInsurance() {

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
