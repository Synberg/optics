package org.synberg.oi.figures;

import org.synberg.oi.Ray;
import org.synberg.oi.mathobjects.Vector;

public class Surface extends Figure{
    private final Vector n;  // Нормаль к плоскости
    private final Vector point; // Точка на плоскости

    public Surface(Vector point, Vector n) {
        this.point = point;
        this.n = n;
    }

    public Vector getN() {
        return n;
    }

    public Vector getPoint() {
        return point;
    }

    @Override
    public Vector getNormal(Vector point) {
        return n.normalize();
    }

    @Override
    public Vector intersection(Ray ray) {
        Vector start = ray.getStart();
        Vector direction = ray.getDirection();

        double denominator = n.dot(direction);
        if (Math.abs(denominator) < 1e-6) {
            return null; // Луч параллелен плоскости
        }

        double t = n.dot(point.subtract(start)) / denominator;
        if (t < 0) {
            return null; // Точка пересечения за началом луча
        }
        return start.add(direction.multiply(t));
    }

    public Vector getNormal() {
        return n.normalize();
    }
}
