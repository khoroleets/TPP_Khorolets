package travel.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record User(
    @Id Integer id,
    String username,
    String password,
    String role
) {}