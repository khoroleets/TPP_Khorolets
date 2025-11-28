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
public class RestTravelTypeController {

    private final TravelTypeRepository travelTypeRepository;

    public RestTravelTypeController(TravelTypeRepository travelTypeRepository) {
        this.travelTypeRepository = travelTypeRepository;
    }

    @GetMapping
    public ResponseEntity<List<TravelType>> getAllTravelTypes() {
        return ResponseEntity.ok(travelTypeRepository.findAll());
    }
}
