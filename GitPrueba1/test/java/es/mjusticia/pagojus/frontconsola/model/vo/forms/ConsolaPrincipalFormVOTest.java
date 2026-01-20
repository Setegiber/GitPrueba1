package es.mjusticia.pagojus.frontconsola.model.vo.forms;

/*-
 * #%L
 * pagojus-frontconsola
 * %%
 * Copyright (C) 2023 - 2025 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
 * %%
 * Licencia con arreglo a la EUPL, Versión 1.2 o –en cuanto
 *  sean aprobadas por la Comisión Europea– versiones
 *  posteriores de la EUPL (la «Licencia»)
 *  Solo podrá usarse esta obra si se respeta la Licencia.
 *  Puede obtenerse una copia de la Licencia en:
 * 
 *  https://joinup.ec.europa.eu/software/page/eupl
 * 
 *  Salvo cuando lo exija la legislación aplicable o se acuerde
 *  por escrito, el programa distribuido con arreglo a la
 *  Licencia se distribuye «TAL CUAL»,
 *  SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas
 *  ni implícitas.
 *  Véase la Licencia en el idioma concreto que rige
 *  los permisos y limitaciones que establece la Licencia
 * #L%
 */

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import es.mjusticia.pagojus.frontconsola.enums.Estados;
import es.mjusticia.pagojus.frontconsola.enums.EstadosPermitidos;
import es.mjusticia.pagojus.frontconsola.utils.Utils;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConsolaPrincipalFormVOTest {
  private ConsolaPrincipalFormVO consolaPrincipalFormVO;

  @BeforeEach
  public void setUp() {
    consolaPrincipalFormVO = new ConsolaPrincipalFormVO();
  }

  @Test
  public void testGetSetFiltroIdTasa() {
    String filtroIdTasa = "TASA001";
    consolaPrincipalFormVO.setFiltroIdTasa(filtroIdTasa);
    assertEquals(filtroIdTasa, consolaPrincipalFormVO.getFiltroIdTasa());
  }

  @Test
  public void testGetSetFiltroLocalizador() {
    String filtroLocalizador = "LOC123";
    consolaPrincipalFormVO.setFiltroLocalizador(filtroLocalizador);
    assertEquals(filtroLocalizador, consolaPrincipalFormVO.getFiltroLocalizador());
  }

  @Test
  public void testGetSetFiltroNombreTasa() {
    String filtroNombreTasa = "Tasa de Ejemplo";
    consolaPrincipalFormVO.setFiltroNombreTasa(filtroNombreTasa);
    assertEquals(filtroNombreTasa, consolaPrincipalFormVO.getFiltroNombreTasa());
  }

  @Test
  public void testGetSetFiltroNRC() {
    String filtroNRC = "NRC456";
    consolaPrincipalFormVO.setFiltroNRC(filtroNRC);
    assertEquals(filtroNRC, consolaPrincipalFormVO.getFiltroNRC());
  }

  @Test
  public void testGetSetFechaPagoDesde() {
    String fechaPagoDesde = "2023-01-01";
    consolaPrincipalFormVO.setFechaPagoDesde(fechaPagoDesde);
    assertEquals(fechaPagoDesde, consolaPrincipalFormVO.getFechaPagoDesde());
  }

  @Test
  public void testGetSetFechaPagoHasta() {
    String fechaPagoHasta = "2023-12-31";
    consolaPrincipalFormVO.setFechaPagoHasta(fechaPagoHasta);
    assertEquals(fechaPagoHasta, consolaPrincipalFormVO.getFechaPagoHasta());
  }

  @Test
  public void testGetSetFiltroNombrePagador() {
    String filtroNombrePagador = "Juan Pérez";
    consolaPrincipalFormVO.setFiltroNombrePagador(filtroNombrePagador);
    assertEquals(filtroNombrePagador, consolaPrincipalFormVO.getFiltroNombrePagador());
  }

  @Test
  public void testGetSetFiltroDocumentoPagador() {
    String filtroDocumentoPagador = "12345678A";
    consolaPrincipalFormVO.setFiltroDocumentoPagador(filtroDocumentoPagador);
    assertEquals(filtroDocumentoPagador, consolaPrincipalFormVO.getFiltroDocumentoPagador());
  }

  @Test
  public void testGetSetFiltroNombreEntidad() {
    String filtroNombreEntidad = "Entidad Ejemplo";
    consolaPrincipalFormVO.setFiltroNombreEntidad(filtroNombreEntidad);
    assertEquals(filtroNombreEntidad, consolaPrincipalFormVO.getFiltroNombreEntidad());
  }

  @Test
  public void testGetSetFiltroDocumentoEntidad() {
    String filtroDocumentoEntidad = "87654321B";
    consolaPrincipalFormVO.setFiltroDocumentoEntidad(filtroDocumentoEntidad);
    assertEquals(filtroDocumentoEntidad, consolaPrincipalFormVO.getFiltroDocumentoEntidad());
  }

  @Test
  public void testGetSetFiltroRazonSocial() {
    String filtroRazonSocial = "Empresa S.A.";
    consolaPrincipalFormVO.setFiltroRazonSocial(filtroRazonSocial);
    assertEquals(filtroRazonSocial, consolaPrincipalFormVO.getFiltroRazonSocial());
  }

  @Test
  public void testGetSetFiltroEstadoTasa() {
    String filtroEstadoTasa = "Activo";
    consolaPrincipalFormVO.setFiltroEstadoTasa(filtroEstadoTasa);
    assertEquals(filtroEstadoTasa, consolaPrincipalFormVO.getFiltroEstadoTasa());
  }

  @Test
  public void testInitialValues() {
    // Verificar que los valores iniciales son null
    assertNull(consolaPrincipalFormVO.getFiltroIdTasa());
    assertNull(consolaPrincipalFormVO.getFiltroLocalizador());
    assertNull(consolaPrincipalFormVO.getFiltroNombreTasa());
    assertNull(consolaPrincipalFormVO.getFiltroNRC());
    assertNull(consolaPrincipalFormVO.getFechaPagoDesde());
    assertNull(consolaPrincipalFormVO.getFechaPagoHasta());
    assertNull(consolaPrincipalFormVO.getFiltroNombrePagador());
    assertNull(consolaPrincipalFormVO.getFiltroDocumentoPagador());
    assertNull(consolaPrincipalFormVO.getFiltroNombreEntidad());
    assertNull(consolaPrincipalFormVO.getFiltroDocumentoEntidad());
    assertNull(consolaPrincipalFormVO.getFiltroRazonSocial());
    assertNull(consolaPrincipalFormVO.getFiltroEstadoTasa());
  }
}
