package com.weem.epicinventor;

import com.weem.epicinventor.utility.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends JFrame implements WindowListener {

    private static int DEFAULT_FPS = 30;
    public static String VERSION = "0.7.0";
    public static boolean RELEASE = false;
    public static String loadingText = "Loading";
    private GamePanel gp;
    private boolean isFullScreen = false;

    @SuppressWarnings("LeakingThisInConstructor")
    public Game(long period) {
        super("Game");

        new RepeatingReleasedEventsFixer().install();

        this.setUndecorated(false);

        Thread.currentThread().setUncaughtExceptionHandler(new EIError());

        gp = new GamePanel(this, period, 800, 600);
        Container c = getContentPane();
        gp.setContainer(c);
        c.add(gp, "Center");

        addWindowListener(this);
        pack();
        setResizable(false);
        setVisible(true);

        //add the icon
        Image icon = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/Images/Misc/Icon.png"));
        if (icon != null) {
            setIconImage(icon);
        }

        setTitle("Epic Inventor - v" + VERSION);

        //center the frame
        this.setLocationRelativeTo(null);
    }

    public void setFullScreen(boolean f) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice dev = env.getDefaultScreenDevice();
        this.setBackground(Color.BLACK);

        if (f) {
            this.setResizable(false);
            this.removeNotify();
            this.setUndecorated(true);
            this.addNotify();
            this.pack();
            dev.setFullScreenWindow(this);
            isFullScreen = true;
        } else {
            if(isFullScreen) {
                this.setResizable(false);
                this.removeNotify();
                this.setUndecorated(false);
                this.addNotify();
                this.pack();
                dev.setFullScreenWindow(null);
                this.validate();
                isFullScreen = false;
            }
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
        //gp.resumeGame();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        //gp.pauseGame();
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        //gp.resumeGame();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        //gp.pauseGame();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        gp.stopGame();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    public static void main(String args[]) {
        long period = (long) 1000.0 / DEFAULT_FPS;

        new Game(period * 1000000L);    // ms --> nanosecs
    }
}