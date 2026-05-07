package com.example.user.repository;

import com.example.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Svi korisnici po ulozi
    List<User> findByRoleRoleName(String roleName);

    // Pretraga po dijelu emaila
    @Query("SELECT u FROM User u WHERE u.email LIKE %:keyword%")
    List<User> searchByEmail(@Param("keyword") String keyword);

    // Svi korisnici koji imaju barem jedan auth token
    @Query("SELECT DISTINCT u FROM User u WHERE SIZE(u.authTokens) > 0")
    List<User> findUsersWithActiveTokens();
}