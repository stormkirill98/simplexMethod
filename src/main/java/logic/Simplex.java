package logic;

import logic.enums.End;
import logic.enums.Stage;

import java.util.ArrayList;
import java.util.List;

import static logic.Utilit.isExpressedVar;
import static logic.Utilit.isZero;

public class Simplex implements Cloneable {
  private List<Integer> indexesVarRow = new ArrayList<>();
  private List<Integer> indexesVarCol = new ArrayList<>();
  private List<Row> rows = new ArrayList<>();

  /*стадия алгоритма:
   * ART_BASIS - искусственный базис
   * SIMPLEX - симплекс-метод
   * END - закончен*/
  private Stage stage = Stage.ART_BASIS;
  private int step = 0;

  //число переменных, которые были в задаче с самого начала, без тех которые добавили мы
  private int countOurVar = 0;

  private int[] indexesBaseElement;
  private boolean manuallySetBaseElement = false;

  private int indexNoPositiveColumn = -1;

  public Simplex(List<Limit> limits, int countVar,
                 List<Integer> indexesExpressedVars, Function function){
    for (int i = 0; i < countVar; i++) {
      if (isExpressedVar(i, indexesExpressedVars)){
        indexesVarRow.add(i + 1);
      } else {
        indexesVarCol.add(i + 1);
      }
    }

    for (Limit limit : limits) {
      rows.add(new Row(limit, 0));
    }

    //заполняем последнюю строку в симплекс таблице
    Row lastRow = new Row();
    for (int j = 0; j < indexesVarCol.size(); j++) {
      //индекс переменной через которую выражены другие(вверху симплекс таблицы перпеменные)
      int indexVar = indexesVarCol.get(j);

      double newValue = function.getCoefficients(indexVar - 1).getValue();

      for (int i = 0; i < rows.size(); i++) {
        //индекс выражаемой переменной
        int indexExpressVar = indexesVarRow.get(i);
        Coefficient functionCoef = function.getCoefficients(indexExpressVar - 1);
        newValue -= functionCoef.getValue() * rows.get(i).get(j);
      }

      lastRow.addValue(newValue);
    }


    //считаем значение функции(последняя йчейка в таблице)
    int indexLastCol = indexesVarCol.size();

    double newValue = 0.0;
    for (int i = 0; i < rows.size(); i++) {
      //индекс выражаемой переменной
      int indexExpressVar = indexesVarRow.get(i);
      Coefficient functionCoef = function.getCoefficients(indexExpressVar - 1);
      newValue -= functionCoef.getValue() * rows.get(i).get(indexLastCol);
    }

    lastRow.addValue(newValue);

    rows.add(lastRow);
  }

  //TODO:почему-то при заданной начальное элементе сюда заходит
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
      for (Limit limit : limits) {
        value -= limit.getCoefficient(i).getValue();
      }
      row.addValue(value);
    }
    rows.add(row);
  }

  public double getValue(int i, int j) {
    return rows.get(i).get(j);
  }

  public List<Integer> getIndexesVarRow() {
    return indexesVarRow;
  }

  public List<Integer> getIndexesVarCol() {
    return indexesVarCol;
  }

  public int getCountRows() {
    return rows.size();
  }

  public int getCountCols() {
    return rows.get(0).getSize();
  }

  public Stage getStage() {
    return stage;
  }

  public int[] getIndexesBaseElement() {
    return indexesBaseElement;
  }

  public void setValue(int i, int j, double value) {
    rows.get(i).setValue(j, value);
  }

  public void swap(int indexVarRow, int indexVarCol) {
    Integer bufIndex = indexesVarCol.get(indexVarCol);
    indexesVarCol.set(indexVarCol, indexesVarRow.get(indexVarRow));
    indexesVarRow.set(indexVarRow, bufIndex);
  }

  public void multCol(int index, double value) {
    for (Row row : rows) {
      row.multValue(index, value);
    }
  }

  public void multRow(int index, double value) {
    Row row = rows.get(index);
    row.mult(value);
  }

  public void removeColumn(int index) {
    indexesVarCol.remove(index);
    for (Row row : rows) {
      row.removeValue(index);
    }
  }

  public void subtractRow(int indexRow, int indexCol, double previousCoef) {
    Row subtractRow = rows.get(indexRow);
    for (int i = 0; i < rows.size(); i++) {
      if (i == indexRow) {
        continue;
      }

      Row row = rows.get(i);
      double coef = row.get(indexCol) * previousCoef;
      row.subtract(subtractRow, indexCol, coef);
    }
  }

  //return array [i, j]
  //i, j - индексы базового элемента
  public int[] searchBaseElement() {
    int countVar = indexesVarCol.size();
    int countRow = rows.size() - 1;
    this.manuallySetBaseElement = false;

    //определяем в каких строках нам нужно избавляться от переменной
    boolean[] possibleRow = getPossibleRows();

    //определяем первый подходящий столбец
    int indexFirstPossibleCol = -1;
    for (int i = 0; i < countVar; i++) {
      if (columnIsPossible(i)) {
        indexFirstPossibleCol = i;
        break;
      }
    }

    /*
    если не нашелся первый подходящий столбец, значит
    осталась переменная, которая еще не ушла наверх,
    но шагов уже нет.
    возвращаем {-1, -1} для прекращения алгоритма
    */
    if (indexFirstPossibleCol == -1) {
      return new int[]{indexFirstPossibleCol, -1};
    }

    //считаем отношения в столбце для выбора лучшего базового элемента
    double[] relations = countRelationsInColumn(indexFirstPossibleCol, possibleRow);

    //выбираем лучшее отношение
    int indexRow = searchIndexRow(countRow, relations, possibleRow);

    //если не нашлось базового элемента, при котором можно поменять переменную
    //делаем какой-то случайный шаг
    //TODO:скорее всего вычеркнется не нужная переменная, нужно это проверять
    if (indexRow == -1) {
      for (int i = 0; i < countRow; i++) {
        possibleRow[i] = true;
      }
    }

    indexRow = searchIndexRow(countRow, relations, possibleRow);

    return new int[]{indexRow, indexFirstPossibleCol};
  }

  private boolean[] getPossibleRows() {
    int countRow = rows.size() - 1;

    boolean[] possibleRow = new boolean[countRow];
    if (stage == Stage.ART_BASIS) {
      for (int i = 0; i < indexesVarRow.size(); i++) {
        possibleRow[i] = indexesVarRow.get(i) > countOurVar;
      }
    }
    if (stage == Stage.SIMPLEX) {
      for (int i = 0; i < indexesVarRow.size(); i++) {
        possibleRow[i] = true;
      }
    }

    return possibleRow;
  }

  private boolean columnIsPossible(int index) {
    Row lastRow = rows.get(rows.size() - 1);

    //последний элемент в столбце отрицательный?
    return lastRow.get(index) < 0;
  }

  private double[] countRelationsInColumn(int indexColumn, boolean[] possibleRow) {
    int countVar = indexesVarCol.size();
    int countRow = rows.size() - 1;

    double[] relations = new double[countRow];
    for (int i = 0; i < countRow; i++) {
      if (!possibleRow[i]) {
        relations[i] = Double.MAX_VALUE;
      }

      double value = rows.get(i).get(indexColumn);
      double freeValue = rows.get(i).get(countVar);//свободное значение в таблице(самое последнее в строке)
      if (isZero(value) || value < 0) {
        relations[i] = Double.MAX_VALUE;
        continue;
      }

      relations[i] = freeValue / value;
    }

    return relations;
  }

  private int searchIndexRow(int countRow, double[] relations, boolean[] possibleRow) {
    int indexRow = -1;

    double min = Double.MAX_VALUE;
    for (int i = 0; i < countRow; i++) {
      if ((!isZero(relations[i])
              && relations[i] < 0)
              || !possibleRow[i]) {
        continue;
      }
      if (relations[i] < min) {
        min = relations[i];
        indexRow = i;
      }
    }

    return indexRow;
  }

  //true - если в столбце есть положительные числа
  private boolean columnHavePositiveNumber(int index) {
    for (Row row : rows) {
      Double value = row.get(index);
      if (value > 0) {
        //проверка на ноль, т.к. может -0.0(проверка до -12 порядка)
        if (isZero(value)) {
          continue;
        }
        return true;
      }
    }

    return false;
  }

  //проверяем последнюю строку, все ли в ней нули
  private boolean lastRowIsZero() {
    Row lastRow = rows.get(rows.size() - 1);
    for (int i = 0; i < lastRow.getSize(); i++) {
      if (!isZero(lastRow.get(i))) {
        return false;
      }
    }

    return true;
  }

  //последняя строка не отрицательная
  private boolean lastRowIsNoNegative() {
    Row lastRow = rows.get(rows.size() - 1);
    for (int i = 0; i < lastRow.getSize() - 1; i++) {
      double value = lastRow.get(i);
      if (value < 0) {
        //проверка на ноль, т.к. может -0.0(проверка до -12 порядка)
        if (isZero(value)) {
          continue;
        }

        return false;
      }
    }

    return true;
  }

  private boolean failure() {
    for (int i = 0; i < rows.get(0).getSize(); i++) {
      //если есть столбец, в котором все числа неположительные
      if (!columnHavePositiveNumber(i)) {
        indexNoPositiveColumn = i;
        return true;
      }
    }

    return false;
  }

  private boolean fPositive() {
    Row row = rows.get(rows.size() - 1);
    double fValue = row.get(row.getSize() - 1);
    if (fValue > 0 && !isZero(fValue)) {
      return true;
    }

    return false;
  }

  //проверяем закончился ли метод искуственнго базиса
  public End endArtBasis() {
    if (fPositive()) {
      return End.FAILURE;
    }

    if (lastRowIsZero()) {
      stage = Stage.SIMPLEX;
      return End.SUCCESS_ART_BASIS;
    }

    if (lastRowIsNoNegative()) {
      return End.FAILURE;
    }

    if (failure()) {
      return End.FAILURE;
    }

    return End.CONTINUE;
  }

  //проверяем конец алгоритма
  public End end() {
    if (lastRowIsNoNegative()) {
      stage = Stage.END;
      return End.SUCCESS_ALL;
    }

    if (failure()) {
      return End.FAILURE;
    }

    return End.CONTINUE;
  }

  public void recountLastRow(Function function) {
    Row lastRow = rows.get(rows.size() - 1);
    for (int j = 0; j < lastRow.getSize() - 1; j++) {
      //индекс переменной через которую выражены другие(вверху симплекс таблицы перпеменные)
      int indexVar = indexesVarCol.get(j);

      double newValue = function.getCoefficients(indexVar - 1).getValue();
      newValue = countNewValueLastRow(function, newValue, j);

      lastRow.setValue(j, newValue);
    }


    //считаем значение функции(последняя йчейка в таблице)
    int indexLastCol = lastRow.getSize() - 1;

    double newValue = 0.0;
    newValue = countNewValueLastRow(function, newValue, indexLastCol);

    lastRow.setValue(indexLastCol, newValue);

    rows.set(rows.size() - 1, lastRow);
  }

  private double countNewValueLastRow(Function function, double newValue, int indexCol) {
    for (int i = 0; i < rows.size() - 1; i++) {
      //индекс выражаемой переменной
      int indexExpressVar = indexesVarRow.get(i);
      Coefficient functionCoef = function.getCoefficients(indexExpressVar - 1);
      newValue -= functionCoef.getValue() * rows.get(i).get(indexCol);
    }

    return newValue;
  }

  public Double getFunctionExtr() {
    if (stage != Stage.END) {
      return null;
    }

    Row row = rows.get(rows.size() - 1);
    return row.get(row.getSize() - 1);
  }

  public List<Double> getPointExtr() {
    if (stage != Stage.END) {
      return null;
    }

    if (countOurVar == 0){
      countOurVar = indexesVarCol.size() + indexesVarRow.size();
    }

    //ооздаем лист размером с кол-вом первоночальных переменных
    List<Double> point = new ArrayList<>(countOurVar);

    //инициализируем лист нулями
    for (int i = 0; i < countOurVar; i++) {
      point.add(0.0);
    }

    //переменные которые не нулевые(находящиеся в строках симплекса)
    for (int i = 0; i < rows.size() - 1; i++) {
      Row row = rows.get(i);
      int indexVar = indexesVarRow.get(i) - 1;
      double value = row.get(row.getSize() - 1);
      point.set(indexVar, value);
    }

    return point;
  }

  public boolean canBaseElement(int i, int j) {
    if (i == rows.size() - 1 || j == rows.get(0).getSize() - 1) {
      return false;
    }

    double value = rows.get(i).get(j);
    if (value < 0 || isZero(value)) {
      return false;
    }

    boolean[] possibleRows = getPossibleRows();
    if (!possibleRows[i]) {
      return false;
    }

    if (!columnIsPossible(j)) {
      return false;
    }

    if (!isMinInColumn(i, j, possibleRows)) {
      return false;
    }


    return true;
  }

  private boolean isMinInColumn(int i, int j, boolean[] possibleRows) {
    double[] relations = countRelationsInColumn(j, possibleRows);
    double relation = relations[i];

    for (int index = 0; index < relations.length; index++) {
      if (index == i) {
        continue;
      }
      if (relations[index] < relation) {
        return false;
      }
    }

    return true;
  }

  public void setIndexesBaseElement(int[] indexesBaseElement) {
    this.indexesBaseElement = indexesBaseElement;
    this.manuallySetBaseElement = true;
  }

  public int getIndexNoPositiveColumn() {
    return indexNoPositiveColumn;
  }

  public boolean isManuallySetBaseElement() {
    return manuallySetBaseElement;
  }

  public int getStep() {
    return step;
  }

  public void setStep(int step) {
    this.step = step;
  }



  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("     ");
    for (Integer index : indexesVarCol) {
      result.append("X").append(index).append("   ");
    }
    result.append("\n");

    for (int i = 0; i < rows.size(); i++) {
      String varRow = " ";
      if (i != rows.size() - 1) {
        varRow = "X" + indexesVarRow.get(i);
      }
      result.append(varRow).append(" ").append(rows.get(i)).append("\n");
    }

    return result.toString();
  }

  @Override
  protected Simplex clone() {
    Simplex simplex = null;

    try {
      simplex = (Simplex) super.clone();

      simplex.indexesVarRow = new ArrayList<>(this.indexesVarRow);
      simplex.indexesVarCol = new ArrayList<>(this.indexesVarCol);

      simplex.rows = new ArrayList<>(rows.size());
      for (Row row : this.rows) {
        simplex.rows.add(row.clone());
      }

    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }

    return simplex;
  }
}

//представляет строку коэффициентов в симплекс таблице
class Row implements Cloneable {
  List<Double> row = new ArrayList<>();

  public Row() {
  }

  public Row(Limit limit, int index) {
    for (int i = 0; i < limit.getCountCoefs(); i++) {
      Coefficient coef = limit.getCoefficient(i);
      row.add(coef.getValue());
    }
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

  public void setValue(int index, double value) {
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

  public void subtract(Row row, int badIndex, double coef) {
    for (int i = 0; i < getSize(); i++) {
      if (i == badIndex) {
        continue;
      }

      double newValue = this.row.get(i) - coef * row.get(i);
      this.row.set(i, newValue);
    }
  }

  public int getSize() {
    return row.size();
  }

  public void removeValue(int index) {
    row.remove(index);
  }

  public double get(int index) {
    return row.get(index);
  }

  @Override
  protected Row clone() throws CloneNotSupportedException {
    Row row = (Row) super.clone();
    row.row = new ArrayList<>(this.row);

    return row;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();

    for (Double value : row) {
      result.append(String.format("%.2f", value)).append(" ");
    }

    return result.toString();
  }
}