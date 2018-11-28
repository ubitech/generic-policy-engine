/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine.KieContainersManagement;

import java.util.concurrent.ConcurrentHashMap;
import org.kie.api.runtime.KieSession;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
public class ThreadsGroup {

    private ConcurrentHashMap threadmap = new ConcurrentHashMap();

    public synchronized ConcurrentHashMap getThreadMap() {
        return this.threadmap;
    }

    public synchronized void updateThreadMap(ConcurrentHashMap map) {
        this.threadmap = map;
    }

    public synchronized void addToThreadMap(String factSessionName, KieSession kiesession) {
        this.threadmap.put(factSessionName, kiesession);
    }

    public synchronized void removeFromThreadMap(String factSessionName) {
        this.threadmap.remove(factSessionName);
    }

}//