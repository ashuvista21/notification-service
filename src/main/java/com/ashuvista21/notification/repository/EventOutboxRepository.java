package com.ashuvista21.notification.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.ashuvista21.notification.entities.EventOutbox;

import jakarta.persistence.LockModeType;

@Repository
public interface EventOutboxRepository extends JpaRepository<EventOutbox, UUID>{
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<EventOutbox> findByStatusOrderByCreatedAtAsc(String status, Pageable pageable) ;
	
	/*
	 * @Query(value = """
    SELECT * FROM event_outbox
    WHERE status = 'PENDING'
    ORDER BY created_at ASC
    LIMIT :limit
    FOR UPDATE SKIP LOCKED
""", nativeQuery = true)
List<EventOutbox> findPendingEventsForUpdate(@Param("limit") int limit);
	 */
}
