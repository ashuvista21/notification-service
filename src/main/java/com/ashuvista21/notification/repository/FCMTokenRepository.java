package com.ashuvista21.notification.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ashuvista21.notification.entities.FCMDeviceInfo;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMDeviceInfo, UUID> {

	Optional<FCMDeviceInfo> findByDeviceNameAndActiveTrue(String deviceName) ;
	
	@Modifying(clearAutomatically = true)
	@Query("""
			UPDATE FCMDeviceInfo f 
			SET f.active = false 
			WHERE f.deviceName = :deviceName
	""")
	void deactivateTokens(@Param("deviceName") String deviceName);
}
