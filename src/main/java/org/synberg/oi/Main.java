package org.synberg.oi;

import org.synberg.oi.figures.Lens;
import org.synberg.oi.figures.Surface;
import org.synberg.oi.mathobjects.Vector;

public class Main {
    public static double method1(Ray[] rays, Lens lens) {
        int n = rays.length;
        double f = -1;
        double minSCO = Double.MAX_VALUE;

        for (double z = lens.getB() + 0.1; z <= 20; z += 0.05) {
            Surface surface = new Surface(new Vector(0, 0, z), new Vector(0, 0, 1));
            Vector[] intersections = new Vector[n];

            // Пересечение лучей с плоскостью после преломления
            for (int i = 0; i < n; i++) {
                Ray[] refractedRays = lens.refractedRays(rays[i], 1, 1.52);
                intersections[i] = surface.intersection(refractedRays[1]);
            }

            double[] x0y0 = computeX0Y0(intersections, n);

            // Вычисляем СКО для текущего z
            double sumSquares = 0;
            for (Vector point : intersections) {
                double dx = point.get(0) - x0y0[0];
                double dy = point.get(1) - x0y0[1];
                sumSquares += Math.sqrt(dx * dx + dy * dy / n);
            }
            double sco = sumSquares;
            if (sco < minSCO) {
                minSCO = sco;
                f = z;
            }
        }
        return f;
    }

    public static double method2(Ray[] rays, Lens lens, double n1, double n2) {
        int n = rays.length;
        double f = -1;
        double minSCO = Double.MAX_VALUE;

        for (double z = lens.getB() + 0.1; z <= 20; z += 0.05) {
            Surface surface = new Surface(new Vector(0, 0, z), new Vector(0, 0, 1));
            Vector[] intersections = new Vector[n];
            double[] Lij = new double[n];
            double Lmid = 0;
            // Пересечение лучей с плоскостью после преломления
            for (int i = 0; i < n; i++) {
                Ray[] refractedRays = lens.refractedRays(rays[i], 1, 1.52);
                intersections[i] = surface.intersection(refractedRays[1]);
                Lij[i] = getRayLength(rays[i].getStart(), refractedRays[0].getStart()) * n1 +
                        getRayLength(refractedRays[0].getStart(), refractedRays[1].getStart()) * n2 +
                        getRayLength(refractedRays[1].getStart(), intersections[i]) * n1;
                Lmid += Lij[i];
            }
            Lmid /= n;

            double[] x0y0 = computeX0Y0(intersections, n);

            double sco = 0;
            for (int i = 0; i < n; i++) {
                sco += Math.sqrt(Math.pow(intersections[i].get(0) - x0y0[0], 2)
                        + Math.pow(intersections[i].get(1) - x0y0[1], 2)
                        + Math.pow(Lmid - Lij[i], 2)) / Math.sqrt(2 * n);
            }
            if (sco < minSCO) {
                minSCO = sco;
                f = z;
            }
        }
        return f;
    }

    public static double method3(Ray[] rays, Lens lens, double n1, double n2) {
        int n = rays.length;
        double hjmin = -1;
        double minSCO = Double.MAX_VALUE;
        for (double hj = 10; hj <= 30; hj += 0.1) {
            Vector[] r3 = new Vector[n];
            for (int i = 0; i < n; i++) {
                Ray[] refractedRays = lens.refractedRays(rays[i], 1, 1.52);
                double l3 = (hj - getRayLength(rays[i].getStart(), refractedRays[0].getStart()) * n1 -
                        getRayLength(refractedRays[0].getStart(), refractedRays[1].getStart()) * n2) / n1;
                r3[i] = refractedRays[1].getStart().add(refractedRays[1].getDirection().multiply(l3));
            }
            Vector r0 = new Vector(0, 0, 0);
            for (int i = 0; i < n; i++) {
                r0 = r0.add(r3[i]);
            }
            r0 = r0.multiply(1f / n);
            double sco = 0;
            for (int i = 0; i < n; i++) {
                Vector temp = r0.subtract(r3[i]);
                sco += temp.dot(temp);
            }
            sco = Math.sqrt(sco / n);
            if (sco < minSCO) {
                minSCO = sco;
                hjmin = hj;
            }
        }
        Vector r0 = new Vector(0, 0, 0);
        for (int i = 0; i < n; i++) {
            Ray[] refractedRays = lens.refractedRays(rays[i], 1, 1.52);
            double l3 = (hjmin - getRayLength(rays[i].getStart(), refractedRays[0].getStart()) * n1 -
                    getRayLength(refractedRays[0].getStart(), refractedRays[1].getStart()) * n2) / n1;
            Vector r3 = refractedRays[1].getStart().add(refractedRays[1].getDirection().multiply(l3));
            r0 = r0.add(r3);
        }
        r0 = r0.multiply(1f / n);
        return r0.get(2);
    }

    public static double getRayLength(Vector v1, Vector v2) {
        return Math.sqrt(Math.pow((v1.get(0) - v2.get(0)), 2)
                + Math.pow((v1.get(1) - v2.get(1)), 2)
                + Math.pow((v1.get(2) - v2.get(2)), 2));
    }

    public static double[] computeX0Y0(Vector[] intersections, int n) {
        // Вычисляем центр (x0, y0) пятна
        double sumX = 0, sumY = 0;
        for (Vector point : intersections) {
            sumX += point.get(0);
            sumY += point.get(1);
        }
        return new double[] {sumX / n, sumY / n};
    }

    public static double[] getOptimalParameters(double f, double from, double to, Ray[] rays) {
        double minDifference = Double.MAX_VALUE;
        double r1 = -1, r2 = -1;
        for (double i = from; i < to; i += 0.1) {
            for (double j = from; j < to; j += 0.1) {
                double curF = method1(rays, new Lens(new Vector(0, 0, 0), i, j, 10));
                double curDifference = Math.abs(curF - f);
                if (curDifference < minDifference) {
                    minDifference = curDifference;
                    r1 = i;
                    r2 = j;
                }
            }
        }
        return new double[] {r1, r2};
    }

    public static void main(String[] args) {
        UserInterface.main(args);
    }
}
