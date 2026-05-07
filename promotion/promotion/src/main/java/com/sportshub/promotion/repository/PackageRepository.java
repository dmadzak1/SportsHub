package com.sportshub.promotion.repository;

import com.sportshub.promotion.model.Package;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    List<Package> findByName(String name);

    @Query("""
           SELECT p
           FROM Package p
           WHERE p.price BETWEEN :minPrice AND :maxPrice
           ORDER BY p.price ASC
           """)
    List<Package> findPackagesByPriceRange(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

    @EntityGraph(attributePaths = "promotions")
    @Query("SELECT p FROM Package p WHERE p.packageId = :id")
    Optional<Package> findByIdWithPromotions(@Param("id") Long id);
}