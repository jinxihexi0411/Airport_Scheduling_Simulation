import mpi.MPI;

public class SimulationMainFunction {
    public static void main(String[] args) {
//        int processorNum = Integer.parseInt(args[1]);
//        if (processorNum == 1) {
//            SimulatorConfig_singleCore airportSimulation = new SimulatorConfig_singleCore(86400,21600);
//            airportSimulation.initialSimulation();
//            airportSimulation.stopSimulator();
//            airportSimulation.runSimulator();
//            airportSimulation.printEventLog();
//        } else {
//            MPI.Init(args);
//            SimulatorConfig_multiCore airportSimulation = new SimulatorConfig_multiCore(86400,21600, processorNum);
//            airportSimulation.initialSimulation();
//            airportSimulation.stopSimulator();
//            airportSimulation.runSimulator(5 * 60, airportSimulation.getAirportProcessorMap());
////            airportSimulation.printEventLog();
//            MPI.Finalize();
//        }
//
        for (String i : args) {
            System.out.println(i);
        }

        MPI.Init(args);
        SimulatorConfig_multiCore airportSimulation = new SimulatorConfig_multiCore(86400,21600, MPI.COMM_WORLD.Size());
        airportSimulation.initialSimulation();
        airportSimulation.stopSimulator();
        final long startTime = System.currentTimeMillis();
        airportSimulation.runSimulator(5 * 60);
        final long endTime = System.currentTimeMillis();
        airportSimulation.printEventLog();
        MPI.Finalize();
        
        System.out.println("Running time is: " + (endTime - startTime));
    }
}
