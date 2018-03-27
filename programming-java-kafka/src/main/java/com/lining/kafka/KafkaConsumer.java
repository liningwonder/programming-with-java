package com.lining.kafka;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private static final int THREADS = 1;

    private static final int QUEUSIZE = 1000;
    private static final int CORESIZE = 4;
    private static final int MAXSIZE = 8;
    private static final int KEEPALIVESECONDS = 180;

    public KafkaConsumer(String brokers,
                         final List<String> topics,
                         String groupId,
                         int threads,
                         int taskQueueSize,
                         int coreSize,
                         int maxSize,
                         int keepAliveSeconds,
                         int pluginApiTaskQueueSize,
                         int pluginApiCoreSize,
                         int pluginApiMaxSize,
                         int pluginApiKeepAliveSeconds,
                         int pluginApiThreads,
                         int appPluginApiThreads) {
        if (threads < 1) {
            threads = THREADS;
        }
        if (pluginApiThreads < 1) {
            pluginApiThreads = THREADS;
        }
        if (appPluginApiThreads < 1) {
            appPluginApiThreads = THREADS;
        }
        if(taskQueueSize < 1){
            taskQueueSize = QUEUSIZE;
        }
        if(coreSize < 1){
            coreSize = CORESIZE;
        }
        if(maxSize < 1){
            maxSize = MAXSIZE;
        }
        if(keepAliveSeconds < 1){
            keepAliveSeconds = KEEPALIVESECONDS;
        }
        if(pluginApiTaskQueueSize < 1){
            pluginApiTaskQueueSize = QUEUSIZE;
        }
        if(pluginApiCoreSize < 1){
            pluginApiCoreSize = CORESIZE;
        }
        if(pluginApiMaxSize < 1){
            pluginApiMaxSize = MAXSIZE;
        }
        if(pluginApiKeepAliveSeconds < 1){
            pluginApiKeepAliveSeconds = KEEPALIVESECONDS;
        }
        //初始化工单接口等线程池

        // 不同的线程数配置，topics对应的顺序为：inform, pluginApi, appPluginApi
        logger.info("kafka topic list:{}", topics);
        for (int i = 0; i < topics.size(); i++) {
            String topic = topics.get(i);
            if(StringUtils.isBlank(topic)) {continue;}

            if(i == 0){
                // 创建inform消费线程
                buildThreads(threads, brokers, topic, topics, groupId);

            } else if (i == 1){
                // 创建pluginApi消费线程
                buildThreads(pluginApiThreads, brokers, topic, topics, groupId);

            } else if (i == 2) {
                // 创建appPluginApi消费线程
                buildThreads(appPluginApiThreads, brokers, topic, topics, groupId);

            } else {
                logger.info("未知topic：{}", topic);
            }
        }
    }

    // 创建消费线程
    private void buildThreads(int threads, String brokers,  String topicName, List<String> topics,String groupId) {
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            final ConsumerLoop consumer = new ConsumerLoop(brokers, topicName, topics, groupId);
            executor.submit(consumer);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    logger.info("shutdown kafka client...");
                    consumer.shutdown();
                    executor.shutdown();
                    try {
                        executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
