package org.example.demotest.repository;

import org.example.demotest.entities.Role;
import org.example.demotest.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.role <> :role ORDER BY u.first_name ASC")
    List<User> findAllUsersNotFromCountry(Role role);
    Optional<User> findUserById(Long id);
}
