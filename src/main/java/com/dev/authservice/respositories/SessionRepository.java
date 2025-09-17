package com.dev.authservice.respositories;

import com.dev.authservice.models.Session;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

 //   @Query("SELECT s FROM Session s WHERE s.token = :token AND s.user.id = :userId")
    Optional<Session> findByTokenAndUser_Id(String token, Long userId);

}