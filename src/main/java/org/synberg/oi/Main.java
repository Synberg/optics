package org.synberg.oi;

import org.synberg.oi.figures.Ellipsoid;
import org.synberg.oi.figures.Figure;
import org.synberg.oi.figures.Sphere;
import org.synberg.oi.figures.Surface;
import org.synberg.oi.mathobjects.Vector;

public class Main {
    public static void main(String[] args) {
        // Task 1
        System.out.println("Task 1");
        Ray ray1 = new Ray(new Vector(0, 5, 4),
                new Vector(0, -1, -1));

        Figure surface1 = new Surface(new Vector(0, 0, 1),
                new Vector(0, 0, 2));

        Vector intersection1 = surface1.intersection(ray1);
        System.out.println(intersection1 != null ? "Intersection with surface: " + intersection1 : "No intersection");


        Ray ray2 = new Ray(new Vector(0, 1, 0),
                new Vector(1, 0, 0));
        Figure sphere2 = new Sphere(new Vector(12, -1, 0), 5);
        Vector intersection2 = sphere2.intersection(ray2);
        System.out.println(intersection2 != null ? "Intersection with sphere: " + intersection2 : "No intersection");

        Ray ray3 = new Ray(new Vector(5, 0, 5),
                new Vector(-2, 0, -1));
        Figure ellipsoid3 = new Ellipsoid(new Vector(2, 1, 1), new Vector(-3, 0, 0));
        Vector intersection3 = ellipsoid3.intersection(ray3);
        System.out.println(intersection3 != null ? "Intersection with : " + intersection3 : "No intersection");

        // Task 2
        System.out.println("\nTask 2");
        Ray ray4 = new Ray(new Vector(0, 0, 0), new Vector(1, 0, 0.1));
        Figure surface4 = new Surface(new Vector(-1, 0, 0), new Vector(4, 0, 0));
        Figure sphere4 = new Sphere(new Vector(4, 0, 0), 2);
        Figure ellipsoid4 = new Ellipsoid(new Vector(2, 1, 1), new Vector(4, 0, 0));
        Vector direction4_1 = surface4.reflectedRayDirection(ray4);
        Vector direction4_2 = sphere4.reflectedRayDirection(ray4);
        Vector direction4_3 = ellipsoid4.reflectedRayDirection(ray4);
        System.out.println("Direction of reflected ray after intersection with surface: " + direction4_1);
        System.out.println("Direction of reflected ray after intersection with sphere: " + direction4_2);
        System.out.println("Direction of reflected ray after intersection with ellipsoid: " + direction4_3);

        // Task 3
        System.out.println("\nTask 3");
        Ray ray5 = ray4;
        Figure sphere5 = sphere2;
        Vector direction5 = sphere5.refractedRayDirection(ray5, 1, 1.5);
        System.out.println("Direction of refracted ray after intersection with surface: " + direction5);
    }
}