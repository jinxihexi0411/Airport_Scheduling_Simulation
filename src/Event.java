import java.io.Serializable;

public class Event implements Comparable<Event>, Serializable {
    public static final int EVENT_NORMAL = 1;
    public static final int EVENT_EMERGENCY = 10;
    private EventHandler m_handler;
    private int m_time;
    private int m_eventId;
    private int m_eventType;
    private int m_eventPriority = EVENT_NORMAL;
    static private int m_nextId = 0;

    Event() {
        m_eventId = m_nextId++;
    }

    Event(int delay, EventHandler handler, int eventType) {
        this();
        m_time = delay;
        m_handler = handler;
        m_eventType = eventType;
    }

    public int getId() {
        return m_eventId;
    }

    public int getTime() {
        return m_time;
    }

    public int getType() {
        return m_eventType;
    }

    public int getEventPriority() { return m_eventPriority; }

    public EventHandler getHandler() {
        return m_handler;
    }

    public void setTime(int time) {
        m_time = time;
    }

    public void setEventPriority(int priorityIndicator) { m_eventPriority = priorityIndicator; }

    public void setHandler(EventHandler handler) {
        m_handler = handler;
    }

    @Override
    public int compareTo(Event event) {
        int timeCmp = Integer.valueOf(getTime()).compareTo(Integer.valueOf(event.getTime()));
        int priorityCmp = Integer.valueOf(event.getEventPriority()).compareTo(Integer.valueOf(getEventPriority()));

        if(timeCmp != 0) {
            return timeCmp;
        } else if (priorityCmp != 0) {
            return priorityCmp;
        }

        return Integer.valueOf(this.getId()).compareTo(Integer.valueOf(event.getId()));
    }
}
