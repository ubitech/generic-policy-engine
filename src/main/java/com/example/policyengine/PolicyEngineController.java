/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine;

import static com.example.policyengine.Util.createKjar;
import static com.example.policyengine.Util.deployKjar;
import org.json.JSONObject;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@RestController
public class PolicyEngineController {

//    @Autowired
//    private RabbitTemplate template;
//
//    @Autowired
//    private FanoutExchange fanout;

    @RequestMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }

    /*
    
    
     curl -X POST http://localhost/policy/update    -H Host:policyengine.docker.localhost    -H 'content-type: application/json' \
     -d '{
     "deployed_graph": "my-x-kjar",
     "rules": "package rules.package1;\n import com.example.policyengine.facts.*\n declare  MonitoredComponent \n  @expires( 5m )\n  @role( event )\n end\n rule \"My First policy_name Rule\"\n when\n $o: Object()\n then\n System.out.println(\" >>> Rule Fired for Object policy_name changed: \"+$o.toString());\n end\n rule \"My Second policy_name Rule\"\n when\n        $tot0 := java.lang.Double( $tot0 >170.0 ) from accumulate($m0 := MonitoredComponent( name== \"vnf1\" && metric== \"CPULoad\" ) over window:time(70s)from entry-point \"MonitoringStream\" ,\n        average( $m0.getValue() )  )\n     then\n System.out.println(\" >>> Rule Fired for MonitoredComponent policy example\");\n end\n\n rule \"My Third policy_name Rule\"\n when\n $sampleFact: SampleFact()\n then\n $sampleFact.dosomething($sampleFact.getValue());\n end"
     }'
     */
    @RequestMapping(value = "/policy/update", method = RequestMethod.POST)
    public boolean getRecommendations(@RequestBody String policyUpdateInfo) {
        JSONObject policyUpdateInfoJson = new JSONObject(policyUpdateInfo);

        String groupId = "generic.policy.rule";
        String version = "1.0.0-SNAPSHOT";
        String artifactId = policyUpdateInfoJson.getString("deployed_graph");
        String rules = policyUpdateInfoJson.getString("rules");

        //check if worker that gets this request has the enforced policy
        createKjar(groupId, artifactId, version, rules);
        deployKjar(artifactId);

//        template.convertAndSend(fanout.getName(), queue.getName(), "se parakalooooooooooo", m -> {
//            m.getMessageProperties().setAppId("tng-policy-mngr");
//            m.getMessageProperties().setReplyTo(queue.getName());
//            m.getMessageProperties().setCorrelationId("eno adio galdkgjald");
//            return m;
//        });
        
//        template.convertAndSend(fanout.getName(), "", "se parakalooooooooooo");
        

        return true;
    }

}
