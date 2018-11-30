/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
public class Util {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(Util.class);

    public static void createKjar(String groupId, String artifactId, String version, String rules) {

        //read file into stream, try-with-resources
        Path copyFrom = Paths.get("sample-kjar");
        Path copyTo = Paths.get(artifactId);

        try (Stream<String> stream = Files.lines(Paths.get("sample-kjar/pom.xml"))) {

            //create new kjar repository
            //Files.copy(copyFrom, copyTo);
            copyFolder(copyFrom, copyTo);

            // update pom file
            File pomfile = new File(artifactId + "/pom.xml");

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(pomfile);

            document.getDocumentElement().normalize();

            System.out.println("Root element :" + document.getDocumentElement().getNodeName());

            stream.forEach(System.out::println);

            String groupIdretrieved = document.getElementsByTagName("groupId").item(0).getTextContent();

            // log.info("looking for node value...." + groupIdretrieved);
            document.getElementsByTagName("groupId").item(0).setTextContent(groupId);
            document.getElementsByTagName("artifactId").item(0).setTextContent(artifactId);
            document.getElementsByTagName("version").item(0).setTextContent(version);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);

            String xmlString = result.getWriter().toString();
            System.out.println(xmlString);

            Files.write(Paths.get(artifactId + "/pom.xml"), xmlString.getBytes());

            //update kmodule.xml
            String kmodulexmlString = "<kmodule xmlns=\"http://jboss.org/kie/6.0.0/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
                    + "	<kbase name=\"rules\" packages=\"rules\" eventProcessingMode=\"stream\" default=\"true\">\n"
                    + "        <ksession name=\"" + artifactId + "\" type=\"stateful\">\n"
                    + "        </ksession>\n"
                    + "    </kbase>\n"
                    + "</kmodule>";

            Files.write(Paths.get(artifactId + "/src/main/resources/META-INF/kmodule.xml"), kmodulexmlString.getBytes());

            //update rules.drl
            Files.write(Paths.get(artifactId + "/src/main/resources/rules/rules.drl"), rules.getBytes());

            //update rules
            File rulesFile = new File(artifactId + "/src/main/resources/rules/rules.drl");
            String fileContext = FileUtils.readFileToString(rulesFile);
            fileContext = fileContext.replaceAll("policy_name", artifactId);
            FileUtils.write(rulesFile, fileContext);

        } catch (IOException e) {
        } catch (ParserConfigurationException | SAXException ex) {
            Logger.getLogger(PolicyengineApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(PolicyengineApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(PolicyengineApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static boolean deployKjar(String artifactId) {

        try {
            String[] command = {"./deploykjar.sh", artifactId};
            log.info("A procces will be executed");
//            Process process = Runtime.getRuntime().exec(command);
//            process.waitFor();

//            ProcessBuilder pb = new ProcessBuilder();
//            pb.command(command);
//            System.out.println("Run echo command");
//            Process process = pb.start();
//            int errCode = process.waitFor();
//            System.out.println("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
//            System.out.println("Echo Output:\n" + output(process.getInputStream()));
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("tasklist: " + line);
            }
            int errCode = process.waitFor();
            System.out.println("Echo command executed, any errors? " + (errCode == 0 ? "No" : "Yes"));
            System.out.println("Echo Output:\n" + output(process.getInputStream()));

            log.info("procces is executed " + process.exitValue());

        } catch (IOException ex) {
            Logger.getLogger(PolicyengineApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static void copyFolder(Path src, Path dest) {
        try {
            Files.walk(src).forEach(s -> {
                try {
                    Path d = dest.resolve(src.relativize(s));
                    if (Files.isDirectory(s)) {
                        if (!Files.exists(d)) {
                            Files.createDirectory(d);
                        }
                        return;
                    }
                    Files.copy(s, d);// use flag to override existing
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String output(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }

}
