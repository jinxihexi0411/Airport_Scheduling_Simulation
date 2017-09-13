//YOUR NAME HERE

public class AirportEvent extends Event {
    public static final int PLANE_ARRIVES = 2;
    public static final int PLANE_LANDED = 3;
    public static final int PLANE_TAKEOFF = 0;
    public static final int PLANE_DEPARTS = 1;

    private AirFlight airFlight;

    AirportEvent(int delay, EventHandler handler, AirFlight airFlight, int eventType) {
        super(delay, handler, eventType);
        this.airFlight = airFlight;
    }

    public AirFlight getAirFlight() {
        return this.airFlight;
    }
}
