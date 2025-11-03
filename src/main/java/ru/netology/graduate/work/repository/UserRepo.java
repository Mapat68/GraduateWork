package ru.netology.graduate.work.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.graduate.work.model.User;

@Repository
@Transactional
public interface UserRepo extends JpaRepository<User, String> {

    User findByUsername(String username);
}
