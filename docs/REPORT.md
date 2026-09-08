# Group 04 Report - CPU Process Scheduling

## 1. Problem Analysis

**Case Study:** CPU Process Scheduling

The operating system must schedule several CPU processes. The assignment requires comparing FCFS, which follows FIFO order, with Round Robin, which uses a circular queue and a fixed time quantum.

### Input
- Process ID
- Arrival Time
- Burst Time
- Remaining Time
- Time Quantum = 3 for Round Robin

### Output
- CPU execution order
- Waiting Time
- Turnaround Time
- Context Switch count
- Average Waiting Time
- Average Turnaround Time
- Experimental execution time

### Constraints
- FCFS processes are served in arrival order.
- Round Robin gives each ready process at most 3 CPU time units per turn.
- If a process is unfinished, it returns to the rear of the circular queue.
- The required example does not specify Arrival Time, so this report assumes all four required processes arrive at time 0 in order P1, P2, P3, P4.

### What does the Queue store?
The ready Queue stores processes waiting for CPU time. Round Robin rotates unfinished processes from the front to the rear, giving each process a fair CPU opportunity.

### Why Queue?
Queue naturally represents the order of processes waiting for CPU service. FCFS directly follows FIFO. Round Robin extends the same idea by re-enqueuing unfinished processes after their time quantum.

## 2. Queue Design

- **FCFS:** FIFO Queue concept; the process at the front runs until completion.
- **Round Robin:** Circular Queue concept; the front process receives one quantum, then an unfinished process moves to the rear.

## 3. Pseudocode - Algorithm A: FCFS

```text
FCFS(processes)
    sort processes by Arrival Time
    while queue is not empty
        p <- dequeue()
        run p until completion
        record completion time
    end while
```

## 4. Pseudocode - Algorithm B: Round Robin

```text
ROUND_ROBIN(processes, quantum = 3)
    put arrived processes into circular queue
    while queue is not empty or unprocessed processes remain
        if queue is empty
            advance time to next arrival
        end if
        p <- dequeue()
        run <- min(quantum, p.remainingTime)
        execute p for run time units
        update remainingTime
        add newly arrived processes to queue
        if p.remainingTime > 0
            enqueue p at rear
        else
            record completion time
        end if
    end while
```

## 5. Queue Trace

### FCFS Trace

| Step | Operation | Queue Before | CPU | Queue After | Output |
|---:|---|---|---|---|---|
| 1 | ENQUEUE P1 | [] | - | [P1] | - |
| 2 | ENQUEUE P2 | [P1] | - | [P1,P2] | - |
| 3 | ENQUEUE P3 | [P1,P2] | - | [P1,P2,P3] | - |
| 4 | ENQUEUE P4 | [P1,P2,P3] | - | [P1,P2,P3,P4] | - |
| 5 | DEQUEUE | [P1,P2,P3,P4] | P1 | [P2,P3,P4] | P1 completes at 8 |
| 6 | DEQUEUE | [P2,P3,P4] | P2 | [P3,P4] | P2 completes at 12 |
| 7 | DEQUEUE | [P3,P4] | P3 | [P4] | P3 completes at 21 |
| 8 | DEQUEUE | [P4] | P4 | [] | P4 completes at 26 |

The queue changes because FIFO removes the process at the front first. A completed process leaves the ready queue permanently.

### Round Robin Trace (q = 3)

| Step | Time | Operation | Queue Before | CPU | Queue After |
|---:|---|---|---|---|---|
| 1 | 0-3 | DEQUEUE P1 | [P1,P2,P3,P4] | P1, 3 units | [P2,P3,P4,P1] |
| 2 | 3-6 | DEQUEUE P2 | [P2,P3,P4,P1] | P2, 3 units | [P3,P4,P1,P2] |
| 3 | 6-9 | DEQUEUE P3 | [P3,P4,P1,P2] | P3, 3 units | [P4,P1,P2,P3] |
| 4 | 9-12 | DEQUEUE P4 | [P4,P1,P2,P3] | P4, 3 units | [P1,P2,P3,P4] |
| 5 | 12-15 | DEQUEUE P1 | [P1,P2,P3,P4] | P1, 3 units | [P2,P3,P4,P1] |
| 6 | 15-16 | DEQUEUE P2 | [P2,P3,P4,P1] | P2, 1 unit | [P3,P4,P1] |
| 7 | 16-19 | DEQUEUE P3 | [P3,P4,P1] | P3, 3 units | [P4,P1,P3] |
| 8 | 19-21 | DEQUEUE P4 | [P4,P1,P3] | P4, 2 units | [P1,P3] |
| 9 | 21-23 | DEQUEUE P1 | [P1,P3] | P1, 2 units | [P3] |
| 10 | 23-26 | DEQUEUE P3 | [P3] | P3, 3 units | [] |

This satisfies the required 10+ operations. After each incomplete quantum, the process is placed at the rear. A completed process is removed permanently.

## 6. Correctness / Invariant

**FIFO invariant:** At the beginning of each scheduling step, the process at the front of the ready queue is the earliest eligible process according to the algorithm's queue order.

**FCFS:** Because a process is dequeued from the front and runs until completion, no later process can run before it. Therefore the execution order follows arrival order.

**Round Robin:** At each turn, only the front process receives CPU time. If it is unfinished, it is enqueued at the rear. Thus every waiting process eventually gets another turn as long as CPU work continues. This provides time sharing and prevents a ready process from being permanently bypassed under the basic fixed-queue assumptions.

## 7. Time Complexity

For a Queue implemented with `ArrayDeque`:

| Operation | Complexity |
|---|---:|
| enqueue() | O(1) amortized |
| dequeue() | O(1) |
| peek() | O(1) |
| search() | O(n) |
| display() | O(n) |

For the scheduling algorithms:

- **FCFS:** O(n log n) when sorting by arrival time is required; O(n) after the input is already in arrival order.
- **Round Robin:** O(k), where k is the number of time slices/context turns. With bounded burst times, k is proportional to the total CPU work divided into quanta. Queue operations themselves are O(1).

## 8. Space Complexity

Both algorithms require O(n) space for the process list and ready queue. Round Robin additionally stores remaining time for each process, still O(n) total auxiliary space.

## 9. Java Implementation

Implemented in `src/`:

- `Process.java` - process data model
- `QueueScheduler.java` - FCFS and Round Robin implementations
- `Main.java` - demonstration and required test cases
- `Experiment.java` - timing experiment

The implementation uses `ArrayDeque` as the queue structure because it supports constant-time insertion/removal at the queue ends and directly models FIFO/circular ready-queue behavior.

## 10. Test Cases

The assignment requires at least six cases: Normal, Empty Queue, Single Item, Large Queue, Special/Edge Case, and Cancel Case.

The project includes all six in `Main.java`.

**Important limitation:** cancellation is not a scheduling operation explicitly listed in Group 04's case story. Therefore the Cancel Case is represented as a pre-scheduling removal scenario rather than adding an unrelated scheduling command to the core algorithms. If the instructor expects a literal `CANCEL` operation, the group should add and document that operation separately.

## 11. Experimental Comparison

Required input sizes:

- n = 100
- n = 1,000
- n = 10,000
- n = 50,000

`Experiment.java` uses:

- `System.nanoTime()`
- warm-up runs
- a fixed seed
- average of 5 measured rounds

Run:

```bash
javac -d out src/*.java
java -cp out Experiment
```

The generated timing values are machine-dependent. They must be copied from the group's own machine into the final report; do not invent or reuse timing values from another computer.

### Expected theoretical trend

FCFS has low scheduling overhead because each process is dispatched once. Round Robin performs more queue rotations and can therefore have greater overhead, especially when the quantum is small relative to burst times. The measured values should be interpreted as an empirical observation, not as a replacement for the theoretical complexity analysis.

## 12. Algorithm Comparison

| Topic | FCFS | Round Robin |
|---|---|---|
| Data Structure | FIFO Queue | Circular Queue |
| Principle | First arrival runs first | Each process receives a time quantum |
| Enqueue | O(1) | O(1) |
| Dequeue | O(1) | O(1) |
| Waiting Time | Can be high for short jobs behind long jobs | More balanced for interactive workloads, depending on quantum |
| Space | O(n) | O(n) |
| Fairness | Arrival-order fairness | Time-sharing fairness |
| Advantage | Simple, low overhead | Responsive and prevents one ready process from monopolizing CPU |
| Limitation | Convoy effect; poor responsiveness when a long job is first | More context switches and quantum-selection overhead |
| Suitable case | Batch/non-interactive workloads | Interactive/time-sharing systems |

## Required Question: Which is more suitable for Interactive Systems?

**Round Robin** is generally more suitable for interactive systems because each ready process receives CPU time within a fixed quantum instead of waiting for an earlier process to finish completely. A suitable quantum balances responsiveness against context-switch overhead.

## 13. Required Questions / Discussion

1. **FCFS vs Round Robin:** FCFS is simple and has low overhead, but a long process can delay all later processes. Round Robin improves responsiveness by dividing CPU access into time slices.
2. **Waiting Time:** With the required all-arrival-at-0 assumption, FCFS average waiting time is **10.25** time units. Round Robin with q=3 has average waiting time **15.00** time units.
3. **Turnaround Time:** FCFS average turnaround time is **16.75**. Round Robin average turnaround time is **21.50**.
4. **Context Switch:** Round Robin has **9** process-to-process switches for the required example, excluding the initial dispatch.

## 14. Submission Checklist

- [x] Problem Analysis
- [x] Queue Design
- [x] Pseudocode Algorithm A
- [x] Pseudocode Algorithm B
- [x] Queue Trace >= 10 operations
- [x] Correctness / Invariant
- [x] Time Complexity
- [x] Space Complexity
- [x] Java Program
- [x] Test Cases >= 6
- [x] Experimental Comparison program
- [x] Algorithm comparison
- [ ] Final report formatting/personal details
- [ ] Slide Presentation
- [x] GitHub Repository structure
- [ ] Final Generative AI usage record

## 15. Generative AI Usage Record

Suggested disclosure:

> Generative AI was used as an assistant for understanding the assignment requirements, organizing the report structure, drafting pseudocode, reviewing algorithm logic, and generating an initial Java implementation. The students should verify, test, explain, and revise the final work themselves before submission.

## 16. Group Work Still Required

The following items should be completed by the students:

- Add group-member names, student IDs, course/instructor information.
- Run the experiment on the group's own computer and record the actual nanoTime results.
- Take screenshots or other evidence required by the instructor.
- Prepare the final report document and format it according to class requirements.
- Prepare the slide presentation.
- Explain the algorithms and code during presentation/defense.
- Review the Cancel Case requirement with the instructor if a literal cancel operation is expected.
