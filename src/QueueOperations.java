import java.util.ArrayList;
import java.util.List;

// คลาสรวมคำสั่งเสริมที่ใช้จัดการข้อมูลใน Queue
public final class QueueOperations {
    // ไม่ต้องสร้าง object ของคลาสนี้ เพราะใช้เมธอด static
    private QueueOperations() {}

    // สร้าง List ใหม่โดยลบ Process ตัวแรกที่มี ID ตรงกับ processId
    // ข้อมูล Queue ต้นฉบับจะไม่ถูกแก้ไขโดยตรง
    public static List<Process> cancel(List<Process> queue, String processId) {
        // ตรวจว่า Queue ที่ส่งเข้ามาไม่ใช่ null
        if (queue == null) throw new IllegalArgumentException("Queue must not be null");

        // List นี้จะเก็บผลลัพธ์หลังจากลบ Process แล้ว
        List<Process> result = new ArrayList<>();
        // ใช้บอกว่าเราเคยลบ Process ที่ตรงกันไปแล้วหรือยัง
        boolean removed = false;

        // เดินดู Process ทุกตัวใน Queue ตามลำดับ
        for (Process p : queue) {
            // ลบเฉพาะตัวแรกที่ ID ตรงกัน และยังไม่เคยลบ
            if (!removed && p.getId().equals(processId)) {
                removed = true;
            } else {
                // ถ้าไม่ใช่ตัวที่ต้องลบ ให้เก็บไว้ใน List ใหม่
                result.add(p);
            }
        }

        // ส่ง Queue/List ใหม่กลับไป
        return result;
    }
}
