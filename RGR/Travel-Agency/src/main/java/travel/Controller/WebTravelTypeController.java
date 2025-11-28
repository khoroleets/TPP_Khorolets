package travel.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import travel.Model.TravelType;
import travel.Repository.TravelTypeRepository;

import java.util.Optional;

@Controller
public class WebTravelTypeController {

    private final TravelTypeRepository travelTypeRepository;

    public WebTravelTypeController(TravelTypeRepository travelTypeRepository) {
        this.travelTypeRepository = travelTypeRepository;
    }

    @GetMapping("/travel-types")
    public String showTravelTypes(Model model) {
        model.addAttribute("allTypes", travelTypeRepository.findAll());
        return "travel-types";
    }

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
}
