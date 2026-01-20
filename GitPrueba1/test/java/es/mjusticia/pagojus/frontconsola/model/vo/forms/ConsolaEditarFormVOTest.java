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
public class ConsolaEditarFormVOTest {
  private ConsolaEditarFormVO consolaEditarFormVO;

  @BeforeEach
  public void setUp() {
    consolaEditarFormVO = new ConsolaEditarFormVO();
  }

  @Test
  public void testGetSetNrc() {
    String nrc = "NRC123";
    consolaEditarFormVO.setNrc(nrc);
    assertEquals(nrc, consolaEditarFormVO.getNrc());
  }

  @Test
  public void testGetSetExpediente() {
    String expediente = "EXP456";
    consolaEditarFormVO.setExpediente(expediente);
    assertEquals(expediente, consolaEditarFormVO.getExpediente());
  }

  @Test
  public void testGetSetEstadoTasa() {
    String estadoTasa = "VALIDO";
    consolaEditarFormVO.setEstadoTasa(estadoTasa);
    assertEquals(estadoTasa, consolaEditarFormVO.getEstadoTasa());
  }

  @Test
  public void testGetSetIdPago() {
    String idPago = "Pago789";
    consolaEditarFormVO.setIdPago(idPago);
    assertEquals(idPago, consolaEditarFormVO.getIdPago());
  }

  @Test
  public void testGetSetIdLocalizador() {
    String idLocalizador = "Loc123";
    consolaEditarFormVO.setIdLocalizador(idLocalizador);
    assertEquals(idLocalizador, consolaEditarFormVO.getIdLocalizador());
  }

  @Test
  public void testGetSetCodTasa() {
    String codTasa = "TASA001";
    consolaEditarFormVO.setCodTasa(codTasa);
    assertEquals(codTasa, consolaEditarFormVO.getCodTasa());
  }

  @Test
  public void testGetSetDescTasa() {
    String descTasa = "Descripción de la Tasa";
    consolaEditarFormVO.setDescTasa(descTasa);
    assertEquals(descTasa, consolaEditarFormVO.getDescTasa());
  }

  @Test
  public void testGetSetImporte() {
    String importe = "123.45";
    consolaEditarFormVO.setImporte(importe);
    assertEquals(importe, consolaEditarFormVO.getImporte());
  }

  @Test
  public void testGetSetDocumentoPagador() {
    String documentoPagador = "12345678A";
    consolaEditarFormVO.setDocumentoPagador(documentoPagador);
    assertEquals(documentoPagador, consolaEditarFormVO.getDocumentoPagador());
  }

  @Test
  public void testGetSetDocumentoRepresentante() {
    String documentoRepresentante = "87654321B";
    consolaEditarFormVO.setDocumentoRepresentante(documentoRepresentante);
    assertEquals(documentoRepresentante, consolaEditarFormVO.getDocumentoRepresentante());
  }

  @Test
  public void testGetSetNombreCompletoRepresentante() {
    String nombreCompletoRepresentante = "Ana López";
    consolaEditarFormVO.setNombreCompletoRepresentante(nombreCompletoRepresentante);
    assertEquals(nombreCompletoRepresentante, consolaEditarFormVO.getNombreCompletoRepresentante());
  }

  @Test
  public void testGetSetImporteTasa() {
    String importeTasa = "456.78";
    consolaEditarFormVO.setImporteTas(importeTasa);
    assertEquals(importeTasa, consolaEditarFormVO.getImporteTasa());
  }

  @Test
  public void testGetSetFhPago() {
    String fhPago = "2023-12-31";
    consolaEditarFormVO.setFhPago(fhPago);
    assertEquals(fhPago, consolaEditarFormVO.getFhPago());
  }

  @Test
  public void testGetSetNombreCompleto() {
    String nombreCompleto = "Juan Pérez";
    consolaEditarFormVO.setNombreCompleto(nombreCompleto);
    assertEquals(nombreCompleto, consolaEditarFormVO.getNombreCompleto());
  }

  @Test
  public void testGetSetCodAplicacion() {
    String codAplicacion = "App123";
    consolaEditarFormVO.setCodAplicacion(codAplicacion);
    assertEquals(codAplicacion, consolaEditarFormVO.getCodAplicacion());
  }

  @Test
  public void testInitialValues() {
    // Verificar que los valores iniciales son null
    assertNull(consolaEditarFormVO.getNrc());
    assertNull(consolaEditarFormVO.getExpediente());
    assertNull(consolaEditarFormVO.getEstadoTasa());
    assertNull(consolaEditarFormVO.getIdPago());
    assertNull(consolaEditarFormVO.getIdLocalizador());
    assertNull(consolaEditarFormVO.getCodTasa());
    assertNull(consolaEditarFormVO.getDescTasa());
    assertNull(consolaEditarFormVO.getImporte());
    assertNull(consolaEditarFormVO.getDocumentoPagador());
    assertNull(consolaEditarFormVO.getDocumentoRepresentante());
    assertNull(consolaEditarFormVO.getNombreCompletoRepresentante());
    assertNull(consolaEditarFormVO.getImporteTasa());
    assertNull(consolaEditarFormVO.getFhPago());
    assertNull(consolaEditarFormVO.getNombreCompleto());
    assertNull(consolaEditarFormVO.getCodAplicacion());
  }

}
