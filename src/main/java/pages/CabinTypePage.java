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

public class CabinTypePage extends BasePage {

    private final By selectCabinButton = By.cssSelector("button[automation-id^='cabin-type-selection-button-']");

    private final By yatchSelector = By.cssSelector("div.cabin-type__content__name > div > span:nth-child(2)");

    //constructor Cabin
    public CabinTypePage (){}

    //implemento metodo abstracto de clase BasePage abstracta
    @Override
    public boolean isAt() {
        return !driver.findElements(selectCabinButton).isEmpty();
    }

    //selecciona cabina
    public NextStep selectCabin(){

        closeGenericPopup();

        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {

            out.println("I'm inside selectCabin");

            //obtengo la lista de cabinas
            List<WebElement> cabintList = Waits.waitForVisibilityOfElements(selectCabinButton);

            Assert.assertFalse(ActionHelpers.isEmpty(cabintList), "ERROR: No cabin options found");

            out.println(cabintList.size() + " cabin(s) found");

            boolean isYatchSelected;

            //solo una cabina disponible, clicca solo next
            if (cabintList.size() == 1) {

                out.println("Only one cabin available");

                isYatchSelected = isYatchFromElement(cabintList.get(0));

                closeGenericPopup();

                jsClickByLocator(CommonDOM.nextButton);

                return isYatchSelected ? NextStep.CABIN_POSITION : NextStep.EXPERIENCE;
            }

            //varias cabinas disponibles
            int index = ActionHelpers.randomInt(0, cabintList.size() - 1);

            WebElement selectedButton = cabintList.get(index);

            out.println("Random cabin index selected:" + index);

            scrollToElementSmoothly(selectedButton);

            clickWithActions(selectedButton);

            isYatchSelected = isYatchFromElement(selectedButton);

            out.println("Is Yatch selected?" + isYatchSelected);

            closeGenericPopup();

            jsClickByLocator(CommonDOM.nextButton);

            return isYatchSelected ? NextStep.CABIN_POSITION : NextStep.EXPERIENCE;



        }catch (Exception e){

            out.println("ERROR: No cabin found -" + e.getMessage());

            Assert.fail("Cabin selection failed:" + e.getMessage());

            return NextStep.EXPERIENCE;
        }

    }

    private boolean isYatchFromElement (WebElement cabinElement){
        try{
            List<WebElement> yatchLabels = cabinElement.findElements(yatchSelector);

            return yatchLabels.stream().map(e->e.getText().toLowerCase()).anyMatch(text->text.contains("yacht")||text.contains("yatch"));
        }catch(Exception e){
            return false;
        }
    }
}
