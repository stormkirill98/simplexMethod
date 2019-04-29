package logic;

import logic.enums.End;
import logic.enums.Stage;
import logic.enums.TypeProblem;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {
  private Function function;
  private Function addFunction;
  private List<Limit> limits = new ArrayList<>();

  private Stage stage = Stage.ART_BASIS;

  private Simplex simplex;

  public Algorithm(double[][] limits) {
    for (int i = 0; i < limits.length; i++) {
      Limit limit = new Limit(limits[i]);
      this.limits.add(limit);
    }
    simplex = new Simplex(this.limits);
  }

  public Algorithm() {

  }

  public void setFunction(Function function) {
    this.function = function;
  }

  public void setStage(Stage stage){
    this.stage = stage;
  }

  public void addLimit(Limit limit){
    limits.add(limit);
  }

  //check Bi and make valid their
  public void makeValid(){
    for (Limit limit : limits){
      limit.makeValid();
    }
  }


  public void simplex(){
    //simplex method
    stage = Stage.SIMPLEX;

    simplex.recountLastRow(function);
    System.out.println(simplex);

    End end = simplex.end();
    while (end == End.CONTINUE){
      end = makeStep();
    }
    System.out.println(end);

    if (end == End.FAILURE){
      return;
    }

    System.out.printf("Extr = %.2f\n", simplex.getFunctionExtr());
    System.out.println("Point: " + simplex.getPointExtr());
  }

  public void recountLastRow(){
    simplex.recountLastRow(function);
  }

  public double getFunctionExtr(){
    return simplex.getFunctionExtr();
  }

  public List<Double> getPointExtr(){
    return simplex.getPointExtr();
  }

  public End makeStep(){
    //находим индексы базового элемента
    int[] indexes = simplex.getIndexesBaseElement();
    //simplex.setIndexesBaseElement(indexes);

    //переменная слева не ушла, но шагов уже нет
    if (indexes[0] == -1 && indexes[1] == -1){
      return End.FAILURE;
    }
    System.out.println("indexes " + indexes[0] + " " + indexes[1]);
    simplex.swap(indexes[0], indexes[1]);
    System.out.println(simplex);


    //умножаем строку и столбец и задаем значение в ячейку
    double value = simplex.getValue(indexes[0], indexes[1]);
    System.out.println("value = " + value);
    simplex.multCol(indexes[1], (-1 / value));
    simplex.multRow(indexes[0], (1 / value));
    simplex.setValue(indexes[0], indexes[1], (1 / value));
    System.out.println(simplex);

    //считаем другие строки, вычитая из нее строку
    simplex.subtractRow(indexes[0], indexes[1], -value);
    System.out.println(simplex);


    //удаляем столбец, если это искственный базис
    if(stage == Stage.ART_BASIS){
      simplex.removeColumn(indexes[1]);
      System.out.println(simplex);
    }

    //исксственный базиз закончен?
    if (stage == Stage.ART_BASIS){
      return simplex.endArtBasis();
    }

    //алгоритм закончился?
    return simplex.end();
  }

  //создаем искусственный базис
  public void createArtBasis(){
    //создаем новую функцию
    addFunction = new Function(TypeProblem.MIN);
    for (int i = 0; i < limits.size(); i++) {
      addFunction.addCoefficient(new Coefficient(1.0, function.getCountVar() + i + 1));
    }

    //добавляем в ограничения новые переменные
    for (int i = 0; i < limits.size(); i++){
      Limit limit = limits.get(i);
      limit.addCoefArtBasis(new Coefficient(1.0, function.getCountVar() + i + 1));
    }

    simplex = new Simplex(limits);
  }

  public Simplex getSimplex(){
    return simplex;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(function + "\n");
    result.append(addFunction).append("\n");

    for (Limit limit : limits) {
      result.append(limit).append("\n");
    }

    return result.toString();
  }
}
