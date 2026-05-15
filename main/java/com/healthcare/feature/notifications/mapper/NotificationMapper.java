package com.healthcare.feature.notifications.mapper;

import com.healthcare.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NotificationMapper {
    Optional<Notification> findById(@Param("id") Long id);

    List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    List<Notification> findByUserIdAndIsReadFalse(@Param("userId") Long userId);

    long countByUserIdAndIsReadFalse(@Param("userId") Long userId);

    void insertNotification(Notification notification);

    void updateNotification(Notification notification);

    default Notification save(Notification notification) {
        if (notification.getId() == null) {
            insertNotification(notification);
        } else {
            updateNotification(notification);
        }
        return findById(notification.getId()).orElse(notification);
    }

    default void saveAll(List<Notification> notifications) {
        if (notifications == null) {
            return;
        }
        for (Notification notification : notifications) {
            save(notification);
        }
    }
}
