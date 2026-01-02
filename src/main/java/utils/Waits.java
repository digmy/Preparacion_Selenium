package utils;

import driver.DatadriverFactory;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.Logger;

//evito duplicar WebDriverWait
public class Waits {

    private static int TIMEOUT_SECONDS = 10;
    //ayuda a debuggear
    private static final Logger logger = LogManager.getLogger(Waits.class);

    // constructor privado en clase utilitaria
    private Waits() {}

    private static WebDriverWait getWait(){
        return new WebDriverWait(
                DatadriverFactory.getDriver(),
                Duration.ofSeconds(TIMEOUT_SECONDS)
        );

    }
    //espera a que el elemento sea visible por localizador
    public static WebElement waitForVisibility(By locator){
        logger.info("Esperando visibilidad del elemento: " + locator);
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    //espera a que el elemento sea visible por elemento
    public static WebElement waitForVisibilityOfElement(WebElement element){
        logger.info("Esperando visibilidad del elemento WebElement");
        return getWait().until(ExpectedConditions.visibilityOf(element));
    }

    //espera a que los elementos sean visibles por localizador
    public static List<WebElement> waitForVisibilityOfElements(By locator){
        logger.info("Esperando visibilidad del elemento: " + locator);
        return getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    //espera a que los elementos sean visibles por lista de elementos
    public static List<WebElement> waitForVisibilityOfAll(WebElement element){
        logger.info("Esperando visibilidad del elemento WebElement");
        return getWait().until(ExpectedConditions.visibilityOfAllElements(element));
    }

    //espera a que el elemento por su localizador sea clickable
    public static WebElement waitForClickableByLocator(By locator){
        logger.info("Esperando visibilidad del elemento: " + locator);
        return getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    //espera a que el elemento sea clickable
    public static WebElement waitForClickableByElement(WebElement element){
        logger.info("Esperando visibilidad del elemento WebElement");
        return getWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    //espera que el loader desaparezca
    public static void waitUntilLoaderDisappear(By locator){
        logger.info("Esperando visibilidad del elemento: " + locator);
        getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

}
/*Qué decir en entrevista sobre esta clase (MEMORIZÁ)

“Centralizo los waits explícitos en una clase utilitaria.
Cada método crea su propio WebDriverWait usando el driver actual del ThreadLocal, lo que evita problemas en ejecución paralela.
Así evito usar Thread.sleep y mantengo los Page Objects limpios.”*/
