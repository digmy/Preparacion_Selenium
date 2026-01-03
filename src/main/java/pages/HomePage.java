package pages;

import core.BasePage;
import dom.CommonDOM;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utils.Waits;

import static java.lang.System.out;

public class HomePage extends BasePage {

    //destino
    public final By destinationDropdown = By.cssSelector("div[automation-id*='search-destination']");
    public final By destinationEnables = By.cssSelector("div[automation-id^='checklist-item']");

    //partenza
    public final By departureDropdown = By.cssSelector("div[automation-id*='search-departures']");
    public final By departureEnables = By.cssSelector("div.checklist.dropdown__body div.checkbox-label.checklist-item__label.checkbox-label--enabled");

    //date
    public final By dateDropdown = By.cssSelector("div[automation-id*='search-dates']");
    public final By calendarMonthDropdown = By.cssSelector("span[automation-id^='datepicker-month-']");

    //quicksearch
    public final By searchButton = By.cssSelector("div[automation-id='search-button']");

    //constructor de la clase HomePage que llama automaticamente al super
    public HomePage(){}

    //implemento metodo abstracto de clase BasePage abstracta
    @Override
    public boolean isAt() {
        return !driver.findElements(searchButton).isEmpty();
    }

    //FYI
    public SearchPage fullSearchFlow() {

        out.println("I'm inside FYI");

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try {
            Waits.waitForVisibility(searchButton);

            selectDestination();
            selectDepartureDate();
            selectDeparture();
            return quickSearch();

        } catch (Exception ex) {
            out.println("ERROR CRITIC: Cannot execute QuickSearch.");
            Assert.fail("Cannot execute the search: " + ex.getMessage());

            // Esto hace que el compilador entienda que no se continúa
            throw new RuntimeException("Search flow failed", ex);
        }
    }

    // método para búsqueda rápida
    public SearchPage quickSearch() {
        try {
            out.println("Clicking search button");
            WebElement searchBut = Waits.waitForClickableByLocator(searchButton);
            clickWithActions(searchBut);
            return new SearchPage();
        } catch (Exception e) {
            Assert.fail("QuickSearch failed: " + e.getMessage());

            throw new RuntimeException("QuickSearch failed", e);
        }
    }

    //método que permite seleccionar random un destino
    public void selectDestination (){
        out.println("I'm inside selectDestination " + "Selecting: " + destinationDropdown);
        selectOptionFromDropdown(destinationDropdown,destinationEnables,"destination");

    }

    //método que permite seleccionar random una fecha
    public void selectDepartureDate (){
        out.println("I'm inside selectDate " + "Selecting: " + departureDropdown);
        selectOptionFromDropdown(dateDropdown,calendarMonthDropdown,"date");
    }

    //método que permite seleccionar random un lugar de partida
    public void selectDeparture () {
        out.println("I'm inside selectDeparture " + "Selecting: " + departureDropdown);
        selectOptionFromDropdown(departureDropdown,departureEnables,"departure");
    }
}
