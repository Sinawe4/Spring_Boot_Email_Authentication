package com.example.jwt.repository;

import com.example.jwt.domain.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member, Long> {
    Member findByUsername(String username);
}
