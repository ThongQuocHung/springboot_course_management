package group_2.cursus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import group_2.cursus.entity.SubCategory;

import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    Optional<SubCategory> findBySubCategoryName(String subCategoryName);
}
