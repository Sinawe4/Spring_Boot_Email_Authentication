package com.example.jwt.repository;

import com.example.jwt.domain.Salt;
import org.springframework.data.repository.CrudRepository;

public interface SaltRepository extends CrudRepository<Salt, Long> {
}
