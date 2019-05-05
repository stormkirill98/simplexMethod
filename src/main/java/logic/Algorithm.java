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

  //TODO: проверить на вычеркивание всех переменных сверху
  public Algorithm(double[][] limits) {
    if (!checkRank(limits)){
      stage = Stage.END;
      return;
    }

    for (int i = 0; i < limits.length; i++) {
      Limit limit = new Limit(limits[i]);
      this.limits.add(limit);
    }
    simplex = new Simplex(this.limits);

    makeValid();
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


  public void recountLastRow(){
    simplex.recountLastRow(function);
  }

  public double getFunctionExtr(){
    return simplex.getFunctionExtr();
  }

  public List<Double> getPointExtr(){
    return simplex.getPointExtr();
  }

  public Stage getStage() {
    return stage;
  }

  //TODO:если с самого начала плохой симплекс искуственного базиса, то делается лишний шаг ничего не меняющий
  public End makeStep(){
    //находим индексы базового элемента
    int[] indexes = simplex.searchBaseElement();

    //переменная слева не ушла, но шагов уже нет
    if (indexes[0] == -1 && indexes[1] == -1){
      if (stage == Stage.ART_BASIS){
        return simplex.endArtBasis();
      }
      return simplex.end();
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
      //TODO: можно избавиться от двух внешних циклов и перейти тут сразу к симплекс методу

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

  public boolean checkRank(double[][] array){
    if (array == null){
      return false;
    }

    int rank = Utilit.rankOfMatrix(array);
    System.out.println("rank " + rank);
    if (rank == array.length && rank <= array[0].length){
      return true;
    }

    return false;
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
