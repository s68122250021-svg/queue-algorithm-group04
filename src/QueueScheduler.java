import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class QueueScheduler {
    public record Execution(String processId, int start, int end, int remainingAfter) {}
    public record Metrics(Map<String, Integer> waitingTime, Map<String, Integer> turnaroundTime,
                          double averageWaitingTime, double averageTurnaroundTime, int contextSwitches) {}
    public record Result(List<Execution> execution, Metrics metrics) {}

    private QueueScheduler() {}

    public static Result fcfs(List<Process> input) {
        validate(input);
        List<Process> processes = copy(input);
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        List<Execution> execution = new ArrayList<>();
        Map<String, Integer> completion = new HashMap<>();
        int time = 0;
        for (Process p : processes) {
            time = Math.max(time, p.getArrivalTime());
            int start = time;
            time += p.getBurstTime();
            execution.add(new Execution(p.getId(), start, time, 0));
            completion.put(p.getId(), time);
        }
        return buildResult(input, execution, completion, 0);
    }

    public static Result roundRobin(List<Process> input, int quantum) {
        validate(input);
        if (quantum <= 0) throw new IllegalArgumentException("Time quantum must be > 0");
        List<Process> processes = copy(input);
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        Deque<Process> queue = new ArrayDeque<>();
        List<Execution> execution = new ArrayList<>();
        Map<String, Integer> completion = new HashMap<>();
        int time = 0, next = 0, contextSwitches = 0;
        String last = null;

        while (next < processes.size() || !queue.isEmpty()) {
            if (queue.isEmpty()) time = Math.max(time, processes.get(next).getArrivalTime());
            while (next < processes.size() && processes.get(next).getArrivalTime() <= time) {
                queue.addLast(processes.get(next++));
            }
            Process p = queue.removeFirst();
            if (last != null && !last.equals(p.getId())) contextSwitches++;
            last = p.getId();
            int run = Math.min(quantum, p.getRemainingTime());
            int start = time;
            p.runFor(run);
            time += run;
            execution.add(new Execution(p.getId(), start, time, p.getRemainingTime()));
            while (next < processes.size() && processes.get(next).getArrivalTime() <= time) {
                queue.addLast(processes.get(next++));
            }
            if (p.getRemainingTime() > 0) queue.addLast(p);
            else completion.put(p.getId(), time);
        }
        return buildResult(input, execution, completion, contextSwitches);
    }

    private static Result buildResult(List<Process> input, List<Execution> execution,
                                      Map<String, Integer> completion, int contextSwitches) {
        Map<String, Integer> waiting = new HashMap<>();
        Map<String, Integer> turnaround = new HashMap<>();
        double waitSum = 0, turnSum = 0;
        for (Process p : input) {
            int ta = completion.get(p.getId()) - p.getArrivalTime();
            int wt = ta - p.getBurstTime();
            waiting.put(p.getId(), wt);
            turnaround.put(p.getId(), ta);
            waitSum += wt;
            turnSum += ta;
        }
        Metrics metrics = new Metrics(waiting, turnaround,
                waitSum / input.size(), turnSum / input.size(), contextSwitches);
        return new Result(List.copyOf(execution), metrics);
    }

    private static List<Process> copy(List<Process> input) {
        List<Process> copy = new ArrayList<>();
        for (Process p : input) copy.add(new Process(p.getId(), p.getArrivalTime(), p.getBurstTime()));
        return copy;
    }

    private static void validate(List<Process> input) {
        if (input == null || input.isEmpty()) throw new IllegalArgumentException("Process list must not be empty");
    }
}
