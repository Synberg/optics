package org.synberg.oi;

import org.synberg.oi.mathobjects.Vector;

public class Ray {
    private final Vector start;  // Начальная точка
    private final Vector direction;     // Направление (предполагаем, что нормализованное)

    public Ray(Vector start, Vector direction) {
        this.start = start;
        this.direction = direction.normalize();
    }

    public Vector getStart() {
        return start;
    }

    public Vector getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "r0 = " + start + ", e = " + direction;
    }
}
