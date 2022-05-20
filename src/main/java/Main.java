import java.io.*;

public class Main {

    public static String lunar_version_string;
    public static String version = "1.8.9";
    public static File mappingsFolder;
    public static File outputFolder;
    public static File inputFolder;
    public static File lunarprodFile;
    public static File outputFile;
    public static File errorsFile;

    public static void main(String[] args) {

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

        // make output.txt file in output folder
        outputFile = new File("output/" + version + "/output.txt");
        if (outputFile.exists()) {
            System.out.println("clearing output.txt");
            outputFile.delete();
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                System.out.println("creating output.txt");
                outputFile.createNewFile();
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

        ClassDumper.dumpClasses();

        System.out.println("complete!");
    }
}
