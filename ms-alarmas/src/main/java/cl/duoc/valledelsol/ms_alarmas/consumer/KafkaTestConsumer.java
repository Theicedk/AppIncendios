package cl.duoc.valledelsol.ms_alarmas.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTestConsumer {

    @KafkaListener(topics = "topic-prueba-incendio", groupId = "ms-alarmas-grupo-prueba")
    public void escuchar(String mensaje) {
        System.out.println("Alarma recibió: " + mensaje);
    }
}