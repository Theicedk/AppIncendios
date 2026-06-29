package cl.duoc.valledelsol.ms_reportes.service;

import cl.duoc.valledelsol.ms_reportes.dto.ReporteDTO;
import cl.duoc.valledelsol.ms_reportes.dto.ReporteListaDTO;
import cl.duoc.valledelsol.ms_reportes.entity.Reporte;
import cl.duoc.valledelsol.ms_reportes.enums.EstadoIncendio;
import cl.duoc.valledelsol.ms_reportes.exception.IllegalStateTransitionException;
import cl.duoc.valledelsol.ms_reportes.repository.ReporteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock private ReporteRepository reporteRepository;
    @Mock private KafkaTemplate<String, String> kafkaTemplate;
    @Mock private ObjectMapper objectMapper;

    private ReporteServiceImpl service;

    @BeforeEach
    void setUp() {
        // Inicializa el servicio inyectando los mocks creados
        service = new ReporteServiceImpl(reporteRepository, kafkaTemplate, objectMapper);
    }

    @Test
    @DisplayName("Transición REPORTADO -> EN_CORROBORACION debe ser válida")
    void testCorroborar_desdeReportado_ok() {
        Reporte r = new Reporte();
        r.setId(1L);
        r.setEstadoIncendio(EstadoIncendio.REPORTADO);

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reporteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReporteListaDTO resultado = service.iniciarCorroboracion(1L);

        assertThat(resultado.estado()).isEqualTo("EN_CORROBORACION");
    }

    @Test
    @DisplayName("Transición REPORTADO -> VERIFICADO debe ser inválida (debe fallar)")
    void testVerificar_desdeReportado_lanzaExcepcion() {
        Reporte r = new Reporte();
        r.setId(1L);
        r.setEstadoIncendio(EstadoIncendio.REPORTADO);

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> service.verificarReporte(1L))
            .isInstanceOf(IllegalStateTransitionException.class);
    }

    @Test
    @DisplayName("Verificar un reporte en estado correcto debe publicar un evento en Kafka")
    void testVerificar_publicaKafka() throws Exception {
        Reporte r = new Reporte();
        r.setId(1L);
        r.setEstadoIncendio(EstadoIncendio.EN_CORROBORACION);
        r.setLatitud(-33.45);
        r.setLongitud(-70.66);
        r.setGrupoId(100L);

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reporteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        service.verificarReporte(1L);

        verify(kafkaTemplate, times(1)).send(eq("topic-prueba-incendio"), anyString());
    }

    @Test
    @DisplayName("Al pasar a ATENDIDO, todos los reportes del mismo grupo deben cerrarse masivamente")
    void testAtender_cierraGrupoCompleto() {
        Reporte r = new Reporte();
        r.setId(1L);
        r.setEstadoIncendio(EstadoIncendio.VERIFICADO);
        r.setGrupoId(555L);

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reporteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.atenderReporte(1L);

        verify(reporteRepository).cerrarGrupo(555L, EstadoIncendio.ATENDIDO, 1L);
    }

    @Test
    @DisplayName("Valores legacy como ACTIVO deben mapearse a REPORTADO")
    void testLegacyActivoValueMapsToReported() {
        assertEquals(EstadoIncendio.REPORTADO, EstadoIncendio.fromLegacyValue("ACTIVO"));
    }
}