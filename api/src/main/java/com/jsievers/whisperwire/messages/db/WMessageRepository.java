package com.jsievers.whisperwire.messages.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WMessageRepository extends JpaRepository<WMessageEntity, String> {
}
