package com.weem.epicinventor.hud;

import com.weem.epicinventor.*;
import com.weem.epicinventor.actor.*;
import com.weem.epicinventor.placeable.*;
import com.weem.epicinventor.utility.*;

import java.awt.*;
import java.util.ArrayList;

public class HUDScreenSettings extends HUD {

    private final static int TITLE_WIDTH = 420;
    private final static int TITLE_HEIGHT = 37;
    private final static int TITLE_X = 188;
    private final static int TITLE_Y = 110;
    private final static int BUTTON_ARROW_WIDTH = 29;
    private final static int BUTTON_ARROW_HEIGHT = 46;
    private final static int BUTTON_ARROW_LEFT_X = 258;
    private final static int BUTTON_ARROW_LEFT_Y = 155;
    private final static int BUTTON_ARROW_RIGHT_X = 527;
    private final static int BUTTON_ARROW_RIGHT_Y = 155;
    private final static int BUTTON_SAVE_WIDTH = 232;
    private final static int BUTTON_SAVE_HEIGHT = 52;
    private final static int BUTTON_SAVE_X = 290;
    private final static int BUTTON_SAVE_Y = 480;
    private final static int BUTTON_KEYS_WIDTH = 232;
    private final static int BUTTON_KEYS_HEIGHT = 52;
    private final static int BUTTON_KEYS_X = 290;
    private final static int BUTTON_KEYS_Y = 415;
    private final static int LABEL_RESOLUTION_WIDTH = 124;
    private final static int LABEL_RESOLUTION_HEIGHT = 18;
    private final static int LABEL_RESOLUTION_X = 344;
    private final static int LABEL_RESOLUTION_Y = 123;
    private final static int LABEL_MUSIC_WIDTH = 160;
    private final static int LABEL_MUSIC_HEIGHT = 18;
    private final static int LABEL_MUSIC_X = 327;
    private final static int LABEL_MUSIC_Y = 123;
    private final static int LABEL_SFX_WIDTH = 208;
    private final static int LABEL_SFX_HEIGHT = 19;
    private final static int LABEL_SFX_X = 302;
    private final static int LABEL_SFX_Y = 123;
    private final static int RESOLUTION_WIDTH = 232;
    private final static int RESOLUTION_HEIGHT = 52;
    private final static int RESOLUTION_X = 291;
    private final static int RESOLUTION_Y = 151;
    private final static int RESOLUTION_TEXT_X = 50;
    private final static int RESOLUTION_TEXT_Y = 35;
    private final static int JDK_WIDTH = 327;
    private final static int JDK_HEIGHT = 114;
    private final static int JDK_X = 234;
    private final static int JDK_Y = 252;
    private final static int SPACER = 100;
    private final static int SPACER2 = 200;
    private final static int BUTTON_DONATE_WIDTH = 119;
    private final static int BUTTON_DONATE_HEIGHT = 102;
    private final static int BUTTON_DONATE_X = 33;
    private final static int BUTTON_DONATE_Y = 155;
    private final static int BUTTON_HELP_WIDTH = 119;
    private final static int BUTTON_HELP_HEIGHT = 102;
    private final static int BUTTON_HELP_X = 646;
    private final static int BUTTON_HELP_Y = 155;
    private ArrayList resolutions;
    private int currentResolution;
    private float currentVolumeMusic;
    private float currentVolumeFX;
    private boolean allowSoundChanges;

    public HUDScreenSettings(HUDManager hm, Registry rg, int x, int y, int w, int h) {
        super(hm, rg, x, y, w, h);

        setImage("HUD/ScreenSettings/BG");

        if (!(GameController.props.getProperty("os.name").contains("Linux") && GameController.props.getProperty("java.runtime.name").contains("OpenJDK"))) {
            allowSoundChanges = true;
        } else {
            allowSoundChanges = false;
        }

        HUDArea hudArea = null;

        //title
        hudArea = addArea(TITLE_X, TITLE_Y, TITLE_WIDTH, TITLE_HEIGHT, "title");
        hudArea.setImage("HUD/ScreenSettings/TitleCredits");

        //resolution
        hudArea = addArea(LABEL_RESOLUTION_X, LABEL_RESOLUTION_Y, LABEL_RESOLUTION_WIDTH, LABEL_RESOLUTION_HEIGHT, "label_resolution");
        hudArea.setImage("HUD/ScreenSettings/LabelResolution");
        hudArea = addArea(BUTTON_ARROW_LEFT_X, BUTTON_ARROW_LEFT_Y, BUTTON_ARROW_WIDTH, BUTTON_ARROW_HEIGHT, "resolution_left");
        hudArea.setImage("HUD/ScreenSettings/ButtonArrowLeft");
        hudArea = addArea(BUTTON_ARROW_RIGHT_X, BUTTON_ARROW_RIGHT_Y, BUTTON_ARROW_WIDTH, BUTTON_ARROW_HEIGHT, "resolution_right");
        hudArea.setImage("HUD/ScreenSettings/ButtonArrowRight");
        hudArea = addArea(RESOLUTION_X, RESOLUTION_Y, RESOLUTION_WIDTH, RESOLUTION_HEIGHT, "resolution");
        hudArea.setImage("HUD/ScreenSettings/BGResolution");
        hudArea.setFont("SansSerif", Font.BOLD, 28);
        hudArea.setTextXY(RESOLUTION_TEXT_X, RESOLUTION_TEXT_Y);

        if (allowSoundChanges) {
            //music
            hudArea = addArea(LABEL_MUSIC_X, LABEL_MUSIC_Y + SPACER, LABEL_MUSIC_WIDTH, LABEL_MUSIC_HEIGHT, "label_music");
            hudArea.setImage("HUD/ScreenSettings/LabelMusicVolume");
            hudArea = addArea(BUTTON_ARROW_LEFT_X, BUTTON_ARROW_LEFT_Y + SPACER, BUTTON_ARROW_WIDTH, BUTTON_ARROW_HEIGHT, "music_left");
            hudArea.setImage("HUD/ScreenSettings/ButtonArrowLeft");
            hudArea = addArea(BUTTON_ARROW_RIGHT_X, BUTTON_ARROW_RIGHT_Y + SPACER, BUTTON_ARROW_WIDTH, BUTTON_ARROW_HEIGHT, "music_right");
            hudArea.setImage("HUD/ScreenSettings/ButtonArrowRight");
            hudArea = addArea(RESOLUTION_X, RESOLUTION_Y + SPACER, RESOLUTION_WIDTH, RESOLUTION_HEIGHT, "music");
            hudArea.setImage("HUD/ScreenSettings/BGResolution");
            hudArea.setFont("SansSerif", Font.BOLD, 28);
            hudArea.setTextXY(RESOLUTION_TEXT_X + 52, RESOLUTION_TEXT_Y);

            //sound
            hudArea = addArea(LABEL_SFX_X, LABEL_SFX_Y + SPACER2, LABEL_SFX_WIDTH, LABEL_SFX_HEIGHT, "label_sound");
            hudArea.setImage("HUD/ScreenSettings/LabelSoundFXVolume");
            hudArea = addArea(BUTTON_ARROW_LEFT_X, BUTTON_ARROW_LEFT_Y + SPACER2, BUTTON_ARROW_WIDTH, BUTTON_ARROW_HEIGHT, "sound_left");
            hudArea.setImage("HUD/ScreenSettings/ButtonArrowLeft");
            hudArea = addArea(BUTTON_ARROW_RIGHT_X, BUTTON_ARROW_RIGHT_Y + SPACER2, BUTTON_ARROW_WIDTH, BUTTON_ARROW_HEIGHT, "sound_right");
            hudArea.setImage("HUD/ScreenSettings/ButtonArrowRight");
            hudArea = addArea(RESOLUTION_X, RESOLUTION_Y + SPACER2, RESOLUTION_WIDTH, RESOLUTION_HEIGHT, "sound");
            hudArea.setImage("HUD/ScreenSettings/BGResolution");
            hudArea.setFont("SansSerif", Font.BOLD, 28);
            hudArea.setTextXY(RESOLUTION_TEXT_X + 52, RESOLUTION_TEXT_Y);
        } else {
            hudArea = addArea(JDK_X, JDK_Y, JDK_WIDTH, JDK_HEIGHT, "jdk");
            hudArea.setImage("HUD/ScreenSettings/JDK");
        }
        
        //keys
        hudArea = addArea(BUTTON_KEYS_X, BUTTON_KEYS_Y, BUTTON_KEYS_WIDTH, BUTTON_KEYS_HEIGHT, "keys");
        hudArea.setImage("HUD/ScreenSettings/ButtonKeys");

        //save
        hudArea = addArea(BUTTON_SAVE_X, BUTTON_SAVE_Y, BUTTON_SAVE_WIDTH, BUTTON_SAVE_HEIGHT, "save");
        hudArea.setImage("HUD/ScreenSettings/ButtonSave");

        //donate
        hudArea = addArea(BUTTON_DONATE_X, BUTTON_DONATE_Y, BUTTON_DONATE_WIDTH, BUTTON_DONATE_HEIGHT, "donate");
        hudArea.setImage("HUD/Common/Donate");

        //help
        //hudArea = addArea(BUTTON_HELP_X, BUTTON_HELP_Y, BUTTON_HELP_WIDTH, BUTTON_HELP_HEIGHT, "help");
        //hudArea.setImage("HUD/Common/Help");

        resolutions = new ArrayList();
        resolutions.add("800x600");
        resolutions.add("1024x768");
        resolutions.add("1152x864");
        resolutions.add("1280x720");
        resolutions.add("1280x768");
        resolutions.add("1280x800");
        resolutions.add("1280x960");
        resolutions.add("1280x1024");
        resolutions.add("1360x768");
        resolutions.add("1366x768");
        resolutions.add("1440x900");
        resolutions.add("1600x900");
        resolutions.add("1600x1024");
        resolutions.add("1680x1050");
        resolutions.add("1920x1080");
        resolutions.add("1920x1200");
        //resolutions.add("Full Screen");

        currentResolution = Settings.resolution;
        currentVolumeMusic = Settings.volumeMusic;
        currentVolumeFX = Settings.volumeFX;
    }

    @Override
    public void update() {
        if (shouldRender) {
            HUDArea hudArea;

            //update slots
            for (int i = 0; i < hudAreas.size(); i++) {
                hudArea = hudAreas.get(i);
                if (hudArea.getType().equals("resolution")) {
                    hudArea.setText(resolutions.get(currentResolution).toString());
                } else if (hudArea.getType().equals("music")) {
                    hudArea.setText(Integer.toString((int) currentVolumeMusic));
                } else if (hudArea.getType().equals("sound")) {
                    hudArea.setText(Integer.toString((int) currentVolumeFX));
                }
            }
        }

        super.update();
    }

    @Override
    public void HUDAreaClicked(HUDArea ha) {
        HUDArea hudArea = null;

        for (int i = 0; i < hudAreas.size(); i++) {
            hudArea = hudAreas.get(i);
            if (hudArea == ha) {
                if (hudArea.getType().equals("resolution_left")) {
                    currentResolution--;
                    if (currentResolution < 0) {
                        currentResolution = (resolutions.size() - 1);
                    }
                } else if (hudArea.getType().equals("resolution_right")) {
                    currentResolution++;
                    if (currentResolution > (resolutions.size() - 1)) {
                        currentResolution = 0;
                    }
                } else if (hudArea.getType().equals("music_right")) {
                    currentVolumeMusic++;
                    if (currentVolumeMusic > 10) {
                        currentVolumeMusic = 10;
                    }
                    hudManager.updateMusicVolume(currentVolumeMusic);
                } else if (hudArea.getType().equals("music_left")) {
                    currentVolumeMusic--;
                    if (currentVolumeMusic < 0) {
                        currentVolumeMusic = 0;
                    }
                    hudManager.updateMusicVolume(currentVolumeMusic);
                } else if (hudArea.getType().equals("sound_right")) {
                    currentVolumeFX++;
                    if (currentVolumeFX > 10) {
                        currentVolumeFX = 10;
                    }
                    SoundClip cl = new SoundClip("Projectile/Arrow", currentVolumeFX);
                } else if (hudArea.getType().equals("sound_left")) {
                    currentVolumeFX--;
                    if (currentVolumeFX < 0) {
                        currentVolumeFX = 0;
                    }
                    SoundClip cl = new SoundClip("Projectile/Arrow", currentVolumeFX);
                } else if (hudArea.getType().equals("keys")) {
                    hudManager.loadHUD(HUDManager.HUDType.ScreenKeys);
                } else if (hudArea.getType().equals("save")) {
                    Settings.resolution = currentResolution;
                    Settings.volumeMusic = (int) currentVolumeMusic;
                    Settings.volumeFX = (int) currentVolumeFX;
                    Settings.save();
                    hudManager.updateMusicVolume();
                    hudManager.unloadHUD(name);
                    hudManager.loadHUD(HUDManager.HUDType.ScreenMain);
                } else if (hudArea.getType().equals("jdk")) {
                    String url = "http://epicinventor.com/forum/index.php?topic=851.0";

                    try {
                        Desktop.getDesktop().browse(java.net.URI.create(url));
                    } catch (Exception e) {
                    }
                } else if (hudArea.getType().equals("donate")) {
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
}