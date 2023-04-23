package util;

import at.ac.tuwien.ase.groupphase.backend.dto.ParticipantDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.entity.Region;

import java.time.LocalDateTime;
import java.util.ArrayList;

public final class Constants {

    public static final Long VALID_USER_ID = 12121212L;
    public static final String VALID_USER_USERNAME = "Parkster";
    public static final String VALID_USER_EMAIL = "parker@gmail.com";
    public static final String VALID_USER_PASSWORD = "12345678";
    public static final byte[] VALID_USER_PASSWORD_BYTES = "12345678".getBytes();
    public static final Region VALID_USER_REGION = Region.LOWER_AUSTRIA;
    public static final String VALID_USER_POSTAL_CODE = "1040";
    public static final Integer VALID_WINS = 420;
    public static final LocalDateTime VALID_LOCALDATETIME = LocalDateTime.of(1990, 1, 1, 1, 1);

    public static final Participant VALID_PARTICIPANT_1 = new Participant(VALID_USER_ID, VALID_USER_EMAIL,
            VALID_USER_PASSWORD_BYTES, VALID_USER_USERNAME, Boolean.FALSE, new ArrayList<>(), VALID_USER_POSTAL_CODE,
            VALID_WINS, VALID_USER_REGION, VALID_LOCALDATETIME, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>());

    public static final ParticipantDto VALID_PARTICIPANT_DTO_1 = new ParticipantDto(VALID_USER_USERNAME,
            VALID_USER_EMAIL, VALID_USER_POSTAL_CODE, VALID_WINS, VALID_USER_REGION, VALID_LOCALDATETIME);

    // IF changes are doen for the existing user, SelfServiceData.sql needs to be adjusted accordingly
    public static final String EXISTING_USER_USERNAME = "John";
    public static final String EXISTING_USER_EMAIL = "John@gmail.com";
    public static final String EXISTING_USER_PASSWORD = "12345678";
    public static final Region EXISTING_USER_REGION = Region.UPPER_AUSTRIA;
    public static final String EXISTING_USER_POSTAL_CODE = "1010";

    public static final String INVALID_EMAIL = "peterHuber.com";

}
