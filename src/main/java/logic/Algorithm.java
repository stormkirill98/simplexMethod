package logic;

import logic.enums.TypeProblem;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {
  private Function function;
  private Function addFunction;
  private List<Limit> limits = new ArrayList<>();

  public Algorithm() {
  }

  public void setFunction(Function function) {
    this.function = function;
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

  public void searchStartVector(){
    createArtBasis();
    Simplex simplex = new Simplex(limits);

    System.out.println(simplex);

    //находим индексы базового элемента
    int[] indexes = simplex.searchBaseElement();
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

    //удаляем столбец
    simplex.removeColumn(indexes[1]);
    System.out.println(simplex);

  }

  //создаем искусственный базис
  private void createArtBasis(){
    //создаем новую функцию
    addFunction = new Function(TypeProblem.MIN);
    addFunction.addCoefficient(new Coefficient(1.0, function.getCountVar() + 1));
    addFunction.addCoefficient(new Coefficient(1.0, function.getCountVar() + 2));

    //добавляем в ограничения новые переменные
    for (int i = 0; i < limits.size(); i++){
      Limit limit = limits.get(i);
      limit.addCoefArtBasis(new Coefficient(1.0, function.getCountVar() + i + 1));
    }
  }


  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(function + "\n");
    result.append(addFunction + "\n");

    for (Limit limit : limits) {
      result.append(limit).append("\n");
    }

    return result.toString();
  }
}
