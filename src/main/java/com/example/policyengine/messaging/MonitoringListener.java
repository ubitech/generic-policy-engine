/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine.messaging;

import com.example.policyengine.KieContainersManagement.KieUtil;
import com.example.policyengine.PolicyengineApplication;
import com.example.policyengine.facts.MonitoredComponent;
import com.example.policyengine.facts.SampleFact;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@Component
public class MonitoringListener {

    private static Logger log = LoggerFactory.getLogger(MonitoringListener.class);

    @Autowired
    private KieUtil kieUtil;

    
    /*
    content_type: application/json
     {
     "ObjectType": "SampleFact",
     "deployed_graph":"my-x-kjar",
     "ObjectData":{"value": "SampleFactValue","deployed_graph":"my-x-kjar"}
     }
    
     content_type: application/json
     {
     "ObjectType": "MonitoredComponent",
     "deployed_graph":"my-x-kjar",
     "ObjectData":{"name": "vnf1","metric":"CPULoad","value":80,"deployed_graph":"my-x-kjar"}
     }
     */
    @RabbitListener(queues = PolicyengineApplication.MONITORING_QUEUE)
    public void monitoringAlertReceived(byte[] message) {

        String messageAsString = new String(message, StandardCharsets.UTF_8);
        log.info("New Monitoring Message: " + messageAsString);

        JSONObject messageToJSON = new JSONObject(messageAsString);
         String objectType = messageToJSON.getString("ObjectType");
        String containerName = messageToJSON.getString("deployed_graph");

        if (!kieUtil.seeThreadMap().containsKey(containerName)) {
            log.info("Missing Knowledge base " + containerName);
            return;
        }

       String objectAsString = messageToJSON.getJSONObject("ObjectData").toString();
        KieSession ksession = (KieSession) kieUtil.seeThreadMap().get(containerName);


       
        switch (objectType) {
            case "SampleFact":
                SampleFact sampleFact = new Gson().fromJson(objectAsString, SampleFact.class);
                //System.out.println("convertStringToObject result for sample fact object " + sampleFact.getValue());
                ksession.insert(sampleFact);
                break;
            case "MonitoredComponent":
                MonitoredComponent component = new Gson().fromJson(objectAsString, MonitoredComponent.class);
                EntryPoint monitoringStream = ksession.getEntryPoint("MonitoringStream");
                monitoringStream.insert(component);
                break;
            default:
                 break;
        }

    }

}
