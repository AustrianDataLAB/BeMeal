package at.ac.tuwien.ase.groupphase.backend.datagenerator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("SmallDataGenerator")
public class DataGeneratorHealthIndicator implements HealthIndicator {

    private boolean isReady = false;

    @Override
    public Health health() {
        if (isReady) {
            return Health.up().build();
        }
        return Health.down().withDetail("reason", "db data is generation is in progress").build();
    }

    public void setReady() {
        this.isReady = true;
    }
}
