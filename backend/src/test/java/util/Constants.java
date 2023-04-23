package util;

import at.ac.tuwien.ase.groupphase.backend.entity.Region;

public final class Constants {

    public static final String VALID_USER_USERNAME = "Parkster";
    public static final String VALID_USER_EMAIL = "parker@gmail.com";
    public static final String VALID_USER_PASSWORD = "12345678";
    public static final Region VALID_USER_REGION = Region.LOWER_AUSTRIA;
    public static final String VALID_USER_POSTAL_CODE = "1040";

    // IF changes are doen for the existing user, SelfServiceData.sql needs to be adjusted accordingly
    public static final String EXISTING_USER_USERNAME = "John";
    public static final String EXISTING_USER_EMAIL = "John@gmail.com";
    public static final String EXISTING_USER_PASSWORD = "12345678";
    public static final Region EXISTING_USER_REGION = Region.UPPER_AUSTRIA;
    public static final String EXISTING_USER_POSTAL_CODE = "1010";

    public static final String INVALID_EMAIL = "peterHuber.com";

}
