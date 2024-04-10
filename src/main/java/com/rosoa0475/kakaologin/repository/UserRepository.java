package com.rosoa0475.kakaologin.repository;

import com.rosoa0475.kakaologin.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
