import java.io.*;
import java.util.jar.JarFile;
import util.FileUtil;

public class Main {
    public static void main(String[] args) {
        String version = "1.8.9";
        String lunar_version_string;

        // if version is 1.8.9, lunar_version_string is v1_8
        // if version is 1.7.10, lunar_version_string is v1_7

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

        File mappingsFolder = new File("mappings/" + version);
        if (!mappingsFolder.exists()) {
            System.out.println("making mappings folder");
            mappingsFolder.mkdirs();
        }

        File outputFolder = new File("output/" + version);
        if (!outputFolder.exists()) {
            System.out.println("making output folder");
            outputFolder.mkdirs();
        }

        File inputFolder = new File("input/" + version);
        if (!inputFolder.exists()) {
            System.out.println("making input folder");
            inputFolder.mkdirs();
        }

        // check if "lunar-prod" file exists in the input folder
        // if not tell the user to put it there and exit
        File lunarprodFile = new File("input/" + version + "/lunar-prod");
        if (!lunarprodFile.exists()) {
            System.out.println("input file not found");
            System.out.println("please put the file lunar-prod in the input folder");
            System.exit(0);
        } 
        else
        {
            System.out.println("lunar-prod file found");
        }

        // check if mappings folder is empty, if it isnt empty, delete everything in it
        if (mappingsFolder.listFiles().length > 0) {
            for (File file : mappingsFolder.listFiles()) {
                System.out.println("deleted old " + file.getName() + " mapping");
                file.delete();
            }
        }

        System.out.println("downloading new mappings");
        MappingsDownloader.download(version, mappingsFolder);

        // make output.txt file in output folder
        File outputFile = new File("output/" + version + "/output.txt");
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
        File errorsFile = new File("output/" + version + "/errors.txt");
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

        try {
            FileUtil.walk(new File("./input"), "").forEach(lunarFile -> {
                // print out the file name
                System.out.println("found: " + lunarFile.getName());
                
                // if the file name is "lunar-prod" open it as a jar file
                // print the contents of the jar file

                if (lunarFile.getName().equals("lunar-prod")) {
                    try {
                        JarFile jarFile = new JarFile(lunarFile);
                        jarFile.stream().forEach(jarEntry -> {
                            //System.out.println(jarEntry.getName());
                            if (jarEntry.getName().equals("patch/" + lunar_version_string + "/mappings.txt")) {
                                System.out.println("found: mappings.txt");

                                try {
                                    BufferedReader mappingsReader = new BufferedReader(new FileReader(new File("./mappings/" + version + "/" + version + ".srg")));
                                    String mappingsLine;

                                    String found = "";

                                    while ((mappingsLine = mappingsReader.readLine()) != null) {
                                        // split on space
                                        String[] mappingsLineSplit = mappingsLine.split(" ");

                                        if (!mappingsLineSplit[0].contains("CL"))
                                            continue;

                                        //System.out.println(mappingsLineSplit[1]);

                                        // loop through the jar file and compare mappingsLineSplit[1] to every line in the jar file
                                        // reset reader to the beginning of the jar file

                                        InputStream input = jarFile.getInputStream(jarEntry);
                                        BufferedReader jarReader = new BufferedReader(new InputStreamReader(input));
                                        String jarLine;

                                        while ((jarLine = jarReader.readLine()) != null) {
                                            String[] splitJarLine = jarLine.split(" ");
                                            if (splitJarLine[1].equals(mappingsLineSplit[1])) {
                                                found = "lunar: '" + splitJarLine[0] + "' obf: '" + mappingsLineSplit[1] + "' srg: '" + mappingsLineSplit[2] + "'";

                                                // save the output to output.txt
                                                try {
                                                    BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile, true));
                                                    outputWriter.write(found);
                                                    outputWriter.newLine();
                                                    outputWriter.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        if (found.isEmpty()) {
                                            try {
                                                BufferedWriter writer = new BufferedWriter(new FileWriter(errorsFile, true));
                                                writer.write(mappingsLineSplit[2]);
                                                writer.newLine();
                                                writer.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        found = "";
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("finished deobfoscating classes");
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("compleate!");
    }
}
