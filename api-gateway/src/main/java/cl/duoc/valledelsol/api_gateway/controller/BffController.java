package cl.duoc.valledelsol.api_gateway.controller;

import cl.duoc.valledelsol.api_gateway.dto.DashboardDTO;
import cl.duoc.valledelsol.api_gateway.dto.FocoDTO;
import cl.duoc.valledelsol.api_gateway.dto.ReporteDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/bff")
public class BffController {

    private final WebClient webClient;

    public BffController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/dashboard-combinado")
    public Mono<DashboardDTO> getDashboard() {
        Mono<List<ReporteDTO>> reportesMono = webClient.get()
                .uri("http://localhost:8081/api/reportes")
                .retrieve()
                .bodyToFlux(ReporteDTO.class)
                .collectList();

        Mono<List<FocoDTO>> focosMono = webClient.get()
                .uri("http://localhost:8083/api/focos")
                .retrieve()
                .bodyToFlux(FocoDTO.class)
                .collectList();

        return Mono.zip(reportesMono, focosMono)
                .map(tuple -> new DashboardDTO(tuple.getT1(), tuple.getT2()));
    }
}
