/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.SnakeGame;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

/**
 *
 * @author Bui Quang Nhat Chau
 */
public class SimpleSnakeGame extends JFrame {

    Timer timer;

    public SimpleSnakeGame() {
        initWindows();
    }

    private void initWindows() {
        setLayout(new BorderLayout());

        JLabel seperator = new JLabel();
        seperator.setPreferredSize(new Dimension(140, 20));
        // this panel show game score
        final JPanel pGameInfo = new JPanel();
        int width = 150;
        int height = getHeight();

        pGameInfo.setPreferredSize(new Dimension(width, height));

        JLabel lblScore = new JLabel("Score");
        lblScore.setPreferredSize(new Dimension(140, 50));
        lblScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblScore.setFont(new Font("Arial", 0, 18));

        JLabel lblDisplayScore = new JLabel("0");
        lblDisplayScore.setPreferredSize(new Dimension(140, 25));
        lblDisplayScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblDisplayScore.setFont(new Font("Arial", 0, 35));

        JLabel lblSnakeLength = new JLabel("Length");
        lblSnakeLength.setPreferredSize(new Dimension(140, 50));
        lblSnakeLength.setHorizontalAlignment(SwingConstants.CENTER);
        lblSnakeLength.setFont(new Font("Arial", 0, 18));

        JLabel lblDisplaySnakeLength = new JLabel("5");
        lblDisplaySnakeLength.setPreferredSize(new Dimension(140, 25));
        lblDisplaySnakeLength.setHorizontalAlignment(SwingConstants.CENTER);
        lblDisplaySnakeLength.setFont(new Font("Arial", 0, 35));

        // this panel is the main game
        final MySecondPanel panel = new MySecondPanel();

        final JButton btnReset = new JButton("Reset");
        btnReset.setEnabled(false);
        btnReset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.resetGame();
                timer = panel.getTimer();
                btnReset.setEnabled(false);
            }
        });

        pGameInfo.add(seperator);
        pGameInfo.add(lblScore);
        pGameInfo.add(lblDisplayScore);
        pGameInfo.add(seperator);
        pGameInfo.add(lblSnakeLength);
        pGameInfo.add(lblDisplaySnakeLength);
        pGameInfo.add(seperator);
        pGameInfo.add(btnReset);

        add(pGameInfo, BorderLayout.EAST);

        panel.setPreferredSize(new Dimension(650, getHeight()));
        panel.setButtonReset(btnReset);
        panel.setLabelScore(lblDisplayScore);
        panel.setLabelSnakeLength(lblDisplaySnakeLength);

        timer = panel.getTimer();
        add(panel, BorderLayout.WEST);

        // set main frame properties
        setTitle("Snake game");
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setFocusable(true);
        panel.requestFocusInWindow();

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e); //To change body of generated methods, choose Tools | Templates.
                timer.stop();
            }

        });
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new WindowsLookAndFeel());
        } catch (Exception e) {
        }

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                SimpleSnakeGame game = new SimpleSnakeGame();
                game.setVisible(true);
            }
        });
    }
}

class MySecondPanel extends JPanel implements ActionListener {

    private final int DELAY_TIME = 70;
    private final int SNAKE_BODY_SIZE = 10;
    private final int SNAKE_PIXEL_GAPS = 13;
    private final int INITIAL_SNAKE_LENGTH = 5;
    private LinkedList<Shape> THE_SNAKE;
    private boolean FOOD_EATEN;
    private boolean GAME_OVER;
    private boolean START_FLAG;
    private Shape FOOD;
    private Timer TIMER;
    private JButton btnReset;
    private JLabel lblScore;
    private JLabel lblSnakeLength;
    private int MOVING_DIRECTION;
    private int SCORE;

    public MySecondPanel() {
        initGame();
        setBackground(Color.white);
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyReleased(e); //To change body of generated methods, choose Tools | Templates.
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_UP) {
                    if (MOVING_DIRECTION != 3) {
                        MOVING_DIRECTION = 1;
                    }
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    if (MOVING_DIRECTION != 4) {
                        MOVING_DIRECTION = 2;
                    }
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    if (MOVING_DIRECTION != 1) {
                        MOVING_DIRECTION = 3;
                    }
                } else if (keyCode == KeyEvent.VK_LEFT) {
                    if (MOVING_DIRECTION != 2) {
                        MOVING_DIRECTION = 4;
                    }
                }
                if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_DOWN) {
                    if (!TIMER.isRunning()) {
                        TIMER.start();
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.

        if (!START_FLAG) {
            drawFood(g);
        }
        if (START_FLAG) {
            START_FLAG = false;
            initSnake2();
            addFood();
        }
        if (FOOD_EATEN) {
            addPixel();
            addFood();
            lblScore.setText((SCORE += 10) + "");
            lblSnakeLength.setText(THE_SNAKE.size() + "");
            FOOD_EATEN = false;
        }
        drawSnake(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveSnake(MOVING_DIRECTION);
        if (!GAME_OVER) {
            repaint();
        }
        if (checkDeath()) {
            TIMER.stop();
            btnReset.setEnabled(true);
            JOptionPane.showMessageDialog(null, "You die");
        }
    }

    public void setLabelScore(JLabel lblScore) {
        this.lblScore = lblScore;
    }

    public void setLabelSnakeLength(JLabel lblSnakeLength) {
        this.lblSnakeLength = lblSnakeLength;
    }

    public void setButtonReset(JButton btnReset) {
        this.btnReset = btnReset;
    }

    public void resetGame() {
        initGame();
//        initSnake2();
        setFocusable(true);
        requestFocusInWindow();
        repaint();
        lblScore.setText(SCORE + "");
        lblSnakeLength.setText("5");
    }

    private void initGame() {
        MOVING_DIRECTION = 0;
        SCORE = 0;
        THE_SNAKE = new LinkedList<>();
        START_FLAG = true;
        FOOD_EATEN = false;
        GAME_OVER = false;
        initTimer();
    }

    private void addFood() {
        int width = getWidth();
        int height = getHeight();

        Random r = new Random();
        int x = Math.abs(r.nextInt()) % width;
        int y = Math.abs(r.nextInt()) % height;

        if (x < 0 || x + SNAKE_BODY_SIZE > width || y < 0 || y + SNAKE_BODY_SIZE > height) {
            addFood();
        } else {
            FOOD = new Rectangle(x, y, SNAKE_BODY_SIZE, SNAKE_BODY_SIZE);
            for (Shape s : THE_SNAKE) {
                if (s.intersects((Rectangle2D) FOOD)) {
                    addFood();
                }
            }
        }
    }

    private void addPixel() {
        Shape s = THE_SNAKE.getFirst();
        int x = s.getBounds().x;
        int y = s.getBounds().y;
        Shape newPixel = new Rectangle(x, y, SNAKE_BODY_SIZE, SNAKE_BODY_SIZE);
        THE_SNAKE.addFirst(newPixel);
    }

    private boolean checkDeath() {
        // check if the head of the snake intersects with game space's border
        Shape head = THE_SNAKE.getLast();
        int x = head.getBounds().x;
        int y = head.getBounds().y;
        if (x < 0 || x + SNAKE_BODY_SIZE > getWidth() || y < 0 || y + SNAKE_BODY_SIZE > getHeight()) {
            return GAME_OVER = true;
        }
        // check if the head of the snake intersects with its body
        int size = THE_SNAKE.size();
        for (int i = size - 2; i >= 0; i--) {
            Shape body = THE_SNAKE.get(i);
            if (head.intersects((Rectangle2D) body)) {
                return GAME_OVER = true;
            }
        }
        return false;
    }

    private void drawFood(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.fill(FOOD);
        g2d.draw(FOOD);
    }

    private void drawSnake(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (Shape s : THE_SNAKE) {
            g2d.fill(s);
            g2d.draw(s);
        }
    }

    public Timer getTimer() {
        return TIMER;
    }

    private void moveSnake(int direction) {
        int size = THE_SNAKE.size();
        Shape s = THE_SNAKE.getLast();
        int x = s.getBounds().x;
        int y = s.getBounds().y;
        Shape temp = new Rectangle(x, y, SNAKE_BODY_SIZE, SNAKE_BODY_SIZE);
        switch (direction) {
            case 1: // up
                y -= SNAKE_PIXEL_GAPS;
                break;
            case 2: // right
                x += SNAKE_PIXEL_GAPS;
                break;
            case 3: // down
                y += SNAKE_PIXEL_GAPS;
                break;
            case 4: // left
                x -= SNAKE_PIXEL_GAPS;
                break;
        }
        ((Rectangle) s).setBounds(x, y, SNAKE_BODY_SIZE, SNAKE_BODY_SIZE);
        if (s.intersects((Rectangle2D) FOOD)) {
            FOOD_EATEN = true;
        }
        for (int i = size - 2; i >= 0; i--) {
            int x1 = temp.getBounds().x;
            int y1 = temp.getBounds().y;
            Shape s2 = THE_SNAKE.get(i);
            int x2 = s2.getBounds().x;
            int y2 = s2.getBounds().y;
            temp = new Rectangle(x2, y2, SNAKE_BODY_SIZE, SNAKE_BODY_SIZE);
            ((Rectangle) s2).setBounds(x1, y1, SNAKE_BODY_SIZE, SNAKE_BODY_SIZE);
        }
    }

    private void initTimer() {
        TIMER = new Timer(DELAY_TIME, this);
    }

    private void initSnake2() {
        int x = getWidth() / 2;
        int y = getHeight() / 2;

        Shape shape = new Rectangle(x, y, SNAKE_BODY_SIZE, SNAKE_BODY_SIZE);
        THE_SNAKE.add(shape);
        while (THE_SNAKE.size() != INITIAL_SNAKE_LENGTH) {
            addPixel();
        }
    }

}
