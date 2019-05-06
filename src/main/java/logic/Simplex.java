package logic;

import logic.enums.End;
import logic.enums.Stage;

import java.util.ArrayList;
import java.util.List;

import static logic.Utilit.isZero;

public class Simplex {
  private List<Integer> indexesVarRow = new ArrayList<>();
  private List<Integer> indexesVarCol = new ArrayList<>();
  private List<Row> rows = new ArrayList<>();

  /*стадия алгоритма:
  * ART_BASIS - искусственный базис
  * SIMPLEX - симплекс-метод
  * END - закончен*/
  private Stage stage = Stage.ART_BASIS;

  //число переменных, которые были в задаче с самого начала, без тех которые добавили мы
  private int countOurVar;
  private int[] indexesBaseElement;

  private int indexNoPositiveColumn = -1;

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

  public double getValue(int i, int j){
    return rows.get(i).getValue(j);
  }

  public List<Integer> getIndexesVarRow(){
    return indexesVarRow;
  }

  public List<Integer> getIndexesVarCol(){
    return indexesVarCol;
  }

  public int getCountRows(){
    return rows.size();
  }

  public int getCountCols(){
    return rows.get(0).getSize();
  }

  public Stage getStage() {
    return stage;
  }

  public int[] getIndexesBaseElement() {
    return indexesBaseElement;
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
    for (Row row : rows) {
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

  //return array [i, j]
  //i, j - индексы базового элемента
  public int[] searchBaseElement() {
    int countVar = indexesVarCol.size();
    int countRow = rows.size() - 1;

    //определяем в каких строках нам нужно избавляться от переменной
    boolean[] possibleRow = new boolean[countRow];
    if (stage == Stage.ART_BASIS) {
      for (int i = 0; i < indexesVarRow.size(); i++) {
        possibleRow[i] = indexesVarRow.get(i) > countOurVar;
      }
    }
    if (stage == Stage.SIMPLEX){
      for (int i = 0; i < indexesVarRow.size(); i++) {
        possibleRow[i] = true;
      }
    }

    //определяем первый подходящий столбец
    int indexFirstPossibleCol = -1;
    Row lastRow = rows.get(rows.size() - 1);
    for (int i = 0; i < countVar; i++) {
      //последний элемент в столбце отрицательный?
      if (lastRow.getValue(i) < 0) {
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
    if (indexFirstPossibleCol == -1){
      return new int[]{indexFirstPossibleCol, -1};
    }

    //считаем отношения в столбце для выбора лучшего базового элемента
    double[] relations = new double[countRow];
    for (int i = 0; i < countRow; i++) {
        if (!possibleRow[i]){
          relations[i] = Double.MAX_VALUE;
        }

        double value = rows.get(i).getValue(indexFirstPossibleCol);
        double freeValue = rows.get(i).getValue(countVar);//свободное значение в таблице(самое последнее в строке)
        if (isZero(value) || value < 0) {
          relations[i] = Double.MAX_VALUE;
          continue;
        }

        relations[i] = freeValue / value;
    }

    //выбираем лучшее отношение
    int indexRow = searchIndexRow(countRow, relations, possibleRow);

    //если не нашлось базового элемента, при котором можно поменять переменную
    if (indexRow == -1){
      for (int i = 0; i < countRow; i++) {
        possibleRow[i] = true;
      }
    }

    indexRow = searchIndexRow(countRow, relations, possibleRow);

    return new int[]{indexRow, indexFirstPossibleCol};
  }

  private int searchIndexRow(int countRow, double[] relations, boolean[] possibleRow){
    int indexRow = -1;

    double min = Double.MAX_VALUE;
    for (int i = 0; i < countRow; i++) {
      if ((!isZero(relations[i])
              && relations[i] < 0)
              || !possibleRow[i]){
        continue;
      }
      if (relations[i] < min){
        min = relations[i];
        indexRow = i;
      }
    }

    return indexRow;
  }

  //true - если в столбце есть положительные числа
  private boolean columnHavePositiveNumber(int index) {
    for (Row row : rows) {
      Double value = row.getValue(index);
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
  private boolean lastRowIsZero(){
    Row lastRow = rows.get(rows.size() - 1);
    for (int i = 0; i < lastRow.getSize(); i++){
      if (!isZero(lastRow.getValue(i))){
        return false;
      }
    }

    return true;
  }

  //последняя строка не отрицательная
  private boolean lastRowIsNoNegative(){
    Row lastRow = rows.get(rows.size() - 1);
    for (int i = 0; i < lastRow.getSize() - 1; i++){
      double value = lastRow.getValue(i);
      if (value < 0){
        //проверка на ноль, т.к. может -0.0(проверка до -12 порядка)
        if (isZero(value)){
          continue;
        }

        return false;
      }
    }

    return true;
  }

  private boolean failure(){
    for (int i = 0; i < rows.get(0).getSize(); i++){
      //если есть столбец, в котором все числа неположительные
      if (!columnHavePositiveNumber(i)){
        indexNoPositiveColumn = i;
        return true;
      }
    }

    return false;
  }

  private boolean fPositive(){
    Row row = rows.get(rows.size() - 1);
    double fValue = row.getValue(row.getSize() - 1);
    if (fValue > 0 && !isZero(fValue)){
      return true;
    }

    return false;
  }

  //проверяем закончился ли метод искуственнго базиса
  public End endArtBasis(){
    if (fPositive()){
      return End.FAILURE;
    }

    if (lastRowIsZero()){
      stage = Stage.SIMPLEX;
      return End.SUCCESS_ART_BASIS;
    }

    if (lastRowIsNoNegative()){
      return End.FAILURE;
    }

    if (failure()){
      return End.FAILURE;
    }

    return End.CONTINUE;
  }

  //проверяем конец алгоритма
  public End end(){
    if (lastRowIsNoNegative()){
      stage = Stage.END;
      return End.SUCCESS_ALL;
    }

    if (failure()){
      return End.FAILURE;
    }

    return End.CONTINUE;
  }

  public void recountLastRow(Function function){
    Row lastRow = rows.get(rows.size() - 1);
    for (int j = 0; j < lastRow.getSize() - 1; j++){
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

    lastRow.setValue(indexLastCol, newValue );

    rows.set(rows.size() - 1, lastRow);
  }

  private double countNewValueLastRow(Function function, double newValue, int indexCol){
    for (int i = 0; i < rows.size() - 1; i++){
      //индекс выражаемой переменной
      int indexExpressVar = indexesVarRow.get(i);
      Coefficient functionCoef = function.getCoefficients(indexExpressVar - 1);
      newValue -= functionCoef.getValue() * rows.get(i).getValue(indexCol);
    }

    return newValue;
  }

  public Double getFunctionExtr(){
    if (stage != Stage.END){
      return null;
    }

    Row row = rows.get(rows.size() - 1);
    return -row.getValue(row.getSize() - 1);
  }

  public List<Double> getPointExtr(){
    if (stage != Stage.END) {
      return null;
    }

    //ооздаем лист размером с кол-вом первоночальных переменных
    List<Double> point = new ArrayList<>(countOurVar);

    //инициализируем лист нулями
    for (int i = 0; i < countOurVar; i++){
      point.add(0.0);
    }

    //переменные которые не нулевые(находящиеся в строках симплекса)
    for (int i = 0; i < rows.size() - 1; i++){
      Row row = rows.get(i);
      int indexVar = indexesVarRow.get(i)  - 1;
      double value = row.getValue(row.getSize() - 1);
      point.set(indexVar, value);
    }

    return point;
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

  public void setIndexesBaseElement(int[] indexesBaseElement) {
    this.indexesBaseElement = indexesBaseElement;
  }

  public int getIndexNoPositiveColumn() {
    return indexNoPositiveColumn;
  }
}

//представляет строку коэффициентов в симплекс таблице
class Row {
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
    StringBuilder result = new StringBuilder();

    for (Double value : row) {
      result.append(String.format("%.2f", value)).append(" ");
    }

    return result.toString();
  }
}