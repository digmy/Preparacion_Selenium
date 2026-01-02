package utils;

import com.github.javafaker.Faker;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class ActionHelpers {
    static final Random random = new Random();
    private static final Faker faker = new Faker();
    private static final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // =============Fechas===========

    // Genera una fecha en formato yyyy-MM-dd (o cualquier formato)
    public static String getDatePlusDays(int days, String pattern) {
        LocalDate date = LocalDate.now().plusDays(days);
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    // Fecha hoy por defecto
    public static String getToday(String pattern) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    // Fecha aleatoria entre un rango de días
    public static String getRandomDateBetween(int minDays, int maxDays, String pattern) {
        int days = random.nextInt(maxDays - minDays + 1) + minDays;
        return getDatePlusDays(days, pattern);
    }

    // Genera una fecha en formato dd/MM/yyyy pero del día corrente más 5 años
    public static String futureDate() {
        LocalDate futureDay = LocalDate.now().plusYears(5);

        return futureDay.format(formato);
    }

    // ===========Email / Strings=============

    // Email fake único usando faker
    public static String fakeEmail() {
        return faker.internet().emailAddress();
    }

    // Email fake con dominio específico
    public static String fakeEmail(String domain) {
        return faker.name().username() + "@" + domain;
    }

    // Email fake con dominio yopmail
    public static String fakeYopmailEmail() {
        return faker.name().username() + "@" + "yopmail.com";
    }

    // String aleatorio (útil para nombres o campos libres)
    public static String randomString(int length) {
        return faker.lorem().characters(length);
    }

    // ===========Datos fake persona=============

    public static String fakeFirstName() {
        return faker.name().firstName();
    }

    public static String fakeLastName() {
        return faker.name().lastName();
    }

    //permite agregar 9digitos sin formatos que puedan afectar el test
    public static String fakePhone() {
        return faker.number().digits(9);
    }

    public static String fakeAddress() {
        return faker.address().streetAddress();
    }

    public static String fakeCity() {
        return faker.address().city();
    }

    //Fecha de cumpleaños aleatoria
    public static String generateBirthday(int minAge, int maxAge) {

        // Elegir edad aleatoria
        int age = faker.number().numberBetween(minAge, maxAge);

        // Punto de partida: fecha de hoy menos la edad
        LocalDate baseDate = LocalDate.now().minusYears(age);

        // Elegimos un día aleatorio dentro del año válido
        LocalDate birthday = baseDate.withDayOfYear(faker.number().numberBetween(1, baseDate.lengthOfYear()));

        return birthday.format(formato);
    }

    //Método que recibe el enum
    public static String generateBirthday(PassengerType type){
        return generateBirthday(type.minAge, type.maxAge);
    }

    // ===========Números Random=============

    // Número aleatorio entre min y max
    public static int randomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    // ============Random Dropdowns / Listas============

    // Selección random de una lista de elementos ( <li>, <button>, <div>, etc.)
    public static void clickRandomElement(List<WebElement> elements) {
        if (elements == null || elements.isEmpty()) return;
        WebElement element = elements.get(random.nextInt(elements.size()));
        element.click();
    }

    // Seleccionar opción random por texto si necesitas devolver el valor
    public static String getRandomTextFromElements(List<WebElement> elements) {
        if (elements == null || elements.isEmpty()) return null;
        WebElement element = elements.get(random.nextInt(elements.size()));
        return element.getText().trim();
    }

    //===========Boolean Random============

    // Verdadero/Falso aleatorio
    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    //===========Vacío============
    public static boolean isEmpty(List<WebElement> elements){
        return elements == null || elements.isEmpty();
    }


}
