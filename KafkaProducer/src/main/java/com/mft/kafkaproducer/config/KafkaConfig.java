package com.mft.kafkaproducer.config;

import com.mft.kafkaproducer.dto.PartitionDTO;
import com.mft.kafkaproducer.dto.TopicDetailsDTO;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private AdminClient getAdminClient() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return AdminClient.create(properties);
    }

    // Create new topic
    public void createTopic(String topicName, int numPartitions, short replicationFactor) {
        try (AdminClient adminClient = getAdminClient()) {
            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);

            CreateTopicsResult result = adminClient.createTopics(Collections.singleton(newTopic));
            result.all().get();
            System.out.println("Topic " + topicName + " created successfully");
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to create topic: " + e.getMessage());
        }
    }


    // Delete topic
    public void deleteTopic(String topicName) {
        try (AdminClient adminClient = getAdminClient()) {
            DeleteTopicsResult result = adminClient.deleteTopics(Collections.singleton(topicName));
            result.all().get(); // Wait for completion
            System.out.println("Topic " + topicName + " deleted successfully");
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete topic: " + e.getMessage());
        }
    }

    // Get partition count
    public int getPartitionCount(String topicName) {
        try (AdminClient adminClient = getAdminClient()) {
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singleton(topicName));
            return result.topicNameValues().get(topicName).get().partitions().size();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get partition count: " + e.getMessage());
        }
    }



    public int getReplicationFactor(String topicName) {
        try (AdminClient adminClient = getAdminClient()) {
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singleton(topicName));
            List<TopicPartitionInfo> partitions = result.topicNameValues().get(topicName).get().partitions();
            // Assuming all partitions have the same replication factor
            return partitions.get(0).replicas().size();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get replication factor: " + e.getMessage());
        }
    }

    // get partitions detail by topic name. no Stream api
    public List<PartitionDTO> getPartitionDetails(String topicName) {
        try (AdminClient adminClient = getAdminClient()) {
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singleton(topicName));
            List<TopicPartitionInfo> partitions = result.topicNameValues().get(topicName).get().partitions();

            List<PartitionDTO> partitionDetails = new ArrayList<>();
            for (TopicPartitionInfo partitionInfo : partitions) {
                String leader = partitionInfo.leader().idString();  // get leader id as string
                List<String> replicas = partitionInfo.replicas().stream()
                        .map(Node::idString)
                        .collect(Collectors.toList());
                List<String> isr = partitionInfo.isr().stream()
                        .map(Node::idString)
                        .collect(Collectors.toList());

                partitionDetails.add(new PartitionDTO(partitionInfo.partition(), leader, replicas, isr));
            }

            return partitionDetails;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get partition details: " + e.getMessage());
        }
    }



    public TopicDetailsDTO getTopicDetails(String topicName) {
        try (AdminClient adminClient = getAdminClient()) {
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singleton(topicName));
            List<TopicPartitionInfo> partitions = result.topicNameValues().get(topicName).get().partitions();

            int totalPartitions = partitions.size();
            int totalReplicas = partitions.stream()
                    .mapToInt(partition -> partition.replicas().size())
                    .sum();

            // Map partition ID to list of broker IDs
            Map<Integer, List<String>> partitionToBrokers = new HashMap<>();
            for (TopicPartitionInfo partitionInfo : partitions) {
                List<String> brokerIds = new ArrayList<>();
                for (Node replica : partitionInfo.replicas()) {
                    brokerIds.add(replica.idString()); // Add the broker ID to the list
                }
                partitionToBrokers.put(partitionInfo.partition(), brokerIds);
            }
            var partitionDetails=getPartitionDetails(topicName);

            return new TopicDetailsDTO(topicName, totalPartitions, totalReplicas, partitionToBrokers,partitionDetails);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get topic details: " + e.getMessage());
        }
    }



}
