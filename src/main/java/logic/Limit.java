package logic;

import java.util.ArrayList;
import java.util.List;

public class Limit {
  private List<Coefficient> coefficients = new ArrayList<>();

  public Limit() {
  }

  public void addCoefficient(Coefficient coef) {
    coefficients.add(coef);
  }

  public void addCoefArtBasis(Coefficient coef){
    coefficients.add(coefficients.size() - 1, coef);
  }

  public Coefficient getCoefficient(int index) {
    return coefficients.get(index);
  }

  public Coefficient getFreeCoef() {
    return coefficients.get(coefficients.size() - 1);
  }

  public Coefficient getLastCoef() {
    return coefficients.get(coefficients.size() - 2);
  }

  public int getCountCoefs(){
    return coefficients.size();
  }

  public void removeCoefficient(int index) {
    coefficients.remove(index);
  }

  //reduce on first coefficient
  public void normilize() {
    if (coefficients.size() == 0) {
      return;
    }

    Double firstCoef = getCoefficient(0).getValue();
    if (firstCoef.equals(1.0)) {
      return;
    }

    coefficients.forEach(coef-> coef.setValue(coef.getValue() / firstCoef));
  }

  //validate b
  public boolean isValid() {
    return getFreeCoef().getValue() >= 0;
  }

  public void makeValid() {
    if (isValid()){
      return;
    }

    coefficients.forEach(coef-> coef.setValue(coef.getValue() * -1));
  }

  @Override
  public String toString(){
    String result = "";

    for(int i = 0; i < coefficients.size() - 1; i++){
      result += getCoefficient(i) + " + ";
    }

    result = result.substring(0, result.length() - 3);

    result += " = " + getFreeCoef();

    return result;
  }
}
