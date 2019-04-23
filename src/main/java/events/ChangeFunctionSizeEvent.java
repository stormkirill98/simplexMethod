package events;

import handlers.MyEventHandler;
import javafx.event.EventType;

public class ChangeFunctionSizeEvent extends MyEvent {
  private final double width, height;

  public ChangeFunctionSizeEvent(double width, double height) {
    super(new EventType(MY_EVENT_TYPE, "ChangeFunctionSizeEvent"));
    this.width = width;
    this.height = height;
  }

  @Override
  public void invokeHandler(MyEventHandler handler) {
    handler.onChangeFunctionSizeEvent(width, height);
  }
}
