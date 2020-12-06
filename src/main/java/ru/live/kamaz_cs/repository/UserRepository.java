package ru.live.kamaz_cs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.live.kamaz_cs.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByActivationCode(String code);
}
