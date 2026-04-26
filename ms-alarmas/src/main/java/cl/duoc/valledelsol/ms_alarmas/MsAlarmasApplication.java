package cl.duoc.valledelsol.ms_alarmas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsAlarmasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAlarmasApplication.class, args);
		System.out.println("Hola Mundo desde el microservicio de alarmas");
	}

}
