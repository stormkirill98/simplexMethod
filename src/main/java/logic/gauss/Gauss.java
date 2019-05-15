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

  public static LinearSystem getExpressedVars(LinearSystem system, List<Integer> indexesExpressVars) {
    System.out.println("input matrix");
    system.print();

    swap(indexesExpressVars);
    System.out.println("after swap");
    system.print();

    directCourse(system);
    System.out.println("after direct");
    system.print();

    reversCourse(system);
    System.out.println("after revers");
    system.print();

    system.returnOrder();
    System.out.println("after returned order vars");
    system.print();

    printExpressVars(system, indexesExpressVars);

    return system;
  }

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

  private static void directCourse(LinearSystem system) {
    ListIterator<Equation> equationListIterator = system.getIterator();
    int i = 1;
    while (equationListIterator.hasNext()) {
      Equation eq = equationListIterator.next();
      Double coef = eq.reduceCoef();

      if (printActions) {
        printAction(coef, i);
        system.print();
      }

      system.plusEquationsFromBegin(eq, i);

      i++;

      system.print();
    }
  }

  private static void reversCourse(LinearSystem system) {
    ListIterator<Equation> equationListIterator = system.getIterator(system.size());
    int i = system.size() - 1;
    while (equationListIterator.hasPrevious()) {
      Equation eq = equationListIterator.previous();
      system.plusEquationsFromEnd(eq, i);
      i--;

      if (i == 0) {
        return;
      }

      system.print();
    }
  }

  private static String printAction(Double coef, int indexEq) {
    String action = String.format("%s * %.2f\n", RomanNumber.toRoman(indexEq), coef);
    System.out.print(action);

    return action;
  }

  private static void printExpressVars(LinearSystem system, List<Integer> indexes) {
    System.out.println("Expressed vars:");

    ListIterator<Equation> equationListIterator = system.getIterator();
    int i = 0;
    while (equationListIterator.hasNext()) {
      Equation eq = equationListIterator.next();
      try {
        eq.printExpressVar(indexes.get(i++));
      } catch (IndexOutOfBoundsException e){
        break;
      }
    }
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

  public static int getCountVars() {
    return countVars;
  }
}