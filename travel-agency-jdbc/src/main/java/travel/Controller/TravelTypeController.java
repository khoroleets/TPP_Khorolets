package travel.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import travel.Model.TravelType;
import travel.Repository.TravelTypeRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/travel-types")
public class TravelTypeController {

    private final TravelTypeRepository travelTypeRepository;

    // Інжектуємо репозиторій через конструктор
    public TravelTypeController(TravelTypeRepository travelTypeRepository) {
        this.travelTypeRepository = travelTypeRepository;
    }

    // Отримати список всіх типів подорожей
    @GetMapping
    public ResponseEntity<List<TravelType>> getAllTravelTypes() {
        return ResponseEntity.ok(travelTypeRepository.findAll());
    }
}