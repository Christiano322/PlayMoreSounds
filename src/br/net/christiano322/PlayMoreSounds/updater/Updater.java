package br.net.christiano322.PlayMoreSounds.updater;

import br.net.christiano322.PlayMoreSounds.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.bukkit.*;
import org.json.simple.*;

public class Updater {

    private String pluginName = PMS.plugin.thisFile.getName();
    private String pluginVersion = PMS.plugin.pVersion;
    private Thread thread;
    private Thread downloadThread;
    private Thread redirectThread;
    private Thread downloadingThread;
    private String resourceId;
    private String updateLink;
    private String updateName;
    private String updateVersion;
    private URL fileUrl;
    private BufferedInputStream inn;
    private File updateFolderr;
    private FileOutputStream foutt;
    private UpdateCheckResult checkResult;
    private UpdateDownloadResult downloadResult;

    public enum UpdateCheckResult {
        AVAILABLE, FAIL, NOT_FOUND
		}

    public enum UpdateDownloadResult {
        SUCCESS, ERROR
		}

    public UpdateCheckResult getCheckResult() {
        threadStarting();
        return checkResult;
    }

    public UpdateDownloadResult getDownloadResult() {
        threadDownload();
        threadRedirect();
        return downloadResult;
    }

    public String getUpdateLink() {
        threadStarting();
        return updateLink;
    }

    public String getUpdateName() {
        threadStarting();
        return updateName;
    }

    public String getUpdateVersion() {
        threadStarting();
        return updateVersion;
    }

    public Updater(String resourceID, boolean download) {
        this.resourceId = resourceID;
        this.thread = new Thread(new UpdaterRunnable());
        this.thread.start();
        if (download) {
            if (getCheckResult().equals(UpdateCheckResult.AVAILABLE)) {
                this.downloadThread = new Thread(new DownloadRunnable());
                this.downloadThread.start();
            }
        }
    }

    private int addZeros(int valueToAddZeros, int valueToCompare) {
        while (String.valueOf(valueToCompare).length() > String.valueOf(valueToAddZeros).length()) {
            valueToAddZeros = valueToAddZeros * 10;
        }
        return valueToAddZeros;
    }

    private class UpdaterRunnable implements Runnable {
        @Override
        public void run() {
            start();
        }
    }

    private class DownloadRunnable implements Runnable {
        @Override
        public void run() {
            download();
        }
    }

    private class RedirectRunnable implements Runnable {
        @Override
        public void run() {
            try {
                getFileUrl();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadingRunnable implements Runnable {
        @Override
        public void run() {
            try {
                downloading(inn, foutt, updateFolderr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void start() {
        try {
            final HttpURLConnection conn = (HttpURLConnection) new URL(
				"https://api.curseforge.com/servermods/files?projectIds=" + resourceId).openConnection();
            conn.setConnectTimeout(5000);
            conn.addRequestProperty("User-Agent", "Updater (PlayMoreSounds)");
            conn.setDoOutput(true);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();

            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.isEmpty()) {
                checkResult = UpdateCheckResult.FAIL;
            }

            JSONObject latestUpdate = (JSONObject) array.get(array.size() - 1);
            updateLink = (String) latestUpdate.get("downloadUrl");
            updateName = (String) latestUpdate.get("name");
            updateVersion = updateName.replace("PlayMoreSounds v", "");
            Integer uVersion = addZeros(Integer.parseInt(updateVersion.replaceAll("\\.", "")),
										Integer.parseInt(pluginVersion.replaceAll("\\.", "")));
            Integer pVersion = addZeros(Integer.parseInt(pluginVersion.replaceAll("\\.", "")),
										Integer.parseInt(updateVersion.replaceAll("\\.", "")));
            if (uVersion > pVersion) {
                this.checkResult = UpdateCheckResult.AVAILABLE;
            } else {
                this.checkResult = UpdateCheckResult.NOT_FOUND;
            }
        } catch (final Exception e) {
            e.printStackTrace();
            this.checkResult = UpdateCheckResult.FAIL;
        }
    }

    public void download() {
        try {
            File updateFolder = new File("plugins", Bukkit.getUpdateFolder());
            if (!updateFolder.exists()) {
                updateFolder.mkdir();
            }
            List<File> files = Arrays.asList(updateFolder.listFiles());
            if (files.contains(new File(updateFolder, pluginName))) {
                new File(updateFolder, pluginName).delete();
            }
            threadDownload();
            this.redirectThread = new Thread(new RedirectRunnable());
            this.redirectThread.start();
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            threadRedirect();
            this.inn = in;
            this.foutt = fout;
            this.updateFolderr = updateFolder;
            this.downloadingThread = new Thread(new DownloadingRunnable());
            this.downloadingThread.start();
            threadDownloading();
            this.downloadResult = UpdateDownloadResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            this.downloadResult = UpdateDownloadResult.ERROR;
        }
    }

    private void downloading(BufferedInputStream in, FileOutputStream fout, File updateFolder) throws IOException {
        try {
            in = new BufferedInputStream(fileUrl.openStream());
            fout = new FileOutputStream(updateFolder + System.getProperty("file.separator") + pluginName);
            final byte[] data = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.downloadResult = UpdateDownloadResult.ERROR;
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    private void threadStarting() {
        if ((this.thread != null) && this.thread.isAlive()) {
            try {
                this.thread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void threadDownloading() {
        if ((this.downloadingThread != null) && this.downloadingThread.isAlive()) {
            try {
                this.downloadingThread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void threadDownload() {
        if ((this.downloadThread != null) && this.downloadThread.isAlive()) {
            try {
                this.downloadThread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void threadRedirect() {
        if ((this.redirectThread != null) && this.redirectThread.isAlive()) {
            try {
                this.redirectThread.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void getFileUrl() throws IOException {
        String location = this.updateLink;
        URL resourceUrl, base, next;
        HttpURLConnection conn;
        String redLoc;
        while (true) {
            resourceUrl = new URL(location);
            conn = (HttpURLConnection) resourceUrl.openConnection();

            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-Agent", "Updater (PlayMoreSounds)");

            switch (conn.getResponseCode()) {
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_MOVED_TEMP:
                    redLoc = conn.getHeaderField("Location");
                    base = new URL(location);
                    next = new URL(base, redLoc);
                    location = next.toExternalForm();
                    continue;
            }
            break;
        }
        this.fileUrl = conn.getURL();
    }
}
