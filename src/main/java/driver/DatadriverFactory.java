package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/*clase estatica que no debe ser instanciada, administra WebDrivers por Thread(paralelismo,
crea drivers según el browser y configura opciones)*/
public class DatadriverFactory {

    /*ThreadLocal lo usé para que cada test tenga su propia instancia de WebDriver y evitar conflictos si en algún
    momento los tests se ejecutan en paralelo. Aunque ahora los corro secuenciales, deja el framework
    preparado.*/
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // private constructor: no quiero que instancien esta clase (utilitaria)
    private DatadriverFactory() {}

    //obtiene el driver actual
    public static WebDriver getDriver(){
        return driver.get();
    }

    //Obtiene el driver actual
    public static void initDriver(String browser) {
        if (browser == null || browser.trim().isEmpty()) {
            browser = "chrome";
        }

        switch (browser.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver.set(new FirefoxDriver());
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                driver.set(new ChromeDriver());
                break;
        }
    }

    //cierra y limpia el driver
    public static void quitDriver(){
        WebDriver wd = driver.get();
        if (wd != null){
            wd.quit();
            driver.remove(); //evita memory leaks en paralelismo
        }
    }
}

/*Pregunta 1: “¿Por qué no usas una variable WebDriver normal?”

Respuesta junior correcta: “Para evitar problemas si más adelante se ejecutan tests en paralelo y cada test necesita su propio driver.”

Pregunta 2: “¿Esto lo usas ya en paralelo?”

Respuesta honesta y correcta: “No, hoy lo uso en ejecución secuencial, pero lo dejé preparado.”

Pregunta 3: “¿Qué pasa si no uso ThreadLocal?”

Respuesta: “Funciona igual en ejecución secuencial, pero en paralelo podrían pisarse los drivers.”
*/
