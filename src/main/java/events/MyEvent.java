package events;

import handlers.MyEventHandler;
import javafx.event.Event;
import javafx.event.EventType;

public abstract class MyEvent extends Event {

  public static final EventType<MyEvent> MY_EVENT_TYPE = new EventType(ANY);

  public MyEvent(EventType<? extends Event> eventType) {
    super(eventType);
  }

  public abstract void invokeHandler(MyEventHandler handler);

}
