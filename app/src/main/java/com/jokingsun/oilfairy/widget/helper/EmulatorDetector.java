package com.jokingsun.oilfairy.widget.helper;

import android.os.Build;

import com.orhanobut.logger.Logger;

import java.io.File;

/**
 * A known Emulator
 * <p>
 * Android Studio Emulator
 * Bluestacks
 * Genymotion
 * Nox
 * Droid4x
 * Andy
 * MEmu player
 *
 * @author cfd058
 */
public class EmulatorDetector {

    /**
     * Commonly used files for emulators
     */
    private final String[] genFilePaths = {"/dev/socket/geny", "/dev/socket/baseband_genyd"};
    private final String[] pipeFilePaths = {"/dev/socket/qemud", "/dev/qemu_pipe"};
    private final String[] x86FilePaths = {"ueventd.android_x86.rc", "x86.prop",
            "ueventd.ttVM_x86.rc", "init.ttVM_x86.rc", "fstab.ttVM_x86",
            "fstab.vbox86", "init.vbox86.rc", "ueventd.vbox86.rc"};
    private final String[] andyFilePaths = {"fstab.andy", "ueventd.andy.rc"};
    private final String[] noxFilePaths = {"fstab.nox", "init.nox.rc", "ueventd.nox.rc"};

    private final String[] buildConfigs = {
            "Genymotion", "google_sdk", "droid4x", "Emulator", "Android SDK built for x86",
            "goldfish", "vbox86", "nox", "generic", "sdk", "sdk_x86", "vbox86p"};

    /**
     * Analysis isEmulator
     */
    public boolean isEmulator() {
        boolean isEmulator = checkBuildConfig() || checkEmulatorFiles();
        Logger.d("EmulatorDetector:isEmulator-->" + isEmulator);

        return isEmulator;
    }

    /**
     * Check BuildConfig to determine whether it is an emulator
     *
     * @return isEmulator
     */
    private boolean checkBuildConfig() {
        /*
          to start with the detection, the easiest way is to check on the build values under BuildConfig.
          All these values can be accessed programmatically and most of the time they contain proof of
          the presence of an emulator.

          Build.MANUFACTURER
          Build.MODEL
          Build.HARDWARE
          Build.FINGERPRINT
          Build.BOARD
          Build.PRODUCT
         */

        try {
            return (Build.MANUFACTURER.contains(buildConfigs[0])
                    || Build.MODEL.contains(buildConfigs[1])
                    || Build.MODEL.toLowerCase().contains(buildConfigs[2])
                    || Build.MODEL.contains(buildConfigs[3])
                    || Build.MODEL.contains(buildConfigs[4])
                    || buildConfigs[5].equals(Build.HARDWARE)
                    || buildConfigs[6].equals(Build.HARDWARE)
                    || Build.HARDWARE.toLowerCase().contains(buildConfigs[7])
                    || Build.FINGERPRINT.startsWith(buildConfigs[8])
                    || buildConfigs[9].equals(Build.PRODUCT)
                    || buildConfigs[1].equals(Build.PRODUCT)
                    || buildConfigs[10].equals(Build.PRODUCT)
                    || buildConfigs[11].equals(Build.PRODUCT)
                    || Build.PRODUCT.toLowerCase().contains(buildConfigs[7])
                    || Build.BOARD.toLowerCase().contains(buildConfigs[7])
                    || (Build.BRAND.startsWith(buildConfigs[8]) && Build.DEVICE.startsWith(buildConfigs[8])));

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Advanced check if the device contains files commonly used by emulators
     *
     * @return whether to include emulator files
     */
    private boolean checkEmulatorFiles() {
        return checkFiles(blueStackFilePaths)
                || (checkFiles(genFilePaths)
                || checkFiles(andyFilePaths)
                || checkFiles(noxFilePaths)
                || checkFiles(x86FilePaths)
                || checkFiles(pipeFilePaths));
    }

    private boolean checkFiles(String[] targetFiles) {

        try {
            for (String targetFile : targetFiles) {
                File file = new File(targetFile);

                if (file.exists()) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            Logger.d("Emulator file lookup exception" + e.getMessage());
            return false;
        }
    }

    private final String[] blueStackFilePaths = {
            "/data/app/com.bluestacks.appmart-1.apk", "/data/app/com.bluestacks.BstCommandProcessor-1.apk",
            "/data/app/com.bluestacks.help-1.apk", "/data/app/com.bluestacks.home-1.apk",
            "/data/app/com.bluestacks.s2p-1.apk", "/data/app/com.bluestacks.searchapp-1.apk",
            "/data/bluestacks.prop", "/data/data/com.androVM.vmconfig", "/data/data/com.bluestacks.accelerometerui",
            "/data/data/com.bluestacks.appfinder", "/data/data/com.bluestacks.appmart",
            "/data/data/com.bluestacks.appsettings", "/data/data/com.bluestacks.BstCommandProcessor",
            "/data/data/com.bluestacks.bstfolder", "/data/data/com.bluestacks.help",
            "/data/data/com.bluestacks.home", "/data/data/com.bluestacks.s2p",
            "/data/data/com.bluestacks.searchapp", "/data/data/com.bluestacks.settings",
            "/data/data/com.bluestacks.setup", "/data/data/com.bluestacks.spotlight",
            "/mnt/prebundledapps/bluestacks.prop.orig"
    };

}
