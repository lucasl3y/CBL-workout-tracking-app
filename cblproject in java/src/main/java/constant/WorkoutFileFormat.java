package constant;

/**
 * Defines the storage path and shared markers for the workout history format.
 */
public final class WorkoutFileFormat {

    // Resolve the history file against the user's home directory,
    // so its location does not depend on the application's working directory.
    public static final String FILE_NAME = java.nio.file.Path.of(
            System.getProperty("user.home"),
            "workout_tracker_history.txt"
    ).toString();
    public static final String SESSION_END = "============================";
    public static final String DATE_PREFIX = "=== Date: ";
    public static final String DATE_SUFFIX = " ===";
    public static final String SET_MARKER = " (Set ";
    public static final String SET_WEIGHT_SEPARATOR = ") | Weight: ";
    public static final String LEGACY_WEIGHT_SEPARATOR = " | Weight: ";
    public static final String WEIGHT_REPS_SEPARATOR = "kg | Reps: ";
    public static final String LEGACY_WEIGHT_SETS_SEPARATOR = "kg | Sets: ";
    public static final String REPS_SEPARATOR = " | Reps: ";
    public static final String DURATION_PREFIX = "Duration: ";
    public static final String SESSION_ID_PREFIX = "Session ID: ";
    public static final String MUSCLES_PREFIX = "Trained Muscles: ";

    private WorkoutFileFormat() {
        // Utility class; instances are not needed.
    }
}
