/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine.messaging;

import com.example.policyengine.PolicyengineApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import org.springframework.amqp.rabbit.annotation.RabbitListener;



/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@Component
public class MonitoringListener {

    private static Logger log = LoggerFactory.getLogger(MonitoringListener.class);

    @RabbitListener(queues = PolicyengineApplication.MONITORING_QUEUE)
    public void monitoringAlertReceived(byte[] message) {

        String messageasstring = new String(message, StandardCharsets.UTF_8);
        log.info("I received this message " + messageasstring);
    }

}
