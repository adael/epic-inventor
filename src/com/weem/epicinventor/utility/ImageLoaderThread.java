package com.weem.epicinventor.utility;

public class ImageLoaderThread extends Thread {
    private boolean imagesLoaded = false;
    private static String configFile = "";
    private static ImageLoader imageLoader = null;

    public ImageLoaderThread(ImageLoader il, String cf) {
        configFile = cf;
        imageLoader = il;
        start();
    }

    public void run() {
        imageLoader.loadImagesFile(configFile);
        imagesLoaded = true;
    }

    public boolean getImagesLoaded() {
        return imagesLoaded;
    }
}
