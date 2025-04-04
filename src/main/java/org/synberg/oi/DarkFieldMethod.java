package org.synberg.oi;

public class DarkFieldMethod {
    public static void applyDarkFieldMask(double[][] fftData, int N, int M) {
        int maskSize = 120;
        for (int k = (N - maskSize) / 2; k < (N + maskSize) / 2; k++) {
            for (int m = (M - maskSize) / 2; m < (M + maskSize) / 2; m++) {
                fftData[k][2 * m] = 0;
                fftData[k][2 * m + 1] = 0;
            }
        }
    }
}
