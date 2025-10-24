package hello;

import org.springframework.web.bind.annotation.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

@RestController
public class CalculatorController {

    @GetMapping("/")
    public String index() {
        return """
        <!DOCTYPE html>
        <html lang="uk">
        <head><meta charset="UTF-8"><title>Калькулятор</title></head>
        <body>
        <h1>Онлайн-калькулятор</h1>
        <form action="/calc" method="post">
            <input type="text" name="expression" placeholder="Введіть вираз (напр. 2+2*3)">
            <button type="submit">Обчислити</button>
        </form>
        </body>
        </html>
        """;
    }

    @PostMapping("/calc")
    public String calc(@RequestParam("expression") String expression) {
        try {
            Expression e = new ExpressionBuilder(expression).build();
            double result = e.evaluate();
            return "Результат: " + result;
        } catch (Exception ex) {
            return "Помилка у виразі: " + ex.getMessage();
        }
    }
}
