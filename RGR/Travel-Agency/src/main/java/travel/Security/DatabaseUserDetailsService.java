package travel.Security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import travel.Repository.UserRepository;

import java.util.Collections;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DatabaseUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Завантажуємо нашого 'User' з БД
        travel.Model.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Конвертуємо його у 'UserDetails', який розуміє Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.username(),
                user.password(),
                // Передаємо роль. .authorities() очікує повну назву, напр. "ROLE_ADMIN"
                Collections.singletonList(new SimpleGrantedAuthority(user.role()))
        );
    }
}