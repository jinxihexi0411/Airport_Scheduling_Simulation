import java.io.Serializable;
import java.util.Random;

public class AirFlight implements Serializable {
    public static final int AIRFLIGHT_STATUS_FLYING = 0;
    public static final int AIRFLIGHT_STATUS_ARRIVING = 1;
    public static final int AIRFLIGHT_STATUS_LANDING = 2;
    public static final int AIRFLIGHT_STATUS_ONGROUND = 3;
    public static final int AIRFLIGHT_STATUS_TAKINGOFF = 4;
    public static final int AIRFLIGHT_STATUS_DEPARTING = 5;

    // Airplane information
    private String airplaneType;
    private String airFlightNum;
    private int maxNumPassenger;
    private double cruiseSpeed; // km/s
    private int maxFuelCapacity; // s
    private double fuelWarningThreshold;

    // Flight origin and destination airport
    private Airport originAirport;
    private Airport destinationAirport;

    // Passenger during the flight
    private int curNumPassenger;

    // Flight information
    private int info_scheduledTakeoffTime;
    private int info_scheduledArrivingTime;
    private int info_scheduledDepartingTime;
    private int info_actualTakeoffTime;
    private int info_flightTime;
    private int info_flightStatus;

    // Info for calculating flight time
    private double EARTH_RADIUS = 6378.137;

    public AirFlight(String airplaneType, int maxNumPassenger, double cruiseSpeed, int maxFuelCapacity, double fuelWarningThreshold) {
        this.airplaneType = airplaneType;
        this.maxNumPassenger = maxNumPassenger;
        this.cruiseSpeed = cruiseSpeed;
        this.maxFuelCapacity = maxFuelCapacity;
        this.fuelWarningThreshold = fuelWarningThreshold;
    }

    public AirFlight(AirFlight af) {
        this.airplaneType = af.getAirplaneType();
        this.maxNumPassenger = af.getMaxNumPassenger();
        this.cruiseSpeed = af.getCruisingSpeed();
        this.maxFuelCapacity = af.getMaxFuelCapacity();
        this.fuelWarningThreshold = af.getFuelWarningThreshold();
    }

    public String getAirFlightNum() {
        return this.airFlightNum;
    }

    public double getFuelWarningThreshold() {
        return this.fuelWarningThreshold;
    }

    public int getFlightStatus() {
        return this.info_flightStatus;
    }

    public int getMaxFuelCapacity() { return this.maxFuelCapacity; }

    public int getCurNumPassenger() {
        return this.curNumPassenger;
    }

    public String getAirplaneType() {
        return this.airplaneType;
    }

    public int getMaxNumPassenger() {
        return this.maxNumPassenger;
    }

    public double getCruisingSpeed() {
        return this.cruiseSpeed;
    }

    public int getFlightTime() {
        return this.info_flightTime;
    }

    public Airport getOriginAirport() {
        return this.originAirport;
    }

    public Airport getDestinationAirport() {
        return this.destinationAirport;
    }

    public int getScheduledTakeoffTime() { return this.info_scheduledTakeoffTime; }

    public int getScheduledDepartingTime() { return this.info_scheduledDepartingTime; }

    public int getScheduledArrivingTime() { return this.info_scheduledArrivingTime; }

    public void setTakeoffSchedule(Airport originAirport, Airport destinationAirport, String airFlightNum, int takeOffTime) {
        this.airFlightNum = airFlightNum;
        double minSOR = 0.8;
        setAirFlightRoute(originAirport, destinationAirport);
        setCurNumPassenger(minSOR);
        setFlightTime();
        this.info_scheduledTakeoffTime = takeOffTime;
        this.info_scheduledDepartingTime = takeOffTime + this.originAirport.getRunwayTimeToTakeoff();
        this.info_scheduledArrivingTime = (takeOffTime + this.originAirport.getRunwayTimeToTakeoff() + getFlightTime() + this.destinationAirport.getRunwayTimeToLand() + 5 * 60) / 60 * 60;
    }

    public void setFlightStatus(int status) {
        info_flightStatus = status;
    }

    private void setAirFlightRoute(Airport originAirport, Airport destinationAirport) {
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
    }

    private void setCurNumPassenger(double minSOR) {
        Random rand = new Random();
        double SOR = rand.nextDouble() * (1.0 - minSOR) + minSOR;
        this.curNumPassenger = (int)((double)getMaxNumPassenger() * SOR);
    }

    private void setActualTakeoffTime(int takeOffTime) {
        this.info_actualTakeoffTime = takeOffTime;
    }

    private void setFlightTime() {
        double lat1 = this.originAirport.getLatitude();
        double lng1 = this.originAirport.getLongitude();
        double lat2 = this.destinationAirport.getLatitude();
        double lng2 = this.destinationAirport.getLongitude();

        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));

        distance = distance * EARTH_RADIUS;
        distance = Math.round(distance * 10000) / 10000;

        this.info_flightTime = (int)(distance / getCruisingSpeed());
    }

    public boolean checkEmergency(int currentTime) {
        int flightTime = currentTime - this.info_actualTakeoffTime;
        int allowedFlightTime = (int)((1.0 - this.fuelWarningThreshold) * (double) this.maxFuelCapacity);
        return (flightTime > allowedFlightTime);
    }

    public boolean checkEmergency() {
        return getFlightTime() > (int)((1.0 - this.fuelWarningThreshold) * (double) this.maxFuelCapacity);
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}
