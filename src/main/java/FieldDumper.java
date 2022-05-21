import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import util.Helper;

import java.io.*;

public class FieldDumper {
    public static void dumpFields() throws IOException {

        System.out.println("dumping fields");
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Helper.extractJar(Main.mcJarFile, Main.mcExtracted);

        // loop through mcExtracted and print out the names of the files
        for (File originalClassFile : Main.mcExtracted.listFiles()) {
            if (originalClassFile.getName().contains(".class")) {
                //System.out.println(file.getName());

                InputStream inputStream = new FileInputStream(originalClassFile);
                ClassReader reader = new ClassReader(inputStream);

                ClassNode lunarDumpClassNode = new ClassNode();
                reader.accept(lunarDumpClassNode, 0);

                BufferedReader mappingsReader = new BufferedReader(new FileReader(Main.outputClassesFile));

                String line;

                boolean found = false;

                while ((line = mappingsReader.readLine()) != null)
                {
                    //System.out.println(line.split("'")[3] + ".class");
                    if (originalClassFile.getName().equals(line.split(" ")[0] + ".class"))
                    {
                        found = true;
                        break;
                    }
                }
                if (found)
                {
                    String lunarClassName = line.split(" ")[1].split("\\.")[3];
                    System.out.println("found class " + originalClassFile.getName() + " : " + lunarClassName + ".class" + " : " + line.split(" ")[2]);

                    // reading dumped mc classes

                    InputStream originalClassInputStream = new FileInputStream(originalClassFile);
                    ClassReader originalClassReader = new ClassReader(originalClassInputStream);

                    ClassNode originalClassNode = new ClassNode();
                    originalClassReader.accept(originalClassNode, 0);

                    File lunarClass = new File("./input/" + Main.version + "/Dump/net/minecraft/" + Main.lunar_version_string + "/" + lunarClassName + ".class");

                    // reading dumped lunar classes

                    if (!lunarClass.exists())
                    {
                        System.out.println("couldnt find " + lunarClassName + ".class on pc\n");
                        continue;
                    }

                    InputStream dumpedClassInputStream = new FileInputStream(lunarClass);
                    ClassReader dumpedClassReader = new ClassReader(dumpedClassInputStream);

                    ClassNode DumpedClassNode = new ClassNode();
                    dumpedClassReader.accept(DumpedClassNode, 0);

                    String[] mcField = new String[1000];
                    String[] lunarField = new String[1000];

                    int i = 0;

                    for (FieldNode field : originalClassNode.fields){
                        mcField[i] = field.name;
                        i++;
                    }

                    i = 0;

                    for (FieldNode field : DumpedClassNode.fields){
                        lunarField[i] = field.name;
                        i++;
                    }

                    for (int j = 0; j < mcField.length; j++) {
                        if (mcField[j] == null)
                            break;

                        BufferedReader srgReader = new BufferedReader(new FileReader(new File("./mappings/" + Main.version + "/" + Main.version + ".srg")));

                        String buffer;

                        while ((buffer = srgReader.readLine()) != null) {

                            String[] mappingsLineSplit = buffer.split(" ");

                            if (!mappingsLineSplit[0].contains("FD"))
                                continue;

                            if (!mappingsLineSplit[2].contains("field"))
                                continue;

                            if (mappingsLineSplit[1].equals(originalClassNode.name + "/" + mcField[j])) {
                                System.out.println("\t" + mcField[j] + " -> " + lunarField[j]);

                                BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Main.outputFieldsFile, true));
                                outputWriter.write(originalClassNode.name + "/" + mcField[j] + " " + DumpedClassNode.name + "/" + lunarField[j] + " " + mappingsLineSplit[2]);
                                outputWriter.newLine();
                                outputWriter.close();

                                break;
                            }
                        }
                    }
                }
                else
                {
                    System.out.println("couldnt find class " + originalClassFile.getName());
                    System.exit(-1);
                }
            }
        }
        System.out.println("finished dumping fields");
    }

}
