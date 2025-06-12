/* 
 * Moussaif Fahd
 * Nasry Sami
 * Louaddi Zakaria  
 * AIT LAADIK Soukaina
 */

// Classe principale de la fenêtre du jeu Tetris et de l'interface graphique

import javax.swing.*;
import java.awt.*;

public class Tetris extends JFrame {

    // Plateau de jeu principal
    private Board board;
    // Labels pour l'affichage du score, des lignes, du niveau et du meilleur score
    private JLabel scoreLabel;
    private JLabel linesLabel;
    private JLabel levelLabel;
    private JLabel highScoreLabel;
    // Panneau pour afficher la prochaine pièce
    private NextPanel nextPanel;
    // Gestionnaire du meilleur score (base de données)
    private HighScoreManager highScoreManager;
    // Indique si le dernier score était un nouveau record
    private boolean lastGameWasHighScore = false;
    // Bouton pour réinitialiser le meilleur score
    private JButton resetHighScoreButton;

    // Constantes pour la taille du plateau
    public static final int BOARD_WIDTH_IN_CELLS = 10;
    public static final int BOARD_HEIGHT_IN_CELLS_VISIBLE = 20;

    // Constructeur : initialise la fenêtre et l'UI
    public Tetris() {
        highScoreManager = new HighScoreManager();
        initUI();
    }

    // Initialise l'interface graphique et tous les panneaux
    private void initUI() {
        // Panneau principal avec disposition en grille
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.DARK_GRAY);

        // Création et configuration du plateau de jeu
        board = new Board(this);
        board.setBackground(Color.BLACK);
        board.setMinimumSize(new Dimension(400, 800));
        board.setPreferredSize(new Dimension(600, 1200));

        // Panneau central pour les instructions
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        instructionsPanel.setBackground(new Color(25, 25, 25));
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));
        instructionsPanel.setMinimumSize(new Dimension(250, 200));
        instructionsPanel.setPreferredSize(new Dimension(300, 400));
        instructionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Comment jouer");
        title.setForeground(Color.ORANGE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea instructions = new JTextArea(
            """
            Appuyez sur 'S' pour démarrer ou recommencer

            Contrôles :
            - Flèche gauche/droite ou A/D : Déplacer
            - Flèche bas ou S : Descente rapide
            - Flèche haut ou W : Rotation droite
            - Z : Rotation gauche
            - Espace : Chute instantanée
            - P : Pause/Reprendre

            Faites des lignes pour marquer des points !
            """
        );
        instructions.setEditable(false);
        instructions.setFocusable(false);
        instructions.setOpaque(false);
        instructions.setForeground(Color.WHITE);
        instructions.setFont(new Font("Arial", Font.PLAIN, 16));
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setAlignmentY(Component.CENTER_ALIGNMENT);
        instructions.setHighlighter(null);
        instructions.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        instructions.setBorder(null);

        JPanel instructionsTextPanel = new JPanel();
        instructionsTextPanel.setLayout(new BoxLayout(instructionsTextPanel, BoxLayout.X_AXIS));
        instructionsTextPanel.setOpaque(false);
        instructionsTextPanel.add(Box.createHorizontalGlue());
        instructionsTextPanel.add(instructions);
        instructionsTextPanel.add(Box.createHorizontalGlue());

        instructionsPanel.add(title);
        instructionsPanel.add(Box.createVerticalStrut(20));
        instructionsPanel.add(instructionsTextPanel);
        instructionsPanel.add(Box.createVerticalGlue());

        // Panneau latéral pour le score, le niveau, la prochaine pièce, etc.
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(30, 30, 30));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        sidePanel.setMinimumSize(new Dimension(180, 200));
        sidePanel.setPreferredSize(new Dimension(200, 400));

        // Labels pour le score, les lignes et le niveau
        scoreLabel = new JLabel("Score : 0");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 22));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        linesLabel = new JLabel("Lignes : 0");
        linesLabel.setForeground(Color.WHITE);
        linesLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        linesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        levelLabel = new JLabel("Niveau : 1");
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidePanel.add(scoreLabel);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(linesLabel);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(levelLabel);
        sidePanel.add(Box.createVerticalStrut(40));

        // Panneau pour afficher la prochaine pièce
        nextPanel = new NextPanel();
        nextPanel.setPreferredSize(new Dimension(140, 140));
        nextPanel.setMinimumSize(new Dimension(100, 100));
        nextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextPanel.setBackground(new Color(40, 40, 40));
        nextPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1), "Prochaine", 0, 0, new Font("Arial", Font.BOLD, 14), Color.LIGHT_GRAY
        ));
        sidePanel.add(nextPanel);

        // Affichage du meilleur score
        highScoreLabel = new JLabel("Meilleur score : " + highScoreManager.getHighScore());
        highScoreLabel.setForeground(Color.YELLOW);
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(Box.createVerticalStrut(20));
        sidePanel.add(highScoreLabel);

        // Bouton pour réinitialiser le meilleur score
        resetHighScoreButton = new JButton("Réinitialiser le meilleur score");
        resetHighScoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetHighScoreButton.setFocusable(false);
        resetHighScoreButton.addActionListener(e -> {
            highScoreManager.resetHighScore();
            highScoreLabel.setText("Meilleur score : 0");
        });
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(resetHighScoreButton);

        sidePanel.add(Box.createVerticalGlue());

        // Panneau pour afficher le logo avec fond blanc
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setMaximumSize(new Dimension(200, 140));
        logoPanel.setPreferredSize(new Dimension(200, 140));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.setOpaque(true);

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            java.net.URL logoUrl = getClass().getClassLoader().getResource("logo-ensa-berrechid.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                Image img = logoIcon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
            } else {
                logoLabel.setText("Logo non trouvé");
                logoLabel.setForeground(Color.RED);
            }
        } catch (Exception ex) {
            logoLabel.setText("Erreur chargement logo");
            logoLabel.setForeground(Color.RED);
        }
        logoPanel.add(logoLabel);
        sidePanel.add(logoPanel);

        // Label du projet en bas du panneau latéral
        JLabel projectLabel = new JLabel("PROJET POUR MODULE JAVA - ENSAB");
        projectLabel.setForeground(new Color(255, 140, 0));
        projectLabel.setFont(new Font("Arial", Font.BOLD, 20));
        projectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(projectLabel);

        // Placement des panneaux dans la fenêtre principale
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(board, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.25;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(instructionsPanel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.25;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(sidePanel, gbc);

        setContentPane(mainPanel);

        setTitle("Tetris Simple");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setResizable(true);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                board.updateCellSize();
                board.repaint();
                nextPanel.repaint();
            }
        });
    }

    // Met à jour l'affichage du score, des lignes et du niveau
    public void updateScoreAndLines(int score, int lines, int level) {
        scoreLabel.setText("Score : " + score);
        linesLabel.setText("Lignes : " + lines);
        levelLabel.setText("Niveau : " + level);
    }

    // Met à jour le meilleur score et indique si un nouveau record a été atteint
    public void updateHighScore(int score) {
        int currentHigh = highScoreManager.getHighScore();
        if (score > currentHigh) {
            highScoreManager.saveScore(score);
            highScoreLabel.setText("Meilleur score : " + score);
            lastGameWasHighScore = true;
        } else {
            highScoreLabel.setText("Meilleur score : " + currentHigh);
            lastGameWasHighScore = false;
        }
    }

    // Retourne le meilleur score précédent
    public int getPreviousHighScore() {
        return highScoreManager.getHighScore();
    }

    // Indique si la dernière partie était un nouveau record
    public boolean wasLastGameHighScore() {
        return lastGameWasHighScore;
    }

    // Retourne le panneau de la prochaine pièce
    public NextPanel getNextPanel() {
        return nextPanel;
    }

    // (Non utilisé, mais requis par Board)
    public int getCellSize() {
        return 0;
    }

    // Point d'entrée du programme
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Tetris game = new Tetris();
            game.setVisible(true);
        });
    }

    // Panneau pour afficher la prochaine pièce à jouer
    public static class NextPanel extends JPanel {
        private Shape nextShape;

        public void setNextShape(Shape shape) {
            this.nextShape = shape;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (nextShape == null || nextShape.getPieceShape() == Shape.PieceShape.NoShape) return;

            int cell = Math.min(getWidth(), getHeight()) / 5;
            int offsetX = getWidth() / 2;
            int offsetY = getHeight() / 2;

            int minX = 0, maxX = 0, minY = 0, maxY = 0;
            for (int i = 0; i < 4; i++) {
                minX = Math.min(minX, nextShape.getX(i));
                maxX = Math.max(maxX, nextShape.getX(i));
                minY = Math.min(minY, nextShape.getY(i));
                maxY = Math.max(maxY, nextShape.getY(i));
            }
            int shapeWidth = (maxX - minX + 1) * cell;
            int shapeHeight = (maxY - minY + 1) * cell;
            int baseX = offsetX - shapeWidth / 2 - minX * cell;
            int baseY = offsetY - shapeHeight / 2 - minY * cell;

            for (int i = 0; i < 4; i++) {
                int x = nextShape.getX(i);
                int y = nextShape.getY(i);
                drawSquare(g, baseX + x * cell, baseY + y * cell, cell, nextShape.getColor());
            }
        }

        // Dessine un carré d'une pièce dans le panneau "Prochaine"
        private void drawSquare(Graphics g, int x, int y, int size, Color color) {
            g.setColor(color);
            g.fillRect(x + 1, y + 1, size - 2, size - 2);
            g.setColor(color.brighter());
            g.drawLine(x, y + size - 1, x, y);
            g.drawLine(x, y, x + size - 1, y);
            g.setColor(color.darker());
            g.drawLine(x + 1, y + size - 1, x + size - 1, y + size - 1);
            g.drawLine(x + size - 1, y + size - 1, x + size - 1, y + 1);
        }
    }
}
