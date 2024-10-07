package com.toyproject.musinsa.repository.user;


import com.toyproject.musinsa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//<Entity, PK type>

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);

    // memberName으로 DB 회원 테이블 조회.
    Optional<User> findByUsername(String username);
}
