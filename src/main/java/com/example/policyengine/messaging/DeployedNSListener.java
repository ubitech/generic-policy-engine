/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine.messaging;

import com.example.policyengine.PolicyengineApplication;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.el.stream.Stream;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@Component
public class DeployedNSListener {

    private static Logger log = LoggerFactory.getLogger(DeployedNSListener.class);

    @RabbitListener(queues = PolicyengineApplication.NS_INSTATIATION_QUEUE)
    public void deployedNSMessageReceived(byte[] message) {

        
            String messageToString = new String(message, StandardCharsets.UTF_8);
            log.info("I received this message " + messageToString);
            
            //deploy kjar to nexus
            //info needed
            String groupId = "tng.policy.rule";
            String version = "1.0.0-SNAPSHOT";
            JSONObject messageToJSON = new JSONObject(messageToString);
            String artifactId = messageToJSON.getString("nsr");
            //<groupId>org.drools.workshop</groupId>
            //<artifactId>my-second-drools-kjar</artifactId>
            //<version>1.0.3-SNAPSHOT</version>
            
            //update pom file
            log.info("update pom file");
            try {
            File file = new File("sample-kjar/pom.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            
            
            document.getElementsByTagName("groupId").item(0).setNodeValue(groupId);
            
            
           


            
            
            //rules
            //create
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            java.util.logging.Logger.getLogger(DeployedNSListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
