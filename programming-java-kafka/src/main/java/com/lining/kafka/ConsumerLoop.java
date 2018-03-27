package com.lining.kafka;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConsumerLoop implements Runnable {

    private final org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer;

    private static Logger logger = LoggerFactory.getLogger(ConsumerLoop.class);

    private String topicName;

    private List<String> topics;

    public ConsumerLoop(String brokers,
                        String topicName,
                        List<String> topics,
                        String groupId) {
        this.topics = topics;
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new org.apache.kafka.clients.consumer.KafkaConsumer(props);
        this.topicName = topicName;
        this.topics = topics;
    }

    @Override
    public void run() {
        try {
            List<String> topicList = new ArrayList<>();
            topicList.add(topicName);
            //订阅主题
            consumer.subscribe(topicList);

            logger.info("kafka consumer started for topic: {}", topicName);

            //轮询
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
                //遍历记录列表
                for (ConsumerRecord<String, String> record : records) {
                    logger.debug("KafkaConsumer receive message, key:{}, value:{}", record.key(), record.value());
                    logger.info("KafkaConsumer receive message, topic:{}", topicName);
                    if (record.topic().equals(topics.get(0))) {
                        //使用线程池获取线程处理记录

                    }else if(record.topic().equals(topics.get(1))){

                    }else if(record.topic().equals(topics.get(2))){

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("kafka消费异常",e);
        } finally {
            logger.info("consumer.close!!!");
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
    
}
