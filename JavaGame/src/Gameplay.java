import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.Timer;

public class Gameplay extends JPanel implements KeyListener, ActionListener {

    private static final String PLAYER_DATA_FILE = "data_players.txt";
    private boolean play = false;
    private int score = 0;
    private int scoreNext;
    private boolean isPause = false;
    private boolean hello = true;

    public int col = 5, row = 3;
    public int colNext = 5, rowNext = 3;

    private String playerName;

    private ArrayList<Player> playersList;

    private int totalBricks = row * col;
    public int level = 1;

    final Timer timer;
    final int delay = 8;

    private int playerX = 310;

    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -1;
    private int ballYdir = -2;

    private MapGenerator map;

    public Gameplay() {
        map = new MapGenerator(row, col);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        getPlayerName();
        timer.start();
    }

    public void paint(Graphics g) {
        // background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // drawing map
        map.draw((Graphics2D) g);

        // borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // the scores
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("" + score, 590, 30);

        // the paddle
        g.setColor(Color.green);
        g.fillRect(playerX, 550, 100, 8);

        // the ball
        g.setColor(Color.yellow);
        g.fillOval(ballposX, ballposY, 20, 20);

        // khi bạn thắng game
        if (totalBricks <= 0) {
            play = false;
            level++;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Bạn đã thắng Level " + (level - 1), 210, 300);

            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Ấn Enter để sang Level " + level, 230, 350);
        }

        // khi bạn thua game
        if (ballposY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Bạn đã thua, Tổng điểm: " + score, 170, 300);

            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Ấn Enter để chơi lại!", 230, 350);

        }

        // khi bạn Pause game
        if (isPause) {
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Tạm dừng!", 230, 300);

            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Ấn P để tiếp tục", 230, 350);
        }

        // hiển thị thông báo chào mừng với tên người chơi
        if (hello) {
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Chào bạn!", 260, 300);

            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Nhấn Enter để bắt đầu", 230, 350);
        }

        g.dispose();
    }

    public void keyPressed(KeyEvent e) {
        // set nút di chuyển paddle sang phải
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }

        // set nút di chuyển paddle sang trái
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }

        // set nút enter
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // khi thua
            if (ballposY > 570) {
                getPlayerData();
                getPlayerName();

                score = 0;
                play = true;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -1;
                ballYdir = -2;
                playerX = 310;
                totalBricks = row * col;
                map = new MapGenerator(row, col);
                repaint();
            } else if (totalBricks == 0) {
                play = false;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -1;
                ballYdir = -2;
                playerX = 310;
                if (level % 2 == 0) {
                    colNext += 2;
                } else {
                    rowNext++;
                }
                scoreNext += score;
                totalBricks = colNext * rowNext;
                map = new MapGenerator(rowNext, colNext);

                repaint();
            } else if (hello) {
                hello = false;
                play = true;
                timer.start();
                map = new MapGenerator(row, col);
                repaint();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_P) {
            isPause = !isPause;
            pauseGame();
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void moveRight() {
        play = true;
        playerX += 20;
    }

    public void moveLeft() {
        play = true;
        playerX -= 20;
    }

    public void pauseGame() {
        if (isPause) {
            timer.stop();
            repaint();
        } else {
            timer.start();
        }
    }

    public void getPlayerName() {
        playerName = JOptionPane.showInputDialog("Nhập tên của bạn: ");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Người-Chơi";
        }
    }

    public void writePlayerData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PLAYER_DATA_FILE, true))) {
            writer.write(playerName + " " + score);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Không thể ghi dữ liệu người chơi vào file: " + e.getMessage());
        }
    }

    public void readPlayerData() {
        playersList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(PLAYER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String playerName = parts[0];
                int playerScore = Integer.parseInt(parts[1]);
                playersList.add(new Player(playerName, playerScore));
            }
        } catch (IOException e) {
            System.out.println("Không thể đọc dữ liệu người chơi từ file: " + e.getMessage());
        }
    }

    public void showTopPlayers() {
        readPlayerData();
        playersList.sort((p1, p2) -> Integer.compare(p2.playerScore, p1.playerScore));
        StringBuilder message = new StringBuilder("Danh sách 10 người chơi có điểm cao nhất:\n");
        int count = Math.min(playersList.size(), 10);
        for (int i = 0; i < count; i++) {
            message.append(i + 1).append(". ").append(playersList.get(i).playerName)
                    .append(" - Điểm: ").append(playersList.get(i).playerScore).append("\n");
        }
        JOptionPane.showMessageDialog(null, message.toString(), "Top Players", JOptionPane.INFORMATION_MESSAGE);
    }

    public void getPlayerData() {
        playersList = new ArrayList<>();
        Player player = new Player(playerName, score);
        playersList.add(player);
        playersList.sort((p1, p2) -> Integer.compare(p2.playerScore, p1.playerScore));
        writePlayerData();
        showTopPlayers();
    }

    // phương thức này xử lý sự kiện được gọi khi có một hành động xảy ra
    // có thể là hành động của người chơi thưc hiện
    // hoặc là đến lượt di chuyển của quả bóng
    public void actionPerformed(ActionEvent e) {
        // bắt đầu đồng hồ đếm thời gian
        timer.start();

        // kiểm tra trò chơi, hiện có đang chơi hay không thông qua biến bool play
        if (play) {
            // nếu bóng chạm paddle ở vị trí X
            if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 30, 8))) {
                // thay đổi hướng di chuyển của quả bóng theo chiều dọc
                ballYdir = -ballYdir;

                // và cả hướng đi theo chiều ngang
                ballXdir = -2;
            }
            // nếu bóng chạm paddle vào bên phải hoặc bên trái của paddle
            else if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX + 70, 550, 30, 8))) {
                // đổi hướng quả bóng theo chiều ngang
                ballYdir = -ballYdir;

                // đổi hướng di chuyển theo chiều dọc
                ballXdir = ballXdir + 1;
            }
            // nếu bóng chạm ngay giữa của paddle
            else if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX + 30, 550, 40, 8))) {
                // chỉ thay đổi hướng di chuyển theo chiều dọc
                ballYdir = -ballYdir;
            }

            // kiểm tra va chạm của bóng và tất cả các viên gạch trên màn hình
            A:
            // dùng 2 vòng lặp, duyệt qua tất cả các viên gạch trên màn hình
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    // nếu 1 viên gạch còn tồn tại hay trọng số > 0
                    if (map.map[i][j] > 0) {

                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);

                        // nếub óng chạm gạch
                        if (ballRect.intersects(rect)) {
                            // set trọng số = 0
                            // đánh dấu là không tồn tại
                            map.setBrickValue(0, i, j);

                            // cộng điểm khi bóng chạm gạch
                            score += 5;

                            // trừ bớt đi 1 viên gạch
                            totalBricks--;

                            // when ball hit right or left of brick
                            if (ballposX + 19 <= rect.x || ballposX + 1 >= rect.x + rect.width) {
                                ballXdir = -ballXdir;
                            }
                            // when ball hits top or bottom of brick
                            else {
                                ballYdir = -ballYdir;
                            }

                            break A;
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;

            if (ballposX < 0) {
                ballXdir = -ballXdir;
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir;
            }
            if (ballposX > 670) {
                ballXdir = -ballXdir;
            }

            repaint();
        }
    }
}
