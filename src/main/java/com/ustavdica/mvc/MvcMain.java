package com.ustavdica.mvc;

import com.ustavdica.mvc.controller.SquareController;
import com.ustavdica.mvc.model.SquareModel;
import com.ustavdica.mvc.view.SquareView;

import javax.swing.*;
import java.awt.*;

public class MvcMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MVC Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());

            // Create model and view, and pass them to controller
            SquareModel model = new SquareModel(48);
            SquareView view = new SquareView();
            SquareController controller = new SquareController(model, view);

            // Add view to the frame
            frame.add(view);

            frame.pack();
            frame.setVisible(true);
        });
    }
}
