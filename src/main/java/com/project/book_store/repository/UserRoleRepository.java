package com.project.book_store.repository;

import com.project.book_store.entity.concretes.UserRole;
import com.project.book_store.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {


    @Query("SELECT r FROM UserRole r WHERE r.roleType=?1")
    Optional<UserRole> findByERoleEquals(RoleType roleType);

    @Query("SELECT (count(r)>0) FROM UserRole r where r.roleType = ?1")
    boolean existsByERoleEquals(RoleType roleType);

}