package me.exrates.exception;

public class UnsupportedChartTypeException extends RuntimeException {

    public UnsupportedChartTypeException(String chartType) {
        super("No such chart type " + chartType);
    }
}