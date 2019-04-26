package handlers;

import events.MyEvent;
import javafx.event.EventHandler;

public abstract class MyEventHandler implements EventHandler<MyEvent> {

  public abstract void onChangeFunctionSizeEvent(int width, int height);

  @Override
  public void handle(MyEvent event) {
    event.invokeHandler(this);
  }
}
