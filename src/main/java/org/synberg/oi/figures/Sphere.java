package org.synberg.oi.figures;

import org.synberg.oi.Ray;
import org.synberg.oi.mathobjects.Vector;

public class Sphere extends Figure {
    private final double radius;
    private final Vector center;

    public Sphere(Vector center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public Vector getCenter() {
        return center;
    }

    @Override
    public Vector getNormal(Vector point) {
        return point.subtract(center).multiply(1 / radius).normalize();
    }

    @Override
    public Vector intersection(Ray ray) {
        Vector start = ray.getStart();
        Vector direction = ray.getDirection();

        Vector a = start.subtract(center); // Вектор от центра сферы до начала луча
        double b = a.dot(direction);

        double discriminant = Math.pow(b, 2) -
                a.dot(a) + radius * radius;

        if (discriminant < 0) {
            return null; // Нет пересечения
        }

        double sqrtD = Math.sqrt(discriminant);
        double t1 = -b - sqrtD;
        double t2 = -b + sqrtD;

        if (t1 >= 0.01) {
            return start.add(direction.multiply(t1)); // Ближайшая точка пересечения
        } else if (t2 >= 0.01) {
            return start.add(direction.multiply(t2)); // Если луч начался внутри сферы
        }

        return null; // Луч направлен от сферы
    }

}
