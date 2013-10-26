package com.weem.epicinventor.network;

import java.io.*;

public class UpdateResource implements Serializable {
    
    protected static final long serialVersionUID = 10000L;
    public String id = "";
    public String action = "None";
    
    public UpdateResource(String pid) {
        id = pid;
    }

    private void readObject(ObjectInputStream aInputStream) throws Exception {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
        aOutputStream.defaultWriteObject();
    }
}
