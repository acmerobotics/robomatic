package com.acmerobotics.robomatic.util;

public class CautiousBinarySearch {

    private double lower, upper, k;

    public CautiousBinarySearch(double lower, double upper) {
        this(lower, upper, 0.5);
    }

    public CautiousBinarySearch(double lower, double upper, double k) {
        this.lower = lower;
        this.upper = upper;
        this.k = k;
    }

    public double update(boolean over) {
        double window = upper - lower;
        double change = window * k;
        if (over) upper -= change;
        else lower += change;
        return (upper + lower) / 2.0;
    }

    public double getLower() {
        return lower;
    }

    public double getUpper() {
        return upper;
    }

}
