package logic;

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
}
