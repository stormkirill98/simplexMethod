package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Equation {
    private List<Double> equation;

    public Equation() {
        equation = new ArrayList<>();
    }

    public Equation(Equation eq) {
        equation = new ArrayList<>(eq.equation);
    }

    public void add(Double num) {
        equation.add(num);
    }

    public Double get(int i) {
        return equation.get(i);
    }

    public void mult(Double coefficient) {
        ListIterator<Double> iterator = equation.listIterator();

        while (iterator.hasNext()) {
            Double number = iterator.next();
            iterator.set(number * coefficient);
        }
    }

    public Double reduceCoef() {
        int i = 0;
        while (equation.get(i) == 0) {
            i++;

            if (i >= equation.size()) {
                return 0.0;
            }
        }
        Double coef = 1 / equation.get(i);
        mult(coef);
        return coef;
    }

    public int size() {
        return equation.size();
    }

    public ListIterator<Double> getIterator() {
        return equation.listIterator();
    }

    public void print() {
        for (Double num : equation) {
            String fractionNum = Utilit.convertDecimalToFraction(num);
            System.out.printf("%7s ", fractionNum);
        }
    }

    public boolean equals(Equation equation) {
        for (int i = 0; i < this.size(); i++) {
            if (!this.equation.get(i).equals(equation.get(i))) {
                return false;
            }
        }
        return true;
    }

    public Equation plusEquation(Equation eqAdded) {
        Equation eq = new Equation(eqAdded);
        int indexFirstNotZero = eq.indexFirstNotZero();
        if (indexFirstNotZero < 0) {
            return this;
        }
        Double coef = -this.equation.get(indexFirstNotZero);
        eq.mult(coef);

       /* if (Main.printActions) {
            if (coef > 0) {
                System.out.printf(" + %.2f", coef);
            } else {
                System.out.printf(" - %.2f", Math.abs(coef));
            }
        }*/

        Equation newEq = new Equation();
        for (int i = 0; i < this.equation.size(); i++) {
            newEq.add(this.equation.get(i) + eq.get(i));
        }

        return newEq;
    }

    private int indexFirstNotZero() {
        for (int i = 0; i < this.equation.size(); i++) {
            if (Math.abs(equation.get(i)) > Utilit.TOLERANCE) {
                return i;
            }
        }

        return -1;
    }

    public void printExpressVar() {
        int index = indexFirstNotZero();
        if (index < 0) {
            return;
        }
        StringBuilder str = new StringBuilder("x"
                + (index) + " = "
                + Utilit.convertDecimalToFraction(equation.get(equation.size() - 1)));

        index++;

        for (; index < equation.size() - 1; index++) {
            if (equation.get(index) == 0) {
                continue;
            }

            double num = equation.get(index);
            if (num > 0) {
                String numStr = Utilit.convertDecimalToFraction(num);
                if (numStr.equals("1")) {
                    str.append(" - x").append(index + 1);
                } else {
                    str.append(" - ").append(numStr).append("*x").append(index + 1);
                }
            } else {
                String numStr = Utilit.convertDecimalToFraction(Math.abs(num));
                if (numStr.equals("1")) {
                    str.append(" + x").append(index + 1);
                } else {
                    str.append(" + ").append(numStr).append("*x").append(index + 1);
                }
            }
        }

        System.out.println(str);
    }

    public boolean validate(){
        boolean allCoefZero = true;
        for (int i = 0; i < size() - 1; i++){
            if (Math.abs(equation.get(i)) > Utilit.TOLERANCE){
                allCoefZero = false;
            }
        }

        return !allCoefZero;
    }

}