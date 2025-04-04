package org.synberg.oi;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.synberg.oi.figures.Ellipsoid;
import org.synberg.oi.figures.Figure;
import org.synberg.oi.figures.Sphere;
import org.synberg.oi.figures.Surface;
import org.synberg.oi.mathobjects.Vector;

public class RayTracingApp extends Application {
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 900;
    private static final int SCALE = 50; // 1 единица = 50 пикселей

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawScene(gc);

        stage.setTitle("Ray Tracing Visualization");
        stage.setScene(new Scene(new javafx.scene.layout.StackPane(canvas)));
        stage.show();
    }

    private void drawScene(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        // Оси координат
        gc.strokeLine(0, HEIGHT / 2, WIDTH, HEIGHT / 2); // Ось X
        gc.strokeLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT); // Ось Y

        int variant = 2;
        Vector center = null;
        Figure figure = null;
        switch(variant) {
            case 0:
                center = new Vector(5, 2, 0);
                Vector abc = new Vector(3, 2, 1);
                figure = new Ellipsoid(abc, center);
                drawEllipsoid(gc, center, abc);
                break;
            case 1:
                center = new Vector(3, 2, 0);
                double sphereRadius = 2;
                figure = new Sphere(center, sphereRadius);
                drawSphere(gc, center, sphereRadius);
                break;
            case 2:
                center = new Vector(4, 0, 0);
                figure = new Surface(new Vector(-1, -0.3, 0), center);
                drawPlane(gc, center, ((Surface) figure).getNormal());
                break;
        }

        // Рисуем точку центра
        drawDot(gc, center, Color.AQUAMARINE);

        // Исходный луч
        Ray ray = new Ray(new Vector(0, 1, 0), new Vector(1, 0.2, 0));
        Vector intersection = figure.intersection(ray);

        int bounces = 5;
        if (intersection != null) {
            drawRay(gc, ray, Color.BLUE, intersection);

            drawDot(gc, intersection, Color.RED);

            // Отраженный луч
            Vector reflectedIntersection = intersection;
            Ray currentRay = ray;
            int currentBounces = 0;

            // Определяем нормаль в точке пересечения
            Vector normal = figure.getNormal(intersection);

            // Проверяем, входит луч или выходит
            boolean entering = normal.dot(ray.getDirection()) < 0; // Если скалярное произведение < 0, то луч заходит внутрь
            if (entering) {
                Ray reflectedRay = new Ray(intersection, figure.reflectedRayDirection(ray));
                drawRay(gc, reflectedRay, Color.RED, reflectedRay.getStart().add(reflectedRay.getDirection().multiply(20)));
            } else {
                while (reflectedIntersection != null && currentBounces < bounces) {
                    Ray reflectedRay = new Ray(reflectedIntersection, figure.reflectedRayDirection(currentRay));
                    currentRay = reflectedRay;
                    reflectedIntersection = figure.intersection(reflectedRay);
                    if (reflectedIntersection == null) {
                        drawRay(gc, reflectedRay, Color.RED, reflectedRay.getStart().add(reflectedRay.getDirection().multiply(20)));
                    } else {
                        drawRay(gc, reflectedRay, Color.RED, reflectedIntersection);
                    }
                    currentBounces++;
                }
            }

            // Преломленный луч
            Ray[] refractedRays = figure.refractedRays(ray, 1, 1.52);
            if (refractedRays[1] != null) {
                drawRay(gc, refractedRays[0], Color.PURPLE, refractedRays[1].getStart());
            } else {
                drawRay(gc, refractedRays[0], Color.PURPLE, refractedRays[0].getStart().add(refractedRays[0].getDirection().multiply(20)));
            }

            if (refractedRays[1] != null) {
                drawRay(gc, refractedRays[1], Color.ORANGE, refractedRays[1].getStart().add(refractedRays[1].getDirection().multiply(20)));
            }
        } else {
            drawRay(gc, ray, Color.BLUE, ray.getStart().add(ray.getDirection().multiply(20)));
        }
    }

    private void drawRay(GraphicsContext gc, Ray ray, Color color, Vector end) {
        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeLine(WIDTH / 2 + ray.getStart().get(0) * SCALE,
                HEIGHT / 2 - ray.getStart().get(1) * SCALE,
                WIDTH / 2 + end.get(0) * SCALE,
                HEIGHT / 2 - end.get(1) * SCALE);
    }

    private void drawEllipsoid(GraphicsContext gc, Vector center, Vector abc) {
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(2);
        gc.strokeOval(WIDTH / 2 + (center.get(0) - abc.get(0)) * SCALE,
                HEIGHT / 2 - (center.get(1) + abc.get(1)) * SCALE,
                2 * abc.get(0) * SCALE,
                2 * abc.get(1) * SCALE);
    }

    public void drawDot(GraphicsContext gc, Vector intersection, Color color) {
        gc.setFill(color);
        gc.fillOval(WIDTH / 2 + intersection.get(0) * SCALE - 3,
                HEIGHT / 2 - intersection.get(1) * SCALE - 3,
                6, 6);
    }

    private void drawSphere(GraphicsContext gc, Vector center, double radius) {
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(2);
        gc.strokeOval(WIDTH / 2 + (center.get(0) - radius) * SCALE,
                HEIGHT / 2 - (center.get(1) + radius) * SCALE,
                2 * radius * SCALE,
                2 * radius * SCALE);
    }

    private void drawPlane(GraphicsContext gc, Vector point, Vector normal) {
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(2);

        // Выбираем первый направляющий вектор (примерно вдоль оси X)
        Vector u = new Vector(-normal.get(1), normal.get(0), 0).normalize().multiply(10);
        // Выбираем второй направляющий вектор (перпендикулярный и normal, и u)
        Vector v = normal.cross(u).normalize().multiply(10);

        // Четыре точки квадрата (можно увеличить масштаб)
        Vector p1 = point.add(u).add(v);
        Vector p2 = point.add(u).subtract(v);
        Vector p3 = point.subtract(u).subtract(v);
        Vector p4 = point.subtract(u).add(v);

        // Рисуем "ромб", который визуально будет отображать плоскость
        gc.strokeLine(WIDTH / 2 + p1.get(0) * SCALE, HEIGHT / 2 - p1.get(1) * SCALE,
                WIDTH / 2 + p2.get(0) * SCALE, HEIGHT / 2 - p2.get(1) * SCALE);
        gc.strokeLine(WIDTH / 2 + p2.get(0) * SCALE, HEIGHT / 2 - p2.get(1) * SCALE,
                WIDTH / 2 + p3.get(0) * SCALE, HEIGHT / 2 - p3.get(1) * SCALE);
        gc.strokeLine(WIDTH / 2 + p3.get(0) * SCALE, HEIGHT / 2 - p3.get(1) * SCALE,
                WIDTH / 2 + p4.get(0) * SCALE, HEIGHT / 2 - p4.get(1) * SCALE);
        gc.strokeLine(WIDTH / 2 + p4.get(0) * SCALE, HEIGHT / 2 - p4.get(1) * SCALE,
                WIDTH / 2 + p1.get(0) * SCALE, HEIGHT / 2 - p1.get(1) * SCALE);
    }

}