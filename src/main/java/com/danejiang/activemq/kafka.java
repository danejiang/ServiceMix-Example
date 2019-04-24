package com.danejiang.activemq;

import com.danejiang.service.mainService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

public class kafka {
    private static final transient Logger logger = Logger.getLogger("kafka");

    public static boolean send(String topicName, String topicMessage) {
        try {
            Thread.currentThread().setContextClassLoader(null);
            Properties props = new Properties();
            props.put("bootstrap.servers", "hadoop01:9092");
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

            Producer<String, String> producer = null;

            try {
                producer = new KafkaProducer<>(props);
                producer.send(new ProducerRecord<String, String>(topicName, topicMessage));
            } catch (Exception e) {
                logger.info("Send Kafka Message error:" + e.toString());
                return false;
            } finally {
                producer.close();
            }

            logger.info("Send Kafka Message:" + topicName);

            return true;
        } catch (Exception ex) {
            logger.info("Send Kafka Message error:" + ex.toString());
            return false;
        }
    }

    private static KafkaConsumer<String, String> kafkaConsumer = null;
    public static boolean receive() {
        try {
            Thread.currentThread().setContextClassLoader(null);
            Properties props = new Properties();
            props.put("bootstrap.servers", "hadoop01:9092");
            props.put("group.id", "Group-1");
            props.put("enable.auto.commit", "true");
            props.put("auto.commit.interval.ms", "1000");
            props.put("auto.offset.reset", "earliest");
            props.put("session.timeout.ms", "30000");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

            kafkaConsumer = new KafkaConsumer<>(props);
            kafkaConsumer.subscribe(Collections.singletonList("test"));

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            new Thread(df.format(new Date())) {
                public void run() {
                    while (true) {
                        ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                        for (ConsumerRecord<String, String> record : records) {
                            logger.info("Receive Kafka Message:" + record.topic()+",Result:"+ mainService.doKafka(record.topic(),record.value()));
                        }
                    }
                }
            }.start();

            return true;
        } catch (Exception e) {
            logger.info("Start receive Kafka Message error:" + e.toString());
            return false;
        }
    }

    public static void stop() {
        try {
            // 关闭会话和连接
            if (kafkaConsumer != null) kafkaConsumer.close();

            logger.info("Stop Kafka Message.");
        } catch (Exception e) {
            logger.info("Stop Kafka Message error:" + e.toString());
        }
    }
}
