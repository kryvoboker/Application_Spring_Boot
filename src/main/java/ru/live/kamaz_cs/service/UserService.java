package ru.live.kamaz_cs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.live.kamaz_cs.domain.Role;
import ru.live.kamaz_cs.domain.User;
import ru.live.kamaz_cs.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, MailSender mailSender, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = repository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public boolean addUser(User user) {
        final User userFromDb = repository.findByUsername(user.getUsername());

        if (userFromDb != null) { // если пользователь найден в базе данных, то возвращаем false и сообщаем, что он не создан
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        repository.save(user);

        sendMessage(user);

        return true; // если пользователь не найден в базе данных, то возвращаем true и сообщаем, что он создан
    }

    private void sendMessage(User user) {
        if (!StringUtils.isEmpty(user.getEmail())) { // StringUtils.isEmpty() - спринговая утилита на проверку строки, не пустая ли она и не равна ли она null
            String message = String.format(
                    "Hello, %s! \n" + // %s - это имя user
                            "Welcome to Application_Spring_Boot. Please visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Activation code", message);
        }
    }

    public boolean activateUser(String code) {
        User user = repository.findByActivationCode(code);

        if (user == null) { // если пользователь не найде, то активацию он не прошел
            return false;
        }

        user.setActivationCode(null);
        repository.save(user);

        return true;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public void saveUser(User user, String username, Map<String, String> form) {
        user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
    }

    public void updateProfile(User user, String password, String email) {
        final String userEmail = user.getEmail();
        final boolean isEmailChanged = (email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email));

        if (isEmailChanged) {
            user.setEmail(email);

            if (!StringUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (!StringUtils.isEmpty(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        repository.save(user);

        if (isEmailChanged) {
            sendMessage(user);
        }
    }
}
