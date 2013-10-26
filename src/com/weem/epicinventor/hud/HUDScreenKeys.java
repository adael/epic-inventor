package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;

import java.awt.*;
import java.awt.event.*;

public class HUDScreenKeys extends HUD {

    private final static int BUTTON_DONATE_WIDTH = 119;
    private final static int BUTTON_DONATE_HEIGHT = 102;
    private final static int BUTTON_DONATE_X = 33;
    private final static int BUTTON_DONATE_Y = 155;
    private final static int BUTTON_HELP_WIDTH = 119;
    private final static int BUTTON_HELP_HEIGHT = 102;
    private final static int BUTTON_HELP_X = 646;
    private final static int BUTTON_HELP_Y = 155;
    private int newButtonMoveRight;
    private int newButtonMoveLeft;
    private int newButtonJump;
    private int newButtonAction;
    private int newButtonRobot;
    private int newButtonInventory;
    private int newButtonPause;
    private String message = "";

    public HUDScreenKeys(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/ScreenKeys/BG");

        HUDArea hudArea = null;
        
        //donate
        hudArea = addArea(BUTTON_DONATE_X, BUTTON_DONATE_Y, BUTTON_DONATE_WIDTH, BUTTON_DONATE_HEIGHT, "donate");
        hudArea.setImage("HUD/Common/Donate");
        
        //help
        //hudArea = addArea(BUTTON_HELP_X, BUTTON_HELP_Y, BUTTON_HELP_WIDTH, BUTTON_HELP_HEIGHT, "help");
        //hudArea.setImage("HUD/Common/Help");
    }

    @Override
    public void settingsKey(int k, Character c) {
        if (k != KeyEvent.VK_ESCAPE
                && k != KeyEvent.VK_TAB
                && k != KeyEvent.VK_ENTER
                && k != KeyEvent.VK_0
                && k != KeyEvent.VK_1
                && k != KeyEvent.VK_2
                && k != KeyEvent.VK_3
                && k != KeyEvent.VK_4
                && k != KeyEvent.VK_5
                && k != KeyEvent.VK_6
                && k != KeyEvent.VK_7
                && k != KeyEvent.VK_8
                && k != KeyEvent.VK_9
                && k != KeyEvent.VK_SHIFT) {
            if (newButtonMoveRight == 0) {
                newButtonMoveRight = k;
                if (newButtonMoveLeft == k) {
                    newButtonMoveLeft = 0;
                }
                if (newButtonJump == k) {
                    newButtonJump = 0;
                }
                if (newButtonAction == k) {
                    newButtonAction = 0;
                }
                if (newButtonRobot == k) {
                    newButtonRobot = 0;
                }
                if (newButtonInventory == k) {
                    newButtonInventory = 0;
                }
                if (newButtonPause == k) {
                    newButtonPause = 0;
                }
            } else if (newButtonMoveLeft == 0) {
                newButtonMoveLeft = k;
                if (newButtonMoveRight == k) {
                    newButtonMoveRight = 0;
                }
                if (newButtonJump == k) {
                    newButtonJump = 0;
                }
                if (newButtonAction == k) {
                    newButtonAction = 0;
                }
                if (newButtonRobot == k) {
                    newButtonRobot = 0;
                }
                if (newButtonInventory == k) {
                    newButtonInventory = 0;
                }
                if (newButtonPause == k) {
                    newButtonPause = 0;
                }
            } else if (newButtonJump == 0) {
                newButtonJump = k;
                if (newButtonMoveRight == k) {
                    newButtonMoveRight = 0;
                }
                if (newButtonMoveLeft == k) {
                    newButtonMoveLeft = 0;
                }
                if (newButtonAction == k) {
                    newButtonAction = 0;
                }
                if (newButtonRobot == k) {
                    newButtonRobot = 0;
                }
                if (newButtonInventory == k) {
                    newButtonInventory = 0;
                }
                if (newButtonPause == k) {
                    newButtonPause = 0;
                }
            } else if (newButtonAction == 0) {
                newButtonAction = k;
                if (newButtonMoveRight == k) {
                    newButtonMoveRight = 0;
                }
                if (newButtonMoveLeft == k) {
                    newButtonMoveLeft = 0;
                }
                if (newButtonJump == k) {
                    newButtonJump = 0;
                }
                if (newButtonRobot == k) {
                    newButtonRobot = 0;
                }
                if (newButtonInventory == k) {
                    newButtonInventory = 0;
                }
                if (newButtonPause == k) {
                    newButtonPause = 0;
                }
            } else if (newButtonRobot == 0) {
                newButtonRobot = k;
                if (newButtonMoveRight == k) {
                    newButtonMoveRight = 0;
                }
                if (newButtonMoveLeft == k) {
                    newButtonMoveLeft = 0;
                }
                if (newButtonJump == k) {
                    newButtonJump = 0;
                }
                if (newButtonAction == k) {
                    newButtonAction = 0;
                }
                if (newButtonInventory == k) {
                    newButtonInventory = 0;
                }
                if (newButtonPause == k) {
                    newButtonPause = 0;
                }
            } else if (newButtonInventory == 0) {
                newButtonInventory = k;
                if (newButtonMoveRight == k) {
                    newButtonMoveRight = 0;
                }
                if (newButtonMoveLeft == k) {
                    newButtonMoveLeft = 0;
                }
                if (newButtonJump == k) {
                    newButtonJump = 0;
                }
                if (newButtonAction == k) {
                    newButtonAction = 0;
                }
                if (newButtonRobot == k) {
                    newButtonRobot = 0;
                }
                if (newButtonPause == k) {
                    newButtonPause = 0;
                }
            } else if (newButtonPause == 0) {
                newButtonPause = k;
                if (newButtonMoveRight == k) {
                    newButtonMoveRight = 0;
                }
                if (newButtonMoveLeft == k) {
                    newButtonMoveLeft = 0;
                }
                if (newButtonJump == k) {
                    newButtonJump = 0;
                }
                if (newButtonAction == k) {
                    newButtonAction = 0;
                }
                if (newButtonRobot == k) {
                    newButtonRobot = 0;
                }
                if (newButtonInventory == k) {
                    newButtonInventory = 0;
                }
            } 

            checkIfDone();
        }
    }

    private void checkIfDone() {
        if (newButtonMoveRight != 0 && newButtonMoveLeft != 0 && newButtonJump != 0 && newButtonAction != 0 && newButtonRobot != 0 && newButtonInventory != 0 && newButtonPause != 0) {
            Settings.buttonMoveRight = newButtonMoveRight;
            Settings.buttonMoveLeft = newButtonMoveLeft;
            Settings.buttonJump = newButtonJump;
            Settings.buttonAction = newButtonAction;
            Settings.buttonRobot = newButtonRobot;
            Settings.buttonInventory = newButtonInventory;
            Settings.buttonPause = newButtonPause;
            hudManager.unloadHUD(name);
        }
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);
            if (hudArea == ha) {
                if (hudArea.getType().equals("donate")) {
                    String url = "http://epicinventor.com/donate.php";

                    try {
                        Desktop.getDesktop().browse(java.net.URI.create(url));
                    } catch (Exception e) {
                    }
                } else if (hudArea.getType().equals("help")) {
                    String url = "http://epicinventor.com/help";

                    try {
                        Desktop.getDesktop().browse(java.net.URI.create(url));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    @Override
    public void update() {
        if (shouldRender) {
            if (newButtonMoveRight == 0) {
                message = "Press the 'Move Right' key";
            } else if (newButtonMoveLeft == 0) {
                message = "Press the 'Move Left' key";
            } else if (newButtonJump == 0) {
                message = "Press the 'Jump' key";
            } else if (newButtonAction == 0) {
                message = "Press the 'Action' key";
            } else if (newButtonRobot == 0) {
                message = "Press the 'Toggle Robot' key";
            } else if (newButtonInventory == 0) {
                message = "Press the 'Toggle Inventory' key";
            } else if (newButtonPause == 0) {
                message = "Press the 'Pause' key";
            } else {
                message = "Saving Configuration...";
            }
        }

        super.update();
    }

    @Override
    public void render(Graphics g) {
        super.render(g);

        g.setColor(Color.white);
        g.setFont(new Font("SansSerif", Font.BOLD, 26));

        //center the text
        FontMetrics fm = g.getFontMetrics();
        int messageWidth = fm.stringWidth(message);
        int messageAscent = fm.getMaxAscent();
        int messageDescent = fm.getMaxDescent();
        int messageX = (hudManager.getPWidth() / 2) - (messageWidth / 2);
        int messageY = (hudManager.getPHeight() / 2) - (messageDescent / 2) + (messageAscent / 2);

        g.drawString(message, messageX, messageY - 100);
    }
}