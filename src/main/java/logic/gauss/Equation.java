package logic.gauss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

class Equation {
  private List<Double> equation = new ArrayList<>();

  public Equation() {
  }

  public Equation(double[] coefs) {
    for (double coef : coefs) {
      equation.add(coef);
    }
  }

  Equation(Equation eq) {
    equation = new ArrayList<>(eq.equation);
  }

  void add(Double num) {
    equation.add(num);
  }

  Double get(int i) {
    return equation.get(i);
  }

  void mult(Double coefficient) {
    ListIterator<Double> iterator = equation.listIterator();

    while (iterator.hasNext()) {
      Double number = iterator.next();
      iterator.set(number * coefficient);
    }
  }

  Double reduceCoef() {
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

  int size() {
    return equation.size();
  }

  void print() {
    for (Double num : equation) {
      String fractionNum = Utility.convertDecimalToFraction(num);
      System.out.printf("%7s ", fractionNum);
    }
  }

  boolean equals(Equation equation) {
    for (int i = 0; i < this.size(); i++) {
      if (!this.equation.get(i).equals(equation.get(i))) {
        return false;
      }
    }
    return true;
  }

  Equation plusEquation(Equation eqAdded) {
    Equation eq = new Equation(eqAdded);
    int indexFirstNotZero = eq.indexFirstNotZero();
    if (indexFirstNotZero < 0) {
      return this;
    }
    Double coef = -this.equation.get(indexFirstNotZero);
    eq.mult(coef);

    if (Gauss.printActions) {
      if (coef > 0) {
        System.out.printf(" + %.2f", coef);
      } else {
        System.out.printf(" - %.2f", Math.abs(coef));
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
      if (Math.abs(equation.get(i)) > Utility.TOLERANCE) {
        return i;
      }
    }

    return -1;
  }

  void swap(int i1, int i2) {
    Collections.swap(equation, i1, i2);
  }

  void printExpressVar(int index) {
    if (index < 0 || index > size() - 1) {
      return;
    }
    StringBuilder str = new StringBuilder("x"
            + (index + 1) + " = "
            + Utility.convertDecimalToFraction(equation.get(equation.size() - 1)));


    for (int i = 0; i < equation.size() - 1; i++) {
      if (i == index) {
        continue;
      }
      if (equation.get(i) == 0) {
        continue;
      }

      double num = equation.get(i);
      if (num > 0) {
        String numStr = Utility.convertDecimalToFraction(num);
        if (numStr.equals("1")) {
          str.append(" - x").append(i + 1);
        } else {
          str.append(" - ").append(numStr).append("*x").append(i + 1);
        }
      } else {
        String numStr = Utility.convertDecimalToFraction(Math.abs(num));
        if (numStr.equals("1")) {
          str.append(" + x").append(i + 1);
        } else {
          str.append(" + ").append(numStr).append("*x").append(i + 1);
        }
      }
    }

    System.out.println(str);
  }

  public boolean validate() {
    boolean allCoefZero = true;
    for (int i = 0; i < size() - 1; i++) {
      if (Math.abs(equation.get(i)) > Utility.TOLERANCE) {
        allCoefZero = false;
      }
    }

    return !allCoefZero;
  }

}