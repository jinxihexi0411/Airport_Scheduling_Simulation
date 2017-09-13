import mpi.MPI;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class SimulatorEngine implements EventHandler {
    private static final int TAG_SEND_EVENT = 99;
    private int lookAhead;
    private int m_currentTime;
    private int myRank;
    private PriorityQueue<Event> m_eventList;
    private List<MPIMessage> m_messageList;
    private boolean m_running;
    private HashMap<String, Integer> m_map;

    SimulatorEngine() {
        m_running = false;
        m_currentTime = 0;
        m_eventList = new PriorityQueue<Event>();
        m_messageList = new LinkedList<MPIMessage>();
    }

    void run() {
        m_running = true;
        while (m_running && !m_eventList.isEmpty()) {
            Event event = m_eventList.poll();
            m_currentTime = event.getTime();
            event.getHandler().handle(event);
        }
    }

    void run(int lookAhead) {
        this.myRank = MPI.COMM_WORLD.Rank();
        this.lookAhead = lookAhead;
        m_running = true;

        while (isRunning() && !isEventListEmpty()) {
            int LBTStime =  getLBTS();
            while (!m_eventList.isEmpty()) {
                m_currentTime = m_eventList.peek().getTime();
                if (m_currentTime < LBTStime) {
                    Event event = m_eventList.poll();
                    event.getHandler().handle(event);
                } else {
                    break;
                }
            }

            MPI.COMM_WORLD.Barrier();
            sendMessage(2000);
        }
    }

    public void handle(Event event) {
        SimulatorEvent ev = (SimulatorEvent)event;

        switch(ev.getType()) {
            case SimulatorEvent.STOP_EVENT:
                m_running = false;
                System.out.println("Pro" + myRank + " - Simulator stopping at time: " + ev.getTime());
                break;
            default:
                System.out.println("Invalid event type");
        }
    }

    public void schedule(Event event) {
        m_eventList.add(event);
    }

    public void stop() {
        m_running = false;
    }

    public boolean isRunning() {
        boolean[] running = new boolean[1];
        running[0] = m_running;
        MPI.COMM_WORLD.Allreduce(running, 0, running, 0, 1, MPI.BOOLEAN, MPI.LAND);
        return running[0];
    }

    public int getCurrentTime() {
        return m_currentTime;
    }

    public int getLBTS() {
        int[] startTime = new int[1];
        int[] LBTS = new int[1];

        startTime[0] = m_eventList.isEmpty() ? Integer.MAX_VALUE : m_eventList.peek().getTime();
        MPI.COMM_WORLD.Allreduce(startTime, 0, LBTS, 0, 1, MPI.INT, MPI.MIN);
        return LBTS[0] + lookAhead;
    }

    public boolean isEventListEmpty() {
        boolean[] eventListEmpty = new boolean[1];
        eventListEmpty[0] = m_eventList.isEmpty();
        MPI.COMM_WORLD.Allreduce(eventListEmpty, 0, eventListEmpty, 0, 1, MPI.BOOLEAN, MPI.LAND);
        return eventListEmpty[0];
    }

    public void addMessageList(MPIMessage message) {
        m_messageList.add(message);
    }

    public void sendMessage(int bufferSize) {
        int[] revMessageNum = new int[MPI.COMM_WORLD.Size()];
        for (MPIMessage sendMessage : m_messageList) {
            if (sendMessage.getTargetRank() == myRank) {
                m_eventList.add(sendMessage.getEvent());
            } else {
                revMessageNum[sendMessage.getTargetRank()]++;
                sendHelper(sendMessage.getTargetRank(), sendMessage.getEvent(), bufferSize);
            }
        }
        m_messageList = new LinkedList<MPIMessage>();

        MPI.COMM_WORLD.Allreduce(revMessageNum, 0, revMessageNum, 0, MPI.COMM_WORLD.Size(), MPI.INT, MPI.SUM);
        for (int i = 0; i < revMessageNum[myRank]; i++) {
            receiveHelper(bufferSize);
        }

    }

    public void sendHelper(int targetRank, Event sendEvent, int bufferSize) {
        ByteBuffer byteBuff = ByteBuffer.allocateDirect(bufferSize + MPI.SEND_OVERHEAD);
        MPI.Buffer_attach(byteBuff);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(sendEvent);
            out.flush();
            byte[] bytes = bos.toByteArray();
            MPI.COMM_WORLD.Isend(bytes, 0, bytes.length, MPI.BYTE, targetRank, TAG_SEND_EVENT);
        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    public void receiveHelper(int bufferSize) {
        byte[] bytes = new byte[bufferSize];
        Event recvEvent = null;
        MPI.COMM_WORLD.Recv(bytes, 0, bufferSize, MPI.BYTE, MPI.ANY_SOURCE, TAG_SEND_EVENT);

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            recvEvent = (Event) in.readObject();
        }
        catch(IOException ex) {
            System.out.println(ex);
        }
        catch(ClassNotFoundException cnf) {
            System.out.println(cnf);
        }
        AirFlight af = ((AirportEvent) recvEvent).getAirFlight();

        m_eventList.add(recvEvent);
    }

    public HashMap<String, Integer> getMap() {
        return this.m_map;
    }

    public void setMap(HashMap<String, Integer> map) {
        this.m_map = map;
    }
 }
