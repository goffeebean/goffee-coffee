package com.goffeebean.config;

import com.goffeebean.entity.Roast;
import com.goffeebean.entity.RoastLevel;
import com.goffeebean.repository.RoastRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final RoastRepository roastRepository;

    public DataSeeder(RoastRepository roastRepository) {
        this.roastRepository = roastRepository;
    }

    @Override
    public void run(String... args) {
        roastRepository.save(new Roast(null, "Ethiopia Yirgacheffe", "Ethiopia", RoastLevel.LIGHT,
                new BigDecimal("14.99"), "Floral and bright, with notes of citrus and jasmine."));
        roastRepository.save(new Roast(null, "Colombia Supremo", "Colombia", RoastLevel.MEDIUM,
                new BigDecimal("12.50"), "Balanced and nutty, with a hint of chocolate."));
        roastRepository.save(new Roast(null, "Sumatra Mandheling", "Indonesia", RoastLevel.DARK,
                new BigDecimal("13.75"), "Earthy and full-bodied, low acidity."));
        roastRepository.save(new Roast(null, "Guatemala Antigua", "Guatemala", RoastLevel.MEDIUM,
                new BigDecimal("15.25"), "Smoky and spicy, with a smooth finish."));
        roastRepository.save(new Roast(null, "Kenya AA", "Kenya", RoastLevel.LIGHT,
                new BigDecimal("16.00"), null));
    }
}
