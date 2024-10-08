import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.ImageIO;

public class Main {

    static final ArrayList<JLabel> enemyList = new ArrayList<>();
    static final ArrayList<JLabel> bulletList = new ArrayList<>();
    static final ArrayList<JLabel> enemy2List = new ArrayList<>();
    static final ArrayList<Integer> enemy2ListHp = new ArrayList<>();

    static class Box implements Runnable {

        Container container;
        public Box(Container container2) {
            container = container2;
        }

        public void run() {
            try {
                Iterator<JLabel> bulletI;
                while (true) {
                    bulletI = bulletList.iterator();
                    synchronized (bulletList) {
                        while (bulletI.hasNext()) {
                            JLabel bulletL = bulletI.next();
                            Rectangle bulletR = bulletL.getBounds();
                            Iterator<JLabel> enemyI = enemyList.iterator();
                            Iterator<JLabel> enemy2I = enemy2List.iterator();
                            while (enemyI.hasNext() || enemy2I.hasNext()) {
                                synchronized (enemyList) {
                                    synchronized (enemy2List) {
                                        if (!enemy2List.isEmpty()) {
                                            JLabel enemy2L = enemy2I.next();
                                            int hp = enemy2ListHp.get(enemy2List.indexOf(enemy2L));
                                            Rectangle enemy2R = enemy2L.getBounds();
                                            if (bulletR.intersects(enemy2R)) {
                                                if (enemy2ListHp.get(enemy2List.indexOf(enemy2L))<=0) {
                                                    container.remove(enemy2L);
                                                    container.remove(bulletL);
                                                    enemy2L.repaint();
                                                    bulletL.repaint();
                                                    enemy2I.remove();
                                                    bulletI.remove();
                                                }
                                                hp--;
                                                enemy2ListHp.set(enemy2List.indexOf(enemy2L),hp);
                                                container.remove(bulletL);
                                                bulletI.remove();
                                            }
                                        }
                                        if (!enemyList.isEmpty()){
                                            JLabel enemyL = enemyI.next();
                                            Rectangle enemyR = enemyL.getBounds();
                                            if (bulletR.intersects(enemyR)) {
                                                container.remove(enemyL);
                                                container.remove(bulletL);
                                                enemyL.repaint();
                                                bulletL.repaint();
                                                enemyI.remove();
                                                bulletI.remove();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Box box = new Box(container);
                Thread thread = new Thread(box);
                thread.start();
            }


        }
    }

    static class Bullet implements Runnable {
        JLabel player;
        Container container;

        public Bullet(JLabel label, Container container2) {
            player = label;
            container = container2;
        }

        public void run() {
            JLabel label = new JLabel(new ImageIcon("src/Bullet.png"));
            int bx = player.getX() + 15;
            final int[] by = {player.getY()};
            bulletList.add(label);
            label.setBounds(bx, by[0], 20, 20);
            container.add(label);

            while (true) {
                if (by[0] < 10) {
                    break;
                }
                SwingUtilities.invokeLater(() -> label.setBounds(bx, by[0] -= 5, 20, 20));
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            container.remove(label);
            bulletList.remove(label);

        }
    }
    static class EnemyPlane implements Runnable {
        Container container;
        public EnemyPlane(Container container2) {
            container = container2;
        }
        public void run() {
            Random random = new Random();
            JLabel label = new JLabel(new ImageIcon("src/Plane.png"));
            enemyList.add(label);
            int ex = random.nextInt(10,381);
            int ey = 10;
            label.setBounds(ex , ey, 50, 50);
            container.add(label);

            while (true) {

                if (ey > 400) {
                    break;
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                ey = label.getY();
                label.setBounds(ex, ey+=1, 50, 50);
                label.repaint();

            }

            container.remove(label);
            enemyList.remove(label);

        }
    }

    static class EnemyPlane2 implements Runnable {
        Container container;
        public EnemyPlane2(Container container2) {
            container = container2;
        }
        public void run() {
            Random random = new Random();
            JLabel label = new JLabel(new ImageIcon("src/Plane2.png"));
            enemy2List.add(label);
            enemy2ListHp.add(2);
            int ex = random.nextInt(10,381);
            int ey = 10;
            label.setBounds(ex , ey, 50, 50);
            container.add(label);

            while (true) {

                if (ey > 400) {
                    break;
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                ey = label.getY();
                label.setBounds(ex, ey+=1, 50, 50);
                label.repaint();

            }

            container.remove(label);
            if (enemy2ListHp.indexOf(enemy2List.indexOf(label))>=0) {
                int indexHp = enemy2List.indexOf(label);
                enemy2List.remove(label);
                enemy2ListHp.remove(indexHp);
            }

        }
    }


    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("MINE2D | HOMEPAGE");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = frame.getContentPane();
        container.setLayout(null);
        frame.getContentPane().setBackground(Color.cyan);
        frame.getContentPane().setVisible(true);

        JLabel label = new JLabel(new ImageIcon("src/PLAYER.png"));
        label.setBounds(150,200,50,50);
        container.add(label);


        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int lx = label.getX();
                int ly = label.getY();
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    label.setBounds(lx -= 10, ly, 50, 50);
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    label.setBounds(lx += 10, ly, 50, 50);
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    label.setBounds(lx, ly-=10, 50, 50);
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    label.setBounds(lx, ly+=10, 50, 50);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    Bullet bullet = new Bullet(label, container);
                    Thread thread = new Thread(bullet);
                    thread.start();
                }
            }
        });

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EnemyPlane enemyPlane = new EnemyPlane(container);
                Thread thread = new Thread(enemyPlane);
                thread.start();
            }
        });

        timer.start();

        Timer timer2 = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EnemyPlane2 enemyPlane2 = new EnemyPlane2(container);
                Thread thread = new Thread(enemyPlane2);
                thread.start();
            }
        });

        timer2.start();

        frame.setVisible(true);

        Box box = new Box(container);
        Thread thread = new Thread(box);
        thread.start();
    }
}