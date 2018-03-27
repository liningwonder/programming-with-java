package com.lining.kafka.offset;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * description:
 * date 2018-03-27
 *
 * @author lining1
 * @version 1.0.0
 */
public class ConsumerSyncOffset implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerSyncOffset.class);

    private final KafkaConsumer<String, String> consumer;//消费者对象
    private final List<String> topics;//主题列表
    private final int id;//消费者群组id

    public ConsumerSyncOffset(int id,
                              String groupId,
                              List<String> topics) {
        this.id = id;
        this.topics = topics;
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(topics);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
                for (ConsumerRecord<String, String> record : records) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("partition", record.partition());//分区
                    data.put("offset", record.offset());//分区偏移量
                    data.put("key", record.key());//键
                    data.put("value", record.value());//值
                    System.out.println(this.id + ": " + data);
                    //阻塞方式手动提交偏移量
                    try {
                        //此方法会一直尝试提交偏移量，阻塞方式会降低效率
                        consumer.commitSync();
                    } catch (CommitFailedException e) {
                        e.printStackTrace();
                        LOG.error("commit failed", e);
                    }
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
}
