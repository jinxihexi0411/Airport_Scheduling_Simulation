public class MPIMessage {
    private int targetRank;
    private Event event;

    MPIMessage(int targetRank, Event event) {
        this.targetRank = targetRank;
        this.event = event;
    }

    public int getTargetRank() {
        return this.targetRank;
    }

    public Event getEvent() {
        return this.event;
    }
}
