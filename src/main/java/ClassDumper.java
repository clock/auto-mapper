import util.FileUtil;

import java.io.*;
import java.util.jar.JarFile;

public class ClassDumper {
    public static void dumpClasses()
    {
        System.out.println("dumping classes");
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            FileUtil.walk(new File("./input"), "").forEach(lunarFile -> {

                // print out the file name
                System.out.println("found: " + lunarFile.getName());

                if (lunarFile.getName().equals("lunar-prod-optifine.jar")) {
                    try {
                        JarFile jarFile = new JarFile(lunarFile);
                        jarFile.stream().forEach(jarEntry -> {
                            //System.out.println(jarEntry.getName());
                            if (jarEntry.getName().equals("patch/" + Main.lunar_version_string + "/mappings.txt")) {
                                System.out.println("found: mappings.txt");

                                try {
                                    BufferedReader mappingsReader = new BufferedReader(new FileReader(new File("./mappings/" + Main.version + "/" + Main.version + ".srg")));
                                    String mappingsLine;

                                    String found = "";

                                    while ((mappingsLine = mappingsReader.readLine()) != null) {
                                        // split on space
                                        String[] mappingsLineSplit = mappingsLine.split(" ");

                                        if (!mappingsLineSplit[0].contains("CL"))
                                            continue;

                                        // loop through the jar file and compare mappingsLineSplit[1] to every line in the jar file
                                        // reset reader to the beginning of the jar file

                                        InputStream input = jarFile.getInputStream(jarEntry);
                                        BufferedReader jarReader = new BufferedReader(new InputStreamReader(input));
                                        String jarLine;

                                        while ((jarLine = jarReader.readLine()) != null) {
                                            String[] splitJarLine = jarLine.split(" ");
                                            if (splitJarLine[1].equals(mappingsLineSplit[1])) {
                                                System.out.println("\t" + mappingsLineSplit[1] + "->" + splitJarLine[0]);
                                                found = mappingsLineSplit[1] + " " + splitJarLine[0] + " " + mappingsLineSplit[2];
                                                //System.out.println(found);
                                                // save the output to output.txt
                                                try {
                                                    BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Main.outputClassesFile, true));
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
                                                BufferedWriter writer = new BufferedWriter(new FileWriter(Main.errorsFile, true));
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
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("finished deobfuscation classes");
    }

}
