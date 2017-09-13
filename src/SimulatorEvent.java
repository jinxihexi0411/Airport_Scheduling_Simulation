public class SimulatorEvent extends Event {
    public static final int STOP_EVENT = 99;

    SimulatorEvent(int delay, EventHandler handler, int eventType) {
        super(delay, handler, eventType);
    }
}
