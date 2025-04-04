package org.synberg.oi.figures;

import org.synberg.oi.mathobjects.Matrix;
import org.synberg.oi.Ray;
import org.synberg.oi.mathobjects.Vector;

public class Ellipsoid extends Figure {
    private Vector abc;
    private Vector center;

    public Ellipsoid(Vector center, Vector abc) {
        this.center = center;
        this.abc = abc;
    }

    public Vector getABC() {
        return abc;
    }

    public Vector getCenter() {
        return center;
    }

    @Override
    public Vector getNormal(Vector point) {
        return new Vector((point.get(0) - center.get(0)) / abc.get(0),
                (point.get(1) - center.get(1)) / abc.get(1),
                (point.get(2) - center.get(2)) / abc.get(2));
    }

    @Override
    public Vector intersection(Ray ray) {
        Vector start = ray.getStart();
        Vector direction = ray.getDirection();

        Vector d = start.subtract(center);
        Matrix Mabc = new Matrix(3, 3,
                abc.get(1) * abc.get(2), 0, 0,
                0, abc.get(0) * abc.get(2), 0,
                0, 0, abc.get(0) * abc.get(1));

        Vector M_abc_e_T = Mabc.multiply(direction);
        Vector M_abc_d_T = Mabc.multiply(d);

        double A = M_abc_e_T.dot(M_abc_e_T);
        double B = 2 * (M_abc_e_T.dot(M_abc_d_T));
        double C = M_abc_d_T.dot(M_abc_d_T) - Math.pow(abc.get(0) * abc.get(1) * abc.get(2), 2);

        double discriminant = B * B - 4 * A * C;

        if (discriminant < 0) {
            return null;
        }

        double sqrtD = Math.sqrt(discriminant);
        double t1 = (-B + sqrtD) / (2 * A);
        double t2 = (-B - sqrtD) / (2 * A);

        // Выбираем ближайший положительный t
        double t = Double.POSITIVE_INFINITY;
        if (t1 >= 0.01) t = Math.min(t, t1);
        if (t2 >= 0.01) t = Math.min(t, t2);

        if (t == Double.POSITIVE_INFINITY) {
            return null; // Оба t отрицательные, пересечения нет
        }

        return start.add(direction.multiply(t));
    }
}
