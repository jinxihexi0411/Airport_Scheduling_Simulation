import mpi.MPI;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Airport implements EventHandler, Serializable {
    private String m_airportName;
    private String m_cityName;

    private int m_inTheAir;
    private int m_onTheGround;

    private int m_runwayTimeToLand;
    private int m_runwayTimeToTakeoff;
    private Integer[] m_runwayNextOpenTime;
    private HashMap<Integer, Integer> runwayMap = new HashMap<Integer, Integer>();

    private double m_latitude;
    private double m_longitude;

    private int m_runwayNum;
    private int m_runwayUseNum;

    private int stat_departPassengerNum;
    private int stat_arrivePassengerNum;
    private int stat_totalAirplaneCycleTime;
    private int stat_firstArriveTime;

    public Airport(String airportName, String cityName, int runwayTimeToLand, int runwayTimeToTakeoff, double latitude, double longtitude, int runwayNum) {
        m_airportName = airportName;
        m_cityName = cityName;

        m_inTheAir =  0;
        m_onTheGround = 0;

        m_runwayUseNum = 0;
        m_runwayNum = runwayNum;

        m_runwayTimeToTakeoff = runwayTimeToTakeoff;
        m_runwayTimeToLand = runwayTimeToLand;
        m_runwayNextOpenTime = new Integer[runwayNum];
        for (int i = 0; i < runwayNum; i++) {
            m_runwayNextOpenTime[i] = Integer.MAX_VALUE;
        }

        m_latitude = latitude;
        m_longitude = longtitude;

        stat_departPassengerNum = 0;
        stat_arrivePassengerNum = 0;
        stat_totalAirplaneCycleTime = 0;
        stat_firstArriveTime = 0;
    }

    public int getRunwayTimeToTakeoff() {
        return this.m_runwayTimeToTakeoff;
    }

    public int getRunwayTimeToLand() {
        return this.m_runwayTimeToLand;
    }

    public String getAirportName() {
        return this.m_airportName;
    }

    public String getCityName() {
        return this.m_cityName;
    }

    public double getLongitude() {
        return this.m_longitude;
    }

    public double getLatitude() {
        return this.m_latitude;
    }

    public int getInTheAirNum() { return this.m_inTheAir; }

    public int getOnTheGroundNum() { return this.m_onTheGround; }

    public boolean getRunwayFreeToUse() { return m_runwayUseNum < m_runwayNum; }

    public int getRunwayNum() { return this.m_runwayNum; }

    public int getRunwayUseNum() { return this.m_runwayUseNum; }

    private int getRunwayNextOpenTime() {
        return Collections.min(Arrays.asList(m_runwayNextOpenTime));
    }

    private void setFirstArrivingTime(int time) {
        this.stat_firstArriveTime = time;
    }

    private void setTotalAirplaneCycleTime(int time) {
        if (getInTheAirNum() > 1) {
            this.stat_totalAirplaneCycleTime += time - this.stat_firstArriveTime;
        }
        setFirstArrivingTime(time);
    }

    private void setRunwayInfo(int time, AirportEvent airportEvent) {
        if (runwayMap.containsKey(airportEvent.getId())) {
            m_runwayUseNum--;
            int i = runwayMap.get(airportEvent.getId());
            m_runwayNextOpenTime[i] = Integer.MAX_VALUE;
            runwayMap.remove(airportEvent.getId());
        } else {
            m_runwayUseNum++;
            int i = 0;
            while (i < this.m_runwayNum && m_runwayNextOpenTime[i] != Integer.MAX_VALUE) {
                i++;
            }
            runwayMap.put(airportEvent.getId(), i);

            if (airportEvent.getType() == AirportEvent.PLANE_LANDED) {
                m_runwayNextOpenTime[i] = time + this.m_runwayTimeToLand;
            } else {
                m_runwayNextOpenTime[i] = time + this.m_runwayTimeToTakeoff;
            }
        }

    }

    private void addDeparturePassengerNum(AirFlight departureFlight) {
        this.stat_departPassengerNum += departureFlight.getCurNumPassenger();
    }

    private void addArrivePassengerNum(AirFlight arrivalFlight) {
        this.stat_arrivePassengerNum += arrivalFlight.getCurNumPassenger();
    }

    public void handle(Event event) {
        AirportEvent airEvent = (AirportEvent) event;
//        System.out.println(airEvent.getType() + " " + airEvent.getAirFlight().getAirFlightNum());
        Simulator.addEventLog(airEvent);

        switch(airEvent.getType()) {
            case AirportEvent.PLANE_ARRIVES:
                if (airEvent.getAirFlight().getFlightStatus() == AirFlight.AIRFLIGHT_STATUS_FLYING) {
                    m_inTheAir++;
                    airEvent.getAirFlight().setFlightStatus(AirFlight.AIRFLIGHT_STATUS_ARRIVING);
                }

                if(getRunwayFreeToUse()) {
                    AirportEvent landedEvent = new AirportEvent(m_runwayTimeToLand, this, airEvent.getAirFlight(), AirportEvent.PLANE_LANDED);
                    airEvent.getAirFlight().setFlightStatus(AirFlight.AIRFLIGHT_STATUS_LANDING);
                    Simulator.schedule(landedEvent);
                    setRunwayInfo(Simulator.getCurrentTime(), landedEvent);

                } else {
                    int timeOffSet = getRunwayNextOpenTime() - Simulator.getCurrentTime() + 1;
                    airEvent.setTime(timeOffSet);

                    // Check emergency of the airflight
                    if (airEvent.getAirFlight().checkEmergency(Simulator.getCurrentTime())) {
                        airEvent.setEventPriority(AirportEvent.EVENT_EMERGENCY);
                    }
                    Simulator.schedule(airEvent);
                }
                break;

            case AirportEvent.PLANE_LANDED:
                m_inTheAir--;

                setRunwayInfo(Simulator.getCurrentTime(), airEvent);
                addArrivePassengerNum(airEvent.getAirFlight());
                airEvent.getAirFlight().setFlightStatus(AirFlight.AIRFLIGHT_STATUS_ONGROUND);
                break;

            case AirportEvent.PLANE_TAKEOFF:
                if (airEvent.getAirFlight().getFlightStatus() == AirFlight.AIRFLIGHT_STATUS_ONGROUND) {
                    m_onTheGround++;
                    airEvent.getAirFlight().setFlightStatus(AirFlight.AIRFLIGHT_STATUS_TAKINGOFF);
                }

                if(getRunwayFreeToUse()) {
                    AirportEvent departEvent = new AirportEvent(m_runwayTimeToTakeoff, this, airEvent.getAirFlight(), AirportEvent.PLANE_DEPARTS);
                    airEvent.getAirFlight().setFlightStatus(AirFlight.AIRFLIGHT_STATUS_DEPARTING);
                    setRunwayInfo(Simulator.getCurrentTime(), departEvent);
                    Simulator.schedule(departEvent);
                } else {
                    int timeOffSet = getRunwayNextOpenTime() - Simulator.getCurrentTime() + 1;
                    airEvent.setTime(timeOffSet);
                    Simulator.schedule(airEvent);
                }

                break;

            case AirportEvent.PLANE_DEPARTS:
                m_onTheGround--;
                setRunwayInfo(Simulator.getCurrentTime(), airEvent);
                addDeparturePassengerNum(airEvent.getAirFlight());

                AirFlight airFlight = airEvent.getAirFlight();
                airFlight.setFlightStatus(AirFlight.AIRFLIGHT_STATUS_FLYING);
                AirportEvent arriveEvent = new AirportEvent(airFlight.getFlightTime(), airFlight.getDestinationAirport(), airEvent.getAirFlight(), AirportEvent.PLANE_ARRIVES);
                if (arriveEvent.getAirFlight().checkEmergency()) {
                    arriveEvent.setEventPriority(Event.EVENT_EMERGENCY);
                }
                if (MPI.COMM_WORLD.Size() == 1) {
                    Simulator.schedule(arriveEvent);
                } else {
                    String desAirportName = airFlight.getDestinationAirport().getAirportName();
                    Simulator.addMessageList(new MPIMessage(Simulator.getMap().get(desAirportName), arriveEvent));
                }

                break;
        }

        setTotalAirplaneCycleTime(Simulator.getCurrentTime());
    }
}
