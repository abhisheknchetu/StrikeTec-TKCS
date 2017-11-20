package com.striketec.mobile.util.sensormanager.mmaGlove;

import java.util.Arrays;


public class Integrator {

    /**
     * @param values     array of measured values.
     * @param stepLength step between two adjacent measurements.
     * @return Integral for the given array of equally distributed measured values.
     * @throws IllegalArgumentException if the given values array has less than 2 values.
     */
    public double computeIntegral(short[] values, double stepLength)
            throws IllegalArgumentException {
        double result;
        int n = values.length;

        if (n < 2) {
            throw new IllegalArgumentException("Values array is expected to have at least 2 values.");
        }
        if (n == 2) {
            result = trapezoidalRule(values[0], values[1], stepLength);
        } else if (n % 2 == 1) {
            // Number of values is odd - use Composite Simpson's Rule.
            result = compositeSimpsonsRule(values, stepLength);
        } else {
            // Number of values is even - use Trapezoidal Rule for the first 2 values,
            // then use Composite Simpson's Rule for the last n-1 values.
            short[] valuesExceptFirst = Arrays.copyOfRange(values, 1, n);

            result = trapezoidalRule(values[0], values[1], stepLength);
            result += compositeSimpsonsRule(valuesExceptFirst, stepLength);
        }

        return result;
    }

    /**
     * Implements <a href="http://en.wikipedia.org/wiki/Simpson's_rule#Composite_Simpson.27s_rule">Composite Simpson's Rule</a>.
     * This integration method requires odd number of values.
     *
     * @param values     array of measured values.
     * @param stepLength step between two adjacent values.
     * @return Numerical integral of the given data series.
     * @throws IllegalArgumentException if the number of values is even, or if number of values < 3.
     */
    private double compositeSimpsonsRule(short[] values, double stepLength)
            throws IllegalArgumentException {
        int n = values.length;
        if (n % 2 == 0) {
            throw new IllegalArgumentException("Composite Simpson's Rule can be invoked only on odd number of values.");
        }
        if (n < 3) {
            throw new IllegalArgumentException("Composite Simpson's Rule requires at least 3 values.");
        }

        // The First and the Last values are added without a coefficient.
        int sum = values[0] + values[n - 1];

        // iterate over odd indexes
        for (int i = 1; i < n - 1; i += 2) {
            sum += 4 * values[i];
        }

        // iterate over even indexes
        for (int i = 2; i < n - 1; i += 2) {
            sum += 2 * values[i];
        }

        return stepLength * sum / 3;
    }

    /**
     * Implements <a href = "http://en.wikipedia.org/wiki/Trapezoidal_rule">Trapezoidal Rule</a>.
     *
     * @param a          measured value.
     * @param b          adjacent measured value.
     * @param stepLength step between two adjacent measurements.
     * @return Approximation to the definite integral between a and b.
     */
    private double trapezoidalRule(short a, short b, double stepLength) {
        return stepLength * (a + b) / 2;
    }
}
