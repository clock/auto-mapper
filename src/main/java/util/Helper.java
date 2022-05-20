package util;

import java.io.*;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Helper {
    public static File getMinecraftDirectory() {
        final String os;
        if ((os = System.getProperty("os.name").toLowerCase()).contains("win"))
            return new File(new File(System.getenv("APPDATA")), ".minecraft");
        if (os.contains("mac"))
            return new File(new File(System.getProperty("user.home")), "Library/Application Support/minecraft");
        if (os.contains("linux"))
            return new File(new File(System.getProperty("user.home")), ".minecraft/");
        throw new RuntimeException("Failed to determine Minecraft directory for OS: " + os);
    }

    public static boolean hasMc(String mcVer) {
        return new File(getMinecraftDirectory(), "versions/" + mcVer + "/" + mcVer + ".jar").exists();
    }

    // will extact a JarFile to the given directory
    public static void extractJar(File jar, File dest) throws IOException {
        if (!dest.exists())
            dest.mkdirs();
        try (JarFile jarFile = new JarFile(jar)) {
            for (JarEntry entry : Collections.list(jarFile.entries())) {
                if (entry.isDirectory())
                    continue;
                File file = new File(dest, entry.getName());
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                try (InputStream in = jarFile.getInputStream(entry);
                     OutputStream out = new FileOutputStream(file)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0)
                        out.write(buf, 0, len);
                }
            }
        }
    }
}