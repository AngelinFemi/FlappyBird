import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBirdGame extends JPanel implements ActionListener, KeyListener {
    private int birdX = 100, birdY = 250, birdVelocity = 0;
    private final int gravity = 1, jumpStrength = -10;
    private final int pipeSpeed = 3, pipeWidth = 60, pipeGap = 150;
    private final int screenWidth = 800, screenHeight = 600;
    private final int groundHeight = 100;
    private int score = 0;
    private boolean gameOver = false, gameStarted = false;
    
    private int backgroundX = 0;
    private final int backgroundSpeed = 1; // Adjust speed as needed

    private Image birdImage, pipeTop, pipeBottom, background, groundImage;
    private ArrayList<Rectangle> pipes;
    private Timer timer;
    private Random rand;

    public FlappyBirdGame() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.CYAN);
        setFocusable(true);
        addKeyListener(this);

        pipes = new ArrayList<>();
        rand = new Random();
        timer = new Timer(20, this);
        timer.start();

        loadImages();
        addPipe();
    }

    private void loadImages() {
        birdImage = new ImageIcon("bird.png").getImage();
        pipeTop = new ImageIcon("pipe_top.png").getImage();
        pipeBottom = new ImageIcon("pipe_bottom.png").getImage();
        background = new ImageIcon("background.png").getImage();
        groundImage = new ImageIcon("ground.png").getImage();
    }

    private void addPipe() {
        int height = 50 + rand.nextInt(250);
        pipes.add(new Rectangle(screenWidth, 0, pipeWidth, height));
        pipes.add(new Rectangle(screenWidth, height + pipeGap, pipeWidth, screenHeight - height - pipeGap - groundHeight));
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            if (gameStarted) {
                birdVelocity += gravity;
                birdY += birdVelocity;

                // Move pipes
                for (int i = 0; i < pipes.size(); i++) {
                    pipes.get(i).x -= pipeSpeed;
                }

                // Move background to the left
                backgroundX -= backgroundSpeed;
                if (backgroundX <= -screenWidth) {
                    backgroundX = 0; // Reset position for seamless scrolling
                }

                if (pipes.get(0).x + pipeWidth < 0) {
                    pipes.remove(0);
                    pipes.remove(0);
                    addPipe();
                    score++;
                }

                checkCollision();
            }
        }
        repaint();
    }

    private void checkCollision() {
        for (Rectangle pipe : pipes) {
            if (new Rectangle(birdX, birdY, 40, 40).intersects(pipe)) {
                gameOver = true;
                timer.stop();
            }
        }
        if (birdY >= screenHeight - groundHeight || birdY < 0) {
            gameOver = true;
            timer.stop();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw moving background (clouds)
        g.drawImage(background, backgroundX, 0, screenWidth, screenHeight, null);
        g.drawImage(background, backgroundX + screenWidth, 0, screenWidth, screenHeight, null);

        // Draw pipes
        for (int i = 0; i < pipes.size(); i += 2) {
            Rectangle topPipe = pipes.get(i);
            Rectangle bottomPipe = pipes.get(i + 1);

            g.drawImage(pipeTop, topPipe.x, topPipe.y, topPipe.width, topPipe.height, null);
            g.drawImage(pipeBottom, bottomPipe.x, bottomPipe.y, bottomPipe.width, bottomPipe.height, null);
        }

        // Draw bird
        g.drawImage(birdImage, birdX, birdY, 40, 40, null);

        // Draw ground using custom image
        g.drawImage(groundImage, 0, screenHeight - groundHeight, screenWidth, groundHeight, null);

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 30);

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over!", 300, 250);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE to Restart", 320, 290);
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                resetGame();
            } else {
                gameStarted = true;
                birdVelocity = jumpStrength;
            }
        }
    }

    private void resetGame() {
        birdY = 250;
        birdVelocity = 0;
        pipes.clear();
        addPipe();
        score = 0;
        gameOver = false;
        gameStarted = false;
        timer.start();
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBirdGame game = new FlappyBirdGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
