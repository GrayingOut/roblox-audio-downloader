package me.grayingout;

import java.io.File;

/**
 * The main class
 */
public class App {
    
    /**
     * The main method
     * 
     * @param args Command line arguments
     * @throws Exception If an exception occurs
     */
    public static void main(String[] args) throws Exception {
        /* Download the audio file from the provided id on the command line */
        File f = RobloxAudioDownloader.download(Long.parseLong(args[0]));

        /* Print the location of the downloaded file */
        System.out.println(f.getAbsolutePath());
    }
}
