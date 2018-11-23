/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine.messaging;

import com.example.policyengine.PolicyengineApplication;
import static com.example.policyengine.Util.createKjar;
import static com.example.policyengine.Util.deployKjar;
import com.example.policyengine.facts.MonitoredComponent;
import com.example.policyengine.facts.SampleFact;
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
import org.kie.api.runtime.rule.EntryPoint;

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
        String groupId = "generic.policy.rule";
        String version = "1.0.0-SNAPSHOT";
        JSONObject messageToJSON = new JSONObject(messageToString);
        String artifactId = messageToJSON.getString("deployed_graph");
        createKjar(groupId,artifactId,version);
        deployKjar(artifactId);

        //enforce policy
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId2 = ks.newReleaseId(groupId, artifactId, version);
        KieContainer kcontainer2 = ks.newKieContainer(releaseId2);
        KieScanner kscanner2 = ks.newKieScanner(kcontainer2);

        kscanner2.start(5000);

        String sessionname = artifactId;
        final KieSession ksession = kcontainer2.newKieSession(sessionname);

        Thread t = new Thread() {

            @Override
            public void run() {
                ksession.fireUntilHalt();
            }
        };

        t.setName(sessionname);

        t.start();

        for (int i = 0; i < 100; i++) {
            kscanner2.scanNow();
            ksession.insert(new String("Eleni"));
            ksession.insert(new SampleFact("SampleFactValue"));

            MonitoredComponent component = new MonitoredComponent("vnf1", "CPULoad", 80, "test");
            EntryPoint monitoringStream = ksession.getEntryPoint("MonitoringStream");

            monitoringStream.insert(component);

            System.out.println("facts number " + ksession.getObjects().size());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        kscanner2.stop();

    }

}
