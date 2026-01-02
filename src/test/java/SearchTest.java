import baseTest.BaseTests;
import dom.CommonDOM;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.Waits;

import java.util.List;

import static java.lang.System.out;

public class SearchTest extends BaseTests {
    public static final By cruiseList = By.cssSelector("div.search-result > div"); // devuelve una lista de cruceros disponibles
    public static final By seeDetailsButton = By.cssSelector("button[id^='SelectItinerary_']");
    public static final By selectCruiseButtonNoFlight = By.cssSelector("button[id^='SelectItinerarySummary_']");
    public static final By promoRibbons = By.cssSelector(".promo-ribbon--text");

    //constructor de la clase SearchResults pasando el driver heredado de la clase BasePage
    public SearchPage(WebDriver driver){
        super(driver);
    }

    //seleccionar una cruise card sin vuelo incluido
    public void selectCruiseCard() {
        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {
            out.println("STEP 1: Loading cruise cards…");

            // Esperar lista de cruceros
            List<WebElement> cruiseList = waitForVisibilityOfElements(SearchResultsDOM.cruiseList);
            Assert.assertFalse(cruiseList.isEmpty(), "ERROR: No cruise options found.");

            out.println("STEP 2: " + cruiseList.size() + " cruises found");

            WebElement selectedCruise = null;

            // Buscar un crucero SIN “VOLI INCLUSI”
            for (WebElement cruise : cruiseList) {

                List<WebElement> ribbons = cruise.findElements(SearchResultsDOM.promoRibbons);

                boolean hasFlightsIncluded = ribbons.stream()
                        .anyMatch(rb -> rb.getText().trim().equalsIgnoreCase("VOLI INCLUSI"));

                if (!hasFlightsIncluded) {
                    selectedCruise = cruise;
                    break;
                }
            }

            Assert.assertNotNull(selectedCruise,
                    "ERROR: All available cruises contain 'VOLI INCLUSI'. No valid cruise to select.");

            out.println("STEP 3: Selecting cruise without 'VOLI INCLUSI'");

            scrollToElement(selectedCruise);

            // Hacer clic en el botón de “ver detalles”
            WebElement detailsButton = selectedCruise.findElement(SearchResultsDOM.seeDetailsButton);
            scrollToElement(detailsButton);

            try {
                clickByElement(detailsButton);
            } catch (Exception e) {
                jsClickByElement(detailsButton);
            }

            out.println("STEP 4: Waiting cruise details page…");

            // Esperar que aparezca el botón seleccionar sin vuelo
            WebElement selectButton =
                    Waits.waitForClickableByLocator(SearchResultsDOM.selectCruiseButtonNoFlight);

            scrollToElement(selectButton);

            try {
                clickByElement(selectButton);
            } catch (Exception ex) {
                jsClickByElement(selectButton);
            }

            out.println("STEP 5: Cruise selected successfully!");

        } catch (Exception e) {
            out.println("ERROR selecting cruise: " + e.getMessage());
            Assert.fail("Fatal error during cruise selection");
        }
    }
}
