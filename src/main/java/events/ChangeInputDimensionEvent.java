package events;

import handlers.MyEventHandler;
import javafx.event.EventType;

public class ChangeInputDimensionEvent extends MyEvent {
  private final int n, m;
/*
  public int getN() {
    return amountVar;
  }

  public int getM() {
    return amountLimit;
  }*/

  public static final EventType<MyEvent> EVENT_DIMENSION_TYPE
          = new EventType<>(MY_EVENT_TYPE);

  public ChangeInputDimensionEvent(int n, int m) {
    super(EVENT_DIMENSION_TYPE);
    this.n = n;
    this.m = m;
  }

  @Override
  public void invokeHandler(MyEventHandler handler) {

  }
}
