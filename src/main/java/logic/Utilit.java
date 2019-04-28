package logic;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.text.DecimalFormat;

public class Utilit {
  static final double TOLERANCE = 1.0E-12;

  static String format(Double value) {
    DecimalFormat decimalFormat = new DecimalFormat("##.####");
    return decimalFormat.format((value));
  }

  public static boolean isZero(Double value) {
    return Math.abs(value) < TOLERANCE;
  }

  public static boolean isDouble(String str) {
    return str.matches("[-+]?\\d*[.,]?\\d*");
  }


  public static Double strToDouble(String str) {
    str = str.replace(",", ".");

    if (str.isEmpty()) {
      return 0.0;
    }

    if (str.equals("-")) {
      return -1.0;
    }

    return Double.valueOf(str);
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

    return "(" + (int) h1 + "/" + (int) k1 + ")";
  }

  public static String numToRim(int num) {
    switch (num) {
      case 1:
        return "I";
      case 2:
        return "II";
      case 3:
        return "III";
      case 4:
        return "IV";
      case 5:
        return "V";
      case 6:
        return "VI";
      case 7:
        return "VII";
      case 8:
        return "VIII";
      case 9:
        return "IX";
      case 10:
        return "X";
      default:
        return String.valueOf(num);
    }
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

  public static void changeSizeTextField(TextField textField) {
    if (textField == null) {
      return;
    }

    Text text = new Text(textField.getText());
    text.setFont(textField.getFont());
    new Scene(new Group(text));
    text.applyCss();

    double lengthText = text.getLayoutBounds().getWidth() + 20;

    if (lengthText < textField.getMinWidth()) {
      lengthText = textField.getMinWidth();
    }

    if (lengthText > textField.getMaxWidth()) {
      lengthText = textField.getMaxWidth();
    }

    textField.setPrefWidth(lengthText);
  }

  public static boolean validateDouble(TextField textField, String oldValue, String newValue){
    if (!isDouble(newValue)) {
      textField.setText(oldValue);
      return false;
    }

    if (newValue.equals(",") || newValue.equals(".")){
      textField.setText(newValue);
      return false;
    }

    if (newValue.isEmpty()){
      textField.setId("text-field-empty");
    } else {
      textField.setId("");
    }

    return true;
  }
}
