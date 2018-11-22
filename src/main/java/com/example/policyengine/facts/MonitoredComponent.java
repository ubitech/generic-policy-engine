/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.policyengine.facts;

/**
 *
 * @author Eleni Fotopoulou <efotopoulou@ubitech.eu>
 */
public class MonitoredComponent {

    private String name;
    private String metric;
    private double value;
    private String groundedGraphid;

    public MonitoredComponent(String name, String metric, double value, String groundedGraphid) {
        this.name = name;
        this.metric = metric;
        this.value = value;
        this.groundedGraphid = groundedGraphid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getGroundedGraphid() {
        return groundedGraphid;
    }

    public void setGroundedGraphid(String groundedGraphid) {
        this.groundedGraphid = groundedGraphid;
    }


    @Override
    public String toString() {
        return "MonitoredComponent: { name=\"" + name + "\""
                + ",metric=\"" + metric + "\""
                + ", value=" + value
                + ",groundedGraphid=\"" + groundedGraphid + "\"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MonitoredComponent that = (MonitoredComponent) o;
        return this.value == that.value && this.name.equals(that.name) && this.groundedGraphid.equals(that.groundedGraphid);
    }

    @Override
    public int hashCode() {
        int result = groundedGraphid.hashCode();
        result = (int) (31 * result + value);
        return result;
    }

}
