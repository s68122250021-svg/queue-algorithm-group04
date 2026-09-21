import java.util.List;

// โปรแกรมตรวจสอบว่าผลลัพธ์หลักของงานตรงกับค่าที่คาดไว้หรือไม่
public class Validation {
    // จุดเริ่มต้นของโปรแกรมตรวจสอบ
    public static void main(String[] args) {
        // สร้างชุดข้อมูล P1-P4 ตามโจทย์
        List<Process> required = List.of(
                new Process("P1", 0, 8),
                new Process("P2", 0, 4),
                new Process("P3", 0, 9),
                new Process("P4", 0, 5)
        );

        // รัน FCFS แล้วเก็บ Metrics ที่ได้
        QueueScheduler.Metrics fcfs = QueueScheduler.fcfs(required).metrics();
        // รัน Round Robin ด้วย Quantum = 3 แล้วเก็บ Metrics ที่ได้
        QueueScheduler.Metrics rr = QueueScheduler.roundRobin(required, 3).metrics();

        // ตรวจ Average Waiting Time ของ FCFS
        checkClose("FCFS average waiting", 10.25, fcfs.averageWaitingTime());
        // ตรวจ Average Turnaround Time ของ FCFS
        checkClose("FCFS average turnaround", 16.75, fcfs.averageTurnaroundTime());
        // ตรวจ Average Waiting Time ของ Round Robin
        checkClose("RR average waiting", 15.00, rr.averageWaitingTime());
        // ตรวจ Average Turnaround Time ของ Round Robin
        checkClose("RR average turnaround", 21.50, rr.averageTurnaroundTime());

        // ตรวจจำนวน Context Switch ของ Round Robin
        if (rr.contextSwitches() != 9) {
            throw new AssertionError("RR context switches expected 9 but got " + rr.contextSwitches());
        }

        // ตรวจว่า Cancel สามารถนำ P2 ออกจาก List ได้จริง
        List<Process> cancelled = QueueOperations.cancel(required, "P2");
        if (cancelled.size() != 3 || cancelled.stream().anyMatch(p -> p.getId().equals("P2"))) {
            throw new AssertionError("Cancel operation did not remove P2 correctly");
        }

        // ถ้าไม่เกิด AssertionError แปลว่าการตรวจสอบทั้งหมดผ่าน
        System.out.println("All required checks passed.");
    }

    // เปรียบเทียบค่าจริงกับค่าที่คาดไว้ โดยยอมให้ต่างกันเล็กน้อย
    private static void checkClose(String label, double expected, double actual) {
        // ถ้าความต่างมากกว่า 1e-9 ให้ถือว่าผลตรวจสอบไม่ผ่าน
        if (Math.abs(expected - actual) > 1e-9) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }
}
