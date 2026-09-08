import java.util.ArrayList;
import java.util.List;

public final class QueueOperations {
    private QueueOperations() {}

    /** Returns a new queue/list with the first process matching id removed. */
    public static List<Process> cancel(List<Process> queue, String processId) {
        if (queue == null) throw new IllegalArgumentException("Queue must not be null");
        List<Process> result = new ArrayList<>();
        boolean removed = false;
        for (Process p : queue) {
            if (!removed && p.getId().equals(processId)) {
                removed = true;
            } else {
                result.add(p);
            }
        }
        return result;
    }
}
