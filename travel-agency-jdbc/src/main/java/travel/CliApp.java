package travel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Component
public class CliApp implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n\n--- ЗАПУСК КОНСОЛЬНОГО ДОДАТКУ ---");
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine();

                if ("exit".equalsIgnoreCase(line)) {
                    System.out.println("--- ЗАВЕРШЕННЯ КОНСОЛЬНОГО ДОДАТКУ ---");
                    break;
                }
                
                try {
                    executeCommand(line);
                } catch (Exception e) {
                    System.err.println("Помилка: " + e.getMessage());
                    // e.printStackTrace(); 
                }
            }
        }
    }

    private void executeCommand(String line) throws Exception {
        if (line == null || line.trim().isEmpty()) {
            return;
        }

        String command;
        String table;
        String paramsString;
        
        try {
            command = line.substring(0, line.indexOf(" ")).trim().toLowerCase();
            table = line.substring(line.indexOf(" ") + 1, line.indexOf("(")).trim();
            paramsString = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();
        } catch (Exception e) {
            throw new Exception("Невірний формат команди. Очікується: command table(...)");
        }

        Map<String, Object> params = parseParams(paramsString);

        switch (command) {
            case "insert":
                handleInsert(table, params);
                break;
            case "read":
                handleRead(table, params);
                break;
            case "update":
                handleUpdate(table, params);
                break;
            case "delete":
                handleDelete(table, params);
                break;
            default:
                System.err.println("Невідома команда: " + command);
        }
    }

    /**
     * Парсинг
     */
    private Object autoParseValue(String value) {
        // 1. Спроба спарсити як Boolean
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;

        // 2. Спроба спарсити як Integer
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e1) { /* не int */ }
        
        // 3. Спроба спарсити як Double
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e2) { /* не double */ }
        
        // 4. Спроба спарсити як Дату (LocalDate)
        // (Проста перевірка, щоб не намагатися парсити "Тест" як дату)
        if (value.contains("-") && value.length() == 10) {
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e3) { /* не дата */ }
        }

        // 5. Якщо нічого не вийшло - це String
        return value; 
    }

    /**
     * Парсер, повертає Map<String, Object>
     */
    private Map<String, Object> parseParams(String paramsString) throws Exception {
        Map<String, Object> params = new LinkedHashMap<>();
        if (paramsString == null || paramsString.isEmpty()) {
            return params;
        }
        
        // 1️. Користувач вводить команду, наприклад:
        //     insert country(name="SELECT * FROM offer WHERE title = '' OR '1'='1';", iso_code=US)
        //    Текст між дужками (...) зберігається у paramsString.
        //
        // 2️. Ми розбиваємо цей текст за комами, щоб отримати пари параметрів:
        //     ["name=\"SELECT * FROM offer WHERE title = '' OR '1'='1';\"", " iso_code=US"]
        //
        // 3️. Далі кожну пару розбиваємо по знаку '=':
        //     pair.split("=")
        //
        //    Але якщо у значенні є додатковий знак '=' усередині лапок,
        //    наприклад у SQL-рядках, це може створити більше ніж 2 частини
        //    і викликати помилку "Очікується key=value".
        //
        // 4️. Якщо split() не повернув рівно дві частини (key і value),
        //     ми викидаємо помилку формату.
        //
        // 5️. Якщо формат правильний, видаляємо зайві пробіли й викликаємо
        //     autoParseValue(valueString) — щоб автоматично визначити тип:
        //     число, дата, логічне або рядок.
        //
        // 6️. У кінці додаємо пару key -> value до колекції params.
        //
        // 7️. Метод повертає готову Map<String, Object> для подальшого
        //     використання у SQL-командах (insert, update, тощо).

        String[] pairs = paramsString.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length != 2) {
                throw new Exception("Невірний формат параметра: '" + pair + "'. Очікується 'key=value'.");
            }
            String key = kv[0].trim();
            String valueString = kv[1].trim();
            
            params.put(key, autoParseValue(valueString));
        }
        return params;
    }

    // --- Методи handleInsert, handleRead, handleUpdate, handleDelete ---
    // --- Вони вже коректно працюють з Map<String, Object> ---

    private void handleInsert(String table, Map<String, Object> params) throws Exception {
        if (params.isEmpty()) {
            throw new Exception("Insert вимагає принаймні один параметр.");
        }

        String columns = String.join(", ", params.keySet());
        String placeholders = String.join(", ", Collections.nCopies(params.size(), "?"));
        String sql = "INSERT INTO " + table + " (" + columns + ") VALUES (" + placeholders + ")";

        Object[] args = params.values().toArray();
        
        int rows = jdbcTemplate.update(sql, args);
        System.out.println("OK. Додано рядків: " + rows);
    }

    private void handleRead(String table, Map<String, Object> params) {
        String sql;
        Object[] args;

        if (params.isEmpty()) {
            sql = "SELECT * FROM " + table;
            args = new Object[0];
        } else if (params.containsKey("id") && params.size() == 1) {
            Object idObj = params.get("id");
            if (!(idObj instanceof Integer)) {
                System.err.println("Помилка: ID '" + idObj + "' має бути цілим числом.");
                return;
            }
            sql = "SELECT * FROM " + table + " WHERE id = ?";
            args = new Object[]{idObj}; 
        } else {
            System.err.println("Read підтримує лише 'read table()' або 'read table(id=...)'");
            return;
        }

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, args);
        
        if (results.isEmpty()) {
            System.out.println("Таблиця '" + table + "' порожня або запис не знайдено.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private void handleUpdate(String table, Map<String, Object> params) throws Exception {
        Object idObj = params.remove("id");

        if (idObj == null) {
            throw new Exception("Update вимагає параметр 'id=...'.");
        }
        if (!(idObj instanceof Integer)) {
            throw new Exception("Помилка: ID '" + idObj + "' має бути цілим числом.");
        }
        if (params.isEmpty()) {
            throw new Exception("Update вимагає хоча б один параметр для оновлення (напр., 'name=NewName').");
        }

        String setClause = params.keySet().stream()
                .map(key -> key + " = ?")
                .collect(Collectors.joining(", "));

        String sql = "UPDATE " + table + " SET " + setClause + " WHERE id = ?";

        List<Object> args = new ArrayList<>(params.values());
        args.add(idObj); 

        int rows = jdbcTemplate.update(sql, args.toArray());
        System.out.println("OK. Оновлено рядків: " + rows);
    }

    private void handleDelete(String table, Map<String, Object> params) throws Exception {
        if (!params.containsKey("id") || params.size() > 1) {
            throw new Exception("Delete вимагає рівно один параметр: 'id=...'.");
        }

        Object idObj = params.get("id");
        if (!(idObj instanceof Integer)) {
            throw new Exception("Помилка: ID '" + idObj + "' має бути цілим числом.");
        }

        String sql = "DELETE FROM " + table + " WHERE id = ?";
        int rows = jdbcTemplate.update(sql, idObj); 
        
        if (rows > 0) {
            System.out.println("OK. Видалено рядків: " + rows);
        } else {
            System.out.println("Запис з id=" + idObj + " не знайдено.");
        }
    }
}