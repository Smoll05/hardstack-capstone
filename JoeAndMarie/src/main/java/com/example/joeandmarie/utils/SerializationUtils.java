package com.example.joeandmarie.utils;

import com.example.joeandmarie.data.model.GameProgress;

import java.io.*;

public class SerializationUtils {
    public static void serialize(String path, GameProgress object) {
        try(FileOutputStream file = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(file)) {
            oos.writeObject(object);

            System.out.println("Serialized Game Progress Object : id " + object.getGameProgressId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameProgress deserialize(String path) {
        try(FileInputStream file = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(file)) {
            return (GameProgress) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
