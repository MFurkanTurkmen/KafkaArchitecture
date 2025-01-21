package com.mft.kafkaproducer.dto;

import java.util.List;

public class PartitionDTO {
    private int partitionId;
    private String leader;
    private List<String> replicas;
    private List<String> isr;

    // Constructor
    public PartitionDTO(int partitionId, String leader, List<String> replicas, List<String> isr) {
        this.partitionId = partitionId;
        this.leader = leader;
        this.replicas = replicas;
        this.isr = isr;
    }

    // Getters and setters
    public int getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public List<String> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<String> replicas) {
        this.replicas = replicas;
    }

    public List<String> getIsr() {
        return isr;
    }

    public void setIsr(List<String> isr) {
        this.isr = isr;
    }
}

