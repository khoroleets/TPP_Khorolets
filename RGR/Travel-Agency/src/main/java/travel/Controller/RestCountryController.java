package travel.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.Model.Country;
import travel.Repository.CountryRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
public class RestCountryController {

    private final CountryRepository countryRepository;

    public RestCountryController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public ResponseEntity<List<Country>> getAllCountries() {
        return ResponseEntity.ok(countryRepository.findAll());
    }
}
