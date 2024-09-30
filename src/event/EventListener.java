package event;

import java.util.ArrayList;

public class EventListener<T> {
    private final ArrayList<Event<T>> eventList;
    public EventListener() {
        eventList = new ArrayList<>();
    }
    public void addEvent(Event<T> event) {
        eventList.add(event);
    }
    public void removeEvent(Event<T> event) {
        eventList.remove(event);
    }
    public void clearEventList() {
        eventList.clear();
    }
    public void invoke(T arg) {
        for (Event<T> event : eventList) {
            event.invoke(arg);
        }
    }
}
