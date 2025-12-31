package Base_Test;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class Base_Tests {

    protected WebDriver driver;

    @BeforeMethod
    public void setup(){

        driver = Datadriver_Factory.createDriver();

        driver.get("https://www.msccrociere.it/");
    }

    @AfterMethod
    public void tearDown(){

        if (driver != null){

            driver.quit();
        }
    }
}
