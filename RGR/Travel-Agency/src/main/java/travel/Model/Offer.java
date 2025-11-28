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
    Integer travelTypeId,
    Integer countryId,
    BigDecimal price,
    LocalDate startDate,
    LocalDate endDate,
    Integer seats,
    Boolean available,
    LocalDateTime createdAt

) {}
