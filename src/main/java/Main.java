import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import util.Helper;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {

    public static String lunar_version_string;
    public static String version = "1.8.9";
    public static File mappingsFolder;
    public static File outputFolder;
    public static File inputFolder;
    public static File netDumpFolder;
    public static File dumpFolder;
    public static File lunarprodFile;
    public static File outputClassesFile;
    public static File outputFieldsFile;
    public static File outputMethodsFile;
    public static File errorsFile;
    public static File mcExtracted;
    public static File mcJarFile;

    public static void main(String[] args) throws IOException, InterruptedException {

        if (version.contains("1.8.9")) {
            lunar_version_string = "v1_8";
        } else if (version.contains("1.7.10")) {
            lunar_version_string = "v1_7";
        } else {
            System.out.println("Error: version is not 1.7 or 1.8");
            return;
        }

        System.out.println("hello world");

        // check if mappings folder exists in the root of the project
        // if not, create it
        mappingsFolder = new File("mappings/" + version);
        if (!mappingsFolder.exists()) {
            System.out.println("making mappings folder");
            mappingsFolder.mkdirs();
        }

        outputFolder = new File("output/" + version);
        if (!outputFolder.exists()) {
            System.out.println("making output folder");
            outputFolder.mkdirs();
        }

        inputFolder = new File("input/" + version);
        if (!inputFolder.exists()) {
            System.out.println("making input folder");
            inputFolder.mkdirs();
        }

        mcExtracted = new File("output/" + version + "/mc");
        if (!mcExtracted.exists()) {
            System.out.println("making mc folder");
            mcExtracted.mkdirs();
        }

        // check if "lunar-prod-optifine" file exists in the input folder
        // if not tell the user to put it there and exit
        lunarprodFile = new File("input/" + version + "/lunar-prod-optifine.jar");
        if (!lunarprodFile.exists()) {
            System.out.println("input file not found");
            System.out.println("please put the file lunar-prod-optifine in the input folder");
            System.exit(0);
        } 
        else
        {
            System.out.println("lunar-prod-optifine file found");
        }

        // check if mappings folder is empty, if it isn't empty, delete everything in it
        if (mappingsFolder.listFiles().length > 0) {
            for (File file : mappingsFolder.listFiles()) {
                System.out.println("deleted old " + file.getName() + " mapping");
                file.delete();
            }
        }

        System.out.println("downloading new mappings");
        MappingsDownloader.download(version, mappingsFolder);

        // make outputClasses.txt file in output folder
        outputClassesFile = new File("output/" + version + "/outputClasses.txt");
        if (outputClassesFile.exists()) {
            System.out.println("clearing outputClasses");
            outputClassesFile.delete();
            try {
                outputClassesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                System.out.println("creating outputClasses");
                outputClassesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        outputFieldsFile = new File("output/" + version + "/outputFields.txt");
        if (outputFieldsFile.exists()) {
            System.out.println("clearing outputClasses");
            outputFieldsFile.delete();
            try {
                outputFieldsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                System.out.println("creating outputFields");
                outputFieldsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        outputMethodsFile = new File("output/" + version + "/outputMethods.txt");
        if (outputMethodsFile.exists()) {
            System.out.println("clearing outputClasses");
            outputMethodsFile.delete();
            try {
                outputMethodsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                System.out.println("creating outputMethods");
                outputMethodsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // make errors.txt file in output folder
        errorsFile = new File("output/" + version + "/errors.txt");
        if (errorsFile.exists()) {
            System.out.println("clearing errors.txt");
            errorsFile.delete();
            try {
                errorsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                System.out.println("creating errors.txt");
                errorsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!Helper.hasMc(version))
        {
            System.out.println("please install minecraft version " + version);
            System.exit(-1);
        }

        // check if the dump folder exists in the input folder
        dumpFolder = new File("input/" + version + "/Dump");
        if (!dumpFolder.exists()) {
            System.out.println("Dump folder not found");
            System.out.println("please dump the game and put the dump in the dump folder");
            System.exit(0);
        } 
        else
        {
            System.out.println("dump folder found");
        }

        // check if the dump folder is empty
        if (dumpFolder.listFiles().length == 0) {
            System.out.println("dump folder is empty");
            System.out.println("please dump the game and put the dump in the dump folder");
            System.exit(0);
        }

        netDumpFolder = new File("input/" + version + "/Dump/net/minecraft/" + lunar_version_string);

        mcJarFile = new File(Helper.getMinecraftDirectory(), "versions/" + version + "/" + version + ".jar");

        if (!mcJarFile.exists())
        {
            System.out.println("could not find " + version + " jar");
            System.exit(-1);
        }

        ClassDumper.dumpClasses();

        FieldDumper.dumpFields();

        MethodDumper.dumpMethods();

        System.out.println("complete!");
    }
}
