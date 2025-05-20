//package org.vrk.accounting.service.kafka.Producer;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//import org.vrk.accounting.domain.Act;
//
//@Service
//public class ActProducer {
//
//    private static final String TOPIC = "act-topic";
//
//    @Autowired
//    private KafkaTemplate<String, Act> kafkaTemplate;
//    public void send(Act act) {
//        kafkaTemplate.send(TOPIC, act);
//    }
//}
