package cl.duoc.valledelsol.api_gateway.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class fallbackController {
    


    @RequestMapping("/errorReportes")
    public ResponseEntity<Map<String, Object>> fallbackReportes() {
        //Map el cual genera un "formulario" en blanco y lo definimos como response
        Map<String, Object> response = new HashMap<>();
        //Dentro del response agregaremos el mensaje de error especifico para el servicio
       response.put("message", "El servicio de reportes no está disponible en este momento. Por favor, inténtelo de nuevo más tarde.");
       //Retornamos un Status del ResponseEntity especificando que cuando el servicio no este disponible
       return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
        @RequestMapping("/errorAlarmas")
    public ResponseEntity<Map<String, Object>> fallbackAlarmas() {
       Map<String, Object> response = new HashMap<>();
       response.put("message", "El servicio de alarmas no está disponible en este momento. Por favor, inténtelo de nuevo más tarde.");
       return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
        @RequestMapping("/errorGeolocalizacion")
    public ResponseEntity<Map<String, Object>> fallbackGeolocalizacion() {
       Map<String, Object> response = new HashMap<>();
       response.put("message", "El servicio de geolocalización no está disponible en este momento. Por favor, inténtelo de nuevo más tarde.");
       return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
