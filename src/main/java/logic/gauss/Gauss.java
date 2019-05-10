package logic.gauss;

import java.util.List;
import java.util.ListIterator;

public class Gauss {
  public static final boolean printActions = true;

  public static LinearSystem getExpressedVars(LinearSystem system, List<Integer> indexesExpressVars) {
    System.out.println("input matrix");
    system.print();

    swap(indexesExpressVars, system);
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

  public static void swap(List<Integer> indexes, LinearSystem system) {
    for (int i = 0; i < indexes.size(); i++) {
      system.swap(i, indexes.get(i));
    }
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

  private static boolean deleteSameEquations(LinearSystem system) {
    boolean delete = false;
    for (int i = 0; i < system.size() - 1; i++) {
      if (system.getEquation(i).equals(system.getEquation(i + 1))) {
        system.delete(i + 1);
        i--;
        delete = true;
      }
    }

    return delete;
  }

  private static void printAction(Double coef, int indexEq) {
    System.out.printf("(%s * %.2f)=>\n", Utility.numToRim(indexEq), coef);
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


}