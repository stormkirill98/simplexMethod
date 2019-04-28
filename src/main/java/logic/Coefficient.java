package logic;

public class Coefficient {
  private Double value;
  private int index;

  public Coefficient(Double value, int index){
    this.index = index;
    this.value = value;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public String toString(){
    if (index == -1){
      return String.format("%.2f", value);
    }
    return String.format("%.2f", value) + " * x" + index;
  }
}
