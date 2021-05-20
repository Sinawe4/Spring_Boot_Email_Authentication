package com.example.jwt.repository;

import com.example.jwt.config.Salt;
import org.springframework.data.repository.CrudRepository;

public interface SaltRepository extends CrudRepository<Salt, Long> {
}
