package org.synberg.oi.figures;

import org.synberg.oi.Ray;
import org.synberg.oi.mathobjects.Vector;

public class Lens extends Figure {
    private Ellipsoid leftSurface;
    private Ellipsoid rightSurface;
    private Vector center;
    private double a;
    private double b;
    private double height;

    public Lens(Vector center, double a, double b, double height) {
        // Определяем центры эллипсоидов на основе общей высоты линзы
        //Vector leftCenter = new Vector(center.get(0), center.get(1), center.get(2) - height / 2);
        //Vector rightCenter = new Vector(center.get(0), center.get(1), center.get(2) + height / 2);

        // abc: (a, b, a) для симметричного эллипсоида
        //leftSurface = new Ellipsoid(new Vector(aLeft, b, aLeft), leftCenter);
        //rightSurface = new Ellipsoid(new Vector(aRight, b, aRight), rightCenter);

        this.leftSurface = new Ellipsoid(center, new Vector(1, height / 2, a));
        this.rightSurface = new Ellipsoid(center, new Vector(1, height / 2, b));

        this.center = center;
        this.a = a;
        this.b = b;
        this.height = height;
    }

    @Override
    public Vector getNormal(Vector point) {
        // Определяем, к какой поверхности принадлежит точка, и получаем нормаль
        if (leftSurface.getNormal(point) != null) {
            return leftSurface.getNormal(point);
        } else {
            return rightSurface.getNormal(point);
        }
    }

    @Override
    public Vector intersection(Ray ray) {
        boolean fromLeft = ray.getStart().get(2) < this.center.get(2) - this.leftSurface.getABC().get(2);
        if (fromLeft) {
            return this.leftSurface.intersection(ray);
        }
        return this.rightSurface.intersection(ray);
    }

    public Vector getCenter() {
        return center;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getHeight() {
        return height;
    }

    public Ellipsoid getLeftSurface() {
        return leftSurface;
    }

    public Ellipsoid getRightSurface() {
        return rightSurface;
    }
}
