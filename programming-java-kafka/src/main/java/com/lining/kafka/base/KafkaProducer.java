package com.lining.kafka.base;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
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
public class KafkaProducer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

    private final Producer<String, String> producer;

    private final String topic;

    public KafkaProducer(String brokers, List<String> topics) {
        if (topics == null || topics.size() == 0) {
            throw new IllegalArgumentException("topic不能为空");
        }
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("acks", "all"); //确保消息可以送达
        props.put("linger.ms", 1); //可以容忍1ms的延时
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new org.apache.kafka.clients.producer.KafkaProducer(props);
        this.topic = topics.get(0);
    }

    public void sendMessage(String key, String data) {
        LOG.info("KafkaProducer adds the record to a buffer: [topic:{},key:{},value:{}]", topic, key, data);
        producer.send(new ProducerRecord(this.topic, key, data));
    }

    public void sendMessageForTopic(String StrTopic, String key, String data) {
        LOG.info("KafkaProducer adds the record to a buffer: [topic:{},key:{},value:{}]", StrTopic, key, data);
        producer.send(new ProducerRecord(StrTopic, key, data));
    }
}
