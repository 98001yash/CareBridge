package com.company.CareBridge.repository;

import com.company.CareBridge.entity.Permission;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.Optional;

public interface PermissionRepository extends JpaRepositoryImplementation<Permission,Long> {

    Optional<Permission> findByName(String name);
}
