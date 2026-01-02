package Base_Test;

import Utils.Config_Reader;
import Driver.Datadriver_Factory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

//abstracta como todas las clases base
public abstract class Base_Tests {

    // protected: accesible por clases hijas (tus tests)
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        String browser = Config_Reader.get("browser");
        String url = Config_Reader.get("url");

        Datadriver_Factory.initDriver(browser);
        driver = Datadriver_Factory.getDriver();

        // waits base
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();

        driver.get(url);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        Datadriver_Factory.quitDriver();
    }

}
/* Porque uso espera implicita rapida:
Evita fallos inmediatos..Sin implícita, el test falla apenas no encuentra el elemento.

Reduce código repetido..No escribís waits en cada findElement.

Hace el framework más tolerante..Ideal para sitios reales con pequeñas demoras (como MSC).

Por qué NO usás SOLO espera explícita en BaseTest
Porque la explícita:

Espera un elemento específico

Requiere saber qué esperar

Pertenece más a la lógica de la página "Base_Page", no al setup del test*/