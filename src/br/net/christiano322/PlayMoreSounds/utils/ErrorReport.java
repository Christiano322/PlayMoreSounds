package br.net.christiano322.PlayMoreSounds.utils;

import br.net.christiano322.PlayMoreSounds.*;
import java.io.*;
import java.text.*;

public class ErrorReport {

    public static void stringToFile(String data, File file, boolean date) throws IOException {
        FileWriter outStream = new FileWriter(file.getAbsolutePath(), true);
        BufferedWriter out = new BufferedWriter(outStream);
        data.replaceAll("\n", System.getProperty("line.separator"));
        if (date) {
            out.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Long.valueOf(System.currentTimeMillis())));
            out.append(System.getProperty("line.separator"));
        }
        out.append(data);
        if (date) {
            out.append(System.getProperty("line.separator"));
        }
        out.close();
    }

    static String getStackTraceToString(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }

    public static void errorReport(Exception e, String name) {
        try {
            File errorFile = new File(PMS.plugin.getDataFolder(), "ERROR.LOG");
            String error = "=====================================================================\n>> Please report this file to Christiano322\n>> https://dev.bukkit.org/projects/playmoresounds/issues\n=====================================================================\n - PlayMoreSounds version: " + PMS.plugin.pVersion + "\n\n" + name + "\n" + getStackTraceToString(e);
            stringToFile(error, errorFile, true);
        } catch (IOException g) {
            g.printStackTrace();
        }
    }
}
