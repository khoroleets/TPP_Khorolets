package travel.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SqlController {

    @GetMapping("/sql")
    @ResponseBody // Ця анотація каже: "Поверни цей текст прямо в браузер, не шукай HTML-файл"
    public String showSimpleMessage() {
        return "Це сторінка адміну, привіт";
    }
    
}
