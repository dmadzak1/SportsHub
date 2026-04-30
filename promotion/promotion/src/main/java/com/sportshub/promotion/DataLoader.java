package com.sportshub.promotion;

import com.sportshub.promotion.model.*;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final PackageRepository packageRepository;
    private final PromotionRepository promotionRepository;
    private final DiscountRepository discountRepository;
    private final PromotionUsageRepository promotionUsageRepository;

    public DataLoader(PackageRepository packageRepository,
                      PromotionRepository promotionRepository,
                      DiscountRepository discountRepository,
                      PromotionUsageRepository promotionUsageRepository) {
        this.packageRepository = packageRepository;
        this.promotionRepository = promotionRepository;
        this.discountRepository = discountRepository;
        this.promotionUsageRepository = promotionUsageRepository;
    }

    @Override
    public void run(String... args) {
        Package individual = packageRepository.save(new Package("INDIVIDUAL", 50.0));
        Package group      = packageRepository.save(new Package("GROUP", 80.0));
        Package family     = packageRepository.save(new Package("FAMILY", 120.0));

        Promotion promo1 = promotionRepository.save(new Promotion(individual, 10.0, LocalDate.now().plusMonths(1)));
        Promotion promo2 = promotionRepository.save(new Promotion(group,      20.0, LocalDate.now().plusMonths(2)));
        Promotion promo3 = promotionRepository.save(new Promotion(family,     15.0, LocalDate.now().minusDays(1))); // istekla

        discountRepository.save(new Discount(promo1, "10% popust za individualni paket"));
        discountRepository.save(new Discount(promo2, "20% popust za grupni paket"));
        discountRepository.save(new Discount(promo2, "Besplatan parking uz grupni paket"));
        discountRepository.save(new Discount(promo3, "15% popust za porodicni paket"));

        promotionUsageRepository.save(new PromotionUsage(1L, promo1, 2));
        promotionUsageRepository.save(new PromotionUsage(2L, promo2, 1));
        promotionUsageRepository.save(new PromotionUsage(1L, promo2, 3));

        System.out.println("✅ Promotion Service: testni podaci uspješno učitani.");
    }
}
