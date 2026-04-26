package com.ashuvista21.notification.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ashuvista21.notification.entities.NotificationChannelStatus;

@Repository
public interface NotificationChannelStatusRepository extends JpaRepository<NotificationChannelStatus, UUID> {

}
