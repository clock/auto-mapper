import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import util.Helper;

import java.io.*;

public class MethodDumper {

    public static void dumpMethods() throws IOException {

        System.out.println("dumping methods");
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
                    //System.out.println("found class " + originalClassFile.getName() + " : " + lunarClassName + ".class" + " : " + line.split("'")[5]);

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

                    String[] mcMethod = new String[1000];
                    String[] lunarMethod = new String[1000];

                    int i = 0;

                    for (MethodNode method : originalClassNode.methods){
                        //System.out.println(originalClassNode.name + "/" + method.name + " : " + method.desc);
                        mcMethod[i] = originalClassNode.name + "/" + method.name + " " + method.desc;
                        i++;
                    }

                    i = 0;

                    for (MethodNode method : DumpedClassNode.methods){
                        lunarMethod[i] = DumpedClassNode.name + "/" + method.name;
                        i++;
                    }

                    for (int j = 0; j < mcMethod.length; j++) {
                        if (mcMethod[j] == null)
                            break;

                        BufferedReader srgReader = new BufferedReader(new FileReader(new File("./mappings/" + Main.version + "/" + Main.version + ".srg")));

                        String buffer;

                        while ((buffer = srgReader.readLine()) != null) {

                            String[] mappingsLineSplit = buffer.split(" ");

                            if (!mappingsLineSplit[0].contains("MD"))
                                continue;

                            if (mcMethod[j].equals(mappingsLineSplit[1] + " " + mappingsLineSplit[2]))
                            {
                                System.out.println("\t" + mcMethod[j] + " -> " + lunarMethod[j]);

                                BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Main.outputMethodsFile, true));
                                outputWriter.write(mcMethod[j] + " " + lunarMethod[j] + " " + mappingsLineSplit[3] + " " + mappingsLineSplit[4]);
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
        System.out.println("finished dumping methods");
    }

}
