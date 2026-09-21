import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// โปรแกรมทดลองเวลาในการทำงานของ FCFS และ Round Robin
public class Experiment {
    // ขนาดข้อมูลที่ต้องทดลองตามโจทย์
    private static final int[] SIZES = {100, 1_000, 10_000, 50_000};
    // จำนวนรอบที่วัดจริง แล้วนำมาหาค่าเฉลี่ย
    private static final int ROUNDS = 5;
    // จำนวนรอบเตรียมเครื่องก่อนเริ่มวัดจริง
    private static final int WARM_UP = 3;
    // Time Quantum ของ Round Robin
    private static final int QUANTUM = 3;
    // Seed เดิมช่วยให้สร้างข้อมูลทดลองซ้ำได้เหมือนเดิม
    private static final long SEED = 20260908L;

    // จุดเริ่มต้นของโปรแกรมทดลอง
    public static void main(String[] args) {
        // พิมพ์หัวตารางให้นำผลไปใช้ต่อในรายงานได้ง่าย
        System.out.println("n,FCFS_avg_ns,RoundRobin_avg_ns");

        // ทดลองทีละขนาดข้อมูล เช่น 100, 1,000, 10,000 และ 50,000
        for (int n : SIZES) {
            // สร้างชุด Process สำหรับขนาด n
            List<Process> data = generate(n);

            // Warm-up: รันก่อนวัดจริงเพื่อลดผลจากการเริ่มต้นของโปรแกรม
            for (int i = 0; i < WARM_UP; i++) {
                QueueScheduler.fcfs(data);
                QueueScheduler.roundRobin(data, QUANTUM);
            }

            // วัดเวลา FCFS 5 รอบแล้วหาเวลาเฉลี่ย
            long fcfs = averageNanos(data, true);
            // วัดเวลา Round Robin 5 รอบแล้วหาเวลาเฉลี่ย
            long rr = averageNanos(data, false);
            // แสดงผลเป็น n, เวลาเฉลี่ย FCFS, เวลาเฉลี่ย Round Robin
            System.out.printf("%d,%d,%d%n", n, fcfs, rr);
        }
    }

    // วัดเวลา Algorithm ที่เลือกหลายรอบ แล้วคืนค่าเฉลี่ยเป็น nanosecond
    private static long averageNanos(List<Process> data, boolean fcfs) {
        long sum = 0;
        // ทำซ้ำตามจำนวนรอบที่กำหนด
        for (int i = 0; i < ROUNDS; i++) {
            // บันทึกเวลาเริ่มต้น
            long start = System.nanoTime();
            // ถ้า fcfs เป็น true ให้รัน FCFS ไม่เช่นนั้นให้รัน Round Robin
            if (fcfs) QueueScheduler.fcfs(data);
            else QueueScheduler.roundRobin(data, QUANTUM);
            // เวลาที่ใช้ = เวลาจบลบเวลาเริ่ม แล้วสะสมไว้ใน sum
            sum += System.nanoTime() - start;
        }
        // คืนค่าเวลาเฉลี่ยจากทั้งหมด ROUNDS รอบ
        return sum / ROUNDS;
    }

    // สร้างข้อมูลทดลองจำนวน n Process
    private static List<Process> generate(int n) {
        // ใช้ Seed เดิมร่วมกับ n เพื่อให้ชุดข้อมูลทำซ้ำได้
        Random random = new Random(SEED + n);
        ArrayList<Process> list = new ArrayList<>(n);

        // สร้าง Process ให้ครบ n ตัว
        for (int i = 0; i < n; i++) {
            // Process ตัวแรกเริ่มที่เวลา 0 ส่วนตัวอื่นสุ่ม Arrival Time
            int arrival = i == 0 ? 0 : random.nextInt(Math.max(1, n / 10 + 1));
            // สุ่ม Burst Time ให้อยู่ในช่วง 1 ถึง 20
            int burst = 1 + random.nextInt(20);
            // เพิ่ม Process ที่สร้างแล้วลงใน List
            list.add(new Process("P" + (i + 1), arrival, burst));
        }
        return list;
    }
}
