package logic;

import com.sun.org.apache.regexp.internal.RE;
import logic.enums.TypeProblem;

import java.util.ArrayList;
import java.util.List;

public class Function {
  private List<Coefficient> coefficients = new ArrayList<>();
  private TypeProblem type;

  //class presents function which extr need to find
  public Function(TypeProblem type){
    this.type = type;
  }

  public void addCoefficient(Coefficient coef){
    coefficients.add(coef);
  }

  public Coefficient getCoefficients(int index){
    return coefficients.get(index);
  }

  public int getCountVar(){
    return coefficients.size();
  }

  public void removeCoefficients(int index){
    coefficients.remove(index);
  }

  public TypeProblem getType() {
    return type;
  }

  public void setType(TypeProblem type) {
    this.type = type;
  }

  public void reverseType(){
    if (type == TypeProblem.MIN){
      type = TypeProblem.MAX;
    } else {
      type = TypeProblem.MIN;
    }

    coefficients.forEach(coef -> coef.setValue(coef.getValue() * -1));
  }

  @Override
  public String toString(){
    String result = "";

    for(Coefficient coef: coefficients){
      result += coef + " + ";
    }

    result = result.substring(0, result.lastIndexOf("+") - 1);

    result += "->" + getType();

    return result;
  }
}
