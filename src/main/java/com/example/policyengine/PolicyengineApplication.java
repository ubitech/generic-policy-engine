package com.example.policyengine;

import com.example.policyengine.facts.MonitoredComponent;
import com.example.policyengine.messaging.DeployedNSListener;
import com.example.policyengine.messaging.MonitoringListener;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
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
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.Queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SpringBootApplication
public class PolicyengineApplication {
    
    private static org.slf4j.Logger log = LoggerFactory.getLogger(PolicyengineApplication.class);

    public final static String NS_INSTATIATION_QUEUE = "policies.service.instances.create";
    public final static String NS_INSTATIATION_TOPIC = "service.instances.create";

    public final static String MONITORING_QUEUE = "son.monitoring.PLC";

    //public final static String MONITORING_QUEUE = "policies.service.instances.monit";
    //public final static String MONITORING_TOPIC = "service.instances.monit";
    @Bean
    TopicExchange exchange() {
        return new TopicExchange("son-kernel", false, false);
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
        //msgadapter.setMessageConverter(jackson2JsonMessageConverter());
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

    //---------------------------------------------
//    @Bean
//    public Queue monitoringQueue() {
//        return new Queue(MONITORING_QUEUE, false);
//    }
//
//    @Bean
//    Binding bindingMonitoringQueue(TopicExchange exchange) {
//        return BindingBuilder.bind(monitoringQueue()).to(exchange).with(MONITORING_TOPIC);
//    }
//
//    @Qualifier("monitoringlistenerAdapter")
//    @Bean
//    MessageListenerAdapter monitoringlistenerAdapter(MonitoringListener receiver) {
//        MessageListenerAdapter msgadapter = new MessageListenerAdapter(receiver, "monitoringAlertReceived");
//        return msgadapter;
//    }
//
//    @Qualifier("monitoringcontainer")
//    @Bean
//    SimpleMessageListenerContainer monitoringcontainer(ConnectionFactory connectionFactory,
//            @Qualifier("monitoringlistenerAdapter") MessageListenerAdapter listenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(monitoringQueue().getName());
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }
    public static void main(String[] args) {
        SpringApplication.run(PolicyengineApplication.class, args);

        //read file into stream, try-with-resources
        String groupId = "tng.policy.rule";
        String version = "1.0.0-SNAPSHOT";
        try (Stream<String> stream = Files.lines(Paths.get("sample-kjar/pom.xml"))) {

            

            File file = new File("sample-kjar/pom.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            
            
            document.getDocumentElement().normalize();

	System.out.println("Root element :" + document.getDocumentElement().getNodeName());
           
           stream.forEach(System.out::println);
          
            String groupIdretrieved = document.getElementsByTagName("groupId").item(0).getTextContent();
            log.info("looking for node value...."+groupIdretrieved);
            
            document.getElementsByTagName("groupId").item(0).setTextContent(groupId);


            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);

            String xmlString = result.getWriter().toString();
            System.out.println(xmlString);
            
            Files.write(Paths.get("sample-kjar/pom.xml"), xmlString.getBytes());

        } catch (IOException e) {
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PolicyengineApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(PolicyengineApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(PolicyengineApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(PolicyengineApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

        //////////////
        KieServices ks = KieServices.Factory.get();

        String groupId2 = "org.drools.workshop";
        String artifactId2 = "my-second-drools-kjar";
        //String version2 = "1.1-SNAPSHOT";
        String version2 = "1.0.3-SNAPSHOT";

        ReleaseId releaseId2 = ks.newReleaseId(groupId2, artifactId2, version2);
        KieContainer kcontainer2 = ks.newKieContainer(releaseId2);
        KieScanner kscanner2 = ks.newKieScanner(kcontainer2);

        kscanner2.start(5000);

        String sessionname = "policy2";
        final KieSession ksession = kcontainer2.newKieSession(sessionname);
        //ksession.fireUntilHalt();

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
