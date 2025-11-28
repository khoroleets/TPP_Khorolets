package travel.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("country")
public record Country(
    @Id Integer id,
    String name,
    String isoCode,
    LocalDateTime createdAt
) {}
