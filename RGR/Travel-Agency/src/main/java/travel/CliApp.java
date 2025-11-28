package travel;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import travel.Model.Country;
import travel.Model.Offer;
import travel.Model.TravelType;
import travel.Repository.CountryRepository;
import travel.Repository.OfferRepository;
import travel.Repository.TravelTypeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CliApp implements CommandLineRunner {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private TravelTypeRepository travelTypeRepository;

    private final Map<String, Class<?>> entityMap = Map.of(
            "country", Country.class,
            "offer", Offer.class,
            "traveltype", TravelType.class
    );

    @Override
    public void run(String... args) {
        // Просто виводимо повідомлення, що сервер працює
        System.out.println("--- Web Server Started (CLI disabled for Docker) ---");

        /*
        System.out.println("\n--- CLI APP STARTED ---");
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                // У Docker тут стається помилка, бо немає введення
                String line = scanner.nextLine().trim(); 
                if (line.isEmpty()) continue;
                if ("exit".equalsIgnoreCase(line)) break;

                try {
                    executeCommand(line);
                } catch (Exception e) {
                    System.err.println("Помилка: " + e.getMessage());
                }
            }
        }
        System.out.println("--- CLI APP FINISHED ---");
        */
    }

    private void executeCommand(String line) throws Exception {
        int spaceIndex = line.indexOf(" ");
        String command;
        String entityPart;
        String paramsPart = "";

        if (spaceIndex == -1) {
            command = line.toLowerCase();
            entityPart = "";
        } else {
            command = line.substring(0, spaceIndex).toLowerCase();
            entityPart = line.substring(spaceIndex + 1).trim();
        }

        // Отримуємо назву сутності та параметри
        String entityName;
        if (entityPart.contains("(")) {
            entityName = entityPart.substring(0, entityPart.indexOf("(")).toLowerCase();
            paramsPart = entityPart.substring(entityPart.indexOf("(") + 1, entityPart.lastIndexOf(")")).trim();
        } else {
            entityName = entityPart.toLowerCase();
        }

        Map<String, String> paramMap = parseParams(paramsPart);

        switch (command) {
            case "insert" -> handleInsert(entityName, paramMap);
            case "read" -> handleRead(entityName, paramMap);
            case "update" -> handleUpdate(entityName, paramMap);
            case "delete" -> handleDelete(entityName, paramMap);
            default -> System.out.println("Unknown command. Use: read|insert|update|delete <entity>");
        }
    }

    private Map<String, String> parseParams(String paramsString) {
        Map<String, String> params = new LinkedHashMap<>();
        if (paramsString == null || paramsString.isEmpty()) return params;

        String[] pairs = paramsString.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(kv[0].trim(), kv[1].trim().replaceAll("^\"|\"$", ""));
            }
        }
        return params;
    }

    // Додавання
    private void handleInsert(String entityName, Map<String, String> params) {
        switch (entityName) {
            case "country" -> {
                Country country = new Country(
                        null,
                        params.get("name"),
                        params.get("isoCode"),
                        LocalDateTime.now()
                );
                countryRepository.save(country);
                System.out.println("Inserted: " + country);
            }
            case "traveltype" -> {
                TravelType type = new TravelType(
                        null,
                        params.get("name"),
                        params.get("description"),
                        LocalDateTime.now()
                );
                travelTypeRepository.save(type);
                System.out.println("Inserted: " + type);
            }
            case "offer" -> {
                Offer offer = new Offer(
                        null,
                        params.get("title"),
                        params.get("description"),
                        parseInt(params.get("travelTypeId")),
                        parseInt(params.get("countryId")),
                        parseBigDecimal(params.get("price")),
                        parseDate(params.get("startDate")),
                        parseDate(params.get("endDate")),
                        parseInt(params.get("seats")),
                        parseBoolean(params.get("available")),
                        LocalDateTime.now()
                );
                offerRepository.save(offer);
                System.out.println("Inserted: " + offer);
            }
            default -> System.out.println("Unknown entity: " + entityName);
        }
    }

    // Читання
    private void handleRead(String entityName, Map<String, String> params) {
        switch (entityName) {
            case "country" -> {
                if (params.containsKey("id")) {
                    countryRepository.findById(parseInt(params.get("id"))).ifPresent(System.out::println);
                } else {
                    countryRepository.findAll().forEach(System.out::println);
                }
            }
            case "traveltype" -> {
                if (params.containsKey("id")) {
                    travelTypeRepository.findById(parseInt(params.get("id"))).ifPresent(System.out::println);
                } else {
                    travelTypeRepository.findAll().forEach(System.out::println);
                }
            }
            case "offer" -> {
                if (params.containsKey("id")) {
                    offerRepository.findById(parseInt(params.get("id"))).ifPresent(System.out::println);
                } else {
                    offerRepository.findAll().forEach(System.out::println);
                }
            }
            default -> System.out.println("Unknown entity: " + entityName);
        }
    }

    // Оновлення
    private void handleUpdate(String entityName, Map<String, String> params) {
        if (!params.containsKey("id")) {
            System.out.println("Update requires id parameter");
            return;
        }
        Integer id = parseInt(params.remove("id"));

        switch (entityName) {
            case "country" -> {
                countryRepository.findById(id).ifPresent(country -> {
                    Country updated = new Country(
                            country.id(),
                            params.getOrDefault("name", country.name()),
                            params.getOrDefault("isoCode", country.isoCode()),
                            country.createdAt()
                    );
                    countryRepository.save(updated);
                    System.out.println("Updated: " + updated);
                });
            }
            case "traveltype" -> {
                travelTypeRepository.findById(id).ifPresent(type -> {
                    TravelType updated = new TravelType(
                            type.id(),
                            params.getOrDefault("name", type.name()),
                            params.getOrDefault("description", type.description()),
                            type.createdAt()
                    );
                    travelTypeRepository.save(updated);
                    System.out.println("Updated: " + updated);
                });
            }
            case "offer" -> {
                offerRepository.findById(id).ifPresent(offer -> {
                    Offer updated = new Offer(
                            offer.id(),
                            params.getOrDefault("title", offer.title()),
                            params.getOrDefault("description", offer.description()),
                            params.containsKey("travelTypeId") ? parseInt(params.get("travelTypeId")) : offer.travelTypeId(),
                            params.containsKey("countryId") ? parseInt(params.get("countryId")) : offer.countryId(),
                            params.containsKey("price") ? parseBigDecimal(params.get("price")) : offer.price(),
                            params.containsKey("startDate") ? parseDate(params.get("startDate")) : offer.startDate(),
                            params.containsKey("endDate") ? parseDate(params.get("endDate")) : offer.endDate(),
                            params.containsKey("seats") ? parseInt(params.get("seats")) : offer.seats(),
                            params.containsKey("available") ? parseBoolean(params.get("available")) : offer.available(),
                            offer.createdAt()
                    );
                    offerRepository.save(updated);
                    System.out.println("Updated: " + updated);
                });
            }
            default -> System.out.println("Unknown entity: " + entityName);
        }
    }

    // Видалення
    private void handleDelete(String entityName, Map<String, String> params) {
        if (!params.containsKey("id")) {
            System.out.println("Delete requires id parameter");
            return;
        }
        Integer id = parseInt(params.get("id"));
        switch (entityName) {
            case "country" -> {
                countryRepository.deleteById(id);
                System.out.println("Deleted country with id=" + id);
            }
            case "traveltype" -> {
                travelTypeRepository.deleteById(id);
                System.out.println("Deleted travelType with id=" + id);
            }
            case "offer" -> {
                offerRepository.deleteById(id);
                System.out.println("Deleted offer with id=" + id);
            }
            default -> System.out.println("Unknown entity: " + entityName);
        }
    }

    // Допоміжні
    private Integer parseInt(String s) {
        return s == null ? null : Integer.parseInt(s);
    }

    private BigDecimal parseBigDecimal(String s) {
        return s == null ? null : new BigDecimal(s);
    }

    private LocalDate parseDate(String s) {
        return s == null ? null : LocalDate.parse(s);
    }

    private Boolean parseBoolean(String s) {
        return s == null ? null : Boolean.parseBoolean(s);
    }
}
