/* 
 * Moussaif Fahd
 * Nasry Sami
 * Louaddi Zakaria  
 * AIT LAADIK Soukaina
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Board extends JPanel implements ActionListener {

    // Largeur et hauteur du plateau en cases visibles
    private static final int BOARD_WIDTH_CELLS = 10;
    private static final int BOARD_HEIGHT_CELLS_VISIBLE = 20;
    // Nombre de lignes cachées en haut pour la génération des pièces
    private static final int HIDDEN_ROWS_ABOVE = 2; 
    private static final int TOTAL_BOARD_HEIGHT_CELLS = BOARD_HEIGHT_CELLS_VISIBLE + HIDDEN_ROWS_ABOVE;

    private int cellSize;

    private Timer timer;
    private boolean isFallingFinished = false; // Vrai si la pièce ne peut plus descendre
    private boolean isStarted = false;         // Vrai si la partie est en cours
    private boolean isPaused = false;          // Vrai si la partie est en pause

    private int numLinesRemoved = 0; // Nombre total de lignes supprimées
    private int score = 0;           // Score du joueur

    private int curPieceX = 0; // Position X de la pièce courante
    private int curPieceY = 0; // Position Y de la pièce courante

    private Shape currentPiece; // Pièce en train de tomber
    private Shape nextPiece;    // Prochaine pièce à afficher

    private Color[][] grid;     // Grille du plateau, chaque case contient la couleur de la pièce posée ou null

    private Tetris parentFrame; // Référence à la fenêtre principale pour mettre à jour l'UI

    // Paramètres pour la vitesse du jeu
    private static final int INITIAL_TIMER_DELAY = 400; // Délai initial en ms
    private static final int MIN_TIMER_DELAY = 80;      // Délai minimum (vitesse max)
    private static final int SPEEDUP_LINES_STEP = 10;   // Nombre de lignes à effacer pour accélérer
    private static final int SPEEDUP_AMOUNT = 40;       // Réduction du délai à chaque palier

    public Board(Tetris parent) {
        this.parentFrame = parent;
        initBoard();
    }

    // Initialisation du plateau et des variables de jeu
    private void initBoard() {
        setFocusable(true);
        addKeyListener(new TAdapter());
        setBackground(Color.BLACK); 

        grid = new Color[BOARD_WIDTH_CELLS][TOTAL_BOARD_HEIGHT_CELLS];
        clearBoardGrid();

        currentPiece = new Shape();
        nextPiece = new Shape();
        nextPiece.setPieceShape(Shape.PieceShape.getRandomShape()); 

        timer = new Timer(INITIAL_TIMER_DELAY, this); 
    }

    // Démarre une nouvelle partie
    public void start() {
        if (isPaused) return;

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        score = 0;
        clearBoardGrid();
        spawnNewPiece();
        timer.start();
        updateStatusBar();
    }

    // Met le jeu en pause ou le reprend
    private void pause() {
        if (!isStarted || isFallingFinished) return; 

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
        } else {
            timer.start();
            updateStatusBar(); 
        }
        repaint(); 
    }

    // Met à jour l'affichage du score, des lignes et du niveau dans la fenêtre principale
    private void updateStatusBar() {
        int level = (numLinesRemoved / SPEEDUP_LINES_STEP) + 1;
        parentFrame.updateScoreAndLines(score, numLinesRemoved, level);
    }

    // Vide la grille du plateau (toutes les cases à null)
    private void clearBoardGrid() {
        for (int i = 0; i < BOARD_WIDTH_CELLS; i++) {
            for (int j = 0; j < TOTAL_BOARD_HEIGHT_CELLS; j++) {
                grid[i][j] = null;
            }
        }
    }

    // Fait apparaître une nouvelle pièce en haut du plateau
    private void spawnNewPiece() {
        currentPiece.setPieceShape(nextPiece.getPieceShape());
        nextPiece.setPieceShape(Shape.PieceShape.getRandomShape());

        curPieceX = BOARD_WIDTH_CELLS / 2;
        curPieceY = -currentPiece.getTopmostRelativeY();

        // Affiche la prochaine pièce dans le panneau latéral
        parentFrame.getNextPanel().setNextShape(nextPiece);

        // Si la nouvelle pièce ne peut pas être placée, la partie est terminée
        if (!tryMove(currentPiece, curPieceX, curPieceY)) {
            currentPiece.setPieceShape(Shape.PieceShape.NoShape);
            isStarted = false;
            isFallingFinished = true;
            timer.stop();
            repaint(); // Redessine pour afficher "Game Over"
            parentFrame.updateHighScore(score);
        } else {
            isFallingFinished = false;
        }
        updateStatusBar();
    }

    // Tente de déplacer la pièce à la position (newX, newY)
    private boolean tryMove(Shape pieceToTry, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int boardX = newX + pieceToTry.getX(i);
            int boardY = newY + pieceToTry.getY(i);

            // Vérifie les limites du plateau
            if (boardX < 0 || boardX >= BOARD_WIDTH_CELLS || boardY < 0 || boardY >= TOTAL_BOARD_HEIGHT_CELLS) {
                return false;
            }
            // Vérifie si la case est déjà occupée
            if (grid[boardX][boardY] != null) {
                return false;
            }
        }

        currentPiece = pieceToTry; 
        curPieceX = newX;
        curPieceY = newY;
        repaint();
        return true;
    }

    // Pose la pièce courante sur la grille et vérifie les lignes complètes
    private void pieceLanded() {
        for (int i = 0; i < 4; i++) {
            int boardX = curPieceX + currentPiece.getX(i);
            int boardY = curPieceY + currentPiece.getY(i);
            if (boardX >= 0 && boardX < BOARD_WIDTH_CELLS && boardY >= 0 && boardY < TOTAL_BOARD_HEIGHT_CELLS) {
                grid[boardX][boardY] = currentPiece.getColor();
            }
        }

        removeFullLines();

        if (!isFallingFinished) { 
            spawnNewPiece();
        }
    }

    // Fait descendre la pièce d'une ligne si possible, sinon la pose
    private void oneLineDown() {
        if (!tryMove(currentPiece, curPieceX, curPieceY + 1)) {
            pieceLanded();
        }
    }

    // Fait tomber la pièce jusqu'en bas instantanément
    private void dropDownHard() {
        int newY = curPieceY;
        while (tryMove(currentPiece, curPieceX, newY + 1)) {
            newY++;
        }
        pieceLanded();
    }

    // Supprime toutes les lignes complètes et met à jour le score
    private void removeFullLines() {
        int numFullLinesInThisTurn = 0;
        for (int y = TOTAL_BOARD_HEIGHT_CELLS - 1; y >= 0; y--) { 
            boolean lineIsFull = true;
            for (int x = 0; x < BOARD_WIDTH_CELLS; x++) {
                if (grid[x][y] == null) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                numFullLinesInThisTurn++;
                // Décale toutes les lignes au-dessus vers le bas
                for (int currentY = y; currentY > 0; currentY--) {
                    for (int x = 0; x < BOARD_WIDTH_CELLS; x++) {
                        grid[x][currentY] = grid[x][currentY - 1];
                    }
                }
                // Vide la première ligne
                for (int x = 0; x < BOARD_WIDTH_CELLS; x++) {
                    grid[x][0] = null;
                }
                y++; // Revérifie la même ligne après le décalage
            }
        }

        if (numFullLinesInThisTurn > 0) {
            numLinesRemoved += numFullLinesInThisTurn;
            // Attribution des points selon le nombre de lignes supprimées d'un coup
            if (numFullLinesInThisTurn == 1) score += 100;
            else if (numFullLinesInThisTurn == 2) score += 300;
            else if (numFullLinesInThisTurn == 3) score += 500;
            else if (numFullLinesInThisTurn == 4) score += 800; 

            updateTimerSpeed();
            updateStatusBar();
        }
    }

    // Ajuste la vitesse du jeu en fonction du nombre de lignes supprimées
    private void updateTimerSpeed() {
        int newDelay = INITIAL_TIMER_DELAY - ((numLinesRemoved / SPEEDUP_LINES_STEP) * SPEEDUP_AMOUNT);
        if (newDelay < MIN_TIMER_DELAY) newDelay = MIN_TIMER_DELAY;
        timer.setDelay(newDelay);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateCellSize(); // Calcule la taille des cases selon la taille du panneau
        drawGameArea(g); 
        drawLandedPieces(g);
        drawCurrentFallingPiece(g);

        if (isPaused) {
            drawPauseScreen(g);
        }
        // Affiche l'écran de fin si la partie est terminée
        if (isFallingFinished && !isStarted) {
            drawGameOverScreen(g);
        }
    }

    // Calcule la taille des cases pour que le plateau s'adapte à la fenêtre
    public void updateCellSize() {
        Dimension size = getSize();
        if (size.width > 0 && size.height > 0) {
            cellSize = Math.min(
                size.width / BOARD_WIDTH_CELLS,
                size.height / BOARD_HEIGHT_CELLS_VISIBLE
            );
        }
    }

    // Dessine la grille du plateau
    private void drawGameArea(Graphics g) {
        Dimension size = getSize();
        int boardPixelWidth = BOARD_WIDTH_CELLS * cellSize;
        int boardPixelHeight = BOARD_HEIGHT_CELLS_VISIBLE * cellSize;

        g.setColor(getBackground()); 
        g.fillRect(0, 0, size.width, size.height);

        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= BOARD_WIDTH_CELLS; i++) {
            g.drawLine(i * cellSize, 0, i * cellSize, boardPixelHeight);
        }
        for (int i = 0; i <= BOARD_HEIGHT_CELLS_VISIBLE; i++) { 
            g.drawLine(0, i * cellSize, boardPixelWidth, i * cellSize);
        }
    }

    // Dessine toutes les pièces déjà posées sur le plateau
    private void drawLandedPieces(Graphics g) {
        for (int x = 0; x < BOARD_WIDTH_CELLS; x++) {
            for (int yGrid = HIDDEN_ROWS_ABOVE; yGrid < TOTAL_BOARD_HEIGHT_CELLS; yGrid++) {
                if (grid[x][yGrid] != null) {
                    int yScreen = yGrid - HIDDEN_ROWS_ABOVE; 
                    drawSquare(g, x * cellSize, yScreen * cellSize, grid[x][yGrid]);
                }
            }
        }
    }

    // Dessine la pièce en train de tomber
    private void drawCurrentFallingPiece(Graphics g) {
        if (currentPiece.getPieceShape() != Shape.PieceShape.NoShape) {
            for (int i = 0; i < 4; i++) {
                int xGrid = curPieceX + currentPiece.getX(i);
                int yGrid = curPieceY + currentPiece.getY(i);

                if (yGrid >= HIDDEN_ROWS_ABOVE) {
                    int yScreen = yGrid - HIDDEN_ROWS_ABOVE;
                    drawSquare(g, xGrid * cellSize, yScreen * cellSize, currentPiece.getColor());
                }
            }
        }
    }

    // Dessine un carré d'une pièce à la position donnée
    private void drawSquare(Graphics g, int screenX, int screenY, Color color) {
        g.setColor(color);
        g.fillRect(screenX + 1, screenY + 1, cellSize - 2, cellSize - 2);

        g.setColor(color.brighter());
        g.drawLine(screenX, screenY + cellSize - 1, screenX, screenY); 
        g.drawLine(screenX, screenY, screenX + cellSize - 1, screenY); 

        g.setColor(color.darker());
        g.drawLine(screenX + 1, screenY + cellSize - 1, screenX + cellSize - 1, screenY + cellSize - 1); 
        g.drawLine(screenX + cellSize - 1, screenY + cellSize - 1, screenX + cellSize - 1, screenY + 1); 
    }

    // Affiche l'écran de pause
    private void drawPauseScreen(Graphics g) {
        g.setColor(new Color(50, 50, 50, 180)); 
        g.fillRect(0, 0, BOARD_WIDTH_CELLS * cellSize, BOARD_HEIGHT_CELLS_VISIBLE * cellSize);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Helvetica", Font.BOLD, 20));
        String msg = "PAUSE";
        FontMetrics fm = getFontMetrics(g.getFont());
        int msgWidth = fm.stringWidth(msg);
        g.drawString(msg, (BOARD_WIDTH_CELLS * cellSize - msgWidth) / 2, (BOARD_HEIGHT_CELLS_VISIBLE * cellSize) / 2);
    }

    // Affiche l'écran de fin de partie avec le score et le meilleur score
    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(50, 50, 50, 200)); 
        g.fillRect(0, 0, BOARD_WIDTH_CELLS * cellSize, BOARD_HEIGHT_CELLS_VISIBLE * cellSize);
        g.setColor(Color.RED);
        g.setFont(new Font("Helvetica", Font.BOLD, 24));
        String msg = "FIN DE PARTIE";
        FontMetrics fm = getFontMetrics(g.getFont());
        int msgWidth = fm.stringWidth(msg);
        g.drawString(msg, (BOARD_WIDTH_CELLS * cellSize - msgWidth) / 2, (BOARD_HEIGHT_CELLS_VISIBLE * cellSize) / 2 - 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Helvetica", Font.PLAIN, 14));
        String scoreMsg = "Score final : " + score;
        int scoreMsgWidth = fm.stringWidth(scoreMsg);
        g.drawString(scoreMsg, (BOARD_WIDTH_CELLS * cellSize - scoreMsgWidth) / 2 + 20, (BOARD_HEIGHT_CELLS_VISIBLE * cellSize) / 2 - 10);

        // Affiche le meilleur score précédent
        int prevHigh = parentFrame.getPreviousHighScore();
        String prevHighMsg = "Meilleur score précédent : " + prevHigh;
        int prevHighMsgWidth = fm.stringWidth(prevHighMsg);
        g.drawString(prevHighMsg, (BOARD_WIDTH_CELLS * cellSize - prevHighMsgWidth) / 2 + 20, (BOARD_HEIGHT_CELLS_VISIBLE * cellSize) / 2 + 10);

        // Affiche "Nouveau meilleur score !" si le joueur a battu le record
        if (parentFrame.wasLastGameHighScore()) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Helvetica", Font.BOLD, 18));
            String newHighMsg = "Nouveau meilleur score !";
            int newHighMsgWidth = fm.stringWidth(newHighMsg);
            g.drawString(newHighMsg, (BOARD_WIDTH_CELLS * cellSize - newHighMsgWidth) / 2 + 20, (BOARD_HEIGHT_CELLS_VISIBLE * cellSize) / 2 + 35);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Helvetica", Font.PLAIN, 14));
        String restartMsg = "Appuyez sur 'S' pour recommencer";
        int restartMsgWidth = fm.stringWidth(restartMsg);
        g.drawString(restartMsg, (BOARD_WIDTH_CELLS * cellSize - restartMsgWidth) / 2 + 15, (BOARD_HEIGHT_CELLS_VISIBLE * cellSize) / 2 + 60);
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
        if (isFallingFinished) { 
            return; 
        }
        if (!isPaused && isStarted) {
            oneLineDown();
        }
        repaint(); 
    }

    // Gestionnaire des touches du clavier pour contrôler le jeu
    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!isStarted) { 
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    start();
                }
                return;
            }

            if (currentPiece.getPieceShape() == Shape.PieceShape.NoShape && !isStarted) {
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    start(); 
                }
                return;
            }

            if (isPaused && e.getKeyCode() != KeyEvent.VK_P) { 
                return;
            }

            int keycode = e.getKeyCode();

            switch (keycode) {
                case KeyEvent.VK_P:
                    pause();
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    tryMove(currentPiece, curPieceX - 1, curPieceY);
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    tryMove(currentPiece, curPieceX + 1, curPieceY);
                    break;
                case KeyEvent.VK_DOWN: 
                case KeyEvent.VK_S:
                    if (!isPaused) oneLineDown();
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    tryMove(currentPiece.rotateRight(), curPieceX, curPieceY);
                    break;
                case KeyEvent.VK_Z: 
                    tryMove(currentPiece.rotateLeft(), curPieceX, curPieceY);
                    break;
                case KeyEvent.VK_SPACE: 
                    dropDownHard();
                    break;
            }
        }
    }
}
