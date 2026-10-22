package constant;

public final class WorkoutFileFormat {

    public static final String FILE_NAME = "workout_history.txt";
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
