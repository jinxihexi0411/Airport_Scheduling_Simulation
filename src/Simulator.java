import java.util.HashMap;

//singleton
public class Simulator {

    //singleton
    private static SimulatorEngine instance = null;
    private static SimulatorLog log = null;

    private Simulator() {}

    public static SimulatorEngine getSim() {
        if(instance == null) {
            instance = new SimulatorEngine();
        }
        return instance;
    }

    public static SimulatorLog getLog() {
        if (log == null) {
            log = new SimulatorLog();
        }
        return log;
    }

    public static void stopAt(int time) {
        Event stopEvent = new SimulatorEvent(time, getSim(), SimulatorEvent.STOP_EVENT);
        schedule(stopEvent);
    }

    public static void run() {
        getSim().run();
    }

    public static void run(int lookAhead) { getSim().run(lookAhead); }

    public static int getCurrentTime() {
        return getSim().getCurrentTime();
    }

    public static void schedule(Event event) {
        event.setTime(event.getTime() + getSim().getCurrentTime());
        getSim().schedule(event);
    }

    public static void addMessageList(MPIMessage message) {
        getSim().addMessageList(message);
    }

    public static HashMap<String, Integer> getMap() {
        return getSim().getMap();
    }

    public static void setMap(HashMap<String, Integer> map) {
        getSim().setMap(map);
    }

    public static void addEventLog(Event event) {
        getLog().addEventLog(event);
    }
}
