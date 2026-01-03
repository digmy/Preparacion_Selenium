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

public class CabinNumberPage extends BasePage {

    //devuelve el primer número de cabina por default ya marcado
    public static final By cabinNumberButton = By.cssSelector("a.swiper-slide");

    //constructor
    public CabinNumberPage(){}

    //implemento metodo abstracto de clase BasePage abstracta
    @Override
    public boolean isAt() {
        return !driver.findElements(cabinNumberButton).isEmpty();
    }

    public NextStep selectCabinNumber(){

        out.println("I'm inside Cabin Number step");

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {
            //obtengo la lista de cabinas
            List<WebElement> cabinNumberCtasList = Waits.waitForVisibilityOfElements(cabinNumberButton);

            Assert.assertFalse(ActionHelpers.isEmpty(cabinNumberCtasList), "ERROR: No cabin number options found");

            out.println(cabinNumberCtasList.size() + " cabin(s) number found");

            //Elegir CTA: si hay 1, ese; si hay varios, random
            WebElement cta = (cabinNumberCtasList.size() == 1)
                    ? cabinNumberCtasList.get(0)
                    : cabinNumberCtasList.get(ActionHelpers.randomInt(0, cabinNumberCtasList.size() - 1));

            scrollToElementSmoothly(cta);
            clickWithActions(cta);
            clickNextRobust();

            return NextStep.PASSENGER_FORM;

        } catch (Exception e) {
            out.println("ERROR: No cabin position found -" + e.getMessage());

            Assert.fail("Cabin position selection failed:" + e.getMessage());

            return NextStep.PASSENGER_FORM;
        }

    }
}
