import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// คลาสหลักที่ใช้คำนวณ CPU Scheduling ของ FCFS และ Round Robin
public final class QueueScheduler {
    // เก็บช่วงเวลาที่ Process ได้ใช้ CPU และเวลาที่เหลือหลังจบช่วงนั้น
    public record Execution(String processId, int start, int end, int remainingAfter) {}

    // เก็บผลการวัด เช่น Waiting Time, Turnaround Time และ Context Switch
    public record Metrics(Map<String, Integer> waitingTime, Map<String, Integer> turnaroundTime,
                          double averageWaitingTime, double averageTurnaroundTime, int contextSwitches) {}

    // รวมรายการ Execution และ Metrics ไว้ในผลลัพธ์เดียว
    public record Result(List<Execution> execution, Metrics metrics) {}

    // ไม่ต้องสร้าง object ของ Scheduler เพราะเมธอดทั้งหมดเป็น static
    private QueueScheduler() {}

    // Algorithm A: FCFS ให้ Process ที่มาก่อนใช้ CPU ก่อน และทำจนเสร็จ
    public static Result fcfs(List<Process> input) {
        // ตรวจสอบข้อมูลก่อนเริ่มทำงาน
        validate(input);
        // คัดลอกข้อมูลเพื่อไม่ให้แก้ Process ต้นฉบับ
        List<Process> processes = copy(input);
        // เรียง Process ตาม Arrival Time จากน้อยไปมาก
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        // เก็บลำดับการทำงานของ CPU
        List<Execution> execution = new ArrayList<>();
        // เก็บเวลาที่ Process แต่ละตัวทำงานเสร็จ
        Map<String, Integer> completion = new HashMap<>();
        int time = 0;

        // ประมวลผล Process ทีละตัวตามลำดับ FCFS
        for (Process p : processes) {
            // ถ้า CPU ยังเร็วกว่าเวลาที่ Process เข้ามา ให้ขยับเวลาไปถึง Arrival Time
            time = Math.max(time, p.getArrivalTime());
            int start = time;
            // FCFS ให้ Process ใช้ CPU จนเสร็จในครั้งเดียว
            time += p.getBurstTime();
            // บันทึกช่วงเวลาที่ Process ได้ CPU
            execution.add(new Execution(p.getId(), start, time, 0));
            // บันทึกเวลาที่ Process เสร็จ
            completion.put(p.getId(), time);
        }

        // นำข้อมูลการทำงานไปคำนวณ Waiting Time และ Turnaround Time
        return buildResult(input, execution, completion, 0);
    }

    // Algorithm B: Round Robin แบ่งเวลาให้แต่ละ Process ตาม Time Quantum
    public static Result roundRobin(List<Process> input, int quantum) {
        // ตรวจสอบข้อมูลก่อนเริ่มทำงาน
        validate(input);
        // Quantum ต้องมากกว่า 0 ไม่เช่นนั้นจะไม่สามารถเดินเวลาได้
        if (quantum <= 0) throw new IllegalArgumentException("Time quantum must be > 0");

        // คัดลอกข้อมูลและเรียงตาม Arrival Time
        List<Process> processes = copy(input);
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        // Queue เก็บ Process ที่พร้อมรอใช้ CPU โดยใช้หลัก FIFO
        Deque<Process> queue = new ArrayDeque<>();
        // เก็บลำดับช่วงการทำงานของ CPU
        List<Execution> execution = new ArrayList<>();
        // เก็บเวลาที่ Process ทำงานเสร็จ
        Map<String, Integer> completion = new HashMap<>();
        int time = 0, next = 0, contextSwitches = 0;
        // จำ Process ก่อนหน้าเพื่อใช้ตรวจ Context Switch
        String last = null;

        // ทำต่อจนกว่า Process จะถูกนำเข้า Queue ครบและ Queue ว่าง
        while (next < processes.size() || !queue.isEmpty()) {
            // ถ้า Queue ว่าง ให้ขยับเวลาไปถึง Process ตัวถัดไปที่เข้ามา
            if (queue.isEmpty()) time = Math.max(time, processes.get(next).getArrivalTime());

            // นำ Process ที่มาถึงแล้วเข้า Queue ด้านท้าย
            while (next < processes.size() && processes.get(next).getArrivalTime() <= time) {
                queue.addLast(processes.get(next++));
            }

            // หยิบ Process ตัวหน้าสุดออกมาให้ CPU ทำงาน
            Process p = queue.removeFirst();
            // ถ้าเปลี่ยนจาก Process ก่อนหน้า ให้นับเป็น Context Switch
            if (last != null && !last.equals(p.getId())) contextSwitches++;
            last = p.getId();

            // รอบนี้ให้ CPU ทำงานไม่เกิน Quantum และไม่เกินเวลาที่เหลือ
            int run = Math.min(quantum, p.getRemainingTime());
            int start = time;
            // หักเวลาที่ Process ใช้ CPU
            p.runFor(run);
            // เดินเวลาไปตามจำนวนที่ Process ได้ทำงาน
            time += run;
            // บันทึกผลของช่วงเวลานี้
            execution.add(new Execution(p.getId(), start, time, p.getRemainingTime()));

            // ระหว่างที่ CPU ทำงาน อาจมี Process ใหม่เข้ามา จึงนำเข้า Queue ก่อน
            while (next < processes.size() && processes.get(next).getArrivalTime() <= time) {
                queue.addLast(processes.get(next++));
            }

            // ถ้ายังทำงานไม่เสร็จ ให้กลับไปต่อท้าย Queue เพื่อรอรอบถัดไป
            if (p.getRemainingTime() > 0) queue.addLast(p);
            // ถ้า Remaining Time เป็น 0 แปลว่า Process เสร็จแล้ว
            else completion.put(p.getId(), time);
        }

        // สรุปผล Waiting Time, Turnaround Time และ Context Switch
        return buildResult(input, execution, completion, contextSwitches);
    }

    // คำนวณค่าประเมินผลหลังจากได้ตารางการทำงานแล้ว
    private static Result buildResult(List<Process> input, List<Execution> execution,
                                      Map<String, Integer> completion, int contextSwitches) {
        // Map สำหรับเก็บ Waiting Time และ Turnaround Time ของแต่ละ Process
        Map<String, Integer> waiting = new HashMap<>();
        Map<String, Integer> turnaround = new HashMap<>();
        double waitSum = 0, turnSum = 0;

        // คำนวณค่าของ Process แต่ละตัว
        for (Process p : input) {
            // Turnaround Time = เวลาที่เสร็จ - เวลาที่เข้ามา
            int ta = completion.get(p.getId()) - p.getArrivalTime();
            // Waiting Time = Turnaround Time - Burst Time
            int wt = ta - p.getBurstTime();
            waiting.put(p.getId(), wt);
            turnaround.put(p.getId(), ta);
            waitSum += wt;
            turnSum += ta;
        }

        // หาค่าเฉลี่ยโดยหารด้วยจำนวน Process ทั้งหมด
        Metrics metrics = new Metrics(waiting, turnaround,
                waitSum / input.size(), turnSum / input.size(), contextSwitches);
        // ส่ง Execution และ Metrics กลับไปเป็น Result
        return new Result(List.copyOf(execution), metrics);
    }

    // สร้างสำเนา Process เพื่อให้แต่ละ Algorithm ใช้ข้อมูลแยกจากกัน
    private static List<Process> copy(List<Process> input) {
        List<Process> copy = new ArrayList<>();
        // สร้าง Process ใหม่จากข้อมูลเดิมทีละตัว
        for (Process p : input) copy.add(new Process(p.getId(), p.getArrivalTime(), p.getBurstTime()));
        return copy;
    }

    // ตรวจสอบว่า List ที่ส่งเข้ามาไม่เป็น null และไม่ว่าง
    private static void validate(List<Process> input) {
        if (input == null || input.isEmpty()) throw new IllegalArgumentException("Process list must not be empty");
    }
}
