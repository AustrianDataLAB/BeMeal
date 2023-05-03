package at.ac.tuwien.ase.groupphase.backend.validator;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostalCodeValidator {

    List<String> postalCodes;

    public PostalCodeValidator() {
        loadData();
    }

    private void loadData() {
        postalCodes = new ArrayList<>();
        BufferedReader reader;
        try {
            // if a directory contains a space doesnt work -> replace
            // todo look into this again
            String path = PostalCodeValidator.class.getClassLoader().getResource("postal-codes-austria.txt").getFile()
                    .replace("%20", " ");
            ;
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) == null)
                    break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            postalCodes.add(line);
        }
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPostalCodeValid(String postalCode) {
        return postalCodes.contains(postalCode);
    }
}
