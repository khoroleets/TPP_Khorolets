package travel.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.Model.Offer;
import travel.Repository.OfferRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offers")
public class RestOfferController {

    private final OfferRepository offerRepository;

    public RestOfferController(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(offerRepository.findAll());
    }

    @GetMapping("/country/{countryId}")
    public ResponseEntity<List<Offer>> getOffersByCountry(@PathVariable Integer countryId) {
        return ResponseEntity.ok(offerRepository.findByCountryId(countryId));
    }
}
