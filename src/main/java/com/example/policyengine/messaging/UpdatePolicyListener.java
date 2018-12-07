/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine.messaging;

import static com.example.policyengine.Util.createKjar;
import static com.example.policyengine.Util.deployKjar;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@Component
public class UpdatePolicyListener {
    
    private static final Logger log = LoggerFactory.getLogger(MonitoringListener.class);
    
    /*
    content_type: application/json
    {
     "deployed_graph": "my-x-kjar",
     "rules": "package rules.package1;\n import com.example.policyengine.facts.*\n declare  MonitoredComponent \n  @expires( 5m )\n  @role( event )\n end\n rule \"My First policy_name Rule\"\n when\n $o: Object()\n then\n System.out.println(\" >>> Rule Fired for Object policy_name changed: \"+$o.toString());\n end\n rule \"My Second policy_name Rule\"\n when\n        $tot0 := java.lang.Double( $tot0 >170.0 ) from accumulate($m0 := MonitoredComponent( name== \"vnf1\" && metric== \"CPULoad\" ) over window:time(70s)from entry-point \"MonitoringStream\" ,\n        average( $m0.getValue() )  )\n     then\n System.out.println(\" >>> Rule Fired for MonitoredComponent policy example\");\n end\n\n rule \"My Third policy_name Rule\"\n when\n $sampleFact: SampleFact()\n then\n $sampleFact.dosomething($sampleFact.getValue());\n end"
     }
    
    */
    
    @RabbitListener(queues = "#{autoDeleteQueue1.name}")
    public void updatePolicyMessageReceived(byte[] policyUpdateInfo) {
        
        String messageAsString = new String(policyUpdateInfo, StandardCharsets.UTF_8);
        log.info("New updatePolicy Message Received: " + messageAsString);
        
        JSONObject policyUpdateInfoJson = new JSONObject(messageAsString);
        String groupId = "generic.policy.rule";
        String version = "1.0.0-SNAPSHOT";
        String artifactId = policyUpdateInfoJson.getString("deployed_graph");
        
        Path artifactIdPath = Paths.get(artifactId);
        
        if (Files.exists(artifactIdPath)) {
            String rules = policyUpdateInfoJson.getString("rules");
            
            createKjar(groupId, artifactId, version, rules);
            deployKjar(artifactId);
        } else {
            log.info("update policy message ignored");
        }
        
    }
    
}
