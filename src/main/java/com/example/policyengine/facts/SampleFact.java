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

    public SampleFact(String value) {

        this.value = value;

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SampleFact: {value=" + value + "}";
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
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        return result;
    }

    public void dosomething(String factvalue) {
        
        log.info("I did something " + factvalue);

    }

}