package org.synberg.oi;

public class PhaseContrastMethod {
    public static void applyPhaseMask(double[][] fftData, int N, int M) {
        int maskSize = 120;
        for (int k = (N - maskSize) / 2; k < (N + maskSize) / 2; k++) {
            for (int m = (M - maskSize) / 2; m < (M + maskSize) / 2; m++) {
                double real = fftData[k][2 * m];
                double imag = fftData[k][2 * m + 1];

                fftData[k][2 * m] = -imag;
                fftData[k][2 * m + 1] = real;
            }
        }
    }
}
