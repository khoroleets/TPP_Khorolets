package travel.Repository;

import org.springframework.data.repository.ListCrudRepository;
import travel.Model.User;
import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User, Integer> {
    
    // Spring Security буде використовувати цей метод
    Optional<User> findByUsername(String username);
}