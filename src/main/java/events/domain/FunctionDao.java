package events.domain;

import logic.enums.TypeProblem;

public class FunctionDao {
  private double[] coefs;
  private TypeProblem typeProblem;

  public FunctionDao(double[] coefs, TypeProblem typeProblem) {
    this.coefs = coefs;
    this.typeProblem = typeProblem;
  }

  public double[] getCoefs() {
    return coefs;
  }

  public TypeProblem getTypeProblem() {
    return typeProblem;
  }
}
