import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to download audio from Roblox
 */
public class RobloxAudioDownloader {

    /**
     * Download a specific audio from its id
     * 
     * @param audioId
     * @throws RobloxAudioDownloadException If a download exception occurs
     * @throws IOException If an I/O exception occurs
     */
    public static File download(long audioId) throws RobloxAudioDownloadException, IOException {
        // Get the URL
        String url = getDownloadURL(audioId);

        // Get the audio data
        File file = downloadAudio(url);

        // Return file
        return file;
    }

    /**
     * Downloads the audio data from the URL to a file
     * 
     * @param url The audio URL
     * @return The file
     */
    private static File downloadAudio(String url) throws RobloxAudioDownloadException, IOException {
        // Get HttpURLConnection
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        // Connect
        con.connect();

        // Get response code
        int code = con.getResponseCode();
        if (code != 200) {
            throw new RobloxAudioDownloadException("Failed to download: Check the audio id and try again");
        }

        // Get data
        InputStream in = con.getInputStream();

        // Create file
        String home = System.getProperty("user.home");
        File file = new File(home + "/Downloads/Roblox-Download-"+UUID.randomUUID()+".mp3");

        // Write to file
        FileOutputStream out = new FileOutputStream(file);

        byte[] buf = new byte[4096]; // Buffer of 4k
        while (in.read(buf, 0, 4096) != -1) {
            out.write(buf);
        }

        // Close file
        out.close();

        // Return file
        return file;
    }

    /**
     * Gets the download URL for an aduio
     * 
     * @param audioId The audio id
     * @return The URL
     */
    private static String getDownloadURL(long audioId) throws RobloxAudioDownloadException, IOException {
        // Get HttpURLConnection
        HttpURLConnection con = (HttpURLConnection) new URL("https://www.roblox.com/library/" + audioId).openConnection();
                
        // Connect
        con.connect();

        // Get response code
        int code = con.getResponseCode();
        if (code != 200) {
            throw new RobloxAudioDownloadException("Failed to download: Check the audio id and try again");
        }

        // Get data
        InputStream in = con.getInputStream();

        StringBuilder response = new StringBuilder();

        int ch = 0;
        while ((ch = in.read()) != -1) {
            response.append((char) ch);
        }

        // Find audio URL tag attribute
        Pattern attributePattern = Pattern.compile("data-mediathumb-url=\".+\"");
        Matcher attributeMatcher = attributePattern.matcher(response);

        if (!attributeMatcher.find()) {
            throw new RobloxAudioDownloadException("Failed to download. Are you sure this asset is audio and is public?");
        }

        // Get the attribute text
        String attribute = response.substring(attributeMatcher.start(), attributeMatcher.end());

        // Get the URL from the attribute
        String url = attribute.substring(attribute.indexOf("\"") + 1, attribute.length() - 1);
        
        // Disconnect
        con.disconnect();
        
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
