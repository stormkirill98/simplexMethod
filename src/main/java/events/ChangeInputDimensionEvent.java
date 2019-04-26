package events;

import handlers.MyEventHandler;
import javafx.event.EventType;

public class ChangeInputDimensionEvent extends MyEvent {
  private final int n, m;

  public ChangeInputDimensionEvent(int width, int height) {
    super(new EventType<>(MY_EVENT_TYPE, "ChangeInputDimensionEvent"));
    this.n = width;
    this.m = height;
  }

  @Override
  public void invokeHandler(MyEventHandler handler) {
    handler.onChangeFunctionSizeEvent(n, m);
  }
}
