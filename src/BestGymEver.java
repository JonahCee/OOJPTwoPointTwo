import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BestGymEver {
    private static final String DataFile = "C:\\Users\\denma\\Desktop\\Lektioner\\Objektorienterat\\Inlämningsuppgifter\\Inlämning 2\\data_inlamningsuppg2.txt";
    private static final String TrainingLog = "training_log.txt";
    private static final DateTimeFormatter DateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Vänligen ange kundens namn eller personnummer:");
        String input = scanner.nextLine();

        try {
            // Lagrar och läser in kundlista (från fil)
            List<Customer> customers = readCustomersFromFile(DataFile);

            // Kontrollera om kund (inte) finns
            Optional<Customer> customer = findCustomer(customers, input);
            if (customer.isPresent()) {
                checkMembershipStatus(customer.get());
            } else {
                System.out.println("Personen finns inte i systemet och är obehörig.");
            }
        } catch (IOException e) {
            System.out.println("Fel vid läsning av fil: " + e.getMessage());
        }
    }

    // Läser in kundinformation från fil
    private static List<Customer> readCustomersFromFile(String filePath) throws IOException {
        List<Customer> customers = new ArrayList<>(); //lagra varje kund
        List<String> lines = Files.readAllLines(Paths.get(filePath)); //läser in alla rader från filen

        for (int i = 0; i < lines.size(); i += 2) {
            String[] customerInfo = lines.get(i).split(", ");
            String personNummer = customerInfo[0];
            String name = customerInfo[1];
            LocalDate lastPaymentDate = LocalDate.parse(lines.get(i + 1), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            customers.add(new Customer(personNummer, name, lastPaymentDate));
        }
        return customers;
    }

    // Hitta kund via personnummer eller namn
    private static Optional<Customer> findCustomer(List<Customer> customers, String input) {
        return customers.stream()
                .filter(c -> c.getPersonNummer().equals(input) || c.getName().equalsIgnoreCase(input))
                .findFirst();
    }

    // Kontrollera om kund är medlem eller f.d. medlem
    private static void checkMembershipStatus(Customer customer) {
        LocalDate today = LocalDate.now();
        if (customer.getLastPaymentDate().isAfter(today.minusYears(1))) {
            System.out.println("Kunden är en nuvarande medlem.");
            logTraining(customer); // Logga att kunden tränar
        } else {
            System.out.println("Kunden är en före detta kund.");
        }
    }

    // Logga träning till fil
    private static void logTraining(Customer customer) {
        LocalDateTime currentDateTime = LocalDateTime.now(); // Datum och tid för besök
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TrainingLog, true))) {
            String logEntry = String.format("%s, %s, %s", customer.getPersonNummer(), customer.getName(), currentDateTime.format(DateTimeFormat));
            writer.write(logEntry);
            writer.newLine();
            System.out.println("Träningen har loggats: " + currentDateTime.format(DateTimeFormat));
        } catch (IOException e) {
            System.out.println("Kunde inte logga träning: " + e.getMessage());
        }
    }
}




// Klass för att hantera kunddata
class Customer {
    private String personNummer;
    private String name;
    private LocalDate lastPaymentDate;

    // Konstruktor
    protected Customer(String personNummer, String name, LocalDate lastPaymentDate) {
        this.personNummer = personNummer;
        this.name = name;
        this.lastPaymentDate = lastPaymentDate;
    }

    protected String getPersonNummer() {
        return personNummer;
    }

    protected String getName() {
        return name;
    }

    protected LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }
}
