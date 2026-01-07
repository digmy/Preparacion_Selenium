package dom;

import org.openqa.selenium.By;

public class CommonDOM {
    //botón que cierra popups
    public static final By GENERIC_CLOSE_BTN =
            By.cssSelector(".close, .btn-close, .popup-close, .fpw_text_container, #didomi-notice-agree-button, .fpw_editor_outline");

    // botón atrás
    public static final By backButton = By.cssSelector("#top-row > section > div > a");

    // botón adelante
    public static final By nextButton = By.cssSelector("section.booking-navigator [automation-id='nav-action-next']");

    //navecita
    public static final By shipLoader = By.cssSelector(".generic-loader-spinner");

    //para cuando hay una sola opcion... ya preseleccionada
    public static final By optionSelected = By.cssSelector("span.button.checked");

    //para asegurarnos que estamos en la pagina correcta
    public static final By correctPage = By.cssSelector("#a-cabins");

}
