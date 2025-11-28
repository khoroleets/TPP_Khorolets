package travel.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import travel.Model.Country;
import travel.Repository.CountryRepository;

import java.util.Optional;

@Controller
@RequestMapping
public class WebCountryController {

    private final CountryRepository countryRepository;

    public WebCountryController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    // Перегляд
    @GetMapping("/countries")
    public String showCountries(Model model) {
        model.addAttribute("allCountries", countryRepository.findAll());
        return "countries";
    }

    @GetMapping("/admin/countries/form")
    @PreAuthorize("hasRole('ADMIN')")
    public String countryForm(@RequestParam(required = false) Integer id, Model model) {
        if (id != null) {
            Optional<Country> country = countryRepository.findById(id);
            model.addAttribute("country", country.orElse(new Country(null, "", "", null)));
        } else {
            model.addAttribute("country", new Country(null, "", "", null));
        }
        return "country-form";
    }

    @PostMapping("/admin/countries/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveCountry(@ModelAttribute Country country) {
        countryRepository.save(country);
        return "redirect:/countries";
    }

    @PostMapping("/admin/countries/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCountry(@PathVariable Integer id) {
        countryRepository.deleteById(id);
        return "redirect:/countries";
    }
}
