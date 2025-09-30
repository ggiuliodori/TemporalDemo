package com.example.demo.repositories;

import com.example.demo.entities.RequestTrace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestTraceRepository extends JpaRepository<RequestTrace, Long> {
}
