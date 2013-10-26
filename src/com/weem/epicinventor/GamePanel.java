package com.weem.epicinventor;

import com.weem.epicinventor.utility.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.*;

public class GamePanel extends JPanel implements Runnable {

    private int pWidth;
    private int pHeight;
    private static final int NO_DELAYS_PER_YIELD = 16;
    private static final int MAX_FRAME_SKIPS = 5;
    private Thread animator;
    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false;
    private volatile boolean isMasterPaused = false;
    private long period;
    private Graphics dbg;
    private Image dbImage = null;
    private Container container;
    private Game game;
    private GameController gameController;
    private volatile boolean keySpacePressed = false;
    private volatile boolean keyRightPressed = false;
    private volatile boolean keyLeftPressed = false;
    private volatile boolean keyUpPressed = false;
    private volatile boolean keyDownPressed = false;
    private volatile boolean keyGatherPressed = false;
    private boolean[] keys = new boolean[65536];

    public GamePanel(Game g, long period, int w, int h) {
        game = g;
        pWidth = w;
        pHeight = h;
        this.period = period;

        gameController = new GameController(pWidth, pHeight, period, this);

        setDoubleBuffered(false);
        setBackground(Color.white);
        setPreferredSize(new Dimension(pWidth - 10, pHeight - 6));

        setFocusable(true);
        requestFocus();

        this.setFocusTraversalKeysEnabled(false);

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                processKeyPress(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                processKeyRelease(e);
            }
        });

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                processMousePress(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                processMouseReleased(e);
            }
        });

        addMouseMotionListener(new MouseInputAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                gameController.getCurrentMousePosition().setLocation(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                gameController.getCurrentMousePosition().setLocation(e.getPoint());
            }
        });

        addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                processMouseWheelMove(e);
            }
        });
    }

    public void setContainer(Container c) {
        container = c;
    }

    public boolean getKey(int k) {
        return keys[k];
    }

    private void processKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();

        keys[keyCode] = true;

        if (gameController.isConsoleOpen()) {
            gameController.consoleKey(keyCode, e.getKeyChar());
        } else if (gameController.isKeysOpen()) {
            gameController.settingsKey(keyCode, e.getKeyChar());
        } else if (gameController.isNewCharacterOpen()) {
            if (keyCode == KeyEvent.VK_TAB) {
                gameController.newCharacterKey(keyCode, e.getKeyChar(), true);
            } else {
                gameController.newCharacterKey(keyCode, e.getKeyChar());
            }
        } else if (gameController.isMultiPlayerJoinOpen()) {
            if (keyCode == KeyEvent.VK_TAB) {
                gameController.multiPlayerJoinKey(keyCode, e.getKeyChar(), true);
            } else {
                gameController.multiPlayerJoinKey(keyCode, e.getKeyChar());
            }
        } else if (gameController.isMultiPlayerHostOpen()) {
            if (keyCode == KeyEvent.VK_TAB) {
                gameController.multiPlayerHostKey(keyCode, e.getKeyChar(), true);
            } else {
                gameController.multiPlayerHostKey(keyCode, e.getKeyChar());
            }
        } else if (keyCode == Settings.buttonPause && gameController.getIsInGame() && gameController.multiplayerMode == gameController.multiplayerMode.NONE) {
            togglePaused();
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            gameController.togglePauseHUD();
        } else {
            if (!isPaused && !isMasterPaused) {
                if (gameController.getIsInGame()) {
                    if (keyCode == Settings.buttonInventory) {
                        gameController.toggleMasterHUD();
                    } else if (keyCode == Settings.buttonMoveLeft) {
                        if (!keyLeftPressed) {
                            keyLeftPressed = true;
                            keyRightPressed = false;
                            gameController.stopActions();
                            gameController.moveLeft();
                        }
                    } else if (keyCode == Settings.buttonMoveRight) {
                        if (!keyRightPressed) {
                            keyRightPressed = true;
                            keyLeftPressed = false;
                            gameController.stopActions();
                            gameController.moveRight();
                        }
                    } else if (keyCode == Settings.buttonJump) {
                        if (!keySpacePressed) {
                            keySpacePressed = true;
                            gameController.stopActions();
                            gameController.jump();
                        }
                    } else if (keyCode == Settings.buttonAction) {
                        if (!keyGatherPressed) {
                            keyGatherPressed = true;
                            gameController.stopActions();
                            gameController.startGather();
                        }
                    } else if (keyCode == Settings.buttonRobot) {
                        gameController.robotToggleActivated();
                    } else if (keyCode == KeyEvent.VK_ENTER) {
                        gameController.keyEnterPressed();
                    } else if (keyCode == KeyEvent.VK_SHIFT) {
                        gameController.shiftPressed();
                    } else if (keyCode == KeyEvent.VK_0) {
                        gameController.stopActions();
                        gameController.numPressed(0);
                    } else if (keyCode == KeyEvent.VK_1) {
                        gameController.stopActions();
                        gameController.numPressed(1);
                    } else if (keyCode == KeyEvent.VK_2) {
                        gameController.stopActions();
                        gameController.numPressed(2);
                    } else if (keyCode == KeyEvent.VK_3) {
                        gameController.stopActions();
                        gameController.numPressed(3);
                    } else if (keyCode == KeyEvent.VK_4) {
                        gameController.stopActions();
                        gameController.numPressed(4);
                    } else if (keyCode == KeyEvent.VK_5) {
                        gameController.stopActions();
                        gameController.numPressed(5);
                    } else if (keyCode == KeyEvent.VK_6) {
                        gameController.stopActions();
                        gameController.numPressed(6);
                    } else if (keyCode == KeyEvent.VK_7) {
                        gameController.stopActions();
                        gameController.numPressed(7);
                    } else if (keyCode == KeyEvent.VK_8) {
                        gameController.stopActions();
                        gameController.numPressed(8);
                    } else if (keyCode == KeyEvent.VK_9) {
                        gameController.stopActions();
                        gameController.numPressed(9);
                    }
                }
            }
        }
    }

    private void processKeyRelease(KeyEvent e) {
        int keyCode = e.getKeyCode();

        keys[keyCode] = false;

        if (gameController.isNewCharacterOpen()) {
        } else if (keyCode == Settings.buttonMoveLeft) {
            keyLeftPressed = false;
            if (!keyRightPressed) {
                gameController.stopXMove();
            }
        } else if (keyCode == Settings.buttonMoveRight) {
            keyRightPressed = false;
            if (!keyLeftPressed) {
                gameController.stopXMove();
            }
        } else if (keyCode == Settings.buttonJump) {
            keySpacePressed = false;
            gameController.stopJump();
        } else if (keyCode == Settings.buttonAction) {
            keyGatherPressed = false;
            gameController.stopActions();
        } else if (keyCode == KeyEvent.VK_SHIFT) {
            gameController.shiftRelease();
        }
    }

    private void processMousePress(MouseEvent e) {
        int buttonCode = e.getButton();
        Point clickPoint = e.getPoint();

        if (buttonCode == MouseEvent.BUTTON1) {
            gameController.handleClick(clickPoint);
        } else if (buttonCode == MouseEvent.BUTTON3) {
            gameController.handleRightClick(clickPoint);
        }
    }

    private void processMouseReleased(MouseEvent e) {
        int buttonCode = e.getButton();
        Point clickPoint = e.getPoint();

        if (buttonCode == MouseEvent.BUTTON1) {
            gameController.handleReleased(clickPoint);
        }
    }

    private void processMouseWheelMove(MouseWheelEvent e) {
        int steps = e.getWheelRotation();

        gameController.handleMouseScroll(steps);
    }

    @Override
    public void addNotify() {
        super.addNotify();   // creates the peer
        startGame();         // start the thread
    }

    private void startGame() {
        if (animator == null || !isRunning) {
            animator = new Thread(this);
            animator.start();
        }
    }

    public void setFullScreen(boolean f) {
        game.setFullScreen(f);
    }

    public void resizePanel(int w, int h) {
        if (w == 0 && h == 0) {
            //full screen
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            pWidth = dim.width;
            pHeight = dim.height;

            dbImage = null;

            //resize the panel
            this.setPreferredSize(new Dimension(pWidth, pHeight));
            this.revalidate();
            this.setFocusable(true);
            this.requestFocus();

            //resize the container
            if (container != null) {
                container.setPreferredSize(new Dimension(pWidth, pHeight));
                container.validate();
            }
        } else {
            pWidth = w;
            pHeight = h;

            dbImage = null;
            
            game.setFullScreen(false);

            //resize the panel
            this.setPreferredSize(new Dimension(pWidth - 10, pHeight - 6));
            this.revalidate();
            this.setFocusable(true);
            this.requestFocus();

            //resize the container
            if (container != null) {
                container.setPreferredSize(new Dimension(pWidth - 10, pHeight - 6));
                container.validate();
            }
        }

        //resize the frame
        game.pack();
        game.setLocationRelativeTo(null);
        
        if (w == 0 && h == 0) {
            //full screen
            game.setFullScreen(true);
        }
    }

    public void resumeGame() {
        isPaused = false;
    }

    public void pauseGame() {
        isPaused = true;
    }

    public void togglePaused() {
        isPaused = !isPaused;
    }

    public boolean getIsPaused() {
        return isPaused;
    }

    public void resumeMasterGame() {
        isMasterPaused = false;
    }

    public void pauseMasterGame() {
        isMasterPaused = true;
    }

    public void toggleMasterPaused() {
        isMasterPaused = !isMasterPaused;
    }

    public boolean getIsMasterPaused() {
        return isMasterPaused;
    }

    public void stopGame() {
        isRunning = false;
        gameController.quit();
    }

    @Override
    @SuppressWarnings({"SleepWhileInLoop", "CallToThreadYield"})
    public void run() {
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0L;

        beforeTime = System.nanoTime();

        isRunning = true;

        while (isRunning) {
            gameUpdate();
            gameRender();
            paintScreen();

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1000000L);  // nano -> ms
                } catch (InterruptedException ex) {
                }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            } else {    // sleepTime <= 0; the frame took longer than the period
                excess -= sleepTime;
                overSleepTime = 0L;

                if (++noDelays >= NO_DELAYS_PER_YIELD) {
                    Thread.yield();   // give another thread a chance to run
                    noDelays = 0;
                }
            }

            beforeTime = System.nanoTime();

            int skips = 0;
            while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
                excess -= period;
                gameUpdate();
                skips++;
            }
        }
        System.exit(0);
    }

    private void gameUpdate() {
        gameController.update();
    }

    private void gameRender() {
        if (dbImage == null) {
            dbImage = createImage(pWidth, pHeight);
            if (dbImage == null) {
                EIError.debugMsg("dbImage is null");
                return;
            } else {
                dbg = dbImage.getGraphics();
            }
        }

        dbg.setColor(Color.black);
        dbg.fillRect(0, 0, pWidth, pHeight);

        gameController.render(dbg);
    }

    private void paintScreen() {
        Graphics g;
        try {
            g = this.getGraphics();
            if ((g != null) && (dbImage != null)) {
                g.drawImage(dbImage, 0, 0, null);
            }

            // Sync the display on some systems.
            // (on Linux, this fixes event queue problems)
            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        } catch (Exception e) {
            EIError.debugMsg("Graphics context error: " + e);
        }
    }
}