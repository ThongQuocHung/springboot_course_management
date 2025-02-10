package group_2.cursus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import group_2.cursus.entity.Module;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    
}
