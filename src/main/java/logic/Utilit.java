package logic;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.List;

public class Utilit {
  public static final double TOLERANCE = 1.0E-12;

  public static boolean isZero(Double value) {
    return Math.abs(value) < TOLERANCE;
  }

  public static boolean isDouble(String str) {
    return str.matches("-?\\d*[.,]?\\d*");
  }

  public static boolean isFraction(String str) {return  str.matches("-?\\d+/\\d*"); }

  public static Double strToDouble(String str) {
    str = str.replace(",", ".");

    if (str.isEmpty()) {
      return null;
    }

    if (str.equals("-")) {
      return -1.0;
    }

    return Double.valueOf(str);
  }

  public static Double fractionToDouble(String str) {
    String[] nums = str.split("/");
    int numerator = Integer.valueOf(nums[0]);
    if (nums.length == 1 || nums[1].isEmpty()) {
      return (double)numerator;
    }
    int denominator = Integer.valueOf(nums[1]);

    return  (double)numerator / denominator;
  }

  public static boolean isNatural(String str) {
    return str.matches("\\d*");
  }

  public static String convertDecimalToFraction(double x) {
    if (x < 0) {
      return "-" + convertDecimalToFraction(-x);
    }
    double h1 = 1;
    double h2 = 0;
    double k1 = 0;
    double k2 = 1;
    double b = x;
    do {
      double a = Math.floor(b);
      double aux = h1;
      h1 = a * h1 + h2;
      h2 = aux;
      aux = k1;
      k1 = a * k1 + k2;
      k2 = aux;
      b = 1 / (b - a);
    } while (Math.abs(x - h1 / k1) > x * TOLERANCE);

    if (Math.abs(h1 - k1) < TOLERANCE) {
      return "1";
    }

    if (Math.abs(k1 - 1) < TOLERANCE) {
      return String.valueOf((int) h1);
    }

    return (int) h1 + "/" + (int) k1;
  }

  //переводит число в надстрочный вид
  public static String superscript(String str) {
    str = str.replaceAll("0", "⁰");
    str = str.replaceAll("1", "¹");
    str = str.replaceAll("2", "²");
    str = str.replaceAll("3", "³");
    str = str.replaceAll("4", "⁴");
    str = str.replaceAll("5", "⁵");
    str = str.replaceAll("6", "⁶");
    str = str.replaceAll("7", "⁷");
    str = str.replaceAll("8", "⁸");
    str = str.replaceAll("9", "⁹");
    return str;
  }

  //переводит число в подстрочный вид
  public static String subscript(String str) {
    str = str.replaceAll("0", "₀");
    str = str.replaceAll("1", "₁");
    str = str.replaceAll("2", "₂");
    str = str.replaceAll("3", "₃");
    str = str.replaceAll("4", "₄");
    str = str.replaceAll("5", "₅");
    str = str.replaceAll("6", "₆");
    str = str.replaceAll("7", "₇");
    str = str.replaceAll("8", "₈");
    str = str.replaceAll("9", "₉");
    return str;
  }

  public static boolean validateDouble(TextField textField, String oldValue, String newValue) {
    if (!isDouble(newValue) && !isFraction(newValue)) {
      textField.setText(oldValue);
      return false;
    }

    if (newValue.equals(",") || newValue.equals(".")) {
      textField.setText(newValue);
      return false;
    }

    if (newValue.isEmpty()) {
      textField.setId("text-field-empty");
    } else {
      textField.setId("");
    }

    return true;
  }

  public static boolean isFilled(Pane pane) {
    boolean filled = true;

    ObservableList<Node> childrens = pane.getChildren();

    for (Node node : childrens) {
      TextField textField;
      try {
        textField = (TextField) node;
      } catch (ClassCastException e) {
        continue;
      }

      if (textField.getText().isEmpty()) {
        if (filled) {
          filled = false;
        }
        textField.setId("text-field-empty");
      }
    }

    return filled;
  }

  static void swap(double mat[][],
                   int row1, int row2, int col) {
    for (int i = 0; i < col; i++) {
      double temp = mat[row1][i];
      mat[row1][i] = mat[row2][i];
      mat[row2][i] = temp;
    }
  }

  public static boolean isExpressedVar(int i, List<Integer> indexesExpressedVars) {
    for (int j = 0; j < indexesExpressedVars.size(); j++) {
      if (i == indexesExpressedVars.get(j)) {
        return true;
      }
    }

    return false;
  }


  static double[][] toSquare(double arr[][]) {
    double[][] mat = new double[arr.length][arr[0].length];
    for (int i = 0; i < arr.length; i++) {
      if (arr[i].length >= 0) {
        System.arraycopy(arr[i], 0, mat[i], 0, arr[i].length);
      }
    }

    int n = mat.length;
    int m = mat[0].length;
    if (n == m) {
      return mat;
    }

    if (n < m) {
      double[][] newMat = new double[m][m];
      copyMatrix(mat, newMat);
      for (int i = n; i < m; i++) {
        for (int j = 0; j < m; j++) {
          newMat[i][j] = 0;
        }
      }

      return newMat;
    } else {
      double[][] newMat = new double[n][n];
      copyMatrix(mat, newMat);
      for (int j = m; j < n - m; j++) {
        for (int i = 0; i < m; i++) {
          newMat[i][j] = 0;
        }
      }

      return newMat;
    }
  }

  static void copyMatrix(double from[][], double to[][]) {
    for (int i = 0; i < from.length; i++) {
      for (int j = 0; j < from[0].length; j++) {
        to[i][j] = from[i][j];
      }
    }
  }

  static int rankOfMatrix(double mat[][]) {
    mat = toSquare(mat);

    int rank = mat.length;

    for (int row = 0; row < rank; row++) {
      if (mat[row][row] != 0) {
        for (int col = 0; col < mat[0].length; col++) {
          if (col != row) {
            double mult =
                    mat[col][row] /
                            mat[row][row];

            for (int i = 0; i < rank; i++)

              mat[col][i] -= mult
                      * mat[row][i];
          }
        }
      } else {
        boolean reduce = true;

        for (int i = row + 1; i < mat[0].length; i++) {
          if (mat[i][row] != 0) {
            swap(mat, row, i, rank);
            reduce = false;
            break;
          }
        }

        if (reduce) {
          rank--;

          for (int i = 0; i < mat[0].length; i++)
            mat[i][row] = mat[i][rank];
        }

        row--;
      }
    }

    return rank;
  }
}

