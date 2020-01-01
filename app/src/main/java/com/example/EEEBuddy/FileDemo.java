package com.example.EEEBuddy;
import java.io.File;

public class FileDemo {
    public static void main(String[] args) {
        File f = null;

        try {
            // creates temporary file
            f = File.createTempFile("tmp", ".txt", new File("Macintosh/Users/wengyuejuan/Desktop"));

            // prints absolute path
            System.out.println("File path: "+f.getAbsolutePath());

            // deletes file when the virtual machine terminate
           // f.deleteOnExit();



        } catch(Exception e) {
            // if any error occurs
            e.printStackTrace();
        }
    }
}