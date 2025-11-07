package travel.Repository;

import org.springframework.data.repository.ListCrudRepository;

import travel.Model.Offer;

import java.math.BigDecimal;
import java.util.List;

public interface OfferRepository extends ListCrudRepository<Offer, Integer> {

    // Spring Data сам зрозуміє, що робити, за назвою методу:
    
    // Знайти всі пропозиції для конкретної країни (за її ID)
    List<Offer> findByCountryId(Integer countryId);

    // Знайти всі пропозиції дорожче за певну ціну
    List<Offer> findByPriceGreaterThan(BigDecimal price);

    // Знайти всі пропозиції, які доступні для бронювання
    List<Offer> findByAvailableTrue();
}