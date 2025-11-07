package travel.Repository;

import org.springframework.data.repository.ListCrudRepository;

import travel.Model.Country;

import java.util.List;

public interface CountryRepository extends ListCrudRepository<Country, Integer> {
    
    // Як приклад, додамо метод пошуку за ISO кодом
    List<Country> findByIsoCode(String isoCode);
}