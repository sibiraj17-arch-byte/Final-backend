package com.healthcare.feature.auth.mapper;

import com.healthcare.entity.User;
import com.healthcare.enums.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findById(@Param("id") Long id);

    Optional<User> findByMobileNumber(@Param("mobileNumber") String mobileNumber);

    boolean existsByMobileNumber(@Param("mobileNumber") String mobileNumber);

    boolean existsByEmail(@Param("email") String email);

    List<User> findByRole(@Param("role") UserRole role);

    List<User> findByIsActiveTrue();

    List<User> findAll();

    void insertUser(User user);

    void updateUser(User user);

    void deleteById(@Param("id") Long id);

    default User save(User user) {
        if (user.getId() == null) {
            insertUser(user);
        } else {
            updateUser(user);
        }
        return findById(user.getId()).orElse(user);
    }

    default void delete(User user) {
        if (user != null && user.getId() != null) {
            deleteById(user.getId());
        }
    }
}
