package cl.duoc.valledelsol.ms_reportes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/reportes/test")
    public String testReporte() {
        return "Microservicio de Reportes responde OK";
    }
}