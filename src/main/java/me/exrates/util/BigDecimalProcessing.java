package me.exrates.util;

import me.exrates.exception.BigDecimalParseException;
import me.exrates.model.enums.ActionType;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * Contained constans and methods to operate with BigDecimal value
 * Created by Valk on 30.04.2016.
 */

public class BigDecimalProcessing {
    protected static final int SCALE = 9;
    protected static final RoundingMode ROUND_TYPE = RoundingMode.HALF_UP;
    protected static final String PATTERN = "###,##0." + new String(new char[SCALE]).replace("\0", "0");
    protected static final String PATTERN_SHORT = "###,##0." + new String(new char[SCALE]).replace("\0", "#");


    /**
     * Checks operands and if operand is "null" sets it to 0
     *
     * @param value1
     * @param value2
     * @param actionType
     * @return result of doAction with the same arguments
     */
    public static BigDecimal doActionLax(BigDecimal value1, BigDecimal value2, ActionType actionType) {
        if (value1 == null) value1 = BigDecimal.ZERO;
        if (value2 == null) value2 = BigDecimal.ZERO;
        return doAction(value1, value2, actionType);
    }

    /**
     * Executes arithmetic operation and returns BigDecimal value with applied <b>SCALE</b> and <b>ROUND_TYPE</b>
     * and removed trailing zeros. Or "null" if at least one of operands is "null"
     * Before execution operation to operands apply <b>SCALE</b> and <b>ROUND_TYPE</b>
     *
     * @param value1     is the first operand for operation
     * @param value2     is the second operand for operation
     * @param actionType
     * @return BigDecimal value with applied <b>SCALE</b> and <b>ROUND_TYPE</b>
     * and removed trailing zeros. Or "null" if at least one of operands is "null"
     */
    public static BigDecimal doAction(BigDecimal value1, BigDecimal value2, ActionType actionType) {
        return doAction(value1, value2, actionType, ROUND_TYPE);
    }

    public static BigDecimal doAction(BigDecimal value1, BigDecimal value2, ActionType actionType, RoundingMode roundingMode) {
        if (value1 == null || value2 == null) {
            return null;
        }
        BigDecimal result = value1;
        value1 = value1.setScale(SCALE, roundingMode);
        value2 = value2.setScale(SCALE, roundingMode);
        switch (actionType) {
            case ADD: {
                result = value1.add(value2);
                break;
            }
            case SUBTRACT: {
                result = value1.subtract(value2);
                break;
            }
            case MULTIPLY: {
                result = value1.multiply(value2);
                break;
            }
            /*calculate value2 percent from value1*/
            case MULTIPLY_PERCENT: {
                result = value1.multiply(value2).divide(new BigDecimal(100), roundingMode);
                break;
            }
            /*calculate the growth in percent value2 relative value1
             * 50, 120 -> 120/50*100-100 -> 140*/
            case PERCENT_GROWTH: {
                result = value2.divide(value1, roundingMode).multiply(BigDecimal.valueOf(100)).add(BigDecimal.valueOf(100).negate());
                break;
            }
            case DEVIDE: {
                result = value1.divide(value2, roundingMode);
                break;
            }
        }
        return normalize(result, roundingMode);
    }

    /**
     * Removes trailing zeros in BigDecimal value
     *
     * @param bigDecimal
     * @return BigDecimal value without trailing zeros
     */
    public static BigDecimal normalize(BigDecimal bigDecimal) {
        return normalize(bigDecimal, ROUND_TYPE);
    }

    public static BigDecimal normalize(BigDecimal bigDecimal, RoundingMode roundingMode) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.setScale(SCALE, roundingMode).stripTrailingZeros().add(BigDecimal.ZERO);
    }

    /**
     * Returns String converted from BigDecimal value
     * with <b>Space</b> as group separator and <b>Comma</b> as decimal separator
     * with trailing zeros if trailingZeros is "true" or without if "false"
     *
     * @param bigDecimal value to convert
     * @return string ov value or "0" if value is null
     * 67553.116000000 => 67 553,116 or 67 553,116000000 (depending on trailingZeros)
     */
    public static String formatSpaceComma(BigDecimal bigDecimal, boolean trailingZeros) {
        DecimalFormat df = new DecimalFormat(trailingZeros ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        df.setRoundingMode(ROUND_TYPE);
        dfs.setGroupingSeparator(' ');
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    /**
     * Returns String converted from BigDecimal value
     * with <b>Space</b> as group separator and <b>Point</b> as decimal separator
     * with trailing zeros if trailingZeros is "true" or without if "false"
     *
     * @param bigDecimal value to convert
     * @return string ov value or "0" if value is null
     * 67553.116000000 => 67 553,116 or 67 553,116000000 (depending on trailingZeros)
     */
    public static String formatSpacePoint(BigDecimal bigDecimal, boolean trailingZeros) {
        DecimalFormat df = new DecimalFormat(trailingZeros ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        df.setRoundingMode(ROUND_TYPE);
        dfs.setGroupingSeparator(' ');
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    /**
     * Returns String converted from BigDecimal value
     * with <b>No</b> group separator and <b>Comma</b> as decimal separator
     * with trailing zeros if trailingZeros is "true" or without if "false"
     *
     * @param bigDecimal value to convert
     * @return string ov value or "0" if value is null
     * 67553.116000000 => 67553,116 or 67553,116000000 (depending on trailingZeros)
     */
    public static String formatNoneComma(BigDecimal bigDecimal, boolean trailingZeros) {
        DecimalFormat df = new DecimalFormat(trailingZeros ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        df.setRoundingMode(ROUND_TYPE);
        df.setGroupingUsed(false);
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    /**
     * Returns String converted from BigDecimal value
     * with <b>No</b> group separator and <b>Point</b> as decimal separator
     * with trailing zeros if trailingZeros is "true" or without if "false"
     *
     * @param bigDecimal    value to convert
     * @param trailingZeros determines if trailing zeros will be added
     * @return string ov value or "0" if value is null
     * 67553.116000000 => 67553.116 or 67553.116000000 (depending on trailingZeros)
     */
    public static String formatNonePoint(BigDecimal bigDecimal, boolean trailingZeros) {
        DecimalFormat df = new DecimalFormat(trailingZeros ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        df.setRoundingMode(ROUND_TYPE);
        df.setGroupingUsed(false);
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    public static String formatNonePoint(BigDecimal bigDecimal, Integer minDecimalPlace) {
        minDecimalPlace = Math.min(minDecimalPlace, SCALE);
        DecimalFormat df = new DecimalFormat("###,##0." +
                new String(new char[minDecimalPlace]).replace("\0", "0") +
                new String(new char[SCALE - minDecimalPlace]).replace("\0", "#"));
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        df.setGroupingUsed(false);
        df.setRoundingMode(ROUND_TYPE);
        df.setGroupingUsed(false);
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    public static String formatNonePoint(String value, Integer minDecimalPlace) {
        BigDecimal formatted = value == null ? BigDecimal.ZERO : new BigDecimal(value);

        return formatNonePoint(formatted, minDecimalPlace);
    }

    public static String formatNonePoint(String value, boolean trailingZeros) {
        BigDecimal formatted = value == null ? BigDecimal.ZERO : new BigDecimal(value);
        return formatNonePoint(formatted, trailingZeros);
    }

    /**
     * Returns String converted from BigDecimal value by formatNonePoint method
     * but result is quoted
     *
     * @param bigDecimal    value to convert
     * @param trailingZeros determines if trailing zeros will be added
     *                      67553.116000000 => "67553.116" or "67553.116000000" (depending on trailingZeros)
     * @return string ov value or "0" if value is null
     */
    public static String formatNonePointQuoted(BigDecimal bigDecimal, boolean trailingZeros) {
        return toQuoted(formatNonePoint(bigDecimal, trailingZeros));
    }

    public static String formatLocale(String value, Locale locale, boolean trailingZeros) {
        BigDecimal decimalValue = StringUtils.isEmpty(value) ? BigDecimal.ZERO : new BigDecimal(value);
        return formatLocale(decimalValue, locale, trailingZeros);
    }

    /**
     * Returns String converted from BigDecimal value
     * with group and decimal separators according to locale
     * with trailing zeros if trailingZeros is "true" or without if "false"
     *
     * @param bigDecimal    value to convert
     * @param locale        to convert format
     * @param trailingZeros determines if trailing zeros will be added
     * @return string of value or "0" if value is null
     * - ru: 67553.116000000 => 67 553,116 or 67 553,116000000 (depending on trailingZeros)
     * - en: 67553.116000000 => 67,553.116 or 67,553.116000000 (depending on trailingZeros)
     */
    public static String formatLocale(BigDecimal bigDecimal, Locale locale, boolean trailingZeros) {
        DecimalFormat df = new DecimalFormat(trailingZeros ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
        df.setDecimalFormatSymbols(dfs);
        df.setGroupingUsed(false);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }


    public static String formatLocale(String value, Locale locale, Integer minDecimalPlace) {
        return formatLocale(new BigDecimal(value), locale, minDecimalPlace);
    }


    /**
     * Returns String converted from BigDecimal value
     * with group and decimal separators according to locale
     * with trailing zeros length of max <b>minDecimalPlace<b/> and max <b>SCALE<b/>
     *
     * @param bigDecimal      value to convert
     * @param locale          to convert format
     * @param minDecimalPlace determines the minimal trailing zeros that will be added
     * @return string of value or "0" if value is null
     * Examples for the minDecimalPlace equals 3 and the locale equals "ru"
     * - ru: 67553 => 67 553,000
     * - ru: 67553.1234 => 67 553,1234
     * - ru: 67553.1234567895 => 67 553,1345679
     */
    public static String formatLocale(BigDecimal bigDecimal, Locale locale, Integer minDecimalPlace) {
        minDecimalPlace = Math.min(minDecimalPlace, SCALE);
        DecimalFormat df = new DecimalFormat("###,##0." +
                new String(new char[minDecimalPlace]).replace("\0", "0") +
                new String(new char[SCALE - minDecimalPlace]).replace("\0", "#"));
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
        df.setDecimalFormatSymbols(dfs);
        df.setGroupingUsed(false);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }


    public static String formatLocaleFixedDecimal(String value, Locale locale, Integer decimalDigits) {
        return formatLocaleFixedDecimal(new BigDecimal(value), locale, decimalDigits);
    }

    public static String formatLocaleFixedDecimal(BigDecimal bigDecimal, Locale locale, Integer decimalDigits) {
        DecimalFormat df = new DecimalFormat("###,##0." +
                new String(new char[decimalDigits]).replace("\0", "0"));
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
        df.setDecimalFormatSymbols(dfs);
        df.setGroupingUsed(false);
        return df.format(bigDecimal == null ? BigDecimal.ZERO : bigDecimal);
    }

    public static String formatLocaleFixedSignificant(String value, Locale locale, Integer minSignificantSymbols) {
        return formatLocaleFixedSignificant(new BigDecimal(value), locale, minSignificantSymbols);
    }

    public static String formatLocaleFixedSignificant(BigDecimal bigDecimal, Locale locale, Integer minSignificantSymbols) {
        int integerPartLength = bigDecimal.toPlainString().split("\\.")[0].length();
        int minDecimalPlace = minSignificantSymbols - integerPartLength;
        return formatLocale(bigDecimal, locale, minDecimalPlace >= 2 ? minDecimalPlace : 2);
    }

    /**
     * Simply adds quotes to string value
     * with trailing zeros
     *
     * @param bigDecimalString strin to quated
     * @return 123456.123 => "123456.123"
     */
    public static String toQuoted(String bigDecimalString) {
        return '"' + bigDecimalString + '"';
    }

    public static BigDecimal parseLocale(String bigDecimal, Locale locale, Integer minDecimalPlace) {
        if (bigDecimal == null || locale == null || minDecimalPlace == null) {
            return null;
        }

        minDecimalPlace = Math.min(minDecimalPlace, SCALE);
        DecimalFormat df = new DecimalFormat("###,##0." +
                new String(new char[minDecimalPlace]).replace("\0", "0") +
                new String(new char[SCALE - minDecimalPlace]).replace("\0", "#"));
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
        df.setDecimalFormatSymbols(dfs);
        df.setParseBigDecimal(true);
        try {
            return (BigDecimal) df.parse(bigDecimal);
        } catch (ParseException e) {
            throw new BigDecimalParseException(e);
        }
    }

    public static BigDecimal parseLocale(String bigDecimal, Locale locale, boolean trailingZeros) {
        if (bigDecimal == null || locale == null) {
            return null;
        }

        DecimalFormat df = new DecimalFormat(trailingZeros ? PATTERN : PATTERN_SHORT);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
        df.setDecimalFormatSymbols(dfs);
        df.setParseBigDecimal(true);
        try {
            return (BigDecimal) df.parse(bigDecimal);
        } catch (ParseException e) {
            throw new BigDecimalParseException(e);
        }
    }

    public static BigDecimal doAction(String value1, String value2, ActionType actionType) {
        BigDecimal decimalValue1 = value1 == null ? null : new BigDecimal(value1);
        BigDecimal decimalValue2 = value2 == null ? null : new BigDecimal(value2);
        return doAction(decimalValue1, decimalValue2, actionType);
    }

    public static boolean isNonNegative(BigDecimal value) {
        return value != null && value.signum() >= 0;
    }

    public static BigDecimal parseNonePoint(String bigDecimal) {
        return parseLocale(bigDecimal, Locale.ENGLISH, false);
    }


}
