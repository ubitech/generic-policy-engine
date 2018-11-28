/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine.facts;

import org.slf4j.LoggerFactory;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
public class SampleFact {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(SampleFact.class);

    private String value;
    private String deployed_graph;

    public SampleFact(String value, String deployed_graph) {
        this.deployed_graph = deployed_graph;
        this.value = value;

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDeployed_graph() {
        return deployed_graph;
    }

    public void setDeployed_graph(String deployed_graph) {
        this.deployed_graph = deployed_graph;
    }
    
    

    @Override
    public String toString() {
        return "SampleFact: {deployed_graph="+ deployed_graph +", value=" + value + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SampleFact that = (SampleFact) o;
        return this.value.equals(that.value) && this.deployed_graph.equals(that.deployed_graph);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode() + deployed_graph.hashCode();
        return result;
    }

    public void dosomething(String factvalue) {

        log.info("I did something " + factvalue);

    }

}
