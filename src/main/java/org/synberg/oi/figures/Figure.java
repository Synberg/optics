package org.synberg.oi.figures;

import org.synberg.oi.Ray;
import org.synberg.oi.mathobjects.Vector;

public abstract class Figure {
    public abstract Vector getNormal(Vector point);
    public abstract Vector intersection(Ray ray);

    public Vector reflectedRayDirection(Ray ray) {
        Vector intersection = this.intersection(ray);  // Получаем точку пересечения
        if (intersection == null) {
            return null; // Нет пересечения
        }

        Vector n = this.getNormal(intersection);  // Получаем нормаль в точке
        Vector direction = ray.getDirection().normalize();  // Делаем направление единичным

        return direction.subtract(n.multiply(2 * direction.dot(n))); // Формула отражения
    }

    public Vector refractedRayDirection(Ray ray, double n1, double n2) {
        Vector direction = ray.getDirection();
        Vector n = this.getNormal(this.intersection(ray));
        Vector a = direction.multiply(n1);
        Vector b = n.multiply(n1 * direction.dot(n));
        double c = 1 - Math.sqrt((n2 * n2 - n1 * n1) / (Math.pow(direction.dot(n), 2) * n1 * n1) + 1);
        //System.out.println(Math.pow(direction.dot(n), 2));
        return a.subtract(b.multiply(c)).multiply(1 / n2).normalize();
    }

    public Ray[] refractedRays(Ray ray, double n1, double n2) {
        Ray ray1 = new Ray(this.intersection(ray), this.refractedRayDirection(ray, n1, n2));
        Vector intersection = this.intersection(ray1);
        Ray ray2 = null;
        if (intersection != null) {
            ray2 = new Ray(this.intersection(ray1), this.refractedRayDirection(ray1, n2, n1));
        }
        return new Ray[] {ray1, ray2};
    }
}
