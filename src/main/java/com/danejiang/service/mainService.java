package com.danejiang.service;

import com.danejiang.activemq.activemq;
import com.danejiang.activemq.kafka;

import java.util.logging.Logger;

public class mainService {
    private static final transient Logger logger = Logger.getLogger("mainService");

    public static void main(String args[]) {
        //activemq生产者发送消息
        activemq.send("Topic", "DaneJiang", "Hello");

        //kafka生产者发送消息
        kafka.send("DaneJiang","Hello");
    }

    public static void start() {
        try {
            //activemq消费者启动
            activemq.receive("Topic", "DaneJiang");

            //kafka消费者启动
            kafka.receive();

            logger.info("MainService start success.");
        } catch (Exception ex) {
            logger.info("MainService start error:" + ex.toString());
        }
    }

    public static void stop() {
        try {
            //activemq停止
            activemq.stop();

            //kafka停止
            kafka.stop();
        } catch (Exception ex) {
            logger.info("MainService stop error:" + ex.toString());
        }
    }

    public static String doMQ(String topicName, String topicMessage) {
        try {
            String result = "";
            switch (topicName.toUpperCase()) {
                case "DANEJIANG":
                    result = topicMessage + " World!";
                    break;
                default:
                    result = "Receive Error Type:Type=" + topicName + ",Message=" + topicMessage;
                    break;
            }

            return result;
        } catch (Exception ex) {
            logger.info("doMQ error:" + ex.toString());
            return ex.toString();
        }
    }

    public static String doKafka(String topicName, String topicMessage) {
        try {
            String result = "";
            switch (topicName.toUpperCase()) {
                case "DANEJIANG":
                    result = topicMessage + " World!";
                    break;
                default:
                    result = "Receive Error Type:Type=" + topicName + ",Message=" + topicMessage;
                    break;
            }

            return result;
        } catch (Exception ex) {
            logger.info("doKafka error:" + ex.toString());
            return ex.toString();
        }
    }
}
