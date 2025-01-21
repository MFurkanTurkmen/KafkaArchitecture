package com.mft.kafkaconsumer.service;

import com.mft.kafkaconsumer.dto.ProductDto;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

@Service
public class ConsumerListener {


    @KafkaListener(topics = "p2r3")
    public void listen2(ConsumerRecord<String, ProductDto> message){
        System.out.println("key: "+message.key());
        System.out.println("value: "+message.value());
        System.out.println("offset: "+message.offset());
        System.out.println("partition: "+message.partition());
        System.out.println("name alanııı: "+message.value().getName());

    }


    @KafkaListener(topics = "p3r3")
    public void listen3(ConsumerRecord<String, ProductDto> message){
        System.out.println("key: "+message.key());
        System.out.println("value: "+message.value());
        System.out.println("offset: "+message.offset());
        System.out.println("partition: "+message.partition());
        System.out.println("name alanııı: "+message.value().getName());

    }
    @KafkaListener(topicPartitions = @TopicPartition(topic = "test-topic", partitions = {"0", "1", "2"}))
    public void listenToAllPartitions(ConsumerRecord<String, ProductDto> message) {
        System.out.println("key: " + message.key());
        System.out.println("value: " + message.value());
        System.out.println("offset: " + message.offset());
        System.out.println("partition: " + message.partition());
        System.out.println("name: " + message.value().getName());
    }

}
