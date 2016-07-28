package kz.nicnbk.repo.model.base;

/**
 * Holds constants for database columns constraints.
 * For class local constant: C_${CLASS_UPPER_CASE}_${NAME}
 * For package local constant: P_${PACKAGE_UPPER_CASE}_${NAME}
 * For generic constant: G_${NAME}
 *
 */
public final class DataConstraints {
    private DataConstraints() {
        // Constants keeper class. Prevent instantiation.
    }

    /**
     * Class local constants.
     */
    public static final int C_TYPE_ENTITY_CODE = 10;
    public static final int C_TYPE_ENTITY_NAME = 250;

    public static final int C_TYPE_ENTITY_DESCRIPTION_SHORT = 500;
    public static final int C_TYPE_ENTITY_DESCRIPTION_LONG = 1000;

    public static final int C_COUNTRY_CODE = 3;
    public static final int C_COUNTRY_SHORT_NAME = 100;


    /**
     * Package local constants.
     */

    public static final int P_MODEL_NAME = 200;
    public static final int P_MODEL_CODE = 10;

    /**
     * Generic constants.
     */
}
