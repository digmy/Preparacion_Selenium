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
import org.openqa.selenium.support.ui.Select;
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

    //==============Clicks===========

    //click por elemento
    protected void clickByElement(WebElement element) {
        logger.info("Click en elemento: " + element);
        Waits.waitForClickableByElement(element).click();
    }

    public void clickNextRobust() {

        // Reintento simple 3 veces
        for (int i = 0; i < 3; i++) {
            try {
                closeGenericPopup();
                Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

                List<WebElement> nexts = driver.findElements(CommonDOM.nextButton);
                Assert.assertFalse(nexts.isEmpty(), "NEXT button not found in DOM");

                WebElement nextVisible = nexts.stream()
                            .filter(WebElement::isDisplayed)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("NEXT button found but none is displayed"));

                scrollToElementSmoothly(nextVisible);
                closeGenericPopup();

                try {
                    clickByElement(nextVisible);
                } catch (Exception e) {
                    jsClickByElement(nextVisible);
                }

                return; // si hizo click bien, salimos

            } catch (Exception e) {
                // si falla, reintenta el loop
                out.println("NEXT attempt " + (i + 1) + " failed: " + e.getMessage());
            }
        }

        Assert.fail("NEXT click failed after retries");
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

    //click con JS (si falla el click normal) por elemento
    protected void jsClickByElement(WebElement element) {
        logger.warn("Click por JS fallback");
        Waits.waitForVisibilityOfElement(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    //===========scroll lento================
    public void scrollToElementSmoothly(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript( "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element );
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

    //==========Selección =========
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
                throw new AssertionError("Error: No options available");
            }

            int index = ActionHelpers.randomInt(0, enableOptions.size() - 1);
            WebElement selected = enableOptions.get(index);
            out.println(description + "seleccionado: " + selected.getText());
            scrollToElementSmoothly(selected);
            clickWithActions(selected);

        } catch (Exception e) {
            out.println("ERROR selecting");
        }
    }

    protected void selectFirstAvailableOption(By selectLocator) {

        WebElement selectEl = Waits.waitForVisibility(selectLocator);
        Select select = new Select(selectEl);

        String selected = select.getFirstSelectedOption().getText().toLowerCase();

        // Si ya está seleccionado (no "Selezionare") no hacemos nada
        if (!selected.contains("selezionare")) {
            out.println("Select already has value -> skipping");
            return;
        }

        List<WebElement> options = select.getOptions();
        Assert.assertTrue(options.size() > 1, "No selectable options found");

        // opción 1 = primera válida (saltamos "Selezionare")
        select.selectByIndex(1);

        out.println("Selected option -> " + options.get(1).getText());
    }

    //=============Verificas=======================

    public boolean clickIfButton(WebElement element) {
        String tag = element.getTagName();
        String clazz = element.getAttribute("class");

        boolean alreadySelected = clazz != null && clazz.contains("checked");
        if (alreadySelected) {
            out.println("Already selected (checked) -> skipping click");
            return false;
        }

        if ("button".equalsIgnoreCase(tag)) {
            try {
                clickWithActions(element);
                out.println("Clicked (BUTTON)");
                return true;
            } catch (Exception e) {
                out.println("Click failed on BUTTON -> trying JS");
                jsClickByElement(element);
                return true;
            }
        }

        out.println("Preselected (SPAN) -> skipping click");
        return false;
    }

    public boolean isPresent(By locator){
        return !driver.findElements(locator).isEmpty();
    }

    public boolean isVisible(By locator){
        try {
            return isPresent(locator) && driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isNextEnabled() {
        try {
            List<WebElement> nexts = driver.findElements(CommonDOM.nextButton);
            if (nexts.isEmpty()) return false;

            WebElement nextVisible = nexts.stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElse(null);

            return nextVisible != null && nextVisible.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    protected void waitUntilNextEnabled() {
        try {
            Waits.waitUntil(this::isNextEnabled);
        } catch (Exception ignored) {
            out.println("NEXT did not become enabled within timeout");
        }
    }

    //Verifica que existe la pàgina
    public abstract boolean isAt();//se comporta distinto en cada pàgina


}
/*en POM los atributos en la clase basepage no pueden ser static por consecutividad ningun metodo que los
 utilice debe llevar static... un static no puede inicializarse en un constructor*/