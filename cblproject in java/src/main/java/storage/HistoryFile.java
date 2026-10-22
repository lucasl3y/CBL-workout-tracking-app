package storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Parsed contents of the workout history file. */
public final class HistoryFile {

    private final List<String> prefixLines;
    private final List<List<String>> sessionBlocks;

    /**
     * Copies the supplied lists and makes both list levels unmodifiable.
     */
    public HistoryFile(List<String> prefixLines, List<List<String>> sessionBlocks) {
        this.prefixLines = Collections.unmodifiableList(new ArrayList<>(prefixLines));

        ArrayList<List<String>> blocksCopy = new ArrayList<>();
        for (List<String> block : sessionBlocks) {
            blocksCopy.add(Collections.unmodifiableList(new ArrayList<>(block)));
        }
        this.sessionBlocks = Collections.unmodifiableList(blocksCopy);
    }

    public List<String> getPrefixLines() {
        return prefixLines;
    }

    public List<List<String>> getSessionBlocks() {
        return sessionBlocks;
    }
}
