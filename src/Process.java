public final class Process {
    private final String id;
    private final int arrivalTime;
    private final int burstTime;
    private int remainingTime;

    public Process(String id, int arrivalTime, int burstTime) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Process ID must not be blank");
        }
        if (arrivalTime < 0 || burstTime < 0) {
            throw new IllegalArgumentException("Arrival and burst time must be non-negative");
        }
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }

    public String getId() { return id; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingTime() { return remainingTime; }

    public void reset() { remainingTime = burstTime; }

    public void runFor(int amount) {
        if (amount < 0 || amount > remainingTime) {
            throw new IllegalArgumentException("Invalid CPU run amount");
        }
        remainingTime -= amount;
    }

    @Override
    public String toString() {
        return id + "(AT=" + arrivalTime + ", BT=" + burstTime + ")";
    }
}
