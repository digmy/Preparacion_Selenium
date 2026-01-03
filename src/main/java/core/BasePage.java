package core;

import dom.CommonDOM;
import driver.DatadriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import utils.ActionHelpers;
import utils.Waits;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.out;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final Actions actions;
    //ayuda a debuggear
    private static final Logger logger = LogManager.getLogger(BasePage.class);

    protected BasePage(){
        this.driver = DatadriverFactory.getDriver();
        this.actions = new Actions(driver);
    }

    //============Verifica que existe la pàgina==============
    public abstract boolean isAt();//se comporta distinto en cada pàgina

    //==============Clicks===========

    //click por elemento
    protected void clickByElement(WebElement element) {
        logger.info("Click en elemento: " + element);
        Waits.waitForClickableByElement(element).click();
    }

    //click random
    protected void clickRandom(By locator) {
        List<WebElement> list = Waits.waitForVisibilityOfElements(locator);
        Assert.assertFalse(list.isEmpty(), "ERROR: No elements to click randomly");
        WebElement randomElement = list.get(ActionHelpers.randomInt(0, list.size() - 1));
        scrollToElement(randomElement);

        try {
            clickByElement(randomElement);
        } catch (Exception e) {
            jsClickByElement(randomElement);
        }

        out.println("Random element clicked.");
    }

    //============Actions metodos=============

    //click con Actions
    public void clickWithActions(WebElement element) {
        try {
            logger.info("Click con Actions");
            actions.moveToElement(element).click().perform();
        } catch (Exception e) {
            logger.warn("Fallo actions click, usando JS");
            jsClickByElement(element);
        }
    }

    //============Escribir=============

    //escribir en input
    protected void type(By locator, String text) {
        WebElement element = Waits.waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    //=========JS operaciones============

    //scroll por elemento
    protected void scrollToElement(WebElement element) {
        logger.info("Haciendo scroll al elemento");
        Waits.waitForVisibilityOfElement(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    //click con JS (si falla el click normal) por localizador
    protected void jsClickByLocator(By locator) {
        WebElement element = Waits.waitForVisibility(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    //click con JS (si falla el click normal) por elemento
    protected void jsClickByElement(WebElement element) {
        logger.warn("Click por JS fallback");
        Waits.waitForVisibilityOfElement(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    //scroll lento
    public void scrollToElementSmoothly(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element
            );
            Thread.sleep(300);
        } catch (Exception e) {
            out.println("Error scrolling to element: " + e.getMessage());
        }
    }

    //===========Popups============

    protected void closeGenericPopup() {
        try {
            List<WebElement> popups = driver.findElements(CommonDOM.GENERIC_CLOSE_BTN);
            if (!popups.isEmpty()) {
                popups.get(0).click();
                out.println("Generic popup closed");
            }
        } catch (Exception ignored) {
        }
    }

    //==========Selección genérica=========
    protected void selectOptionFromDropdown(By dropdownLocator, By optionsLocator, String description) {
        try {
            closeGenericPopup();
            WebElement dropdown = Waits.waitForClickableByLocator(dropdownLocator);
            scrollToElementSmoothly(dropdown);
            clickWithActions(dropdown);

            List<WebElement> options = Waits.waitForVisibilityOfElements(optionsLocator);

            List<WebElement> enableOptions = options.stream().filter(option -> !option.getAttribute("class")
                    .contains("disable")).collect(Collectors.toList());

            if (enableOptions.isEmpty()) {
                throw new AssertionError("Error: No departures available");
            }

            int index = ActionHelpers.randomInt(0, enableOptions.size() - 1);
            WebElement selected = enableOptions.get(index);
            out.println(description + "seleccionado: " + selected.getText());
            scrollToElementSmoothly(selected);
            clickWithActions(selected);

        } catch (Exception e) {
            out.println("ERROR selecting departure");
        }
    }

    public boolean verifyPassengerSection(By locator) {

        try {
            out.println("Searching Passenger section");
            WebElement guestPage = Waits.waitForVisibility(locator);
            return guestPage.isDisplayed();
        } catch (Exception e) {
            out.println("Passenger section was not found");
            return false;
        }
    }

    public boolean verifyCabinNumber(By locator) {

        try {
            out.println("Searching Cabin Type section");
            WebElement cabinNumberPage = Waits.waitForVisibility(locator);
            return cabinNumberPage.isDisplayed();
        } catch (Exception e) {
            out.println("Cabin Type section was not found");
            return false;
        }
    }

    //------------Verificas--------------

    public boolean clickIfButton(WebElement element) {
        String tag = element.getTagName();
        if ("button".equalsIgnoreCase(tag)) {
            try {
                clickWithActions(element);
                out.println("Clicked (BUTTON)");
                return true;
            } catch (Exception e) {
                out.println("Click failed on BUTTON → trying JS");
                jsClickByElement(element);
                return true;
            }
        }
        out.println("Preselected (SPAN) → skipping click");
        return false;
    }
}
/*en POM los atributos en la clase basepage no pueden ser static por consecutividad ningun metodo que los
 utilice debe llevar static... un static no puede inicializarse en un constructor*/