# Queue Algorithm - Group 04

## CPU Process Scheduling

งานปฏิบัติการกลุ่ม: Queue Algorithm Design & Analysis with Java

### Case Study
Operating System จัดสรร CPU ให้หลาย Process โดยเปรียบเทียบ 2 Algorithms ตามโจทย์:

- **Algorithm A: First Come First Served (FCFS)** — ใช้ Queue แบบ FIFO
- **Algorithm B: Round Robin (RR)** — ใช้ Circular Queue
- **Time Quantum = 3**

ชุดข้อมูลบังคับ:

| Process | Burst Time |
|---|---:|
| P1 | 8 |
| P2 | 4 |
| P3 | 9 |
| P4 | 5 |

> **Assumption:** เนื่องจากโจทย์ไม่ได้ระบุ Arrival Time ของชุดข้อมูลบังคับ จึงกำหนดให้ P1, P2, P3, P4 มาถึงที่เวลา 0 และเข้าคิวตามลำดับดังกล่าว เพื่อให้สามารถคำนวณ Waiting Time และ Turnaround Time ได้อย่างชัดเจน

## โครงสร้าง Repository

```text
queue-algorithm-group04/
├─ README.md
├─ .gitignore
├─ src/
│  ├─ Process.java
│  ├─ QueueScheduler.java
│  ├─ Main.java
│  └─ Experiment.java
└─ docs/
   └─ REPORT.md
```

## สิ่งที่ครอบคลุม

- Problem Analysis
- Queue Design
- Pseudocode ของ FCFS และ Round Robin
- Queue Trace มากกว่า 10 operations/steps
- Correctness / FIFO invariant
- Time Complexity
- Space Complexity
- Java Implementation
- Test Cases 6 กรณี
- Experimental Comparison: n = 100, 1,000, 10,000, 50,000
- ตารางเปรียบเทียบ FCFS vs Round Robin
- สรุปความเหมาะสมกับ Interactive System
- บันทึกการใช้ Generative AI

## วิธีรัน

ใช้ JDK 17 หรือใหม่กว่า

```bash
javac -d out src/*.java
java -cp out Main
java -cp out Experiment
```

`Main` จะแสดงผล FCFS, Round Robin, trace, metrics และ test cases

`Experiment` จะ warm-up ก่อนวัดจริง และเฉลี่ย execution time 5 รอบต่อขนาด input โดยใช้ `System.nanoTime()` และ seed คงที่

## ผลคำนวณของชุดข้อมูลบังคับ

เมื่อทุก Process มาถึงที่เวลา 0:

### FCFS

ลำดับการทำงาน: `P1 -> P2 -> P3 -> P4`

- Waiting Time: P1=0, P2=8, P3=12, P4=21
- Average Waiting Time = **10.25**
- Turnaround Time: P1=8, P2=12, P3=21, P4=26
- Average Turnaround Time = **16.75**

### Round Robin (q = 3)

ลำดับช่วงเวลาที่ CPU ทำงาน:

`P1(0-3) -> P2(3-6) -> P3(6-9) -> P4(9-12) -> P1(12-15) -> P2(15-16) -> P3(16-19) -> P4(19-21) -> P1(21-23) -> P3(23-26)`

- Waiting Time: P1=15, P2=12, P3=17, P4=16
- Average Waiting Time = **15.00**
- Turnaround Time: P1=23, P2=16, P3=26, P4=21
- Average Turnaround Time = **21.50**
- Context switches = **9** (นับเฉพาะการเปลี่ยนจาก Process หนึ่งไปอีก Process หนึ่ง ไม่รวม initial dispatch)

## หมายเหตุ

ตัวเลข execution time ใน `Experiment` ต้องรันบนเครื่องจริงของผู้ส่งงาน เพราะขึ้นกับ CPU, JVM, OS และสภาพแวดล้อม การทดลองใน repository จึงเป็นโปรแกรมสำหรับสร้างผลการวัดตามวิธีที่โจทย์กำหนด ไม่ควรเขียนตัวเลขเวลาแบบตายตัวลงเป็นผลทดลองของเครื่องผู้ส่ง
