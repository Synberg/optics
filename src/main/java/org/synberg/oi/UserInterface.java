package org.synberg.oi;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.synberg.oi.figures.Lens;
import org.synberg.oi.mathobjects.Vector;

import java.util.Arrays;

public class UserInterface extends Application {

    private TextField aField;
    private TextField bField;
    private TextField fField;
    private Label resultFocusLabel;
    private Label resultParametersLabel;

    private Ray[] rays;
    private final double n1 = 1;
    private final double n2 = 1.5;
    private final int n = 10;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        rays = new Ray[n];
        double y = 0.25;

        for (int i = 0; i < n; i++) {
            rays[i] = new Ray(new Vector(0, y, -5), new Vector(0, 0, 1));
            y += 0.25;
        }

        primaryStage.setTitle("Lens Focus Calculator");

        aField = new TextField();
        aField.setPromptText("Enter parameter a");

        bField = new TextField();
        bField.setPromptText("Enter parameter b");

        Button calculateFocusButton = new Button("Calculate Focus");
        calculateFocusButton.setOnAction(e -> calculateFocus());

        resultFocusLabel = new Label();

        fField = new TextField();
        fField.setPromptText("Enter parameter f");

        Button calculateParametersButton = new Button("Calculate Optimal Parameters");
        calculateParametersButton.setOnAction(e -> calculateParameters());

        resultParametersLabel = new Label();

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Parameter a:"), aField,
                new Label("Parameter b:"), bField,
                calculateFocusButton,
                resultFocusLabel,
                fField,
                calculateParametersButton,
                resultParametersLabel
        );

        Scene scene = new Scene(layout, 400, 370);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void calculateFocus() {
        try {
            double a = Double.parseDouble(aField.getText());
            double b = Double.parseDouble(bField.getText());

            Lens lens = new Lens(new Vector(0, 0, 0), a, b, 10);

            double result1 = Main.method1(rays, lens);
            double result2 = Main.method2(rays, lens, n1, n2);
            double result3 = Main.method3(rays, lens, n1, n2);

            resultFocusLabel.setText(String.format(
                    "Method 1 Focus: %.3f\nMethod 2 Focus: %.3f\nMethod 3 Focus: %.3f",
                    result1, result2, result3
            ));
        } catch (NumberFormatException e) {
            resultFocusLabel.setText("Invalid input. Please enter numeric values for a and b.");
        } catch (Exception e) {
            resultFocusLabel.setText("Error in calculation: " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void calculateParameters() {
        try {
            double f = Double.parseDouble(fField.getText());
            double[] result = Main.getOptimalParameters(f, 0.5, 5, rays);
            resultParametersLabel.setText(String.format("a = %.1f\nb = %.1f", result[0], result[1]));
        } catch (NumberFormatException e) {
            resultFocusLabel.setText("Invalid input. Please enter numeric values for a and b.");
        } catch (Exception e) {
            resultFocusLabel.setText("Error in calculation: " + Arrays.toString(e.getStackTrace()));
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
