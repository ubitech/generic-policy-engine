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
    private String deployed_graph;

    public MonitoredComponent(String name, String metric, double value, String deployed_graph) {
        this.name = name;
        this.metric = metric;
        this.value = value;
        this.deployed_graph = deployed_graph;
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

    public String getDeployed_graph() {
        return deployed_graph;
    }

    public void setDeployed_graph(String deployed_graph) {
        this.deployed_graph = deployed_graph;
    }


    @Override
    public String toString() {
        return "MonitoredComponent: { name=\"" + name + "\""
                + ",metric=\"" + metric + "\""
                + ", value=" + value
                + ",deployed_graph=\"" + deployed_graph + "\"}";
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
        return this.value == that.value && this.name.equals(that.name) && this.deployed_graph.equals(that.deployed_graph);
    }

    @Override
    public int hashCode() {
        int result = deployed_graph.hashCode();
        result = (int) (31 * result + value);
        return result;
    }

}
