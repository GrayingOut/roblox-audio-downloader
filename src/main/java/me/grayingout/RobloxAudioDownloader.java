package me.grayingout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * This class is used to download audio from Roblox
 */
public final class RobloxAudioDownloader {

    /**
     * Download a specific audio from its id
     * 
     * @param audioId
     * @throws RobloxAudioDownloadException If a download exception occurs
     * @throws IOException If an I/O exception occurs
     */
    public static final File download(long audioId) throws RobloxAudioDownloadException, IOException {
        /* Get the URL, download the file, and return it */
        String url = getDownloadURL(audioId);
        
        File file = downloadAudio(url);

        return file;
    }

    /**
     * Downloads the audio data from the URL to a file
     * 
     * @param url The audio URL
     * @return The file
     */
    private final static File downloadAudio(String url) throws RobloxAudioDownloadException, IOException {
        /* Open HttpURLConnection */
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.connect();

        /* Check success */
        int code = con.getResponseCode();
        if (code != 200) {
            throw new RobloxAudioDownloadException("Failed to download: Check the audio id and try again");
        }

        /* Download the audio file */
        InputStream in = con.getInputStream();

        File file = new File(System.getProperty("user.home") + "/Downloads/Roblox-Download-" + UUID.randomUUID() + ".mp3");
        FileOutputStream out = new FileOutputStream(file);

        byte[] buf = new byte[4096]; /* 4K buffer */

        while (in.read(buf, 0, 4096) != -1) {
            out.write(buf);
        }

        out.close();

        /* Return the downloaded file */
        return file;
    }

    /**
     * Gets the download URL for an audio
     * 
     * @param audioId The audio id
     * @return The URL
     */
    private final static String getDownloadURL(long audioId) throws RobloxAudioDownloadException, IOException {
        /* Open HttpURLConnection */
        HttpURLConnection con = (HttpURLConnection) new URL("https://www.roblox.com/library/" + audioId).openConnection();
        con.connect();

        /* Check success */
        int code = con.getResponseCode();
        if (code != 200) {
            throw new RobloxAudioDownloadException("Failed to download: Check the audio id and try again");
        }

        /* Download the webpage */
        StringBuilder response = new StringBuilder();

        InputStream in = con.getInputStream();
        byte[] buf = new byte[4096];

        while (in.read(buf) != -1) {
            response.append(new String(buf));
        }
        
        con.disconnect();

        /* Get the element */
        Document doc = Jsoup.parse(response.toString());
        Element element = doc.select("div[data-mediathumb-url]").first();

        if (element == null) {
            throw new RobloxAudioDownloadException("Failed to download. Are you sure this asset is audio and is public?");
        }

        /* Get the URL */
        String url = element.attr("data-mediathumb-url");
        
        /* Return the URL */
        return url;
    }

    /**
     * A generic exception for errors while downloading a roblox audio
     */
    static class RobloxAudioDownloadException extends Exception {

        /**
         * Constructs a new exception with the provided message
         * 
         * @param message The error message
         */
        public RobloxAudioDownloadException(String message) {
            super(message);
        }
    }
}
