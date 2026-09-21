import java.util.List;

// จุดเริ่มต้นของโปรแกรมสำหรับทดลอง FCFS, Round Robin และ Test Cases
public class Main {
    // Time Quantum ที่ใช้ใน Round Robin ตามโจทย์
    private static final int QUANTUM = 3;

    // เมธอดหลักที่โปรแกรมเริ่มทำงานจากตรงนี้
    public static void main(String[] args) {
        // สร้างชุด Process หลัก P1-P4 ตามข้อมูลโจทย์
        List<Process> required = List.of(
                new Process("P1", 0, 8),
                new Process("P2", 0, 4),
                new Process("P3", 0, 9),
                new Process("P4", 0, 5)
        );

        System.out.println("=== GROUP 04: CPU PROCESS SCHEDULING ===");
        System.out.println("Time Quantum = " + QUANTUM);
        // เรียก Algorithm FCFS แล้วแสดงผลลัพธ์
        print("FCFS", QueueScheduler.fcfs(required));
        // เรียก Algorithm Round Robin แล้วแสดงผลลัพธ์
        print("Round Robin", QueueScheduler.roundRobin(required, QUANTUM));
        // เรียก Test Cases ทั้งหมด
        printTests();
    }

    // แสดงลำดับการทำงานของ CPU และค่าประเมินผลของ Algorithm
    private static void print(String name, QueueScheduler.Result result) {
        System.out.println("\n--- " + name + " ---");
        // แสดงแต่ละช่วงว่า Process ไหนได้ CPU และเหลือเวลาเท่าไร
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

    // รวม Test Cases ที่ต้องการทดสอบไว้ในเมธอดเดียว
    private static void printTests() {
        System.out.println("\n=== 6 REQUIRED TEST CASES ===");
        // กรณีปกติ มีหลาย Process และใช้ทั้งสอง Algorithm
        runTest("1. Normal Case", List.of(
                new Process("P1", 0, 8), new Process("P2", 0, 4), new Process("P3", 0, 9), new Process("P4", 0, 5)));
        // กรณี Queue ว่าง ต้องไม่พยายามจัดตาราง Process
        runTest("2. Empty Queue", List.of());
        // กรณีมี Process เพียงตัวเดียว
        runTest("3. Single Item", List.of(new Process("P1", 0, 5)));
        // กรณี Queue ขนาดใหญ่ มี 100 Process
        runTest("4. Large Queue", largeProcesses(100));
        // กรณี Edge ที่ทุก Process มี Burst Time เท่ากัน
        runTest("5. Edge Case - Same Burst", List.of(
                new Process("P1", 0, 3), new Process("P2", 0, 3), new Process("P3", 0, 3)));

        // กรณี Cancel: ลบ Process ที่อยู่กลาง Queue และลอง ID ที่ไม่มีอยู่จริง
        System.out.println("\n6. Cancel Case - cancel middle item P2");
        List<Process> queue = List.of(
                new Process("P1", 0, 4), new Process("P2", 0, 2), new Process("P3", 0, 5));
        List<Process> cancelled = QueueOperations.cancel(queue, "P2");
        System.out.println("Before: " + queue);
        System.out.println("After cancelling P2: " + cancelled);
        System.out.println("Cancel non-existing P99: " + QueueOperations.cancel(queue, "P99"));
    }

    // รับ Test Case หนึ่งชุด แล้วแสดงค่าเฉลี่ย Waiting Time ของทั้งสอง Algorithm
    private static void runTest(String label, List<Process> processes) {
        System.out.println("\n" + label);
        // ถ้าไม่มี Process ให้แสดงข้อความและจบ Test Case นี้
        if (processes.isEmpty()) {
            System.out.println("Queue is empty -> no process scheduled.");
            return;
        }
        System.out.printf("FCFS avg wait = %.2f, RR avg wait = %.2f%n",
                QueueScheduler.fcfs(processes).metrics().averageWaitingTime(),
                QueueScheduler.roundRobin(processes, QUANTUM).metrics().averageWaitingTime());
    }

    // สร้าง Process จำนวน n ตัวสำหรับใช้ใน Test Case ขนาดใหญ่
    private static List<Process> largeProcesses(int n) {
        java.util.ArrayList<Process> list = new java.util.ArrayList<>();
        // วนสร้าง Process ให้ครบตามจำนวนที่กำหนด
        for (int i = 1; i <= n; i++) list.add(new Process("P" + i, 0, 1 + (i % 20)));
        return list;
    }
}
