import mpi.MPI;
import java.io.*;
import java.util.*;

public class SimulatorLog {
    private static final char DEFAULT_SEPARATOR = ',';
    private List<EventLog> m_eventLog;
    private int myRank = MPI.COMM_WORLD.Rank();
    private String path_root = System.getProperty("user.dir") + "/output/";
    private String path = System.getProperty("user.dir") + "/output/Pro" + Integer.toString(myRank) + "/";

    class EventLog {
        String airFlightNum;
        int eventTime;
        int eventPriority;
        int eventType;

        EventLog(String airFlightNum, int eventTime, int eventPriority, int eventType) {
            this.airFlightNum = airFlightNum;
            this.eventTime = eventTime;
            this.eventPriority = eventPriority;
            this.eventType = eventType;
        }
    }
    SimulatorLog() {
        m_eventLog = new ArrayList<EventLog>();
        deleteDir(new File(path_root));
    }

    void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    public void saveSimulatorParameter(int simTotalTime, int simStartTime, String fileName) throws IOException {
        File file = new File(path_root + fileName);

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new IOException("Unable to create " + file.getParentFile());

        BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
        try{
            writeLine(writer, Arrays.asList("SimulationTotalTime", "SimulationStartTime", "TimeUnit"));
            writeLine(writer, Arrays.asList(Integer.toString(simTotalTime), Integer.toString(simStartTime), "second"));
        } finally {
            writer.close();
        }
    }

    public void saveAirplane(List<AirFlight> airFlights, String fileName) throws IOException {
        File file = new File(path_root + fileName);

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new IOException("Unable to create " + file.getParentFile());

        BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
        try{
            writeLine(writer, Arrays.asList("airplaneType", "maxNumPassenger", "cruiseSpeed", "maxFuelCapacity", "fuelWarningThreshold"));
            for (AirFlight af : airFlights) {
                writeLine(writer, Arrays.asList(af.getAirplaneType(), Integer.toString(af.getMaxNumPassenger()), Double.toString(af.getCruisingSpeed()), Integer.toString(af.getMaxFuelCapacity()), Double.toString(af.getFuelWarningThreshold())));
            }
        } finally {
            writer.close();
        }
    }

    public void saveAirport(List<Airport> airports, String fileName) throws IOException {
        File file = new File(path + fileName);

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new IOException("Unable to create " + file.getParentFile());

        BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
        try{
            writeLine(writer, Arrays.asList("airportName", "cityName", "takeOffTime", "landTime", "latitude", "longitude", "runwayNum"));
            for (Airport ap : airports) {
                writeLine(writer, Arrays.asList(ap.getAirportName(), ap.getCityName(), Integer.toString(ap.getRunwayTimeToTakeoff()),
                        Integer.toString(ap.getRunwayTimeToLand()), Double.toString(ap.getLatitude()),
                        Double.toString(ap.getLongitude()), Integer.toString(ap.getRunwayNum())));
            }
        } finally {
            writer.close();
        }
    }

    public void saveScheduledEvent(List<Event> scheduledEvents, String fileName) throws IOException {
        File file = new File(path + fileName);

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new IOException("Unable to create " + file.getParentFile());

        BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
        try{
            writeLine(writer, Arrays.asList("airplaneType", "airFlightNum", "originAirport", "destinationAirport", "curNumPassenger", "scheduledTakeoffTime", "scheduledDepartingTime", "scheduledArrivingTime"));
            for (Event e : scheduledEvents) {
                AirportEvent ae = (AirportEvent) e;
                AirFlight af = ae.getAirFlight();
                writeLine(writer, Arrays.asList(af.getAirplaneType(), af.getAirFlightNum(), af.getOriginAirport().getAirportName(),
                        af.getDestinationAirport().getAirportName(), Integer.toString(af.getCurNumPassenger()), Integer.toString(af.getScheduledTakeoffTime()),
                        Integer.toString(af.getScheduledDepartingTime()), Integer.toString(af.getScheduledArrivingTime())));
            }
        } finally {
            writer.close();
        }
    }

    public void saveEventLog(String fileName) throws IOException {
        File file = new File(path + fileName);

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new IOException("Unable to create " + file.getParentFile());

        BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
        try{
            writeLine(writer, Arrays.asList("airFlightNum", "eventTime", "eventPriority", "eventType"));
            for (EventLog eventLog : m_eventLog) {
//                System.out.println("LOG: " + ae.getAirFlight().getAirFlightNum() + " getTime(): " + ae.getTime());
//                AirFlight af = ((AirportEvent)ae).getAirFlight();
                writeLine(writer, Arrays.asList(eventLog.airFlightNum, Integer.toString(eventLog.eventTime),
                        Integer.toString(eventLog.eventPriority), Integer.toString(eventLog.eventType)));
            }
        } finally {
            writer.close();
        }
    }

    public void addEventLog(Event event) {
        AirportEvent ae = (AirportEvent) event;
        AirFlight af = ae.getAirFlight();
        m_eventLog.add(new EventLog(af.getAirFlightNum(), ae.getTime(), ae.getEventPriority(), ae.getType()));
    }

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }
}
