package src.ahmedjordypiia.Vue;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import src.ahmedjordypiia.Modele.*;

import java.io.IOException;

public class TreeOfLifeVisual {

    private final int WINDOW_WIDTH = 1000;
    private final int WINDOW_HEIGHT = 700;

    private Tree tree;
    private double mouseAnchorX;
    private double mouseAnchorY;
    private Canvas canvas;
    private Scale scale = new Scale();
    private final double zoomIntensity = 1.1;
    private Affine affine = new Affine();

    public TreeOfLifeVisual() throws IOException {
        tree = new Tree();
        tree.readNodesCSV("src/main/resources/src/ahmedjordypiia/treeoflife_nodes.csv");
        tree.readLinksCSV("src/main/resources/src/ahmedjordypiia/treeoflife_links.csv");
    }

    public Group getTreeGroup() {
        Group root = new Group();
        root.getTransforms().setAll(affine);

        // Ajouter un zoom initial
        root.setScaleX(1.5); // Zoom de 50% en X
        root.setScaleY(1.5); // Zoom de 50% en Y

        root.setOnScroll(event -> {
            double delta = event.getDeltaY();
            double scale = delta > 0 ? zoomIntensity : 1 / zoomIntensity;
            javafx.geometry.Point2D transformedPoint;
            try {
                transformedPoint = affine.inverseTransform(event.getX(), event.getY());
            } catch (NonInvertibleTransformException e) {
                throw new RuntimeException(e);
            }
            zoom(scale, transformedPoint.getX(), transformedPoint.getY());
            event.consume();
        });

        //Drag and drop
        root.setOnMousePressed(event -> {
            // Capture the mouse position when the mouse button is pressed
            mouseAnchorX = event.getX();
            mouseAnchorY = event.getY();
        });

        root.setOnMouseDragged(event -> {
            // Calculate the difference between the current mouse position and the anchor position
            double deltaX = event.getX() - mouseAnchorX;
            double deltaY = event.getY() - mouseAnchorY;

            // Move the tree by the calculated amount
            for (javafx.scene.Node node : root.getChildren()) {
                node.setTranslateX(node.getTranslateX() + deltaX);
                node.setTranslateY(node.getTranslateY() + deltaY);
            }

            // Update the anchor position
            mouseAnchorX = event.getX();
            mouseAnchorY = event.getY();
        });

        try {
            // Charger les données des nœuds et des liens à partir des fichiers CSV
            Tree tree = new Tree();
            tree.readNodesCSV("src/main/resources/src/ahmedjordypiia/treeoflife_nodes.csv");
            tree.readLinksCSV("src/main/resources/src/ahmedjordypiia/treeoflife_links.csv");

            // Dessiner l'arbre de vie
            drawTree(root, tree, tree.getNodes().get(0), WINDOW_WIDTH / 2.55 , WINDOW_HEIGHT / 1.95, -120, 150, 8, 20, 5, 2.0); // 2.0 est l'épaisseur de ligne initiale
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    public void resetTree() {
        // Réinitialiser l'échelle
        getTreeGroup().setScaleX(1.5);
        getTreeGroup().setScaleY(1.5);

        // Réinitialiser la position
        getTreeGroup().setTranslateX(0);
        getTreeGroup().setTranslateY(0);
    }

    private void drawTree(Group group, Tree tree, Node node, double x, double y, double angle, double length, int depth, double textSize, double circleSize, double lineThickness) {
        // Ajuster la taille du texte en fonction du nombre de branches
        double adjustedTextSize;
        if (node.getChildren().size() >= 2) { // Si le nœud a deux enfants ou plus
            adjustedTextSize = textSize * 0.8; // Réduire la taille du texte de 20%
        } else {
            adjustedTextSize = textSize;
        }

        // Dessiner le nom du nœud pour tous les nœuds, pas seulement les feuilles
        drawNodeName(group, node.getName(), x, y, adjustedTextSize, circleSize);


        if (depth == 0) {
            return;
        }

        // Calculer l'angle entre chaque enfant
        double angleStep;
        if (node == tree.getNodes().get(0)) { // Si le nœud actuel est le nœud racine
            angleStep = 300.0 / (node.getChildren().size()); // Répartir les enfants sur 300 degrés
        } else {
            angleStep = 1.4 * Math.min(180.0 / (Math.max(node.getChildren().size(), 1) + 1), 180.0 / node.getChildren().size());
        }

        // Appels récursifs pour les branches plus petites
        for (int i = 0; i < node.getChildren().size(); i++) {
            Node child = node.getChildren().get(i);

            // Ajuster la longueur en fonction de la profondeur du sous-arbre
            double adjustedLength;
            double nextLength;
            if (node == tree.getNodes().get(0)) { // Si le nœud actuel est un enfant direct du nœud racine
                adjustedLength = length;
                nextLength = length;
            } else if (child.getChildren().isEmpty()) { // Si l'enfant est une feuille
                adjustedLength = length;
                adjustedLength *= 0.55;
                nextLength = length;
                nextLength *= 0.4;
            } else { // Si l'enfant a des sous-arbres
                int maxDepth = child.getMaxDepth();
                adjustedLength = length / (1 + maxDepth * 0.2); // Augmenter la réduction de la longueur en fonction de la profondeur du sous-arbre
                adjustedLength *= 0.7; // Ajouter un facteur de réduction supplémentaire pour éviter le chevauchement
                nextLength = length - adjustedLength;
                nextLength *= 0.4; // Ajouter un facteur de réduction supplémentaire pour éviter le chevauchement
            }

            // Calculer l'angle et le point final de la branche pour cet enfant
            double childAngle;
            if (node.getChildren().size() == 1) { // Si le nœud a un seul enfant
                childAngle = angle; // Dessiner l'enfant à la même angle que le parent
            } else {
                childAngle = angle - 90 + angleStep * (i+0.001); // Calculer l'angle par rapport à l'angle de la branche parente
            }
            double xEnd = x + adjustedLength * Math.cos(Math.toRadians(childAngle));
            double yEnd = y + adjustedLength * Math.sin(Math.toRadians(childAngle));


            // Dessiner la branche
            Line line = new Line(x, y, xEnd, yEnd);
            line.setStroke(Color.rgb(255, 255, 255, 0.5)); // Utiliser une couleur blanche avec une opacité de 50%
            line.setStrokeWidth(lineThickness); // Utiliser l'épaisseur de ligne ajustée
            group.getChildren().add(line);

            // Dessiner l'arbre pour cet enfant
            drawTree(group, tree, child, xEnd, yEnd, childAngle, nextLength, depth - 1, textSize * 0.4, circleSize * 0.5, lineThickness * 0.4); // réduire l'épaisseur de la ligne de 20% à chaque niveau
        }
    }

    private void drawNodeName(Group group, String name, double x, double y, double textSize, double circleSize) {
        Circle circle = new Circle(x, y, circleSize, Color.WHITE); // Fond de cercle blanc
        circle.setOpacity(0.5); // Rendre le cercle semi-transparent
        group.getChildren().add(circle);

        Circle blossom = new Circle(x, y, circleSize - 1, Color.RED); // Fleur rouge
        blossom.setOpacity(0.5); // Rendre la fleur semi-transparente
        group.getChildren().add(blossom);

        javafx.scene.text.Text text = new javafx.scene.text.Text(name);
        text.setFill(Color.WHITE);
        text.setFont(new Font(textSize)); // Utiliser la taille du texte ajustée

        // Obtenir la largeur et la hauteur du texte
        double textWidth = text.getBoundsInLocal().getWidth();
        double textHeight = text.getBoundsInLocal().getHeight();

        // Ajuster la position du texte pour le centrer dans le cercle
        text.setX(x - textWidth / 2);
        text.setY(y + textHeight / 4); // Le décalage de la hauteur est divisé par 4 pour mieux centrer le texte verticalement

        group.getChildren().add(text);
    }

    public Tree getTree() {
        return this.tree;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public void zoomIn() {
        scale.setX(scale.getX() * zoomIntensity);
        scale.setY(scale.getY() * zoomIntensity);
    }

    public void zoomOut() {
        scale.setX(scale.getX() / zoomIntensity);
        scale.setY(scale.getY() / zoomIntensity);
    }
    public void zoom(double factor, double x, double y) {
        affine.prependScale(factor, factor, x, y);
    }

    public void setMouseAnchorX(double x) {
        this.mouseAnchorX = x;
    }

    public void setMouseAnchorY(double y) {
        this.mouseAnchorY = y;
    }


    public double getMouseAnchorX() {
        return mouseAnchorX;
    }

    public double getMouseAnchorY() {
        return mouseAnchorY;
    }
}