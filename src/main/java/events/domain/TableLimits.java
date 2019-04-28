package events.domain;

public class TableLimits {
  private double[][] table;

  public TableLimits(double[][] table){
    this.table = table;
  }

  public double[][] getTable() {
    return table;
  }
}
