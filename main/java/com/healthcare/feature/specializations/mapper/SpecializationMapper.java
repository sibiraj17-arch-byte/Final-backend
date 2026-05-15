package com.healthcare.feature.specializations.mapper;

import com.healthcare.entity.Specialization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SpecializationMapper {
    Optional<Specialization> findById(@Param("id") Long id);

    Optional<Specialization> findByName(@Param("name") String name);

    boolean existsByName(@Param("name") String name);

    List<Specialization> findAll();

    void insertSpecialization(Specialization specialization);

    void updateSpecialization(Specialization specialization);

    void deleteById(@Param("id") Long id);

    default Specialization save(Specialization specialization) {
        if (specialization.getId() == null) {
            insertSpecialization(specialization);
        } else {
            updateSpecialization(specialization);
        }
        return findById(specialization.getId()).orElse(specialization);
    }

    default void delete(Specialization specialization) {
        if (specialization != null && specialization.getId() != null) {
            deleteById(specialization.getId());
        }
    }
}
