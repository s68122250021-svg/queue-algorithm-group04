import java.util.List;

public class Main {
    private static final int QUANTUM = 3;

    public static void main(String[] args) {
        List<Process> required = List.of(
                new Process("P1", 0, 8),
                new Process("P2", 0, 4),
                new Process("P3", 0, 9),
                new Process("P4", 0, 5)
        );

        System.out.println("=== GROUP 04: CPU PROCESS SCHEDULING ===");
        System.out.println("Time Quantum = " + QUANTUM);
        print("FCFS", QueueScheduler.fcfs(required));
        print("Round Robin", QueueScheduler.roundRobin(required, QUANTUM));
        printTests();
    }

    private static void print(String name, QueueScheduler.Result result) {
        System.out.println("\n--- " + name + " ---");
        for (QueueScheduler.Execution e : result.execution()) {
            System.out.printf("%s: %d-%d (remaining=%d)%n", e.processId(), e.start(), e.end(), e.remainingAfter());
        }
        QueueScheduler.Metrics m = result.metrics();
        System.out.println("Waiting Time: " + m.waitingTime());
        System.out.println("Turnaround Time: " + m.turnaroundTime());
        System.out.printf("Average Waiting Time: %.2f%n", m.averageWaitingTime());
        System.out.printf("Average Turnaround Time: %.2f%n", m.averageTurnaroundTime());
        System.out.println("Context Switches: " + m.contextSwitches());
    }

    private static void printTests() {
        System.out.println("\n=== 6 REQUIRED TEST CASES ===");
        runTest("1. Normal Case", List.of(
                new Process("P1", 0, 8), new Process("P2", 0, 4), new Process("P3", 0, 9), new Process("P4", 0, 5)));
        runTest("2. Empty Queue", List.of());
        runTest("3. Single Item", List.of(new Process("P1", 0, 5)));
        runTest("4. Large Queue", largeProcesses(100));
        runTest("5. Edge Case - Same Burst", List.of(
                new Process("P1", 0, 3), new Process("P2", 0, 3), new Process("P3", 0, 3)));

        System.out.println("\n6. Cancel Case - cancel middle item P2");
        List<Process> queue = List.of(
                new Process("P1", 0, 4), new Process("P2", 0, 2), new Process("P3", 0, 5));
        List<Process> cancelled = QueueOperations.cancel(queue, "P2");
        System.out.println("Before: " + queue);
        System.out.println("After cancelling P2: " + cancelled);
        System.out.println("Cancel non-existing P99: " + QueueOperations.cancel(queue, "P99"));
    }

    private static void runTest(String label, List<Process> processes) {
        System.out.println("\n" + label);
        if (processes.isEmpty()) {
            System.out.println("Queue is empty -> no process scheduled.");
            return;
        }
        System.out.printf("FCFS avg wait = %.2f, RR avg wait = %.2f%n",
                QueueScheduler.fcfs(processes).metrics().averageWaitingTime(),
                QueueScheduler.roundRobin(processes, QUANTUM).metrics().averageWaitingTime());
    }

    private static List<Process> largeProcesses(int n) {
        java.util.ArrayList<Process> list = new java.util.ArrayList<>();
        for (int i = 1; i <= n; i++) list.add(new Process("P" + i, 0, 1 + (i % 20)));
        return list;
    }
}
