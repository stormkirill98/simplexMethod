package logic.gauss;

import logic.RomanNumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class LinearSystem {

  private List<Equation> system = new ArrayList<>();

  private List<Integer> orderColumn = new ArrayList<>();

  public LinearSystem(double[][] limits) {
    for (double[] limit : limits) {
      Equation equation = new Equation(limit);
      add(equation);
    }
  }

  public void add(Equation equation) {
    //инициализируем порядок столбцов
    if (orderColumn.size() == 0) {
      for (int i = 0; i < equation.size(); i++) {
        orderColumn.add(i);
      }
    }

    system.add(equation);
  }

  public Equation getEquation(int i) {
    return system.get(i);
  }

  public List<Integer> getOrderColumn() {
    return orderColumn;
  }

  @SuppressWarnings("Duplicates")
  public String plusEquationsFromBegin(Equation eq, int start) {
    String action = "";

    if (system.indexOf(eq) == system.size() - 1) {
      return "";
    }

    ListIterator<Equation> equations = system.listIterator(start);
    while (equations.hasNext()) {
      Equation equation = equations.next();

      Equation newEq = equation.plusEquation(eq);

      action +=  RomanNumber.toRoman(system.indexOf(equation) + 1)
              + equation.getAction() + " * "
              + RomanNumber.toRoman(system.indexOf(eq) + 1) + "\n";

      equations.set(newEq);
    }
    if (Gauss.printActions) {
      System.out.println(action);
      System.out.println("        ⇓");
    }

    return action;
  }

  @SuppressWarnings("Duplicates")
  public String plusEquationsFromEnd(Equation eq, int start) {
    String action = "";

    if (system.indexOf(eq) == 0) {
      return "";
    }

    ListIterator<Equation> equations = system.listIterator(start);
    while (equations.hasPrevious()) {
      Equation equation = equations.previous();

      Equation newEq = equation.plusEquation(eq);

      action +=  RomanNumber.toRoman(system.indexOf(equation) + 1)
              + equation.getAction() + " * "
              + RomanNumber.toRoman(system.indexOf(eq) + 1) + "\n";

      equations.set(newEq);
    }

    if (Gauss.printActions) {
      System.out.println(action);
      System.out.println("        ⇓");
    }

    return action;
  }

  public int size() {
    return system.size();
  }

  public void print() {
    for (Equation eq : system) {
      eq.print();
      System.out.println();
    }
    System.out.println();
  }

  public void swap(int i1, int i2) {
    int countVars = system.get(0).size() - 2;
    if (i1 < 0 || i2 < 0
            || i1 > countVars
            || i2 > countVars) {
      return;
    }
    Collections.swap(orderColumn, i1, i2);
    system.forEach(equation -> {
      equation.swap(i1, i2);
    });
  }

  public void returnOrder() {
    for (int i = 0; i < orderColumn.size(); i++) {
      if (i == orderColumn.get(i)) {
        continue;
      }

      swap(i, orderColumn.get(i));
    }
  }

  public ListIterator<Equation> getIterator() {
    return getIterator(0);
  }

  public ListIterator<Equation> getIterator(int start) {
    return system.listIterator(start);
  }
}