package logic.gauss;

import logic.RomanNumber;
import logic.Utilit;
import logic.enums.End;
import logic.enums.EndGauss;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("Duplicates")
public class Gauss {
  public static final boolean printActions = true;
  private static LinearSystem system = null;

  private static ListIterator<Equation> equationListIterator = null;
  private static int i = 1;
  private static String action = "";

  private static EndGauss end = EndGauss.CONTINUE_DIRECT_GAUSS;

  private static Boolean swap = null;

  private static List<Integer> indexesExpressVars = null;

  //запоминаем переменные для возврата назад
  private static List<LinearSystem> steps = new ArrayList<>();
  private static List<String> actions = new ArrayList<>();
  private static List<Integer> indexesItr = new ArrayList<>();
  private static List<Integer> indexes = new ArrayList<>();
  private static List<EndGauss> ends = new ArrayList<>();

  public static void swap(List<Integer> swapIndexes) {
    boolean inOrder = false;
    if (swapIndexes.get(0) == 0) {
      inOrder = true;
    }
    for (int i = 1; i < swapIndexes.size(); i++) {
      if (!inOrder) {
        break;
      }

      if (swapIndexes.get(i - 1) != swapIndexes.get(i) - 1) {
        inOrder = false;
        break;
      }
    }

    if (!inOrder) {
      for (int i = 0; i < swapIndexes.size(); i++) {
        system.swap(i, swapIndexes.get(i));
      }

      action = "";
      swap = true;

      steps.add(system.clone());
      actions.add("");
      if (equationListIterator != null) {
        indexesItr.add(equationListIterator.previousIndex() + 1);
      } else {
        indexesItr.add(-1);
      }
      indexes.add(i);
      ends.add(end);

      return;
    }

    swap = false;
  }

  public static void backSwap() {
    indexesExpressVars = system.getIndexesExpressedVars();

    if (swap) {
      action = "";
      system.returnOrder();
      steps.add(system.clone());
      actions.add(action);
      indexesItr.add(equationListIterator.previousIndex() + 1);
      indexes.add(i);
      ends.add(end);
    }
  }

  public static EndGauss makeDirectStep() {
    action = "";

    if (system == null) {
      end = EndGauss.END_DIRECT_GAUSS;
      return EndGauss.END_DIRECT_GAUSS;
    }
    if (equationListIterator == null) {
      equationListIterator = system.getIterator();
    }

    if (!equationListIterator.hasNext()) {
      end = EndGauss.END_DIRECT_GAUSS;
      return EndGauss.END_DIRECT_GAUSS;
    }

    Equation eq = equationListIterator.next();
    Double coef = eq.reduceCoef();

    action += printAction(coef, i);
    system.print();

    action += system.plusEquationsFromBegin(eq, i++);

    steps.add(system.clone());
    actions.add(action);
    indexesItr.add(equationListIterator.previousIndex() + 1);
    indexes.add(i - 1);
    ends.add(end);

    system.print();

    if (equationListIterator.hasNext()) {
      end = EndGauss.CONTINUE_DIRECT_GAUSS;
      return EndGauss.CONTINUE_DIRECT_GAUSS;
    } else {
      equationListIterator = null;
      end = EndGauss.END_DIRECT_GAUSS;
      return EndGauss.END_DIRECT_GAUSS;
    }
  }

  public static EndGauss makeReversStep() {
    action = "";

    if (system == null) {
      end = EndGauss.END_REVERSE_GAUSS;
      return EndGauss.END_REVERSE_GAUSS;
    }
    if (equationListIterator == null) {
      equationListIterator = system.getIterator(system.size());
      i = system.size() - 1;
    }
    if (!equationListIterator.hasPrevious()) {
      end = EndGauss.END_REVERSE_GAUSS;
      return EndGauss.END_REVERSE_GAUSS;
    }

    Equation eq = equationListIterator.previous();
    action += system.plusEquationsFromEnd(eq, i--);

    steps.add(system.clone());
    actions.add(action);
    indexesItr.add(equationListIterator.previousIndex() + 1);
    indexes.add(i + 1);
    ends.add(end);

    if (i == 0) {
      end = EndGauss.END_REVERSE_GAUSS;
      return EndGauss.END_REVERSE_GAUSS;
    }

    system.print();

    if (equationListIterator.hasPrevious()) {
      end = EndGauss.CONTINUE_REVERSE_GAUSS;
      return EndGauss.CONTINUE_REVERSE_GAUSS;
    } else {
      end = EndGauss.END_REVERSE_GAUSS;
      return EndGauss.END_REVERSE_GAUSS;
    }
  }

  public static EndGauss backStep() {
    if (steps.size() == 0){
      return end;
    }

    int index = steps.size() - 1;
    if (steps.size() == 1){
      index = steps.size();
    }

    system = steps.get(index - 1);
    steps.remove(index);
    steps.remove(steps.size() - 1);

    action = actions.get(index - 1);
    actions.remove(index);
    actions.remove(actions.size() - 1);

    int indexItr = indexesItr.get(index - 1);
    equationListIterator = system.getIterator(indexItr);
    indexesItr.remove(index);
    indexesItr.remove(indexesItr.size() - 1);

    i = indexes.get(index - 1);
    indexes.remove(index);
    indexes.remove(indexes.size() - 1);

    end = ends.get(index - 1);
    ends.remove(index);
    ends.remove(ends.size() - 1);

    System.out.println("    back step " + end);

    return end;
  }

  private static String printAction(Double coef, int indexEq) {
    String action = RomanNumber.toRoman(indexEq) + " * " + Utilit.convertDecimalToFraction(coef) + "\n";
    System.out.print(action);

    return action;
  }

  public static double[][] getLimits() {

    int countRows = system.size();
    int countColumn = system.size();
    double[][] limits = new double[countRows][countColumn];

    for (int i = 0; i < indexesExpressVars.size(); i++) {
      limits[i] = system.getEquation(i)
              .expressVar(indexesExpressVars.get(i), indexesExpressVars);
    }

    return limits;
  }

  public static void setSystem(LinearSystem system) {
    Gauss.system = system;

    actions.add("");
    steps.add(system.clone());
    indexesItr.add(0);
    indexes.add(i);
    ends.add(end);
  }

  public static LinearSystem getSystem() {
    return system;
  }

  public static String getAction() {
    return action;
  }

  public static void revertVars() {
    i = 1;
    action = "";
    equationListIterator = null;
    system = null;
    swap = null;
    end = EndGauss.CONTINUE_DIRECT_GAUSS;

    steps.clear();
    indexesItr.clear();
    actions.clear();
    indexes.clear();
    ends.clear();
  }

  public static Boolean isSwap() {
    return swap;
  }

  public static List<Integer> getIndexesExpressVars() {
    return indexesExpressVars;
  }
}