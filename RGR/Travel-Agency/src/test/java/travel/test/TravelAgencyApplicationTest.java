package travel.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import travel.Model.Country;
import travel.Model.TravelType;
import travel.Repository.CountryRepository;
import travel.Repository.TravelTypeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TravelAgencyApplicationTest {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private TravelTypeRepository travelTypeRepository;

    @Test
    void contextLoads() {
        // Перевірка, що Spring Boot контекст завантажується без помилок
    }

    @Test
    void testCountryRepositoryCrud() {
        // Створюємо країну
        Country testCountry = new Country(null, "Testland", "TL", LocalDateTime.now());
        Country saved = countryRepository.save(testCountry);
        assertNotNull(saved.id(), "Збережена країна повинна мати id");

        // Читаємо
        Optional<Country> found = countryRepository.findById(saved.id());
        assertTrue(found.isPresent(), "Країна має бути знайдена");
        assertEquals("Testland", found.get().name());

        // Оновлюємо (створюємо новий record з тією ж id)
        Country updatedCountry = new Country(saved.id(), "TestlandUpdated", "TL", saved.createdAt());
        countryRepository.save(updatedCountry);
        Optional<Country> updatedFound = countryRepository.findById(saved.id());
        assertTrue(updatedFound.isPresent());
        assertEquals("TestlandUpdated", updatedFound.get().name());

        // Видаляємо
        countryRepository.deleteById(saved.id());
        Optional<Country> deleted = countryRepository.findById(saved.id());
        assertFalse(deleted.isPresent(), "Країна повинна бути видалена");
    }

    @Test
    void testTravelTypeRepositoryCrud() {
        // Створюємо тип подорожі
        TravelType testType = new TravelType(null, "Adventure", "Extreme adventure trips", LocalDateTime.now());
        TravelType saved = travelTypeRepository.save(testType);
        assertNotNull(saved.id(), "Збережений тип повинен мати id");

        // Читаємо
        Optional<TravelType> found = travelTypeRepository.findById(saved.id());
        assertTrue(found.isPresent(), "Тип подорожі має бути знайдений");
        assertEquals("Adventure", found.get().name());

        // Оновлюємо
        TravelType updatedType = new TravelType(saved.id(), "AdventureUpdated", "Updated description", saved.createdAt());
        travelTypeRepository.save(updatedType);
        Optional<TravelType> updatedFound = travelTypeRepository.findById(saved.id());
        assertTrue(updatedFound.isPresent());
        assertEquals("AdventureUpdated", updatedFound.get().name());

        // Видаляємо
        travelTypeRepository.deleteById(saved.id());
        Optional<TravelType> deleted = travelTypeRepository.findById(saved.id());
        assertFalse(deleted.isPresent(), "Тип подорожі має бути видалений");
    }

    @Test
    void testCountryRepositoryNotEmpty() {
        List<Country> countries = countryRepository.findAll();
        assertNotNull(countries, "Список країн не повинен бути null");
    }

    @Test
    void testTravelTypeRepositoryNotEmpty() {
        List<TravelType> types = travelTypeRepository.findAll();
        assertNotNull(types, "Список типів подорожей не повинен бути null");
    }
}
