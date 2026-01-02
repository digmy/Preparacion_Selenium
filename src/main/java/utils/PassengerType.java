package utils;

public enum PassengerType {
    BABY(0,1),
    KID(2,11),
    TEEN(12,17),
    ADULT(18,90);

    public final int minAge;
    public final int maxAge;

    PassengerType(int minAge, int maxAge){
        this.minAge = minAge;
        this.maxAge = maxAge;
    }
}