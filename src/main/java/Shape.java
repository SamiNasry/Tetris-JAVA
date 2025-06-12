/* 
 * Moussaif Fahd
 * Nasry Sami
 * Louaddi Zakaria  
 * AIT LAADIK Soukaina
 */

// Classe représentant une pièce de Tetris et ses opérations (rotation, couleur, etc.)

import java.awt.Color;
import java.util.Random;

public class Shape {

    // Enumération des formes possibles des pièces de Tetris, avec leurs coordonnées et couleurs
    public enum PieceShape {
        NoShape(new int[][]{{0, 0}, {0, 0}, {0, 0}, {0, 0}}, new Color(0, 0, 0)), // Aucune pièce (vide)
        ZShape(new int[][]{{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}, new Color(204, 102, 102)), // Forme Z
        SShape(new int[][]{{0, -1}, {0, 0}, {1, 0}, {1, 1}}, new Color(102, 204, 102)),  // Forme S
        LineShape(new int[][]{{0, -1}, {0, 0}, {0, 1}, {0, 2}}, new Color(102, 102, 204)), // Ligne
        TShape(new int[][]{{-1, 0}, {0, 0}, {1, 0}, {0, 1}}, new Color(204, 204, 102)),   // Forme T
        SquareShape(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}}, new Color(204, 102, 204)), // Carré
        LShape(new int[][]{{-1, 1}, {0, 1}, {0, 0}, {0, -1}}, new Color(102, 204, 204)),   // Forme L
        JShape(new int[][]{{1, 1}, {0, 1}, {0, 0}, {0, -1}}, new Color(204, 170, 102));    // Forme J

        public final int[][] coords; // Coordonnées relatives des 4 blocs de la pièce
        public final Color color;    // Couleur associée à la pièce

        PieceShape(int[][] coords, Color color) {
            this.coords = coords;
            this.color = color;
        }

        // Retourne une forme aléatoire (hors NoShape)
        public static PieceShape getRandomShape() {
            Random r = new Random();
            PieceShape[] values = PieceShape.values();
            return values[r.nextInt(values.length - 1) + 1];
        }
    }

    protected PieceShape pieceShape; // Type de la pièce courante
    protected int[][] coords;        // Coordonnées des 4 blocs de la pièce

    // Constructeur : initialise la pièce comme vide
    public Shape() {
        coords = new int[4][2];
        setPieceShape(PieceShape.NoShape);
    }

    // Définit la forme de la pièce et copie ses coordonnées
    public void setPieceShape(PieceShape shape) {
        this.pieceShape = shape;
        for (int i = 0; i < 4; i++) {
            System.arraycopy(shape.coords[i], 0, this.coords[i], 0, 2);
        }
    }

    // Retourne le type de la pièce
    public PieceShape getPieceShape() {
        return pieceShape;
    }

    // Retourne la couleur de la pièce
    public Color getColor() {
        return pieceShape.color;
    }

    // Retourne la coordonnée X du i-ème bloc de la pièce
    public int getX(int index) {
        return coords[index][0];
    }

    // Retourne la coordonnée Y du i-ème bloc de la pièce
    public int getY(int index) {
        return coords[index][1];
    }

    // Modifie la coordonnée X du i-ème bloc
    public void setX(int index, int x) {
        coords[index][0] = x;
    }

    // Modifie la coordonnée Y du i-ème bloc
    public void setY(int index, int y) {
        coords[index][1] = y;
    }

    // Retourne la valeur Y la plus haute (la plus petite) de la pièce (utile pour l'apparition)
    public int getTopmostRelativeY() {
        int minY = coords[0][1];
        for (int i = 1; i < 4; i++) {
            if (coords[i][1] < minY) minY = coords[i][1];
        }
        return minY;
    }

    // Retourne une nouvelle pièce correspondant à la rotation droite de la pièce courante
    public Shape rotateRight() {
        if (pieceShape == PieceShape.SquareShape) return this; // Le carré ne change pas

        Shape newShape = new Shape();
        newShape.setPieceShape(this.pieceShape);

        for (int i = 0; i < 4; i++) {
            newShape.setX(i, -getY(i));
            newShape.setY(i, getX(i));
        }
        return newShape;
    }

    // Retourne une nouvelle pièce correspondant à la rotation gauche de la pièce courante
    public Shape rotateLeft() {
        if (pieceShape == PieceShape.SquareShape) return this; // Le carré ne change pas

        Shape newShape = new Shape();
        newShape.setPieceShape(this.pieceShape);

        for (int i = 0; i < 4; i++) {
            newShape.setX(i, getY(i));
            newShape.setY(i, -getX(i));
        }
        return newShape;
    }
}

