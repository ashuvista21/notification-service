package com.ashuvista21.notification.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ashuvista21.notification.entities.EventDescription;

@Repository
public interface EventDescriptionRepository extends JpaRepository<EventDescription, UUID> {
	List<EventDescription> findByEventCodeAndUserId(String eventCode, UUID userId) ;
}
