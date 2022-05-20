import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import util.Helper;

import java.io.*;

public class FieldDumper {
    public static void dumpFields() throws IOException {
        Helper.extractJar(Main.mcJarFile, Main.mcExtracted);

        // loop through mcExtracted and print out the names of the files
        for (File originalClassFile : Main.mcExtracted.listFiles()) {
            if (originalClassFile.getName().contains(".class")) {
                //System.out.println(file.getName());

                InputStream inputStream = new FileInputStream(originalClassFile);
                ClassReader reader = new ClassReader(inputStream);

                ClassNode lunarDumpClassNode = new ClassNode();
                reader.accept(lunarDumpClassNode, 0);

                BufferedReader mappingsReader = new BufferedReader(new FileReader(Main.outputFile));

                String line;

                boolean found = false;

                while ((line = mappingsReader.readLine()) != null)
                {
                    //System.out.println(line.split("'")[3] + ".class");
                    if (originalClassFile.getName().equals(line.split("'")[3] + ".class"))
                    {
                        found = true;
                        break;
                    }
                }
                if (found)
                {
                    String lunarClassName = line.split("'")[1].split("\\.")[3];
                    System.out.println("found class " + originalClassFile.getName() + " : " + lunarClassName + ".class");

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

                    String mcField = "";
                    String lunarField = "";

                    for (FieldNode field : originalClassNode.fields){
                        mcField += field.name + "  ";
                    }

                    for (FieldNode field : DumpedClassNode.fields){
                        lunarField += field.name + "  ";
                    }

                    System.out.println("mc mappings: " + mcField);
                    System.out.println("lunar mappings: " + lunarField + "\n");
                }
                else
                {
                    System.out.println("couldnt find class " + originalClassFile.getName());
                    System.exit(-1);
                }
            }
        }
    }

}
