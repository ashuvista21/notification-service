package com.ashuvista21.notification.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ashuvista21.notification.entities.UserChannelContact;
import com.ashuvista21.notification.enums.NotificationChannelType;

@Repository
public interface UserChannelContactRepository extends JpaRepository<UserChannelContact, Long>{
	boolean existsByUserIdAndChannel(UUID userId, NotificationChannelType channel) ;
	Optional<UserChannelContact> findByUserIdAndChannel(UUID userId, NotificationChannelType channel) ;
}
