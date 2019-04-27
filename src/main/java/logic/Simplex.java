package logic;

import java.util.ArrayList;
import java.util.List;

public class Simplex {
  private List<Integer> indexesVarRow = new ArrayList<>();
  private List<Integer> indexesVarCol = new ArrayList<>();
  private List<Row> rows = new ArrayList<>();

  //число переменных, которые были в задаче с самого начала, без тех которые добавили мы
  private int countOurVar = 0;

  public Simplex(List<Limit> limits) {
    //добавляем индексы переменных в столбцах(представляет верхнюю строку симплекс таблицы)
    Limit firstLimit = limits.get(0);
    int countCoefs = firstLimit.getCountCoefs();
    for (int i = 0; i < countCoefs - 2; i++) {
      Coefficient coef = firstLimit.getCoefficient(i);
      indexesVarCol.add(coef.getIndex());
    }
    countOurVar = indexesVarCol.size();

    for (Limit limit : limits) {
      indexesVarRow.add(limit.getLastCoef().getIndex());
      rows.add(new Row(limit));
    }

    //заполняем последнюю строку в симплекс таблице
    Row row = new Row();
    for (int i = 0; i < countCoefs; i++) {
      if (i == countCoefs - 2) {
        continue;
      }

      double value = 0.0;
      for (int j = 0; j < limits.size(); j++) {
        Limit limit = limits.get(j);
        value -= limit.getCoefficient(i).getValue();
      }
      row.addValue(value);
    }
    rows.add(row);
  }

  public double getValue(int i, int j){
    return rows.get(i).getValue(j);
  }

  public void setValue(int i, int j, double value){
    rows.get(i).setValue(j, value);
  }

  public void swap(int indexVarRow, int indexVarCol) {
    Integer bufIndex = indexesVarCol.get(indexVarCol);
    indexesVarCol.set(indexVarCol, indexesVarRow.get(indexVarRow));
    indexesVarRow.set(indexVarRow, bufIndex);
  }

  public void multCol(int index, double value) {
    for (int i = 0; i < rows.size(); i++) {
      Row row = rows.get(i);
      row.multValue(index, value);
    }
  }

  public void multRow(int index, double value) {
    Row row = rows.get(index);
    row.mult(value);
  }

  public void removeColumn(int index){
    indexesVarCol.remove(index);
    for (Row row : rows){
      row.removeValue(index);
    }
  }

  public void subtractRow(int indexRow, int indexCol, double previousCoef){
    System.out.println("indexRow " + indexRow);
    System.out.println("indexCol " + indexCol);
    System.out.println("coef " + previousCoef);
    Row subtractRow = rows.get(indexRow);
    for (int i = 0; i < rows.size(); i++){
      if (i == indexRow){
        continue;
      }

      Row row = rows.get(i);
      double coef = row.getValue(indexCol) * previousCoef;
      row.subtract(subtractRow, indexCol, coef);
    }
  }

  //return array of [i, j]
  //i, j - индексы базового элемента
  public int[] searchBaseElement() {
    int countVar = indexesVarCol.size();
    int countRow = rows.size() - 1;
    boolean[] possibleRow = new boolean[countRow];

    //определяем в каких строках нам нужно избавляться от переменной
    for (int i = 0; i < indexesVarRow.size(); i++) {
      possibleRow[i] = indexesVarRow.get(i) > countOurVar;
    }

    int indexFirstPossibleCol = -1;//TODO: что-то делать при -1
    //определяем первый подходящий столбец
    Row lastRow = rows.get(rows.size() - 1);
    for (int i = 0; i < countVar; i++) {
      //последний элемент в столбце отрицательный?
      if (lastRow.getValue(i) < 0) {
        indexFirstPossibleCol = i;
        break;
      }
    }


    //считаем отношения в столбце для выбора лучшего базового элемента
    double[] relations = new double[countRow];
    for (int i = 0; i < countRow; i++) {
        if (!possibleRow[i]){
          relations[i] = Double.MAX_VALUE;
        }

        double value = rows.get(i).getValue(indexFirstPossibleCol);
        double freeValue = rows.get(i).getValue(countVar);//свободное значение в таблице(самое последнее в строке)
        if (Utilit.isZero(value)) {
          relations[i] = Double.MAX_VALUE;
        }

        relations[i] = freeValue / value;
    }

    //выбираем лучшее отношение
    int indexRow = -1;//TODO: что-то делать при -1
    double min = Double.MAX_VALUE;
    for (int i = 0; i < countRow; i++) {
      if (Utilit.isZero(relations[i])
              || relations[i] < 0
              || !possibleRow[i]){
        continue;
      }
      if (relations[i] < min){
        min = relations[i];
        indexRow = i;
      }
    }

    return new int[]{indexRow, indexFirstPossibleCol};
  }

  //true - если весь столбец неположительные числа
  private boolean columnNoHavePositiveNumber(int index) {
    boolean bool = false;
    for (int i = 0; i < rows.size(); i++) {
      Row row = rows.get(i);
      Double value = row.getValue(index);
      bool = value < 0 || Utilit.isZero(value);
    }

    return bool;
  }

  public

  @Override
  public String toString() {
    String result = "     ";
    for (Integer index : indexesVarCol) {
      result += "X" + index + "   ";
    }
    result += "\n";

    for (int i = 0; i < rows.size(); i++) {
      String varRow = " ";
      if (i != rows.size() - 1) {
        varRow = "X" + indexesVarRow.get(i);
      }
      result += varRow + " " + rows.get(i) + "\n";
    }

    return result;
  }

  //представляет строку коэффициентов в симплекс таблице
  private class Row {
    List<Double> row = new ArrayList<>();

    public Row() {
    }

    public Row(Limit limit) {
      for (int i = 0; i < limit.getCountCoefs(); i++) {
        //пропускаем добавленные переменные
        if (i == limit.getCountCoefs() - 2) {
          continue;
        }
        Coefficient coef = limit.getCoefficient(i);
        row.add(coef.getValue());
      }
    }

    public void addValue(Double value) {
      row.add(value);
    }

    public void setValue(int index, double value){
      row.set(index, value);
    }

    public void multValue(int index, double value) {
      double newValue = row.get(index) * value;
      row.set(index, newValue);
    }

    public void mult(double value) {
      for (int i = 0; i < row.size(); i++) {
        multValue(i, value);
      }
    }

    public void subtract(Row row, int badIndex, double coef){
      for (int i = 0; i < getSize(); i++){
        if (i == badIndex){
          continue;
        }

        double newValue = this.row.get(i) - coef * row.getValue(i);
        this.row.set(i, newValue);
      }
    }

    public int getSize() {
      return row.size();
    }

    public void removeValue(int index){
      row.remove(index);
    }

    public double getValue(int index) {
      return row.get(index);
    }

    @Override
    public String toString() {
      String result = "";

      for (Double value : row) {
        result += String.format("%.2f", value) + " ";
      }

      return result;
    }
  }
}
