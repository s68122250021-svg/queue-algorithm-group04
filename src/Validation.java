import java.util.List;

/** Simple self-checks for the required P1-P4 scenario. */
public class Validation {
    public static void main(String[] args) {
        List<Process> required = List.of(
                new Process("P1", 0, 8),
                new Process("P2", 0, 4),
                new Process("P3", 0, 9),
                new Process("P4", 0, 5)
        );

        QueueScheduler.Metrics fcfs = QueueScheduler.fcfs(required).metrics();
        QueueScheduler.Metrics rr = QueueScheduler.roundRobin(required, 3).metrics();

        checkClose("FCFS average waiting", 10.25, fcfs.averageWaitingTime());
        checkClose("FCFS average turnaround", 16.75, fcfs.averageTurnaroundTime());
        checkClose("RR average waiting", 15.00, rr.averageWaitingTime());
        checkClose("RR average turnaround", 21.50, rr.averageTurnaroundTime());

        if (rr.contextSwitches() != 9) {
            throw new AssertionError("RR context switches expected 9 but got " + rr.contextSwitches());
        }

        List<Process> cancelled = QueueOperations.cancel(required, "P2");
        if (cancelled.size() != 3 || cancelled.stream().anyMatch(p -> p.getId().equals("P2"))) {
            throw new AssertionError("Cancel operation did not remove P2 correctly");
        }

        System.out.println("All required checks passed.");
    }

    private static void checkClose(String label, double expected, double actual) {
        if (Math.abs(expected - actual) > 1e-9) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }
}
