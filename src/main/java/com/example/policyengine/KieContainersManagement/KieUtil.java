/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine.KieContainersManagement;

import java.util.concurrent.ConcurrentHashMap;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
@Component
public class KieUtil {

    private final ConcurrentHashMap threadmap = new ConcurrentHashMap();

    private void updateMap() {
        ThreadsGroup tg = ThreadsGroupFactory.getThreadGroup();
        tg.updateThreadMap(threadmap);
    }

    private void addToThreadMap(String factSessionName, KieSession kiesession) {
        ThreadsGroup tg = ThreadsGroupFactory.getThreadGroup();
        tg.addToThreadMap(factSessionName, kiesession);
    }

    private void removeFromThreadMap(String factSessionName) {
        ThreadsGroup tg = ThreadsGroupFactory.getThreadGroup();
        tg.removeFromThreadMap(factSessionName);
    }

    public ConcurrentHashMap seeThreadMap() {
        ThreadsGroup tg = ThreadsGroupFactory.getThreadGroup();
        return tg.getThreadMap();
    }

    public void fireKieSession(KieSession kieSession, String factSessionName) {

        Thread t = new Thread() {

            @Override
            public void run() {
                kieSession.fireUntilHalt();
            }
        };

        t.setName(factSessionName);
        //System.out.println("New session Thread creation" + t.toString());
        threadmap.put(factSessionName, kieSession);

        t.start();
        addToThreadMap(factSessionName, kieSession);

    }//EoM

    public void haltKieSession(String factSessionName) {
        KieSession kieSession =  (KieSession) threadmap.get(factSessionName);
        if (kieSession != null) {
            kieSession.halt();
            kieSession.dispose();
            //t.stop();
            threadmap.remove(kieSession);
            //updateMap();
            removeFromThreadMap(factSessionName);
        }//if

    }//EoM   

}