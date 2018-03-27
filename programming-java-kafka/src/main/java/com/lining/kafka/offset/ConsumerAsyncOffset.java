package com.lining.kafka.offset;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * description: 同一个消费者群组里面运行多个消费者，每个消费者运行在自己的线程上
 * 把消费者的逻辑封装在对象中，实现Runable接口，在外层使用ExecutorService开启多个
 * 线程处理
 * date 2018-03-27
 *
 * @author lining1
 * @version 1.0.0
 */
public class ConsumerAsyncOffset implements Runnable {
    private final KafkaConsumer<String, String> consumer;//消费者对象
    private final List<String> topics;//主题列表
    private final int id;//消费者群组id

    public ConsumerAsyncOffset(int id,
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
                }
                //异步提交偏移量，不会重试.
                //此方法支持使用回调来记录错误，不过使用一定极致来保证提交顺序
                consumer.commitAsync();
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
