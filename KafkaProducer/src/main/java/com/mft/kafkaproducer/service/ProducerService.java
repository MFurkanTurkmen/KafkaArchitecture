package com.mft.kafkaproducer.service;

import com.mft.kafkaproducer.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class ProducerService {
    private final KafkaTemplate<String,ProductDto> kafkaTemplate;
    private final KafkaAdmin kafkaAdmin;
    private static int i=0;

    public ProducerService(KafkaTemplate<String, ProductDto> kafkaTemplate, KafkaAdmin kafkaAdmin) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaAdmin = kafkaAdmin;
    }


    public void sendKafka(String topicName) throws ExecutionException, InterruptedException {

        ProductDto productDto = new ProductDto("1"+i,"Laptop"+i,5000,10);
        ProductDto productDto1 = new ProductDto("2"+i,"Mouse"+i,50,100);
        ProductDto productDto2 = new ProductDto("3"+i,"Keyboard"+i,100,50);


       kafkaTemplate.send(topicName, productDto1.getId(), productDto);

        kafkaTemplate.send(topicName, productDto1.getId(), productDto1);
        kafkaTemplate.send(topicName, productDto1.getId(), productDto1);


    }


    public Map<String, TopicListing> listTopics() throws ExecutionException, InterruptedException {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            return adminClient.listTopics().namesToListings().get();
        }
    }

}
