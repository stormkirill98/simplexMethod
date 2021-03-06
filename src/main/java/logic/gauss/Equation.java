package logic.gauss;

import logic.Utilit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class Equation implements Cloneable{
  private List<Double> equation = new ArrayList<>();

  private String action = "";

  public Equation() {
  }

  public Equation(double[] coefs) {
    for (double coef : coefs) {
      equation.add(coef);
    }
  }

  public Equation(Equation eq) {
    equation = new ArrayList<>(eq.equation);
  }

  public void add(Double num) {
    equation.add(num);
  }

  public Double get(int i) {
    return equation.get(i);
  }

  public void mult(Double coefficient) {
    ListIterator<Double> iterator = equation.listIterator();

    while (iterator.hasNext()) {
      Double number = iterator.next();
      iterator.set(number * coefficient);
    }
  }

  public Double reduceCoef() {
    int i = 0;
    while (equation.get(i) == 0) {
      i++;

      if (i >= equation.size()) {
        return 0.0;
      }
    }
    Double coef = 1 / equation.get(i);
    mult(coef);
    return coef;
  }

  public int size() {
    return equation.size();
  }

  public void print() {
    for (Double num : equation) {
      String fractionNum = Utilit.convertDecimalToFraction(num);
      System.out.printf("%7s ", fractionNum);
    }
  }

  public boolean equals(Equation equation) {
    for (int i = 0; i < this.size(); i++) {
      if (!this.equation.get(i).equals(equation.get(i))) {
        return false;
      }
    }
    return true;
  }

  public Equation plusEquation(Equation eqAdded) {
    action = "";

    Equation eq = new Equation(eqAdded);
    int indexFirstNotZero = eq.indexFirstNotZero();
    if (indexFirstNotZero < 0) {
      return this;
    }
    Double coef = -this.equation.get(indexFirstNotZero);
    eq.mult(coef);

    if (Gauss.printActions) {
      if (coef > 0) {
        action = String.format(" + %.2f", coef);
        System.out.print(action);
      } else {
        action = String.format(" - %.2f", Math.abs(coef));
        System.out.print(action);
      }
    }

    Equation newEq = new Equation();
    for (int i = 0; i < this.equation.size(); i++) {
      newEq.add(this.equation.get(i) + eq.get(i));
    }

    return newEq;
  }

  private int indexFirstNotZero() {
    for (int i = 0; i < this.equation.size(); i++) {
      if (Math.abs(equation.get(i)) > Utilit.TOLERANCE) {
        return i;
      }
    }

    return -1;
  }

  public void swap(int i1, int i2) {
    Collections.swap(equation, i1, i2);
  }

  public double[] expressVar(int index, List<Integer> indexesExpressedVars){
    double[] limit = new double[size() - indexesExpressedVars.size()];

    if (index == -1){
      return null;
    }

    int countMissedVar = 0;
    for (int i = 0; i < equation.size(); i++) {
      if (Utilit.isExpressedVar(i, indexesExpressedVars)){
        countMissedVar++;
        continue;
      }

      limit[i - countMissedVar] = equation.get(i);
    }

    return limit;
  }

  public String getAction(){
    return action;
  }

  @Override
  protected Equation clone() throws CloneNotSupportedException {
    Equation equation = (Equation) super.clone();
    equation.equation = new ArrayList<>(this.equation);

    return equation;
  }
}