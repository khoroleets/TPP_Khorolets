package travel.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("offer")
public record Offer(
    @Id Integer id,
    String title,
    String description,
    Integer travelTypeId, // З'єднається з `travel_type_id`
    Integer countryId,    // З'єднається з `country_id`
    BigDecimal price,
    LocalDate startDate,  // З'єднається з `start_date`
    LocalDate endDate,    // З'єднається з `end_date`
    Integer seats,
    Boolean available,
    LocalDateTime createdAt
) {}