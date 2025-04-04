package org.synberg.oi;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import org.synberg.oi.figures.Lens;
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

        // Определение линзы
        Vector center = new Vector(0, 0, 0);
        Lens lens = new Lens(center, 1, 3, 10);

        // Определение плоскости
        Surface surface = new Surface(new Vector(0, 0, 8.15), new Vector(0, 0, 1));

        // Рисуем точку центра
        drawDot(gc, center, Color. BLACK);

        // Рисуем линзу
        drawLens(gc, lens, Color.RED);

        // Рисуем плоскость
        drawPlane(gc, surface, Color.BROWN);

        Ray[] rays = new Ray[20];
        double y = 0.2;
        // Инициализация параллельного пучка лучей
        for (int i = 0; i < 20; i++) {
            rays[i] = new Ray(new Vector(0, y, -5), new Vector(0, 0, 1));
            y += 0.3;
        }

        for (Ray ray : rays) {
            Vector intersection = lens.intersection(ray);

            if (intersection != null) {
                // Рисуем точку пересечения луча с левой поверхностью линзы
                drawDot(gc, intersection, Color.BLACK);
                // Рисуем исходный луч
                drawRay(gc, ray, intersection, Color.BLUE);

                Ray[] refractedRays = lens.refractedRays(ray, 1, 1.52);
                // Если луч попал на линзу, но не на ее край
                if (refractedRays[1] != null) {
                    // Рисуем преломленный левой поверхностью линзы луч
                    drawRay(gc, refractedRays[0], refractedRays[1].getStart(), Color.PURPLE);
                    // Рисуем точку пересечения преломленного левой поверхность линза луча с правой поверхностью линзы
                    drawDot(gc, refractedRays[1].getStart(), Color.BLACK);

                    // Считаем точку пересечения плоскости и преломленного правой поверхностью линзы луча
                    Vector surfaceIntersection = surface.intersection(refractedRays[1]);
                    // Рисуем точку пересечения плоскости и преломленного правой поверхностью линзы луча
                    drawDot(gc, surfaceIntersection, Color.BLACK);
                    // Рисуем преломленный правой поверхностью линзы луч
                    //drawRay(gc, refractedRays[1], surfaceIntersection, Color.ORANGE);
                    drawRay(gc, refractedRays[1], refractedRays[1].getStart().add(refractedRays[1].getDirection().multiply(20)), Color.ORANGE);
                }
            } else {
                drawRay(gc, ray, ray.getStart().add(ray.getDirection().multiply(20)), Color.BLUE);
            }
        }
    }

    private void drawRay(GraphicsContext gc, Ray ray, Vector end, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeLine(WIDTH / 2 + ray.getStart().get(2) * SCALE,
                HEIGHT / 2 - ray.getStart().get(1) * SCALE,
                WIDTH / 2 + end.get(2) * SCALE,
                HEIGHT / 2 - end.get(1) * SCALE);
    }

    public void drawLens(GraphicsContext gc, Lens lens, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(2);

        Vector center = lens.getCenter();
        Vector abc1 = lens.getLeftSurface().getABC();
        Vector abc2 = lens.getRightSurface().getABC();

        gc.strokeArc(WIDTH / 2 + (center.get(2) - abc1.get(2)) * SCALE,
                HEIGHT / 2 - (center.get(1) + abc1.get(1)) * SCALE,
                2 * abc1.get(2) * SCALE, 2 * abc1.get(1) * SCALE, 90, 180, ArcType.OPEN);

        gc.strokeArc(WIDTH / 2 + (center.get(2) - abc2.get(2)) * SCALE,
                HEIGHT / 2 - (center.get(1) + abc2.get(1)) * SCALE,
                2 * abc2.get(2) * SCALE, 2 * abc2.get(1) * SCALE, 270, 180, ArcType.OPEN);
    }

    public void drawDot(GraphicsContext gc, Vector intersection, Color color) {
        gc.setFill(color);
        gc.fillOval(WIDTH / 2 + intersection.get(2) * SCALE - 3,
                HEIGHT / 2 - intersection.get(1) * SCALE - 3,
                6, 6);
    }

    private void drawPlane(GraphicsContext gc, Surface surface, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(2);

        Vector point = surface.getPoint();
        Vector normal = surface.getNormal();

        // Выбираем первый направляющий вектор (примерно вдоль оси X)
        Vector u = new Vector(-normal.get(1), normal.get(2), 0).normalize().multiply(10);
        // Выбираем второй направляющий вектор (перпендикулярный и normal, и u)
        Vector v = normal.cross(u).normalize().multiply(10);

        // Четыре точки квадрата (можно увеличить масштаб)
        Vector p1 = point.add(u).add(v);
        Vector p2 = point.add(u).subtract(v);
        Vector p3 = point.subtract(u).subtract(v);
        Vector p4 = point.subtract(u).add(v);

        // Рисуем "ромб", который визуально будет отображать плоскость
        gc.strokeLine(WIDTH / 2 + p1.get(2) * SCALE, HEIGHT / 2 - p1.get(1) * SCALE,
                WIDTH / 2 + p2.get(2) * SCALE, HEIGHT / 2 - p2.get(1) * SCALE);
        gc.strokeLine(WIDTH / 2 + p2.get(2) * SCALE, HEIGHT / 2 - p2.get(1) * SCALE,
                WIDTH / 2 + p3.get(2) * SCALE, HEIGHT / 2 - p3.get(1) * SCALE);
        gc.strokeLine(WIDTH / 2 + p3.get(2) * SCALE, HEIGHT / 2 - p3.get(1) * SCALE,
                WIDTH / 2 + p4.get(2) * SCALE, HEIGHT / 2 - p4.get(1) * SCALE);
        gc.strokeLine(WIDTH / 2 + p4.get(2) * SCALE, HEIGHT / 2 - p4.get(1) * SCALE,
                WIDTH / 2 + p1.get(2) * SCALE, HEIGHT / 2 - p1.get(1) * SCALE);
    }

}