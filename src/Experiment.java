import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Experiment {
    private static final int[] SIZES = {100, 1_000, 10_000, 50_000};
    private static final int ROUNDS = 5;
    private static final int WARM_UP = 3;
    private static final int QUANTUM = 3;
    private static final long SEED = 20260908L;

    public static void main(String[] args) {
        System.out.println("n,FCFS_avg_ns,RoundRobin_avg_ns");
        for (int n : SIZES) {
            List<Process> data = generate(n);
            for (int i = 0; i < WARM_UP; i++) {
                QueueScheduler.fcfs(data);
                QueueScheduler.roundRobin(data, QUANTUM);
            }
            long fcfs = averageNanos(data, true);
            long rr = averageNanos(data, false);
            System.out.printf("%d,%d,%d%n", n, fcfs, rr);
        }
    }

    private static long averageNanos(List<Process> data, boolean fcfs) {
        long sum = 0;
        for (int i = 0; i < ROUNDS; i++) {
            long start = System.nanoTime();
            if (fcfs) QueueScheduler.fcfs(data);
            else QueueScheduler.roundRobin(data, QUANTUM);
            sum += System.nanoTime() - start;
        }
        return sum / ROUNDS;
    }

    private static List<Process> generate(int n) {
        Random random = new Random(SEED + n);
        ArrayList<Process> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            int arrival = i == 0 ? 0 : random.nextInt(Math.max(1, n / 10 + 1));
            int burst = 1 + random.nextInt(20);
            list.add(new Process("P" + (i + 1), arrival, burst));
        }
        return list;
    }
}
