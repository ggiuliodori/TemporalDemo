package com.example.demo.repositories;

import com.example.demo.entities.EndpointConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EndpointConfigRepository extends JpaRepository<EndpointConfig, Long> {
    Optional<EndpointConfig> findByBusinessKey(String businessKey);
}
