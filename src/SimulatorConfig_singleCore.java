import java.io.IOException;
import java.util.*;

public class SimulatorConfig_singleCore {
    protected List<Airport> airports;
    protected List<AirFlight> airFlights;
    protected int simTotalTime;
    protected int simStartTime;
    protected List<Event> scheduledEvent;
    protected int flightNumPerAirport = 100;
    protected double flightTimeThreshold = 0.5;
    protected int takeOff_landingTime = 1 * 60;
    protected int initialFlightNum = 10000;

    public SimulatorConfig_singleCore(int simTotalTime, int simStartTime) {
        this.simTotalTime = simTotalTime; // 24 hours or 1440 mins;
        this.simStartTime = simStartTime;
        this.airports = new ArrayList<Airport>();
        this.airFlights = new ArrayList<AirFlight>();
        this.scheduledEvent = new ArrayList<Event>();
    }

    public void initialSimulation() {
        initialAirportList();
        initialAirFlightList();
        initialTakeoffEvent(airports, airports, airFlights);
    }

    public void stopSimulator() {
        Simulator.stopAt(simTotalTime);
    }

    public void runSimulator() {
        Simulator.run();
    }

    protected void initialAirFlightList() {
        AirFlight be747 = new AirFlight("Boeing 747", 276, 990.0/3600.0, 15000, 0.05);
        AirFlight ab330 = new AirFlight("Airbus 330", 335, 870.0/3600.0, 15000, 0.05);
        AirFlight ab380 = new AirFlight("Airbus 380", 652, 1040.0/3600.0, 15000, 0.05);
        airFlights.add(be747);
        airFlights.add(ab330);
        airFlights.add(ab380);
    }

    protected void initialAirportList() {
        Airport lax = new Airport("LAX", "Los Angeles CA", takeOff_landingTime, takeOff_landingTime, 33.9428209, -118.4092766, 2);
        airports.add(lax);
        Airport jfk = new Airport("JFK", "New York City NY", takeOff_landingTime, takeOff_landingTime,40.639925, -73.7786950, 2);
        airports.add(jfk);
        Airport atl = new Airport("ATL","Atlanta GA", takeOff_landingTime, takeOff_landingTime,33.6366996, -84.4278640, 2);
        airports.add(atl);
        Airport den = new Airport("DEN","Denver CO", takeOff_landingTime, takeOff_landingTime, 39.8616667, -104.6731667, 2);
        airports.add(den);
        Airport org = new Airport("ORD", "Chicago IL", takeOff_landingTime, takeOff_landingTime, 41.9773201,-87.9080059, 2);
        airports.add(org);
        Airport sfo = new Airport("SFO", "San Francisco CA", takeOff_landingTime, takeOff_landingTime, 37.6155109,-122.3916901, 2);
        airports.add(sfo);
        Airport	mci = new Airport("MCI", "Kansas City MO", takeOff_landingTime, takeOff_landingTime,39.2975,-94.713889,3);
        airports.add(mci);
        Airport	cle = new Airport("CLE", "Cleveland OH", takeOff_landingTime, takeOff_landingTime,41.411667,-81.849722,3);
        airports.add(cle);
        Airport	lit = new Airport("LIT", "Little Rock AR", takeOff_landingTime, takeOff_landingTime,34.729444,-92.224722,3);
        airports.add(lit);
        Airport	mco = new Airport("MCO", "Orlando FL", takeOff_landingTime, takeOff_landingTime,28.4293915,-81.30899459,4);
        airports.add(mco);
        Airport	men = new Airport("MEM", "Memphis TN", takeOff_landingTime, takeOff_landingTime,35.0425,-89.976667,4);
        airports.add(men);
        Airport	phx = new Airport("KPHX", "Phoenix AZ", takeOff_landingTime, takeOff_landingTime,33.43427785,-112.0115836,3);
        airports.add(phx);
        Airport	nkx = new Airport("KNKX", "San Diego CA", takeOff_landingTime, takeOff_landingTime,32.867778,-117.141667,2);
        airports.add(nkx);
        Airport	mia = new Airport("KMIA", "Miami FL", takeOff_landingTime, takeOff_landingTime,25.79324976,-80.29055581,4);
        airports.add(mia);
        Airport	cvg = new Airport("KCVG", "Hebron KY", takeOff_landingTime, takeOff_landingTime,39.048889,-84.667778,4);
        airports.add(cvg);
        Airport	KMSY = new Airport("KMSY", "New Orleans LA", takeOff_landingTime, takeOff_landingTime,29.993333,-90.258056,2);
        airports.add(KMSY);
        Airport	KBOS = new Airport("KBOS", "Boston MA", takeOff_landingTime, takeOff_landingTime,42.363056,-71.006389,6);
        airports.add(KBOS);
        Airport	KDTW = new Airport("KDTW", "Detroit MI", takeOff_landingTime, takeOff_landingTime,42.2125,-83.353333,6);
        airports.add(KDTW);
        Airport	KMSP = new Airport("KMSP", "Minneapolis MN", takeOff_landingTime, takeOff_landingTime,44.881944,-93.221667,4);
        airports.add(KMSP);
        Airport	KMCI = new Airport("KMCI", "Kansas City MO", takeOff_landingTime, takeOff_landingTime,39.2975,-94.713889,3);
        airports.add(KMCI);
        Airport	KSTL = new Airport("KSTL", "St. Louis MO", takeOff_landingTime, takeOff_landingTime,38.747222,-90.361389,4);
        airports.add(KSTL);
        Airport	KCLT = new Airport("KCLT", "Charlotte NC", takeOff_landingTime, takeOff_landingTime,35.213889,-80.943056,4);
        airports.add(KCLT);
        Airport	KPHL = new Airport("KPHL", "Philadelphia PA", takeOff_landingTime, takeOff_landingTime,39.871944,-75.241111,4);
        airports.add(KPHL);
        Airport	KPIT = new Airport("KPIT", "Pittsburgh PA", takeOff_landingTime, takeOff_landingTime,40.491389,-80.232778,4);
        airports.add(KPIT);
        Airport	KSLC = new Airport("KSLC", "Salt Lake City UT", takeOff_landingTime, takeOff_landingTime,40.788333,-111.977778,4);
        airports.add(KSLC);
        Airport	KSEA = new Airport("KSEA", "Seattle WA", takeOff_landingTime, takeOff_landingTime,47.448889,-122.309444,3);
        airports.add(KSEA);
        Airport	KSAV = new Airport("KSAV", "Savannah GA", takeOff_landingTime, takeOff_landingTime,32.1275,-81.202222,2);
        airports.add(KSAV);
    }

    protected void initialTakeoffEvent(List<Airport> oriAirports, List<Airport> desAirports, List<AirFlight> airFlights) {
        Random rand = new Random();
        int lastTime = (int)(simTotalTime * flightTimeThreshold) - simStartTime;

        for (int i = 0; i < oriAirports.size(); i++) {
            Airport oriAirport = oriAirports.get(i);

            for (int j = 0; j < flightNumPerAirport; j++) {
                // Initialize takeoff time from origin airport and calculate expected time arriving at destination airport
                int takeOffTime = (rand.nextInt(lastTime) + simStartTime) / 60 * 60;
//                int takeOffTime = 0;
                // Initialize destination airport
                int randomDesAirportIndex = rand.nextInt(desAirports.size());
                while (desAirports.get(randomDesAirportIndex).getAirportName().equals(oriAirport.getAirportName())) {
                    randomDesAirportIndex = rand.nextInt(desAirports.size());
                }
                Airport desAirport = desAirports.get(randomDesAirportIndex);

                // Initialize the airplane -> 1. Type 2. current passenger number
                AirFlight outboundFlight = new AirFlight(airFlights.get(rand.nextInt(airFlights.size())));
                outboundFlight.setTakeoffSchedule(oriAirport, desAirport, "UA" + String.valueOf(initialFlightNum++), takeOffTime);
                outboundFlight.setFlightStatus(AirFlight.AIRFLIGHT_STATUS_ONGROUND);

                // Schedule outbound flight event
                AirportEvent outBoundFlightEvent = new AirportEvent(takeOffTime, oriAirport, outboundFlight, AirportEvent.PLANE_TAKEOFF);
                Simulator.schedule(outBoundFlightEvent);

                // Save the scheduled flight event locally
                this.scheduledEvent.add(outBoundFlightEvent);
            }
        }
    }

    public void printEventLog() {
        try {
            Simulator.getLog().saveSimulatorParameter(simTotalTime, simStartTime, "log_1core_simulatorSetup.csv");
            Simulator.getLog().saveAirplane(airFlights, "log_1core_airplane.csv");
            Simulator.getLog().saveAirport(airports, "log_1core_airports.csv");
            Simulator.getLog().saveScheduledEvent(scheduledEvent, "log_1core_scheduleEvent.csv");
            Simulator.getLog().saveEventLog("log_1core_event.csv");
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
