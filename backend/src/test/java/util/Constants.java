package util;

import at.ac.tuwien.ase.groupphase.backend.entity.*;
import at.ac.tuwien.ase.groupphase.backend.dto.ParticipantDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Constants {
    public static final String LEAGUE_ENDPOINT_BASEURI = "/api/v1/league";

    public static final Long VALID_USER_ID = 12121212L;
    public static final Long VALID_USER_ID2 = 21212121L;
    public static final Long VALID_USER_ID3 = 345354L;
    public static final String VALID_USER_USERNAME = "Parkster";
    public static final String VALID_USER_USERNAME2 = "Peter";
    public static final String VALID_USER_USERNAME3 = "Hubertus";
    public static final UUID VALID_PICTURE_UUID1 = UUID.fromString("518cf81f-0ff6-4999-b160-756e0dfeac0a");
    public static final UUID VALID_PICTURE_UUID2 = UUID.fromString("717c37e6-94e9-49a1-adf2-35617b91e0a6");
    public static final String VALID_USER_EMAIL = "parker@gmail.com";
    public static final String VALID_USER_EMAIL2 = "peter@gmail.com";
    public static final String VALID_USER_EMAIL3 = "hubertus@gmail.com";
    public static final String VALID_USER_PASSWORD = "12345678";
    public static final byte[] VALID_USER_PASSWORD_BYTES = "12345678".getBytes();
    public static final Region VALID_USER_REGION = Region.LOWER_AUSTRIA;
    public static final String VALID_USER_POSTAL_CODE = "1040";
    public static final Integer VALID_WINS = 420;
    public static final LocalDateTime VALID_LOCALDATETIME = LocalDateTime.of(1990, 1, 1, 1, 1);

    public static final Participant VALID_PARTICIPANT_1 = new Participant(VALID_USER_ID, VALID_USER_EMAIL,
            VALID_USER_PASSWORD_BYTES, VALID_USER_USERNAME, Boolean.FALSE, new ArrayList<>(), VALID_USER_POSTAL_CODE,
            new HashMap<>(), VALID_USER_REGION, VALID_LOCALDATETIME, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>());

    public static final Participant VALID_PARTICIPANT_2 = new Participant(VALID_USER_ID2, VALID_USER_EMAIL2,
            VALID_USER_PASSWORD_BYTES, VALID_USER_USERNAME2, Boolean.FALSE, new ArrayList<>(), VALID_USER_POSTAL_CODE,
            new HashMap<>(), VALID_USER_REGION, VALID_LOCALDATETIME, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>());

    public static final Participant VALID_PARTICIPANT_3 = new Participant(VALID_USER_ID3, VALID_USER_EMAIL3,
            VALID_USER_PASSWORD_BYTES, VALID_USER_USERNAME3, Boolean.FALSE, new ArrayList<>(), VALID_USER_POSTAL_CODE,
            new HashMap<>(), VALID_USER_REGION, VALID_LOCALDATETIME, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>());

    public static final ParticipantDto VALID_PARTICIPANT_DTO_1 = new ParticipantDto(VALID_USER_USERNAME,
            VALID_USER_EMAIL, VALID_USER_POSTAL_CODE, new HashMap<>(), VALID_USER_REGION, VALID_LOCALDATETIME);

    public static final League LEAGUE1 = new League(null, UUID.randomUUID(), GameMode.PICTURE_INGREDIENTS,
            Region.VORARLBERG, 7, "League 1", new ArrayList<>(), new ArrayList<>());
    public static final Challenge CHALLENGE1 = new Challenge(1L, "challenge description",
            LocalDateTime.now().toLocalDate(), LocalDateTime.now().plusDays(7).toLocalDate(), "Recipe",
            new ArrayList<>(), LEAGUE1);

    public static final Recipe RECIPE1 = new Recipe("99", "recipe1", 10, 11, "description", "skill level",
            "518cf81f-0ff6-4999-b160-756e0dfeac0a.png");

    public static final Participant PARTICIPANT_NOID = new Participant(null, VALID_USER_EMAIL,
            VALID_USER_PASSWORD_BYTES, VALID_USER_USERNAME, Boolean.FALSE, new ArrayList<>(), VALID_USER_POSTAL_CODE,
            new HashMap<>(), VALID_USER_REGION, VALID_LOCALDATETIME, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>());

    public static final Submission VALID_SUBMISSION_P1 = new Submission(null, UUID.randomUUID(), LocalDateTime.now(),
            null, null, new ArrayList<>());
    public static final Submission VALID_SUBMISSION_P2 = new Submission(null, UUID.randomUUID(), LocalDateTime.now(),
            null, null, new ArrayList<>());

    // IF changes are doen for the existing user, SelfServiceData.sql needs to be adjusted accordingly
    public static final String EXISTING_USER_USERNAME = "John";
    public static final String EXISTING_USER_EMAIL = "John@gmail.com";
    public static final String EXISTING_USER_PASSWORD = "12345678";
    public static final Region EXISTING_USER_REGION = Region.UPPER_AUSTRIA;
    public static final String EXISTING_USER_POSTAL_CODE = "1010";
    public static final String NON_EXISTING_USER_POSTAL_CODE = "9999";

    public static final String INVALID_EMAIL = "peterHuber.com";
    public static final String VALID_LEAGUE_NAME = "Drei Kuchen hoch";
    public static final GameMode VALID_LEAGUE_GAMEMODE = GameMode.PICTURE;
    public static final Integer VALID_LEAGUE_CHALLENGE_DURATION = 55;
    public static final Region VALID_LEAGUE_REGION = Region.BURGENLAND;

}
