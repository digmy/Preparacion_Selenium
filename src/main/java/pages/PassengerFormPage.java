package pages;

import core.BasePage;
import dom.CommonDOM;
import org.openqa.selenium.By;
import org.testng.Assert;
import utils.ActionHelpers;
import utils.NextStep;
import utils.PassengerType;
import utils.Waits;

import static java.lang.System.out;

public class PassengerFormPage extends BasePage {

    private final By firstNamePassenger1 = By.cssSelector("#FirstName_1_1"); //nombre del pasajero 1
    private final By lastamePassenger1 = By.cssSelector("#LastName_1_1"); //nombre del pasajero 1
    private final By birthDayPassenger1 = By.cssSelector("#DateOfBirth_1_1"); //fecha de nacimiento del pasajero 1
    private final By genderPassenger1 = By.cssSelector("#Gender_1_1"); //sexo del pasajero 1
    private final By countryPassenger1 = By.cssSelector("#CountryOfResidence_1_1");//nombre del pasajero 1

    private final By email = By.cssSelector("#Email__1_1");
    private final By confirmEmail = By.cssSelector("#ConfirmEmail_1_1");
    private final By phoneNumber = By.cssSelector("#PhoneNumber_1_1");

    private final By firstNamePassenger2 = By.cssSelector("#FirstName_2_1"); //nombre del pasajero 2
    private final By lastamePassenger2 = By.cssSelector("#LastName_2_1"); //nombre del pasajero 2
    private final By birthDayPassenger2 = By.cssSelector("#DateOfBirth_2_1"); //fecha de nacimiento del pasajero 2
    private final By genderPassenger2 = By.cssSelector("#Gender_2_1"); //sexo del pasajero 2
    private final By countryPassenger2 = By.cssSelector("#CountryOfResidence_2_1");//nombre del pasajero 2

    private final By documentPassenger1Dropdown = By.cssSelector("#DocumentType_1_1"); //dropdown para seleccionar el tipo de documento del pasajero 1
    private final By documentPassenger2Dropdown = By.cssSelector("#DocumentType_2_1"); //dropdown para seleccionar el tipo de documento del pasajero 2
    private final By ciNumberPassenger1 = By.cssSelector("#DocumentNumber_1_1"); //input para inserir el número de carta de identidad del pasajero 1
    private final By ciNumberPassenger2 = By.cssSelector("#DocumentNumber_2_1"); //input para inserir el número de carta de identidad del pasajero 2
    private final By ciExpiryDatePassenger1 = By.cssSelector("#DateOfExpiry_1_1"); //input para inserir la fecha de vencimiento de carta de identidad del pasajero 1
    private final By ciExpiryDatePassenger2 = By.cssSelector("#DateOfExpiry_2_1"); //input para inserir la fecha de vencimiento de carta de identidad del pasajero 2

    private final By emergencyContact = By.cssSelector("#EmergencyName_1"); //input para inserir el nombre del contacto de emergencia
    private final By emergencyPhone = By.cssSelector("#EmergencyNumber_1"); //input para inserir el número del contacto de emergencia


    public PassengerFormPage(){}

    @Override
    public boolean isAt() {
        return !driver.findElements(firstNamePassenger1).isEmpty();
    }

    public NextStep compileGuestFrom(){

        closeGenericPopup();
        Waits.waitUntilLoaderDisappear(CommonDOM.shipLoader);

        try{
            //email unico para la reserva
            String bookingEmail = ActionHelpers.fakeEmail();

            fillPassenger1(bookingEmail);
            fillPassenger2();
            fillDocumentsIfPresent();
            fillEmergencyContactIfPresent();

            clickNextRobust();

            return NextStep.INSURANCE;

        } catch (Exception e){

            out.println("ERROR: Form compilation failed -" + e.getMessage());
            Assert.fail("Form compilation failed:" + e.getMessage());

            return NextStep.INSURANCE;
        }

    }

    //escribe los datos que pertenecen al primer pasajero
    public void fillPassenger1(String bookingEmail){
        type(firstNamePassenger1, ActionHelpers.fakeFirstName());
        type(lastamePassenger1, ActionHelpers.fakeLastName());
        type(birthDayPassenger1, ActionHelpers.generateBirthday(PassengerType.ADULT));

        clickRandom(genderPassenger1);
        clickRandom(countryPassenger1);

        type(email, bookingEmail);
        type(confirmEmail, bookingEmail);
        type(phoneNumber, ActionHelpers.fakePhone());

    }

    //escribe los datos que pertenecen al segundo pasajero
    public void fillPassenger2(){
        type(firstNamePassenger2, ActionHelpers.fakeFirstName());
        type(lastamePassenger2, ActionHelpers.fakeLastName());
        type(birthDayPassenger2, ActionHelpers.generateBirthday(PassengerType.ADULT));

        clickRandom(genderPassenger2);
        clickRandom(countryPassenger2);

    }

    //escribe los datos que pertenecen al contacto de emergencia
    public boolean fillEmergencyContactIfPresent(){
        try {
            if(!driver.findElements(emergencyContact).isEmpty()
                    && driver.findElement(emergencyContact).isDisplayed()){

                type(emergencyContact, ActionHelpers.fakeFirstName());
                type(emergencyPhone, ActionHelpers.fakePhone());

                return true;
            }

            out.println("Emergency contact not present, skipping");
            return false;

        }catch (Exception e){
            out.println("ERROR: while filling emergency contact -" + e.getMessage());
            Assert.fail("Emergency contact fill failed:" + e.getMessage());

            return false;
        }

    }

    public void fillDocumentsIfPresent() {
        if (!isVisible(documentPassenger1Dropdown)) {
            out.println("Documents section not present -> skipping documents");
            return;
        }

        out.println("Documents section present -> filling documents");

        clickRandom(documentPassenger1Dropdown);
        type(ciNumberPassenger1, ActionHelpers.randomString(7));
        type(ciExpiryDatePassenger1, ActionHelpers.futureDate());

        if (isVisible(documentPassenger2Dropdown)) {
            clickRandom(documentPassenger2Dropdown);
            type(ciNumberPassenger2, ActionHelpers.randomString(7));
            type(ciExpiryDatePassenger2, ActionHelpers.futureDate());
        }
    }
}
