package com.heapoverflow.cli.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.time.Duration;

public class SystemUtils {
    public static void openBrowser(String url) throws IOException {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("Please open the following URL in your browser:\n" + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getOsName(){
        return System.getProperty("os.name");
    }

    public static String getOsVersion(){
        return System.getProperty("os.version");
    }

    public static String getJavaVersion(){
        Runtime.Version version = Runtime.version();
        return version.toString();
    }

    public static String getUptime(){
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        return formatDuration(Duration.ofMillis(uptimeMillis));
    }

    public static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }
}
