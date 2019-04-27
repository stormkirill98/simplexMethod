package handlers;

import events.MyEvent;
import javafx.event.EventHandler;

public abstract class MyEventHandler implements EventHandler<MyEvent> {

  public abstract void onChangeInputDimensionHandler(int n, int m);

  @Override
  public void handle(MyEvent event) {
    event.invokeHandler(this);
  }
}
