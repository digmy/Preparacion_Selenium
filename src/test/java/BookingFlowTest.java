import baseTest.BaseTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;
import utils.NextStep;

public class BookingFlowTest extends BaseTests {

    @Test
    public void bookingFlow_untilCheckout() {

        // 1) HOME -> SEARCH
        HomePage home = new HomePage();
        Assert.assertTrue(home.isAt(), "Not at Home page");

        SearchPage search = home.fullSearchFlow();
        Assert.assertTrue(search.isAt(), "Not at Search page after quick search");

        // 2) SEARCH -> OFFERS (tu método no retorna OffersPage, así que instanciamos y validamos)
        search.selectCruiseCard();

        OffersPage offers = new OffersPage();
        Assert.assertTrue(offers.isAt(), "Not at Offers page");

        // 3) OFFERS en adelante controlado por enum
        NextStep step = offers.selectOffer(); // CABIN_TYPE

        while (step != NextStep.CHECKOUT) {

            switch (step) {

                case CABIN_TYPE -> {
                    CabinTypePage cabinType = new CabinTypePage();
                    Assert.assertTrue(cabinType.isAt(), "Not at Cabin Type page");
                    step = cabinType.selectCabin();
                }

                case EXPERIENCE -> {
                    ExperiencePage exp = new ExperiencePage();
                    Assert.assertTrue(exp.isAt(), "Not at Experience page");
                    step = exp.selectExperience();
                }

                case CABIN_POSITION -> {
                    CabinPositionPage pos = new CabinPositionPage();
                    Assert.assertTrue(pos.isAt(), "Not at Cabin Position page");
                    step = pos.selectCabinPosition();
                }

                case CABIN_NUMBER -> {
                    CabinNumberPage num = new CabinNumberPage();
                    Assert.assertTrue(num.isAt(), "Not at Cabin Number page");
                    step = num.selectCabinNumber();
                }

                case PASSENGER_FORM -> {
                    PassengerFormPage pax = new PassengerFormPage();
                    Assert.assertTrue(pax.isAt(), "Not at Passenger Form page");
                    step = pax.compileGuestFrom();
                }

                case INSURANCE -> {
                    InsurancePage ins = new InsurancePage();
                    Assert.assertTrue(ins.isAt(), "Not at Insurance page");
                    step = ins.continueWithMandatoryInsurance();
                }

                default -> Assert.fail("Unexpected step: " + step);
            }
        }

        // Llegamos a CHECKOUT
        Assert.assertTrue(true, "Reached CHECKOUT");
    }
}
