package com.example.model;

import javax.xml.bind.annotation.XmlElement;

class ConversionFactor {
    private String sourceUnit;
    private String targetUnit;
    private double factor;

    @XmlElement
    public String getSourceUnit() {
        return sourceUnit;
    }

    public void setSourceUnit(String sourceUnit) {
        this.sourceUnit = sourceUnit;
    }

    @XmlElement
    public String getTargetUnit() {
        return targetUnit;
    }

    public void setTargetUnit(String targetUnit) {
        this.targetUnit = targetUnit;
    }

    @XmlElement
    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }
}