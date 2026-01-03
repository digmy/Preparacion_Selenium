package controller;

import org.testng.Assert;
import pages.*;
import utils.NextStep;

public class BookingFlow {
    public void runFromOffersToPassengersForm(){

        NextStep step = new OffersPage().selectOffer();

        while(step != NextStep.PASSENGER_FORM){
            switch (step){
                case CABIN_TYPE -> step = new CabinTypePage().selectCabin();
                case EXPERIENCE -> step = new ExperiencePage().selectExperience();
                case CABIN_POSITION -> step = new CabinPositionPage().selectCabinPosition();
                case CABIN_NUMBER -> step = new CabinNumberPage().selectCabinNumber();
                default -> throw new IllegalStateException("Enexpected step:" + step);
            }
        }
        Assert.assertTrue(new PassengerFormPage().isAt());
    }
}
