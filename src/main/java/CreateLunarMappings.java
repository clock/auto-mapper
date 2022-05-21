import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class fieldsBase {
    public String obfName;
    public String srgName;
    public String lunarName;
    public String mcpName;
    public String comment = "";
}

class methodsBase {
    public String obfName;
    public String srgName;
    public String lunarName;
    public String srgDescriptor;
    public String lunarDescriptor;
    public String mcpName = "";
    public String comment = "";
}

class jsonBase {
    public String srgName;
    public String pgk;
    public String obfName;
    public String lunarPgk;
    public String lunarName;
    List<fieldsBase> fields = new ArrayList<fieldsBase>();
    List<methodsBase> methods = new ArrayList<methodsBase>();
}

public class CreateLunarMappings {
    public static void createMappings() throws IOException {
        Gson gson = new Gson();

        System.out.println("adding mappings to json file");

        //jsonBase test = new jsonBase();
        List<jsonBase> list = new ArrayList<jsonBase>();

        BufferedReader classReader = new BufferedReader(new FileReader(Main.outputClassesFile));

        String classLine;

        while ((classLine = classReader.readLine()) != null) {
            // class information

            jsonBase test = new jsonBase();

            String[] splitString = classLine.split(" ");
            String[] packageSplit = splitString[2].split("/");

            String[] lunarClassSplit = splitString[1].split("\\.");

            String buffer = "";

            for (int i = 0; i < lunarClassSplit.length - 1; i++)
                buffer += lunarClassSplit[i] + "/";

            test.lunarPgk = buffer;
            test.lunarName = lunarClassSplit[lunarClassSplit.length - 1];

            buffer = "";

            for (int i = 0; i < packageSplit.length - 1; i++)
                buffer += packageSplit[i] + "/";

            test.pgk = buffer;
            test.obfName = splitString[0];
            test.srgName = packageSplit[packageSplit.length - 1];

            // fields

            String lineField = "";

            BufferedReader fieldReader = new BufferedReader(new FileReader(Main.outputFieldsFile));

            while ((lineField = fieldReader.readLine()) != null) {

                String[] fieldSplit = lineField.split(" ");

                String[] obfSplit = fieldSplit[0].split("/");

                if (!obfSplit[0].equals(test.obfName))
                    continue;

                fieldsBase fieldBuffer = new fieldsBase();

                String[] lunarFieldSplit = fieldSplit[1].split("/");

                String[] srgFieldSplit = fieldSplit[2].split("/");

                fieldBuffer.lunarName = lunarFieldSplit[lunarFieldSplit.length - 1];
                fieldBuffer.obfName = obfSplit[1];
                fieldBuffer.srgName = srgFieldSplit[lunarFieldSplit.length - 1];

                // loop through fields csv and find srg name of field, extract the mcp name (for a.class the first field is w, the value of w is value

                String csvField = "";

                BufferedReader fieldsCsv = new BufferedReader(new FileReader("mappings/" + Main.version + "/fields.csv"));

                while ((csvField = fieldsCsv.readLine()) != null) {
                    String[] splitCsv = csvField.split(",");

                    if (fieldBuffer.srgName.equals(splitCsv[0])) {
                        String aa = "";
                        for (int i = 3; i < splitCsv.length; i++) {
                            aa += splitCsv[i];
                        }
                        fieldBuffer.comment = aa;
                        fieldBuffer.mcpName = splitCsv[1];
                        aa = "";
                        fieldsCsv.close();
                        break;
                    }
                }

                test.fields.add(fieldBuffer);
                // remove this break later
                //break;
            }

            fieldReader.close();

            // methods

            String methodLine = "";

            BufferedReader methodReader = new BufferedReader(new FileReader(Main.outputMethodsFile));

            while ((methodLine = methodReader.readLine()) != null) {
                String[] methodSplit = methodLine.split(" ");

                String[] obfSplit = methodSplit[0].split("/");

                if (!obfSplit[0].equals(test.obfName))
                    continue;

                methodsBase methodsBuffer = new methodsBase();

                String[] lunarPgkSplit = methodSplit[2].split("/");
                String[] vinPgkSplit = methodSplit[3].split("/");

                methodsBuffer.obfName = obfSplit[1];
                methodsBuffer.lunarName = lunarPgkSplit[lunarPgkSplit.length - 1];

                methodsBuffer.srgName = vinPgkSplit[vinPgkSplit.length - 1];

                methodsBuffer.lunarDescriptor = methodSplit[1];

                methodsBuffer.srgDescriptor = methodSplit[4];

                BufferedReader methodCsv = new BufferedReader(new FileReader("mappings/" + Main.version + "/methods.csv"));

                String csvMethod = "n/a";

                while ((csvMethod = methodCsv.readLine()) != null) {
                    String[] splitCsv = csvMethod.split(",");
                    if (methodsBuffer.srgName.equals(splitCsv[0])) {
                        String aa = "";
                        for (int i = 3; i < splitCsv.length; i++) {
                            aa += splitCsv[i];
                        }
                        methodsBuffer.comment = aa;
                        methodsBuffer.mcpName = splitCsv[1];
                        aa = "";
                        methodCsv.close();
                        break;
                    }
                }

                if (methodsBuffer.comment.isEmpty())
                    methodsBuffer.comment = "n/a";

                if (methodsBuffer.mcpName.isEmpty())
                    methodsBuffer.mcpName = "n/a";

                test.methods.add(methodsBuffer);
                // remove later
                //break;
            }

            methodReader.close();

            // add object to json file
            list.add(test);
            System.out.println("\tfinished " + test.srgName);
            // remove this break later
            //break;
        }

        try (Writer writer = new FileWriter(Main.finalJsonFile)) {
            gson.toJson(list, writer);
        }
    }
}
