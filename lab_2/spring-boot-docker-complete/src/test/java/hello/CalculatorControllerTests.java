package hello;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalculatorControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testSuccess() {
    	ResponseEntity<String> response = restTemplate.postForEntity(
    		    "/calc",
    		    new LinkedMultiValueMap<String, String>() {{ add("expression", "1+1"); }},
    		    String.class
    		);
        assertEquals("Результат: 2.0", response.getBody());
    }

    /*
    @Test
    public void testFailure() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/calc?expression=5-1",
            null, // <-- тіло запиту відсутнє
            String.class
        );
        assertEquals("Результат: 2.0", response.getBody()); // Цей тест має падати
    }
    */
}
