package com.lining.kafka.base;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * description:
 * date 2018-03-26
 *
 * @author lining1
 * @version 1.0.0
 */
public class KafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);

    private final Consumer<String, String> consumer;

    private static final int THREADS = 1;

    private static final int QUEUSIZE = 1000;
    private static final int CORESIZE = 4;
    private static final int MAXSIZE = 8;
    private static final int KEEPALIVESECONDS = 180;

    public KafkaConsumer(String brokers, String groupId) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("group.id", groupId);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<String, String>(props);
    }

    public void subscribeMessage(List<String> topics) {
        LOG.info("KafkaConsumer subscribe topics : {}", topics.toString());
        consumer.subscribe(topics);
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(1000);//1000为超时时间
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.offset() + ": " + record.value());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
