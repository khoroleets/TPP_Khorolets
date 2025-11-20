package travel.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import travel.Model.Country;
import travel.Model.Offer;
import travel.Model.TravelType;
import travel.Repository.CountryRepository;
import travel.Repository.OfferRepository;
import travel.Repository.TravelTypeRepository;

import java.util.Optional;

@Controller
public class WebPageController {

    private final TravelTypeRepository travelTypeRepository;
    private final CountryRepository countryRepository;
    private final OfferRepository offerRepository;

    public WebPageController(TravelTypeRepository travelTypeRepository,
                             CountryRepository countryRepository,
                             OfferRepository offerRepository) {
        this.travelTypeRepository = travelTypeRepository;
        this.countryRepository = countryRepository;
        this.offerRepository = offerRepository;
    }

    // --- ІСНУЮЧІ СТОРІНКИ ---
    @GetMapping("/")
    public String welcomePage() { return "index"; }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/error/403")
    public String accessDeniedPage() { return "error403"; }

    // --- СТОРІНКИ ПЕРЕГЛЯДУ ---

    @GetMapping("/countries")
    public String showCountries(Model model) {
        model.addAttribute("allCountries", countryRepository.findAll());
        return "countries";
    }

    @GetMapping("/travel-types")
    public String showTravelTypes(Model model) {
        model.addAttribute("allTypes", travelTypeRepository.findAll());
        return "travel-types";
    }

    @GetMapping("/offers")
    public String showOffers(Model model) {
        model.addAttribute("allOffers", offerRepository.findAll());
        return "offers";
    }
    
    // --- КЕРУВАННЯ КРАЇНАМИ (ADMIN) ---

    // 1. Показати форму (для створення або редагування)
    @GetMapping("/admin/countries/form")
    @PreAuthorize("hasRole('ADMIN')")
    public String countryForm(@RequestParam(required = false) Integer id, Model model) {
        if (id != null) {
            // Редагування: шукаємо існуючу
            Optional<Country> country = countryRepository.findById(id);
            model.addAttribute("country", country.orElse(new Country(null, "", "", null)));
        } else {
            // Створення: передаємо нову пусту
            model.addAttribute("country", new Country(null, "", "", null));
        }
        return "country-form";
    }

    // 2. Зберегти (Create or Update)
    @PostMapping("/admin/countries/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveCountry(@ModelAttribute Country country) {
        // createdAt автоматично обробляється БД або Spring Data, якщо null
        countryRepository.save(country);
        return "redirect:/countries";
    }

    // 3. Видалити
    @PostMapping("/admin/countries/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCountry(@PathVariable Integer id) {
        countryRepository.deleteById(id);
        return "redirect:/countries";
    }

    // --- КЕРУВАННЯ ТИПАМИ ТУРІВ (ADMIN) ---

    @GetMapping("/admin/travel-types/form")
    @PreAuthorize("hasRole('ADMIN')")
    public String travelTypeForm(@RequestParam(required = false) Integer id, Model model) {
        if (id != null) {
            Optional<TravelType> type = travelTypeRepository.findById(id);
            model.addAttribute("travelType", type.orElse(new TravelType(null, "", "", null)));
        } else {
            model.addAttribute("travelType", new TravelType(null, "", "", null));
        }
        return "travel-type-form";
    }

    @PostMapping("/admin/travel-types/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveTravelType(@ModelAttribute TravelType travelType) {
        travelTypeRepository.save(travelType);
        return "redirect:/travel-types";
    }

    @PostMapping("/admin/travel-types/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteTravelType(@PathVariable Integer id) {
        travelTypeRepository.deleteById(id);
        return "redirect:/travel-types";
    }

    // --- КЕРУВАННЯ ПРОПОЗИЦІЯМИ (ADMIN) ---

    @GetMapping("/admin/offers/form")
    @PreAuthorize("hasRole('ADMIN')")
    public String offerForm(@RequestParam(required = false) Integer id, Model model) {
        if (id != null) {
            Optional<Offer> offer = offerRepository.findById(id);
            model.addAttribute("offer", offer.orElse(null));
        } else {
            // Пуста пропозиція
            model.addAttribute("offer", new Offer(null, "", "", null, null, null, null, null, 0, true, null));
        }
        // Нам потрібні списки країн та типів для випадаючих списків (select)
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