import mpi.MPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimulatorConfig_multiCore extends  SimulatorConfig_singleCore {
    public static final int rootRank = 0;
    private int myRank;
    private int processorNum;
    private List<Airport> localAirports;
    private HashMap<String, Integer> airportProcessorMap;

    public SimulatorConfig_multiCore(int simTotalTime, int simStartTime, int processorNum) {
        super(simTotalTime, simStartTime);

        this.airportProcessorMap = new HashMap<String, Integer>();
        this.localAirports = new ArrayList<Airport>();
        this.myRank = MPI.COMM_WORLD.Rank();
        this.processorNum = processorNum;
        this.initialFlightNum = this.initialFlightNum * (myRank + 1);
    }

    @Override
    public void initialSimulation() {
        this.initialLocalAirportList();
        super.initialAirFlightList();
        super.initialTakeoffEvent(localAirports, airports, airFlights);
    }

    private void initialLocalAirportList() {
        super.initialAirportList();
        for (int i = 0; i < airports.size(); i++) {
            int targetProcId = i % processorNum;
            Airport ap = airports.get(i);
            airportProcessorMap.put(ap.getAirportName(), targetProcId);
            if (targetProcId == myRank) {
                localAirports.add(ap);
            }
        }
        Simulator.setMap(airportProcessorMap);
    }

    public void runSimulator(int lookAhead) {
        Simulator.run(lookAhead);
    }

    public HashMap<String, Integer> getAirportProcessorMap() {
        return airportProcessorMap;
    }

    public void printEventLog() {
        try {
            if (MPI.COMM_WORLD.Rank() == rootRank) {
                Simulator.getLog().saveSimulatorParameter(simTotalTime, simStartTime, "log_multiCore_simulatorSetup.csv");
                Simulator.getLog().saveAirplane(airFlights, "log_multiCore_airplane.csv");
            }
            int myRank = MPI.COMM_WORLD.Rank();
            String head = "log_multiCore_";

            Simulator.getLog().saveAirport(localAirports, (head + "Rank" + Integer.toString(myRank) + "_localAirports.csv"));
            Simulator.getLog().saveScheduledEvent(scheduledEvent, (head+ "Rank" + Integer.toString(myRank) + "_scheduleEvent.csv"));
            Simulator.getLog().saveEventLog((head+ "Rank" + Integer.toString(myRank) + "_event.csv"));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
