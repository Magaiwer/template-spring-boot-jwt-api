package com.serverside.api.repository;

import com.serverside.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(value = "select distinct  p.role from User u inner join u.groups g inner join  g.permissions p where u.id = :id")
    List<String> getPermissions(@Nullable @Param("id") Long  id);
}
