package com.kakaouo.mochi.utils;

import java.io.File;
import java.util.Objects;

public enum Utils {
    ;

    private static File userSetRootDirectory = null;
    private static String defaultRootDirName = "run";

    public static void setRootDirectory(File file) {
        userSetRootDirectory = file;
    }

    public static String getDefaultRootDirName() {
        return defaultRootDirName;
    }

    public static void setDefaultRootDirName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        }
        defaultRootDirName = name;
    }

    public static File getDefaultRootDirectory() {
        if (isRunningInIDE()) {
            var dir = new File(defaultRootDirName + "/");
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new RuntimeException("Root directory cannot be created");
                }
            }
            return dir;
        } else {
            // Return the current path
            return new File("");
        }
    }

    public static File getRootDirectory() {
        return Objects.requireNonNullElseGet(
            userSetRootDirectory,
            Utils::getDefaultRootDirectory);
    }

    public static boolean isRunningInIDE() {
        try {
            Class.forName("com.intellij.rt.execution.application.AppMainV2");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}