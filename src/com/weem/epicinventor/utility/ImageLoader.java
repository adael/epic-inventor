package com.weem.epicinventor.utility;

import com.weem.epicinventor.*;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.color.ColorSpace;

public class ImageLoader extends Thread {

    private static HashMap imagesMap;
    private static HashMap gNamesMap;
    private GraphicsConfiguration gc;
    private long period;
    private static String CONFIG_FILE = "Images.dat";

    public ImageLoader(long p) {
        period = p;
        initLoader();
    }

    public long getPeriod() {
        return period;
    }

    private void initLoader() {
        imagesMap = new HashMap();
        gNamesMap = new HashMap();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
    }

    public void loadImagesFile(String fnm) /* Formats:
    o <fnm>                     // a single image
    n <fnm*.ext> <number>       // a numbered sequence of images
    s <fnm> <number>            // an images strip
    g <name> <fnm> [ <fnm> ]*   // a group of images 
    
    and blank lines and comment lines.
     */ {
        char ch;
        String line;
        String test = GameController.CONFIG_DIR + fnm;

        try {
            InputStream in = getClass().getResourceAsStream(GameController.CONFIG_DIR + fnm);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }

                ch = Character.toLowerCase(line.charAt(0));
                if (ch == 'o') {
                    getFileNameImage(line);
                } else if (ch == 'n') {
                    getNumberedImages(line);
                } else if (ch == 's') {
                    getStripImages(line);
                } else if (ch == 'g') {
                    getGroupImages(line);
                } else {
                    EIError.debugMsg("Do not recognize line: " + line, EIError.ErrorLevel.Error);
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void getFileNameImage(String line) {
        StringTokenizer tokens = new StringTokenizer(line);

        if (tokens.countTokens() == 2) {
            tokens.nextToken();    // skip command label
            loadSingleImage(GameController.IMAGES_DIR + tokens.nextToken(), "", 1.0f, (short) -1, (short) -1, (short) -1);
        } else if (tokens.countTokens() == 7) {
            tokens.nextToken();    // skip command label
            String name = tokens.nextToken();
            String img = tokens.nextToken();
            float transperancy = Float.valueOf(tokens.nextToken());
            short red = Short.valueOf(tokens.nextToken());
            short green = Short.valueOf(tokens.nextToken());
            short blue = Short.valueOf(tokens.nextToken());
            loadSingleImage(GameController.IMAGES_DIR + img, name, transperancy, red, green, blue);
        } else {
            EIError.debugMsg("Wrong no. of arguments for " + line, EIError.ErrorLevel.Error);
        }
    }

    public boolean loadSingleImage(String fnm, String name, float transperancy, short red, short green, short blue) {
        if (name.equals("")) {
            name = getPrefix(fnm);
        }

        if (imagesMap.containsKey(name)) {
            EIError.debugMsg("Error: " + name + "already used", EIError.ErrorLevel.Error);
            return false;
        }

        BufferedImage bi = loadImage(fnm);
        bi = changeTransperancy(bi, transperancy);
        if (red > -1 && green > -1 && blue > -1) {
            bi = changeColor(bi, red, green, blue);
        }
        if (bi != null) {
            ArrayList imsList = new ArrayList();
            imsList.add(bi);
            imagesMap.put(name.replace("/Images/", ""), imsList);
            String imageName[] = name.split("/");
            Game.loadingText = "Loading (" + imageName[imageName.length - 1] + ")";
            return true;
        } else {
            return false;
        }
    }

    private String getPrefix(String fnm) {
        int pos;
        if ((pos = fnm.lastIndexOf(".")) == -1) {
            EIError.debugMsg("No prefix found for filename: ", EIError.ErrorLevel.Error);
            return fnm;
        } else {
            return fnm.substring(0, pos);
        }
    }

    private void getNumberedImages(String line) {
        StringTokenizer tokens = new StringTokenizer(line);

        if (tokens.countTokens() != 3) {
            EIError.debugMsg("Wrong no. of arguments for " + line, EIError.ErrorLevel.Error);
        } else {
            tokens.nextToken();    // skip command label

            String fnm = tokens.nextToken();
            int number = -1;
            try {
                number = Integer.parseInt(tokens.nextToken());
            } catch (Exception e) {
                EIError.debugMsg("Number is incorrect for " + line, EIError.ErrorLevel.Error);
            }

            loadNumImages(GameController.IMAGES_DIR + fnm, number);
        }
    }

    public int loadNumImages(String fnm, int number) {
        String prefix = null;
        String postfix = null;
        int starPosn = fnm.lastIndexOf("*");   // find the '*'
        if (starPosn == -1) {
            EIError.debugMsg("No '*' in filename: " + fnm, EIError.ErrorLevel.Error);
            prefix = getPrefix(fnm);
        } else {   // treat the fnm as prefix + "*" + postfix
            prefix = fnm.substring(0, starPosn);
            postfix = fnm.substring(starPosn + 1);
        }

        if (imagesMap.containsKey(prefix)) {
            EIError.debugMsg("Error: " + prefix + "already used", EIError.ErrorLevel.Error);
            return 0;
        }

        return loadNumImages(prefix, postfix, number);
    }

    private int loadNumImages(String prefix, String postfix, int number) {
        String imFnm;
        BufferedImage bi;
        ArrayList imsList = new ArrayList();
        int loadCount = 0;

        if (number <= 0) {
            EIError.debugMsg("Error: Number <= 0: ", EIError.ErrorLevel.Error);
            imFnm = prefix + postfix;
            if ((bi = loadImage(imFnm)) != null) {
                loadCount++;
                imsList.add(bi);
            }
        } else {   // load prefix + <i> + postfix, where i = 0 to <number-1>
            for (int i = 0; i < number; i++) {
                imFnm = prefix + i + postfix;
                if ((bi = loadImage(imFnm)) != null) {
                    loadCount++;
                    imsList.add(bi);
                }
            }
        }

        if (loadCount == 0) {
            EIError.debugMsg("No images loaded for " + prefix, EIError.ErrorLevel.Warning);
        } else {
            imagesMap.put(prefix.replace("/Images/", ""), imsList);
        }

        return loadCount;
    }

    private void getStripImages(String line) {
        StringTokenizer tokens = new StringTokenizer(line);

        if (tokens.countTokens() != 3) {
            
            EIError.debugMsg("Wrong no. of arguments for ", EIError.ErrorLevel.Error);
        } else {
            tokens.nextToken();    // skip command label

            String fnm = tokens.nextToken();
            int number = -1;
            try {
                number = Integer.parseInt(tokens.nextToken());
            } catch (Exception e) {
                EIError.debugMsg("Number is incorrect for " + line, EIError.ErrorLevel.Error);
            }

            loadStripImages(GameController.IMAGES_DIR + fnm, number);
        }
    }

    public int loadStripImages(String fnm, int number) {
        String name = getPrefix(fnm);
        if (imagesMap.containsKey(name)) {
            EIError.debugMsg("Error: " + name + "already used", EIError.ErrorLevel.Error);
            return 0;
        }

        BufferedImage[] strip = loadStripImageArray(fnm, number);
        if (strip == null) {
            return 0;
        }

        ArrayList imsList = new ArrayList();
        int loadCount = 0;
        for (int i = 0; i < strip.length; i++) {
            loadCount++;
            imsList.add(strip[i]);
        }

        if (loadCount == 0) {
            EIError.debugMsg("No images loaded for " + name, EIError.ErrorLevel.Warning);
        } else {
            imagesMap.put(name.replace("/Images/", ""), imsList);
            String imageName[] = name.split("/");
            Game.loadingText = "Loading (" + imageName[imageName.length - 1] + ")";
        }

        return loadCount;
    }

    private void getGroupImages(String line) {
        StringTokenizer tokens = new StringTokenizer(line);

        if (tokens.countTokens() < 3) {
            EIError.debugMsg("Wrong no. of arguments for " + line, EIError.ErrorLevel.Error);
        } else {
            tokens.nextToken();    // skip command label

            String name = tokens.nextToken();

            ArrayList fnms = new ArrayList();
            fnms.add(tokens.nextToken());  // read filenames
            while (tokens.hasMoreTokens()) {
                fnms.add(tokens.nextToken());
            }

            loadGroupImages(GameController.IMAGES_DIR + name, fnms);
        }
    }

    public int loadGroupImages(String name, ArrayList fnms) {
        if (imagesMap.containsKey(name)) {
            EIError.debugMsg("Error: " + name + "already used", EIError.ErrorLevel.Error);
            return 0;
        }

        if (fnms.isEmpty()) {
            EIError.debugMsg("List of filenames is empty", EIError.ErrorLevel.Error);
            return 0;
        }

        BufferedImage bi;
        ArrayList nms = new ArrayList();
        ArrayList imsList = new ArrayList();
        String nm, fnm;
        int loadCount = 0;

        for (int i = 0; i < fnms.size(); i++) {    // load the files
            fnm = (String) fnms.get(i);
            nm = getPrefix(fnm);
            if ((bi = loadImage(fnm)) != null) {
                loadCount++;
                imsList.add(bi);
                nms.add(nm);
            }
        }

        if (loadCount == 0) {
            EIError.debugMsg("No images loaded for " + name, EIError.ErrorLevel.Warning);
        } else {
            imagesMap.put(name.replace("/Images/", ""), imsList);
            gNamesMap.put(name.replace("/Images/", ""), nms);
        }

        return loadCount;
    }

    public int loadGroupImages(String name, String[] fnms) {
        ArrayList al = new ArrayList(Arrays.asList(fnms));
        return loadGroupImages(name, al);
    }

    // ------------------ access methods -------------------
    public BufferedImage getImage(String name) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            //EIError.debugMsg("No image(s) stored under " + name, EIError.ErrorLevel.Warning);
            return null;
        }

        return (BufferedImage) imsList.get(0);
    }

    public BufferedImage getImage(String name, int pos) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            ////EIError.debugMsg("No image(s) stored under " + name, EIError.ErrorLevel.Warning);
            return null;
        }

        int size = imsList.size();
        if (pos < 0) {
            return (BufferedImage) imsList.get(0);
        } else if (pos >= size) {
            int newPos = pos % size;   // modulo
            return (BufferedImage) imsList.get(newPos);
        }

        return (BufferedImage) imsList.get(pos);
    }

    public BufferedImage getImage(String name, String fnmPrefix) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            //EIError.debugMsg("No image(s) stored under " + name, EIError.ErrorLevel.Warning);
            return null;
        }

        int posn = getGroupPosition(name, fnmPrefix);
        if (posn < 0) {
            return (BufferedImage) imsList.get(0);
        }

        return (BufferedImage) imsList.get(posn);
    }

    private int getGroupPosition(String name, String fnmPrefix) {
        ArrayList groupNames = (ArrayList) gNamesMap.get(name);
        if (groupNames == null) {
            EIError.debugMsg("No group names for " + name, EIError.ErrorLevel.Error);
            return -1;
        }

        String nm;
        for (int i = 0; i < groupNames.size(); i++) {
            nm = (String) groupNames.get(i);
            if (nm.equals(fnmPrefix)) {
                return i;
            }
        }

        EIError.debugMsg("No " + fnmPrefix + " group name found for " + name, EIError.ErrorLevel.Error);
        return -1;
    }

    public ArrayList getImages(String name) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            //EIError.debugMsg("No image(s) stored under " + name, EIError.ErrorLevel.Warning);
            return null;
        }

        EIError.debugMsg("Returning all images stored under " + name, EIError.ErrorLevel.Error);
        return imsList;
    }

    public boolean isLoaded(String name) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            return false;
        }
        return true;
    }

    public int numImages(String name) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            //EIError.debugMsg("No image(s) stored under " + name, EIError.ErrorLevel.Warning);
            return 0;
        }
        return imsList.size();
    }

    public BufferedImage loadImage(String fnm) {
        try {
            BufferedImage im = ImageIO.read(ImageLoader.class.getResource(fnm));
            //ImageIO.read(Art.class.getResource(fileName));

            int transparency = im.getColorModel().getTransparency();
            BufferedImage copy = gc.createCompatibleImage(
                    im.getWidth(), im.getHeight(), transparency);

            Graphics2D g2d = copy.createGraphics();

            g2d.drawImage(im, 0, 0, null);
            g2d.dispose();
            return copy;
        } catch (IOException e) {
            EIError.debugMsg("Load Image error for " + fnm + ":\n" + e, EIError.ErrorLevel.Error);
            return null;
        }
    }

    private BufferedImage makeBIM(Image im, int width, int height) {
        BufferedImage copy = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = copy.createGraphics();

        g2d.drawImage(im, 0, 0, null);
        g2d.dispose();
        return copy;
    }

    public BufferedImage[] loadStripImageArray(String fnm, int number) {
        if (number <= 0) {
            EIError.debugMsg("number <= 0; returning null", EIError.ErrorLevel.Error);
            return null;
        }

        BufferedImage stripIm;
        if ((stripIm = loadImage(fnm)) == null) {
            EIError.debugMsg("Returning null", EIError.ErrorLevel.Error);
            return null;
        }

        int imWidth = (int) stripIm.getWidth() / number;
        int height = stripIm.getHeight();
        int transparency = stripIm.getColorModel().getTransparency();

        BufferedImage[] strip = new BufferedImage[number];
        Graphics2D stripGC;

        for (int i = 0; i < number; i++) {
            strip[i] = gc.createCompatibleImage(imWidth, height, transparency);

            stripGC = strip[i].createGraphics();

            stripGC.drawImage(stripIm,
                    0, 0, imWidth, height,
                    i * imWidth, 0, (i * imWidth) + imWidth, height,
                    null);
            stripGC.dispose();
        }
        return strip;
    }

    public static BufferedImage changeTransperancy(BufferedImage loaded, float transperancy) {
        BufferedImage img = new BufferedImage(loaded.getWidth(), loaded.getHeight(), BufferedImage.TRANSLUCENT);
        Graphics2D g = img.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transperancy));
        g.drawImage(loaded, null, 0, 0);
        g.dispose();

        return img;
    }

    public static BufferedImage changeToGrayscale(BufferedImage source) {
        BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        return op.filter(source, null);
    }

    public static BufferedImage changeColor(BufferedImage img, short R1, short G1, short B1) {
        BufferedImageOp colorizeFilter = createColorizeOp(R1, G1, B1);
        img = colorizeFilter.filter(img, null);

        return img;
    }

    public static LookupOp createColorizeOp(short R1, short G1, short B1) {
        short[] alpha = new short[256];
        short[] red = new short[256];
        short[] green = new short[256];
        short[] blue = new short[256];

        for (short i = 0; i < 256; i++) {
            alpha[i] = i;
            red[i] = (short) ((R1 + i * 0.3) / 2);
            green[i] = (short) ((G1 + i * 0.59) / 2);
            blue[i] = (short) ((B1 + i * 0.11) / 2);
        }

        short[][] data = new short[][]{
            red, green, blue, alpha
        };

        LookupTable lookupTable = new ShortLookupTable(0, data);
        return new LookupOp(lookupTable, null);
    }
}