package at.ac.tuwien.ase.groupphase.backend.validator;

import at.ac.tuwien.ase.groupphase.backend.BackendApplication;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PostalCodeValidator {

    List<String> postalCodes;

    public PostalCodeValidator() {
        loadData();
    }

    private void loadData() {
        postalCodes = new ArrayList<>();
        try (final var reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(Objects
                .requireNonNull(BackendApplication.class.getClassLoader().getResource("postal-codes-austria.txt"))
                .openStream())))) {
            postalCodes = reader.lines().toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPostalCodeValid(String postalCode) {
        return postalCodes.contains(postalCode);
    }
}
