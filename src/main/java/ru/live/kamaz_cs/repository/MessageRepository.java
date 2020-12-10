package ru.live.kamaz_cs.repository;

import org.springframework.data.repository.CrudRepository;
import ru.live.kamaz_cs.domain.Message;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {

    List<Message> findByTag(String tag);

}
