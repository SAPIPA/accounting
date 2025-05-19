package org.vrk.accounting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vrk.accounting.domain.kafka.Notification;
import org.vrk.accounting.service.kafka.Producer.NotificationProducer;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationProducer notificationProducer;

    @PostMapping
    public void sendNotification(@RequestBody Notification notification) {
        notificationProducer.sendNotification(notification);
    }
}
