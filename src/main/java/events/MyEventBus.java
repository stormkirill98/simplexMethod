package events;

public class MyEventBus {

  private static MyEventBus instance;
  private static com.google.common.eventbus.EventBus bus;

  static {
    instance = new MyEventBus();
    bus = new com.google.common.eventbus.EventBus();
  }

  protected MyEventBus() {
  }

  public static void register(Object o) {
    bus.register(o);
  }

  public static void post(Object o) {
    bus.post(o);
  }
}
