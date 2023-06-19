package com.example.stockviewer_v2.watchList;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileWriterReaderSingleton {
    private static FileWriterReaderSingleton instance;


    private FileWriterReaderSingleton() {
        // Private constructor to prevent instantiation
    }

    public static synchronized FileWriterReaderSingleton getInstance() {
        if (instance == null) {
            instance = new FileWriterReaderSingleton();
        }
        return instance;
    }

    public synchronized void saveWatchListToFile(Context context, List<StockPriceRealTime> watchList) {
        try {
            FileOutputStream fos = context.openFileOutput("watchlist.dat", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(watchList);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<StockPriceRealTime> restoreWatchListFromFile(Context context) {
        List<StockPriceRealTime> watchList = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput("watchlist.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            watchList = (List<StockPriceRealTime>) ois.readObject();
            ois.close();
        }
        catch (FileNotFoundException e) {
            // File does not exist yet, return an empty watch list
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return watchList;
    }

}
