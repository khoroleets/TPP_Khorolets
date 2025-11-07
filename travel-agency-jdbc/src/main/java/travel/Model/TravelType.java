package travel.Model; 

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("travel_type")
public record TravelType(
    @Id Integer id,
    String name,
    String description,
    LocalDateTime createdAt
) {}