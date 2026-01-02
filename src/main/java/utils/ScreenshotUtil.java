package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import driver.DatadriverFactory;

//la llamo desde un TestNG Listener cuando un test falla
public class ScreenshotUtil {

    //es una clase utilitaria que no requiere de instanciacion por eso private
    private ScreenshotUtil() {}

    //mejor que out.println
    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);

    //uso final porque es una constante
    public static final String takeScreenshot(String testName) {

        //refuerza el uso correcto del TheardLocal, ya que obtengo el driver correcto en cada caso
        WebDriver driver = DatadriverFactory.getDriver();

        //prevuene NullPointerException(control defensivo)
        if (driver == null) {
            log.error("No WebDriver instance found. Screenshot aborted.");
               return null;
        }

        try {
            // Crear nombre único
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";

            // Crear carpeta si no existe
            String directoryPath = "screenshots/fails/";
            File directory = new File(directoryPath);
            if (!directory.exists()) directory.mkdirs();

            // Definir ruta final
            String filePath = directoryPath + fileName;

            // Tomar screenshot
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(filePath);

            // Guardarlo
            Files.copy(src.toPath(), dest.toPath());

            log.info("Screenshot saved at: " + dest.getAbsolutePath());
            return dest.getAbsolutePath();

        } catch (IOException e) {
            log.error("Error saving screenshot: " + e.getMessage());
            return null;
        }
    }
}
/*
 * Toma captura de pantalla y la guarda en /screenshots/fails/
 *
 * @param testName nombre del test que falló
 * @return ruta donde se guardó la imagen
 */
