package events.domain;

import java.util.List;

public class BasisElement {
  private List<Double> coefs;

  public BasisElement(List<Double> coefs){
    this.coefs = coefs;
  }

  public List<Double> getCoefs() {
    return coefs;
  }
}
