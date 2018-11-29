package com.example.policyengine;

import com.example.policyengine.messaging.DeployedNSListener;
import com.example.policyengine.messaging.MonitoringListener;
import com.example.policyengine.messaging.UpdatePolicyListener;
import org.apache.commons.lang3.RandomStringUtils;
import org.kie.api.KieServices;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AnonymousQueue;

import org.springframework.amqp.core.Queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PolicyengineApplication {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(PolicyengineApplication.class);

    String generatedString = RandomStringUtils.randomAlphabetic(10);
    public final static String NS_INSTATIATION_QUEUE = "policies.service.instances.create";
    public final static String NS_INSTATIATION_TOPIC = "service.instances.create";

    public final static String MONITORING_QUEUE = "son.monitoring.PLC";

    //public final static String POLICY_UPDATE_QUEUE = "policies.service.instances.update";
    //public final static String MONITORING_QUEUE = "policies.service.instances.monit";
    //public final static String MONITORING_TOPIC = "service.instances.monit";
    @Bean
    TopicExchange exchange() {
        return new TopicExchange("son-kernel", false, false);
    }

    @Bean
    public FanoutExchange fanout() {
        return new FanoutExchange("tut.fanout");
    }

    @Bean
    public SimpleMessageConverter simpleMessageConverter() {
        return new SimpleMessageConverter();
    }

    // Configure connection with rabbit mq for NS instantiatin Queue
    @Bean
    public Queue nsInstantiationQueue() {
        return new Queue(NS_INSTATIATION_QUEUE, false);
        //return QueueBuilder.durable(NS_INSTATIATION_QUEUE).build();
    }

    @Bean
    Binding bindingNSInstantiationQueue(TopicExchange exchange) {
        return BindingBuilder.bind(nsInstantiationQueue()).to(exchange).with(NS_INSTATIATION_TOPIC);
    }

    @Qualifier("nsInstantiationlistenerAdapter")
    @Bean
    MessageListenerAdapter nsInstantiationlistenerAdapter(DeployedNSListener receiver) {
        MessageListenerAdapter msgadapter = new MessageListenerAdapter(receiver, "deployedNSMessageReceived");
        return msgadapter;
    }

    @Qualifier("nsInstantiationcontainer")
    @Bean
    SimpleMessageListenerContainer nsInstantiationcontainer(ConnectionFactory connectionFactory,
            @Qualifier("nsInstantiationlistenerAdapter") MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(nsInstantiationQueue().getName());
        container.setMessageListener(listenerAdapter);
        return container;
    }

    // Configure connection with rabbit mq for prometheus alerts Queue
    @Bean
    public Queue monitoringAlerts() {
        return new Queue(MONITORING_QUEUE, false);
    }

    @Bean
    Binding binding2(TopicExchange exchange) {
        return BindingBuilder.bind(monitoringAlerts()).to(exchange).with(monitoringAlerts().getName());

    }

    @Qualifier("listenerAdapter2")
    @Bean
    MessageListenerAdapter listenerAdapter2(MonitoringListener receiver) {
        MessageListenerAdapter msgadapter = new MessageListenerAdapter(receiver, "monitoringAlertReceived");
        return msgadapter;
    }

    @Qualifier("container2")
    @Bean
    SimpleMessageListenerContainer container2(ConnectionFactory connectionFactory,
            @Qualifier("listenerAdapter2") MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(monitoringAlerts().getName());
        container.setMessageListener(listenerAdapter);
        return container;
    }

    // Configure connection with rabbit mq for Policy update Queue
    @Bean
    public Queue autoDeleteQueue1() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding binding1(FanoutExchange fanout,
            Queue autoDeleteQueue1) {
        return BindingBuilder.bind(autoDeleteQueue1).to(fanout);
    }

    @Qualifier("updatePolicyListenerAdapter")
    @Bean
    MessageListenerAdapter updatePolicyListenerAdapter(UpdatePolicyListener receiver) {
        MessageListenerAdapter msgadapter = new MessageListenerAdapter(receiver, "updatePolicyMessageReceived");
        return msgadapter;
    }

    @Qualifier("updatePolicyContainer")
    @Bean
    SimpleMessageListenerContainer updatePolicyContainer(ConnectionFactory connectionFactory,
            @Qualifier("updatePolicyListenerAdapter") MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(autoDeleteQueue1().getName());
        container.setMessageListener(listenerAdapter);
        return container;
    }

    public static void main(String[] args) {
        SpringApplication.run(PolicyengineApplication.class, args);

        String groupId2 = "generic.policy.rule";
        String artifactId2 = "mynewkjar";
        String version2 = "1.0.0-SNAPSHOT";

        //Util.createKjar(groupId2, artifactId2, version2);
        //Util.deployKjar(artifactId2);
        //////////////
        KieServices ks = KieServices.Factory.get();

//        String groupId2 = "org.drools.workshop";
//        String artifactId2 = "my-second-drools-kjar";
//        String version2 = "1.0.3-SNAPSHOT";
//        ReleaseId releaseId2 = ks.newReleaseId(groupId2, artifactId2, version2);
//        KieContainer kcontainer2 = ks.newKieContainer(releaseId2);
//        KieScanner kscanner2 = ks.newKieScanner(kcontainer2);
//
//        kscanner2.start(5000);
//
//        String sessionname = "policy2";
//        final KieSession ksession = kcontainer2.newKieSession(sessionname);
//        //ksession.fireUntilHalt();
//
//        Thread t = new Thread() {
//
//            @Override
//            public void run() {
//                ksession.fireUntilHalt();
//            }
//        };
//
//        t.setName(sessionname);
//
//        t.start();
//
//        for (int i = 0; i < 100; i++) {
//            kscanner2.scanNow();
//            ksession.insert(new String("Eleni"));
//            ksession.insert(new SampleFact("SampleFactValue"));
//
//            MonitoredComponent component = new MonitoredComponent("vnf1", "CPULoad", 80, "test");
//            EntryPoint monitoringStream = ksession.getEntryPoint("MonitoringStream");
//
//            monitoringStream.insert(component);
//
//            System.out.println("facts number " + ksession.getObjects().size());
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        kscanner2.stop();
    }

}
