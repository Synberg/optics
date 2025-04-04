package org.synberg.oi;

import org.jtransforms.fft.DoubleFFT_2D;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main {
    private static final int N = 150;
    private static final int M = 150;
    private static final double A = 5;
    private static final double B = (N * N) / (4.0 * A * M);

    private static void initializeMatrix(double[][] real, double[][] imag) {
        for (int k = 0; k < N; k++) {
            for (int m = 0; m < M; m++) {
                real[k][m] = 1;
                imag[k][m] = 0;
            }
        }
    }

    private static void generateEllipses(double[][] real, double[][] imag) {
        Random rand = new Random();
        for (int x = 12; x < M; x += 25) {
            for (int y = 12; y < N; y += 25) {
                double phi = rand.nextDouble() * Math.PI;
                double cosphi = Math.cos(phi);
                double sinphi = Math.sin(phi);
                for (int i = 0; i < M; i++) {
                    for (int j = 0; j < N; j++) {
                        double l = i - x;
                        double s = j - y;
                        double val = (Math.pow(l * cosphi + s * sinphi, 2) / (A * A)) +
                                (Math.pow(-l * sinphi + s * cosphi, 2) / (B * B));
                        if (val <= 1) {
                            imag[j][i] += Math.sin(1.1);
                        }
                    }
                }
            }
        }
    }

    private static double[][] applyFFT(double[][] real, double[][] imag) {
        DoubleFFT_2D fft2D = new DoubleFFT_2D(N, M);
        double[][] fftData = new double[N][2 * M];
        for (int k = 0; k < N; k++) {
            for (int m = 0; m < M; m++) {
                fftData[k][2 * m] = real[k][m];
                fftData[k][2 * m + 1] = imag[k][m];
            }
        }
        fft2D.complexForward(fftData);
        return fftData;
    }

    private static double[][] applyInverseFFT(double[][] fftData) {
        DoubleFFT_2D fft2D = new DoubleFFT_2D(N, M);
        fft2D.complexInverse(fftData, true);
        double[][] magnitude = new double[N][M];
        for (int k = 0; k < N; k++) {
            for (int m = 0; m < M; m++) {
                magnitude[k][m] = Math.sqrt(Math.pow(fftData[k][2 * m], 2) + Math.pow(fftData[k][2 * m + 1], 2));
            }
        }
        return magnitude;
    }

    private static void visualizeMatrix(double[][] matrix, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                double maxVal = 0;
                for (double[] row : matrix) {
                    for (double val : row) {
                        maxVal = Math.max(maxVal, val);
                    }
                }
                for (int y = 0; y < N; y++) {
                    for (int x = 0; x < M; x++) {
                        int intensity = (int) (255 * matrix[y][x] / maxVal);
                        g.setColor(new Color(intensity, intensity, intensity));
                        g.fillRect(x * 3, y * 3, 3, 3);
                    }
                }
            }
        });
        frame.setVisible(true);
    }

    private static double[][][] calculateAmplitudeAndPhase(double[][] complexData) {
        int N = complexData.length;
        int M = complexData[0].length / 2;
        double[][] amplitude = new double[N][M];
        double[][] phase = new double[N][M];

        for (int k = 0; k < N; k++) {
            for (int m = 0; m < M; m++) {
                double realPart = complexData[k][2 * m];
                double imagPart = complexData[k][2 * m + 1];
                amplitude[k][m] = Math.log(1 + Math.sqrt(realPart * realPart + imagPart * imagPart));
                phase[k][m] = Math.atan2(imagPart, realPart); // Значения от -π до π
            }
        }

        // Нормализация фазы в [0, 1]
        for (int k = 0; k < N; k++) {
            for (int m = 0; m < M; m++) {
                phase[k][m] = (phase[k][m] + Math.PI) / (2 * Math.PI); // Нормализация в [0, 1]
            }
        }

        return new double[][][]{amplitude, phase};
    }

    public static void main(String[] args) {
        double[][] real = new double[N][M];
        double[][] imag = new double[N][M];

        initializeMatrix(real, imag);
        generateEllipses(real, imag);

        // Создаем матрицу комплексных чисел для исходного изображения
        double[][] initialComplex = new double[N][2 * M];
        for (int k = 0; k < N; k++) {
            for (int m = 0; m < M; m++) {
                initialComplex[k][2 * m] = real[k][m];
                initialComplex[k][2 * m + 1] = imag[k][m];
            }
        }

        // Вычисляем амплитуду и фазу для исходного изображения
        double[][][] initialResults = calculateAmplitudeAndPhase(initialComplex);
        visualizeMatrix(initialResults[0], "Initial Image Amplitude");
        visualizeMatrix(initialResults[1], "Initial Image Phase");

        double[][] fftData_DF = applyFFT(real, imag);

        // Вычисляем амплитуду и фазу для преобразованного изображения
        double[][][] FourierResults = calculateAmplitudeAndPhase(fftData_DF);
        visualizeMatrix(FourierResults[0], "Fourier Image Amplitude");
        visualizeMatrix(FourierResults[1], "Fourier Image Phase");

        DarkFieldMethod.applyDarkFieldMask(fftData_DF, N, M);
        double[][] magnitude_DF = applyInverseFFT(fftData_DF);
        visualizeMatrix(magnitude_DF, "Dark Field");

        double[][] fftData_PC = applyFFT(real, imag);
        PhaseContrastMethod.applyPhaseMask(fftData_PC, N, M);
        double[][] magnitude_PC = applyInverseFFT(fftData_PC);
        visualizeMatrix(magnitude_PC, "Phase Contrast");
    }
}