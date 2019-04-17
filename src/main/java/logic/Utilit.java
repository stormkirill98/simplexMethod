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
}
