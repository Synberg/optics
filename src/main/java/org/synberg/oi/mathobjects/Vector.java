package org.synberg.oi.mathobjects;

import java.util.Arrays;

public class Vector {
    private final double[] components;

    public Vector(double... components) {
        this.components = Arrays.copyOf(components, components.length);
    }

    public double[] getComponents() {
        return Arrays.copyOf(components, components.length);
    }

    public Vector add(Vector v) {
        if (components.length != v.components.length) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        double[] result = new double[components.length];
        for (int i = 0; i < components.length; i++) {
            result[i] = components[i] + v.components[i];
        }
        return new Vector(result);
    }

    public Vector subtract(Vector v) {
        return add(v.multiply(-1));
    }

    public Vector multiply(double scalar) {
        double[] result = new double[components.length];
        for (int i = 0; i < components.length; i++) {
            result[i] = components[i] * scalar;
        }
        return new Vector(result);
    }

    public double dot(Vector v) {
        if (components.length != v.components.length) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        double sum = 0;
        for (int i = 0; i < components.length; i++) {
            sum += components[i] * v.components[i];
        }
        return sum;
    }

    public int getLength() {
        return components.length;
    }

    public double get(int i) {
        return components[i];
    }

    public Vector normalize() {
        double length = Math.sqrt(this.dot(this)); // ||v|| = sqrt(x^2 + y^2 + z^2)
        if (length == 0) {
            throw new ArithmeticException("Cannot normalize zero-length vector");
        }
        return this.multiply(1 / length);
    }

    public Vector cross(Vector other) {
        double x = this.get(1) * other.get(2) - this.get(2) * other.get(1);
        double y = this.get(2) * other.get(0) - this.get(0) * other.get(2);
        double z = this.get(0) * other.get(1) - this.get(1) * other.get(0);
        return new Vector(x, y, z);
    }


    @Override
    public String toString() {
        return Arrays.toString(components);
    }
}
