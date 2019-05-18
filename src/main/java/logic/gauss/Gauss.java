package logic.gauss;

import logic.RomanNumber;
import logic.Utilit;

import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("Duplicates")
public class Gauss {
  public static final boolean printActions = true;
  private static LinearSystem system = null;

  private static ListIterator<Equation> equationListIterator = null;
  private static int i = 1;
  private static String action = "";
  private static int countVars = 0;

  private static Boolean swap = null;

  private static List<Integer> indexesExpressVars = null;

  public static void swap(List<Integer> indexes) {
    boolean inOrder = false;
    if (indexes.get(0) == 0){
      inOrder = true;
    }

    for (int i = 1; i < indexes.size(); i++) {
      if (!inOrder){
        break;
      }

      if (indexes.get(i - 1) != indexes.get(i) - 1){
        inOrder = false;
        break;
      }
    }

    if (!inOrder){
      for (int i = 0; i < indexes.size(); i++) {
        system.swap(i, indexes.get(i));
      }

      swap = true;
      return;
    }

    swap = false;
  }

  public static void  backSwap(){
    indexesExpressVars = system.getIndexesExpressedVars();

    if (swap){
      action = "";
      system.returnOrder();
    }
  }

  public static boolean makeDirectStep(){
    action = "";

    if (system == null){
      return false;
    }
    if (equationListIterator == null){
      equationListIterator = system.getIterator();
    }

    if (!equationListIterator.hasNext()){
      return false;
    }

    Equation eq = equationListIterator.next();
    Double coef = eq.reduceCoef();

    action += printAction(coef, i);
    system.print();

    action += system.plusEquationsFromBegin(eq, i++);

    system.print();

    if (equationListIterator.hasNext()){
      return true;
    } else {
      equationListIterator = null;
      return false;
    }
  }

  public static boolean makeReversStep(){
    action = "";

    if (system == null){
      return false;
    }
    if (equationListIterator == null){
      equationListIterator = system.getIterator(system.size());
      i = system.size() - 1;
    }
    if (!equationListIterator.hasPrevious()){
      return false;
    }

    Equation eq = equationListIterator.previous();
    action += system.plusEquationsFromEnd(eq, i--);

    if (i == 0) {
      return false;
    }

    system.print();

    return equationListIterator.hasPrevious();
  }

  private static String printAction(Double coef, int indexEq) {
    String action = String.format("%s * %.2f\n", RomanNumber.toRoman(indexEq), coef);
    System.out.print(action);

    return action;
  }

  public static double[][] getLimits(){

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
    Gauss.countVars = system.getEquation(0).size() - 1;
  }

  public static LinearSystem getSystem() {
    return system;
  }

  public static String getAction(){
    return action;
  }

  public static void revertVars(){
    i = 1;
    action = "";
    equationListIterator = null;
    system = null;
    swap = null;
    countVars = 0;
  }

  public static Boolean isSwap() {
    return swap;
  }

  public static List<Integer> getIndexesExpressVars() {
    return indexesExpressVars;
  }
}