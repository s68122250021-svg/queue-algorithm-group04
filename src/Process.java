// คลาสนี้เก็บข้อมูลของ Process 1 ตัวที่ใช้ในงาน CPU Scheduling
public final class Process {
    // รหัส Process เช่น P1, P2
    private final String id;
    // เวลาที่ Process เข้ามาถึงระบบ
    private final int arrivalTime;
    // เวลาทั้งหมดที่ Process ต้องใช้ CPU
    private final int burstTime;
    // เวลาที่ Process ยังทำงานเหลืออยู่
    private int remainingTime;

    // Constructor: สร้าง Process และตรวจสอบข้อมูลเบื้องต้น
    public Process(String id, int arrivalTime, int burstTime) {
        // ไม่อนุญาตให้รหัสว่างหรือเป็น null
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Process ID must not be blank");
        }
        // Arrival Time และ Burst Time ต้องไม่ติดลบ
        if (arrivalTime < 0 || burstTime < 0) {
            throw new IllegalArgumentException("Arrival and burst time must be non-negative");
        }
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        // ตอนเริ่มต้น Remaining Time เท่ากับ Burst Time
        this.remainingTime = burstTime;
    }

    // คืนรหัสของ Process เช่น P1
    public String getId() { return id; }

    // คืนเวลาที่ Process เข้ามาถึง
    public int getArrivalTime() { return arrivalTime; }

    // คืนเวลาที่ Process ต้องใช้ CPU ทั้งหมด
    public int getBurstTime() { return burstTime; }

    // คืนเวลาที่ Process ยังทำงานเหลืออยู่
    public int getRemainingTime() { return remainingTime; }

    // รีเซ็ต Remaining Time ให้กลับไปเท่ากับ Burst Time เดิม
    public void reset() { remainingTime = burstTime; }

    // จำลองการให้ Process ใช้ CPU ตามจำนวนเวลาที่ระบุ
    public void runFor(int amount) {
        // ตรวจว่าจำนวนเวลาที่ให้ CPU ไม่ติดลบและไม่เกินเวลาที่เหลือ
        if (amount < 0 || amount > remainingTime) {
            throw new IllegalArgumentException("Invalid CPU run amount");
        }
        // หักเวลาที่ใช้ CPU ออกจากเวลาที่เหลือ
        remainingTime -= amount;
    }

    // แปลงข้อมูล Process เป็นข้อความที่อ่านง่ายเวลาแสดงผล
    @Override
    public String toString() {
        return id + "(AT=" + arrivalTime + ", BT=" + burstTime + ")";
    }
}
