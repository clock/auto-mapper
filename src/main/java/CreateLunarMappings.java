import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    public String mcpName = "";
    public String srgDescriptor;
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

        jsonBase test = new jsonBase();
        List<jsonBase> list=new ArrayList<jsonBase>();

        BufferedReader classReader = new BufferedReader(new FileReader(Main.outputClassesFile));
        BufferedReader fieldReader = new BufferedReader(new FileReader(Main.outputFieldsFile));
        BufferedReader methodCsv = new BufferedReader(new FileReader("mappings/" + Main.version + "/methods.csv"));
        BufferedReader fieldsCsv = new BufferedReader(new FileReader("mappings/" + Main.version + "/fields.csv"));
        BufferedReader methodReader = new BufferedReader(new FileReader(Main.outputMethodsFile));

        String classLine;

        while ((classLine = classReader.readLine()) != null)
        {
            // class information

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

            // fields information

            String lineField = "";

            while ((lineField = fieldReader.readLine()) != null) {

                String[] fieldSplit = lineField.split(" ");

                String[] obfSplit = fieldSplit[0].split("/");

                if (!obfSplit[0].equals(test.obfName))
                    break;

                fieldsBase fieldBuffer = new fieldsBase();

                String[] lunarFieldSplit = fieldSplit[1].split("/");

                String[] srgFieldSplit = fieldSplit[2].split("/");

                fieldBuffer.lunarName = lunarFieldSplit[lunarFieldSplit.length - 1];
                fieldBuffer.obfName = obfSplit[1];
                fieldBuffer.srgName = srgFieldSplit[lunarFieldSplit.length - 1];

                // loop through fields csv and find srg name of field, extract the mcp name (for a.class the first field is w, the value of w is value

                String csvField = "";

                while ((csvField = fieldsCsv.readLine()) != null) {
                    String[] splitCsv = csvField.split(",");

                    if (fieldBuffer.srgName.equals(splitCsv[0]))
                    {
                        System.out.println(csvField);
                        String aa = "";
                        for (int i = 3; i < splitCsv.length; i++) {
                            aa += splitCsv[i];
                        }
                        fieldBuffer.comment = aa;
                        aa = "";
                        break;
                    }
                }

                test.fields.add(fieldBuffer);
                // remove this break later
                //break;
            }

            // methods

            String methodLine = "";

            methodsBase methodsBuffer = new methodsBase();

            while ((methodLine = methodReader.readLine()) != null)
            {
                String[] fieldSplit = lineField.split(" ");

                String[] obfSplit = fieldSplit[0].split("/");

                if (!obfSplit[0].equals(test.obfName))
                    break;

                // remove later
                break;
            }

            methodsBuffer.obfName = "b";
            methodsBuffer.srgDescriptor = "()Ljava/lang/String;";
            methodsBuffer.srgName = "func_150668_b";
            methodsBuffer.comment = "Gets the value to perform the action on when this event is raised.  For example, if the action is \"\"open URL\"\", this would be the URL to open.";
            methodsBuffer.mcpName = "getValue";
            methodsBuffer.lunarName = "eseespsspphessapeheheeaae";

            // add object to json file

            test.methods.add(methodsBuffer);

            list.add(test);

            // remove this break later
            break;
        }

        System.out.println(gson.toJson(list));
    }
}
