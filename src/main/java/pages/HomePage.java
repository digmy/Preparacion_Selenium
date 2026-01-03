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

    //opciones
    public final By selectOptions = By.cssSelector(".dropdown__container");

    //partenza
    public final By departureDropdown = By.cssSelector("div[automation-id*='search-departures']");
    public final By departureEnables = By.cssSelector("div.checklist.dropdown__body div.checkbox-label.checklist-item__label.checkbox-label--enabled");

    //date
    public final By dateDropdown = By.cssSelector("div[automation-id*='search-dates']");
    public final By calendarMonthDropdown = By.cssSelector("span[automation-id^='datepicker-month-']");
    public final By calendarDropdown = By.cssSelector("div.vdp-datepicker__calendar--columns-months");

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
            out.println("ERROR CRITIC: Cannot be possible execute QuickSearch neither.");
            Assert.fail("Cannot be possible execute the search");
        }

        out.println(" ENDED FULL SEARCH FLOW");
        return new SearchPage();
    }

    // método para búsqueda rápida
    public SearchPage quickSearch (){
        out.println("Clicking search button");
        WebElement searchBut = Waits.waitForClickableByLocator(searchButton);
        clickWithActions(searchBut);
        return new SearchPage();
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
