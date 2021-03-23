package me.aiglez.gangs.economy;

import com.google.common.base.Preconditions;
import me.aiglez.gangs.users.User;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

public interface Economy {

    String[] METRIC_PREFIXES = new String[]{"", "k", "M", "G", "T"};

    Pattern TRAILING_DECIMAL_POINT = Pattern.compile("[0-9]+\\.[kMGT]");

    Pattern METRIC_PREFIXED_NUMBER = Pattern.compile("\\-?[0-9]+(\\.[0-9])?[kMGT]");

    static String format(final long value) {
        double number = value;
        // if the number is negative, convert it to a positive number and add the minus sign to the output at the end
        boolean isNegative = number < 0;
        number = Math.abs(number);

        String result = new DecimalFormat("##0E0").format(number);

        int index = Character.getNumericValue(result.charAt(result.length() - 1)) / 3;
        result = result.replaceAll("E[0-9]", METRIC_PREFIXES[index]);

        while (result.length() > 4 || TRAILING_DECIMAL_POINT.matcher(result).matches()) {
            int length = result.length();
            result = result.substring(0, length - 2) + result.substring(length - 1);
        }

        return isNegative ? "-" + result : result;
    }

    @Deprecated
    static String oldFormat(double number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 999999) {
            return String.format("%.2fK%n", number / 1000);
        } else if (number < 999999999) {
            return String.format("%.2fM%n", number / 1000000);
        } else if (number > 99999999) {
            return String.format("%.2fB%n", number / 1000000000);
        } else return String.format("%.2fT%n", number / 1000000000 * 100);
    }

    void add(final User user, final double amount);

    void remove(final User user, final double amount);

    double balance(final User user);

    default boolean has(final User user, final double amount) {
        Preconditions.checkNotNull(user, "user may not be null");
        Preconditions.checkArgument(amount >= 0, "amount may not be negative");
        return balance(user) >= amount;
    }
}
