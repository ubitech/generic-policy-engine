package com.example.policyengine.KieContainersManagement;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
public class ThreadsGroupFactory {
   
    private static volatile ThreadsGroup instance = null;
    
    public ThreadsGroupFactory() { 
    }//EoC
    
    public static ThreadsGroup getThreadGroup() {
        if (instance == null) {
            synchronized (ThreadsGroupFactory.class) {
                if (instance == null) {                   
                    instance = new ThreadsGroup();                                          
                }
            }
        }
        return instance;
    }//EoM    
}//EoC
