package com.mft.kafkaproducer.dto;
import java.util.List;
import java.util.Map;
public class TopicDetailsDTO {
    private String topicName;
    private int totalPartitions;
    private int totalReplicas;
    private Map<Integer, List<String>> partitionToBrokers; // Partition ID -> List of Broker IDs
    private List<PartitionDTO> partitionDTOS;

    // Constructor
    public TopicDetailsDTO(String topicName, int totalPartitions, int totalReplicas, Map<Integer, List<String>> partitionToBrokers,List<PartitionDTO> partitionDTOS) {
        this.topicName = topicName;
        this.totalPartitions = totalPartitions;
        this.totalReplicas = totalReplicas;
        this.partitionToBrokers = partitionToBrokers;
        this.partitionDTOS = partitionDTOS;
    }

    public List<PartitionDTO> getPartitionDTOS() {
        return partitionDTOS;
    }

    public void setPartitionDTOS(List<PartitionDTO> partitionDTOS) {
        this.partitionDTOS = partitionDTOS;
    }

    // Getters and Setters
    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getTotalPartitions() {
        return totalPartitions;
    }

    public void setTotalPartitions(int totalPartitions) {
        this.totalPartitions = totalPartitions;
    }

    public int getTotalReplicas() {
        return totalReplicas;
    }

    public void setTotalReplicas(int totalReplicas) {
        this.totalReplicas = totalReplicas;
    }

    public Map<Integer, List<String>> getPartitionToBrokers() {
        return partitionToBrokers;
    }

    public void setPartitionToBrokers(Map<Integer, List<String>> partitionToBrokers) {
        this.partitionToBrokers = partitionToBrokers;
    }
}
