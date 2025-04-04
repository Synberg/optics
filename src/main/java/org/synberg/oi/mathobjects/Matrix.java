package org.synberg.oi.mathobjects;

import java.util.Arrays;

public class Matrix {
    private int rows, cols;
    private double[][] matrix;

    public Matrix(int rows, int cols, double... elements) {
        this.rows = rows;
        this.cols = cols;
        matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = elements[i * rows + j];
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public Vector multiply(Vector v) {
        double[] result = new double[v.getLength()];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i] += matrix[i][j] * v.get(j);
            }
        }
        return new Vector(result);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(matrix);
    }
}
