package travel.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import travel.Model.Offer;
import travel.Repository.CountryRepository;
import travel.Repository.OfferRepository;
import travel.Repository.TravelTypeRepository;

import java.util.Optional;

@Controller
public class WebOfferController {

    private final OfferRepository offerRepository;
    private final CountryRepository countryRepository;
    private final TravelTypeRepository travelTypeRepository;

    public WebOfferController(OfferRepository offerRepository,
                              CountryRepository countryRepository,
                              TravelTypeRepository travelTypeRepository) {
        this.offerRepository = offerRepository;
        this.countryRepository = countryRepository;
        this.travelTypeRepository = travelTypeRepository;
    }

    @GetMapping("/offers")
    public String showOffers(Model model) {
        model.addAttribute("allOffers", offerRepository.findAll());
        return "offers";
    }

    @GetMapping("/admin/offers/form")
    @PreAuthorize("hasRole('ADMIN')")
    public String offerForm(@RequestParam(required = false) Integer id, Model model) {
        if (id != null) {
            Optional<Offer> offer = offerRepository.findById(id);
            model.addAttribute("offer", offer.orElse(null));
        } else {
            model.addAttribute("offer", new Offer(null, "", "", null, null, null, null, null, 0, true, null));
        }
        model.addAttribute("countries", countryRepository.findAll());
        model.addAttribute("travelTypes", travelTypeRepository.findAll());
        
        return "offer-form";
    }

    @PostMapping("/admin/offers/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveOffer(@ModelAttribute Offer offer) {
        offerRepository.save(offer);
        return "redirect:/offers";
    }

    @PostMapping("/admin/offers/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteOffer(@PathVariable Integer id) {
        offerRepository.deleteById(id);
        return "redirect:/offers";
    }
}
