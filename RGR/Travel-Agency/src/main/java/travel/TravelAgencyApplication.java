package travel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

 // Головний клас. Анотація @SpringBootApplication:
 // 1. Налаштовує Spring Boot
 // 2. Сканує всі компоненти (контролери, репозиторії)
 // 3. Запускає вбудований веб-сервер (Tomcat)

@SpringBootApplication
public class TravelAgencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelAgencyApplication.class, args);
    }
}
