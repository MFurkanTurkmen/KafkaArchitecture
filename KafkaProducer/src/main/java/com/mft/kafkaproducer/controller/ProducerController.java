package com.mft.kafkaproducer.controller;

import com.mft.kafkaproducer.config.KafkaConfig;
import com.mft.kafkaproducer.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@RequestMapping("/producer")
@RestController
public class ProducerController {
    private final ProducerService producerService;
    private final KafkaConfig kafkaConfig;

    public ProducerController(ProducerService producerService, KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;

        this.producerService = producerService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTopic(
            @RequestParam String topicName,
            @RequestParam int partitions,
            @RequestParam short replicationFactor) {
        kafkaConfig.createTopic(topicName, partitions,replicationFactor);
        return ResponseEntity.ok("Topic created successfully: " + topicName);
    }

    @GetMapping("list")
    public ResponseEntity<?> list() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(        producerService.listTopics());
    }


    @GetMapping("send1")
    public ResponseEntity<?> sendeKfka(String topicName) throws ExecutionException, InterruptedException {
        producerService.sendKafka(topicName);
        return ResponseEntity.ok("Mesaj gonderildi.");
    }


    @GetMapping("/topic-replica-sayisi")
    public ResponseEntity<Integer> replicaSayisi(String ad) {
        return ResponseEntity.ok(kafkaConfig.getReplicationFactor(ad));
    }



    @DeleteMapping("/delete/{topicName}")
    public ResponseEntity<String> deleteTopic(@PathVariable String topicName) {
        kafkaConfig.deleteTopic(topicName);
        return ResponseEntity.ok("Topic deleted successfully: " + topicName);
    }


    @GetMapping("/partitions-list/{topicName}")
    public ResponseEntity<Integer> getPartitionList(@PathVariable String topicName) {
        return ResponseEntity.ok(kafkaConfig.getPartitionCount(topicName));
    }


    @GetMapping("/partition-detail/{topicName}")
    public ResponseEntity<?> getPartitionDetail(@PathVariable String topicName) {
        return ResponseEntity.ok(kafkaConfig.getPartitionDetails(topicName));
    }



    @GetMapping("/topic-detail/{topicName}")
    public ResponseEntity<?> getTopicDetails(@PathVariable String topicName) {
        return ResponseEntity.ok(kafkaConfig.getTopicDetails(topicName));
    }

}
