package logic;

import logic.enums.End;
import logic.enums.Stage;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {
  private Function function;
  private List<Limit> limits = new ArrayList<>();

  private int step = 0;

  private Stage stage = Stage.ART_BASIS;

  private Simplex simplex;

  private List<Simplex> steps = new ArrayList<>();

  private List<Double> basisElement;

  public Algorithm(double[][] limits, List<Integer> indexesExpressedVars, int countVars, Function function) {
    this.function = function;

    if (!checkRank(limits)) {
      stage = Stage.END;
      return;
    }


    for (int i = 0; i < limits.length; i++) {
      Limit limit = new Limit(limits[i]);//TODO: нужно задавать для коэффициентов правильные индексы
      this.limits.add(limit);
    }
    if (indexesExpressedVars != null){
      simplex = new Simplex(this.limits, countVars,
                            indexesExpressedVars, function);
      stage = Stage.SIMPLEX;
    } else {
      simplex = new Simplex(this.limits);
      makeValid();
    }

  }

  public void setFunction(Function function) {
    this.function = function;
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public void setBasisElement(List<Double> basisElement) {
    this.basisElement = basisElement;
  }

  //check Bi and make valid their
  public void makeValid() {
    for (Limit limit : limits) {
      limit.makeValid();
    }
  }

  public void recountLastRow() {
    steps.add(simplex.clone());

    simplex.recountLastRow(function);
  }

  public double getFunctionExtr() {
    return simplex.getFunctionExtr();
  }

  public List<Double> getPointExtr() {
    return simplex.getPointExtr();
  }

  public Stage getStage() {
    return stage;
  }

  //TODO:если с самого начала плохой симплекс искуственного базиса, то делается лишний шаг ничего не меняющий
  public End makeStep() {
    //делаем копию для возвращения назад
    steps.add(simplex.clone());

    //находим индексы базового элемента или берем заданные вручную
    int[] indexes;
    if (simplex.isManuallySetBaseElement()) {
      indexes = simplex.getIndexesBaseElement();
    } else {
      indexes = simplex.searchBaseElement();
    }

    //переменная слева не ушла, но шагов уже нет
    if (indexes[0] == -1 && indexes[1] == -1) {
      if (stage == Stage.ART_BASIS) {
        return simplex.endArtBasis();
      }
      return simplex.end();
    }
    simplex.swap(indexes[0], indexes[1]);

    //умножаем строку и столбец и задаем значение в ячейку
    double value = simplex.getValue(indexes[0], indexes[1]);
    simplex.multCol(indexes[1], (-1 / value));
    simplex.multRow(indexes[0], (1 / value));
    simplex.setValue(indexes[0], indexes[1], (1 / value));

    //считаем другие строки, вычитая из нее строку
    simplex.subtractRow(indexes[0], indexes[1], -value);

    //удаляем столбец, если это искственный базис
    if (stage == Stage.ART_BASIS) {
      simplex.removeColumn(indexes[1]);
    }

    step++;
    simplex.setStep(step);

    //исксственный базиз закончен?
    if (stage == Stage.ART_BASIS) {
      return simplex.endArtBasis();
    }

    //алгоритм закончился?
    return simplex.end();
  }

  public void backStep(){
    step--;
    simplex = steps.get(steps.size() - 1);
    steps.remove(steps.size() - 1);
    stage = simplex.getStage();
  }

  //создаем искусственный базис
  public void createArtBasis() {
    //добавляем в ограничения новые переменные
    for (int i = 0; i < limits.size(); i++) {
      Limit limit = limits.get(i);
      limit.addCoefArtBasis(new Coefficient(1.0, function.getCountVar() + i + 1));
    }

    simplex = new Simplex(limits);
  }

  public Simplex getSimplex() {
    return simplex;
  }

  public boolean checkRank(double[][] array) {
    if (array == null) {
      return false;
    }

    int rank = Utilit.rankOfMatrix(array);
    if (rank == array.length && rank <= array[0].length) {
      return true;
    }

    return false;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(function + "\n");

    for (Limit limit : limits) {
      result.append(limit).append("\n");
    }

    return result.toString();
  }


}
