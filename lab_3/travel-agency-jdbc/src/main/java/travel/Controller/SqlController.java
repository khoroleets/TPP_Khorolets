package travel.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller; //Використовуємо @Controller
import org.springframework.ui.Model; // 'Model' для передачі даних
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class SqlController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Метод тепер просто повертає НАЗВУ html-файлу (без .html)
     * Spring знайде "sql-form.html" у /resources/templates/
     */
    @GetMapping("/sql")
    public String showSqlForm() {
        return "sql-form"; // Просто повертаємо назву шаблону
    }

    /**
     * Цей метод тепер приймає 'Model model', щоб передати дані у шаблон.
     * Він також повертає назву шаблону "sql-result".
     */
    @PostMapping("/execute-sql")
    public String executeSql(@RequestParam String rawSql, Model model) {
        String message = "";
        List<Map<String, Object>> resultList = null;

        try {
            // Перевіряємо, чи це SELECT, чи інший запит
            String trimmedSql = rawSql.trim().toLowerCase();
            
            if (trimmedSql.startsWith("select")) {
                resultList = jdbcTemplate.queryForList(rawSql);
                message = "Запит (SELECT) виконано. Знайдено рядків: " + (resultList == null ? 0 : resultList.size());
            } else {
                // Для INSERT, UPDATE, DELETE використовуємо .update()
                int updateCount = jdbcTemplate.update(rawSql);
                message = "Запит (non-SELECT) виконано. Змінено рядків: " + updateCount;
            }

        } catch (DataAccessException e) {
            // Якщо сталася помилка SQL
            message = "Помилка SQL: " + e.getMessage();
        } catch (Exception e) {
            // Інші неочікувані помилки
            message = "Невідома помилка: " + e.getMessage();
        }

        // Кладемо наші дані в 'model', щоб Thymeleaf міг їх побачити
        model.addAttribute("message", message);
        model.addAttribute("results", resultList);

        // Повертаємо назву шаблону "sql-result.html"
        return "sql-result";
    }
}