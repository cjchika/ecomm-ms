package com.cjchika.microservices.notification_service.service;

import com.cjchika.microservices.notification_service.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "order-placed")
    public void listen(OrderPlacedEvent orderPlacedEvent){
        log.info("Received Message from order-placed topic {}", orderPlacedEvent);

        MimeMessagePreparator messagePreparatory = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("springshop@exampl.com");
            messageHelper.setTo(orderPlacedEvent.getEmail());
            messageHelper.setSubject(String.format("Your Order with OrderNumber %s is placed successfully", orderPlacedEvent.getOrderNumber()));
            messageHelper.setText(String.format("""
                            Hello,
                            
                            Your order with order number %s is now placed successfully.
                            
                            Best regards
                            Spring Shop
                            """,
                    orderPlacedEvent.getOrderNumber()
            ));
        };  try {
            javaMailSender.send(messagePreparatory);
            log.info("Order notification email sent successfully!");
        } catch (MailException e){
            log.error("Exception occurred while sending mail", e);
            throw new RuntimeException("Exception occurred", e);
        }
    }

}
