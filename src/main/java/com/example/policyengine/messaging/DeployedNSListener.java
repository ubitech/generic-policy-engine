/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine.messaging;

import com.example.policyengine.KieContainersManagement.KieUtil;
import com.example.policyengine.PolicyengineApplication;
import static com.example.policyengine.Util.createKjar;
import static com.example.policyengine.Util.deployKjar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@Component
public class DeployedNSListener {

    private static Logger log = LoggerFactory.getLogger(DeployedNSListener.class);

    @Autowired
    private KieUtil kieUtil;

    /*
     routing_key : service.instances.create
     content_type: application/json
    
     {
	"deployed_graph": "my-x-kjar",
	"rules": "package rules.package1;\n import com.example.policyengine.facts.*\n declare  MonitoredComponent \n  @expires( 5m )\n  @role( event )\n end\n rule \"My First policy_name Rule\"\n when\n $o: Object()\n then\n System.out.println(\" >>> Rule Fired for Object policy_name changed: \"+$o.toString());\n end\n rule \"My Second policy_name Rule\"\n when\n        $tot0 := java.lang.Double( $tot0 >70.0 ) from accumulate($m0 := MonitoredComponent( name== \"vnf1\" && metric== \"CPULoad\" ) over window:time(70s)from entry-point \"MonitoringStream\" ,\n        average( $m0.getValue() )  )\n     then\n System.out.println(\" >>> Rule Fired for MonitoredComponent policy example\");\n end\n\n rule \"My Third policy_name Rule\"\n when\n $sampleFact: SampleFact()\n then\n $sampleFact.dosomething($sampleFact.getValue());\n end"
     }
     */
    @RabbitListener(queues = PolicyengineApplication.NS_INSTATIATION_QUEUE)
    public void deployedNSMessageReceived(byte[] message) {

        String messageToString = new String(message, StandardCharsets.UTF_8);
        log.info("I received this message " + messageToString);

        //deploy kjar to nexus
        String groupId = "generic.policy.rule";
        String version = "1.0.0-SNAPSHOT";
        JSONObject messageToJSON = new JSONObject(messageToString);
        String artifactId = messageToJSON.getString("deployed_graph");
        String rules = messageToJSON.getString("rules");
        createKjar(groupId, artifactId, version, rules);
        boolean succesful_deploy = deployKjar(artifactId);
        
        if (!succesful_deploy) {return;}

        //enforce policy
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId2 = ks.newReleaseId(groupId, artifactId, version);
        KieContainer kcontainer2 = ks.newKieContainer(releaseId2);
        KieScanner kscanner2 = ks.newKieScanner(kcontainer2);

        kscanner2.start(5000);

        String sessionname = artifactId;
        final KieSession ksession = kcontainer2.newKieSession(sessionname);

        kieUtil.fireKieSession(ksession, sessionname);

    }

}
