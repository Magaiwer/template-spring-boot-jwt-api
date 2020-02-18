package com.serverside.api.repository;

import com.serverside.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(value = "select distinct  p.nome from User u inner join fetch u.groups g inner join fetch g.permissions p where u = :user", nativeQuery = true)
    List<String> getPermissions(User user);
}
