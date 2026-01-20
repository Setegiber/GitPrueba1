package es.mjusticia.pagojus.frontconsola.model;

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
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DetalleTasasPagadorDtoTest {

  private DetalleTasasPagadorDto detalleTasasPagadorDto;

  @BeforeEach
  public void setUp() {
    detalleTasasPagadorDto = new DetalleTasasPagadorDto();
  }

  @Test
  public void testGetSetIdPago() {
    Long idPago = 123L;
    detalleTasasPagadorDto.setIdPago(idPago);
    assertEquals(idPago, detalleTasasPagadorDto.getIdPago());
  }

  @Test
  public void testGetSetIdLocalizador() {
    Long idLocalizador = 456L;
    detalleTasasPagadorDto.setIdLocalizador(idLocalizador);
    assertEquals(idLocalizador, detalleTasasPagadorDto.getIdLocalizador());
  }

  @Test
  public void testGetSetDescTasa() {
    String descTasa = "Descripción de la Tasa";
    detalleTasasPagadorDto.setDescTasa(descTasa);
    assertEquals(descTasa, detalleTasasPagadorDto.getDescTasa());
  }

  @Test
  public void testGetSetImporteTasa() {
    BigDecimal importeTasa = new BigDecimal("123.45");
    detalleTasasPagadorDto.setImporteTasa(importeTasa);
    assertEquals(importeTasa, detalleTasasPagadorDto.getImporteTasa());
  }

  @Test
  public void testGetSetNrc() {
    String nrc = "NRC123";
    detalleTasasPagadorDto.setNrc(nrc);
    assertEquals(nrc, detalleTasasPagadorDto.getNrc());
  }

  @Test
  public void testGetSetFhPago() {
    LocalDateTime fhPago = LocalDateTime.now();
    detalleTasasPagadorDto.setFhPago(fhPago);
    assertEquals(fhPago, detalleTasasPagadorDto.getFhPago());
  }

  @Test
  public void testGetSetEstadoTasa() {
    String estadoTasa = "PAGADO";
    detalleTasasPagadorDto.setEstadoTasa(estadoTasa);
    assertEquals(estadoTasa, detalleTasasPagadorDto.getEstado());
    assertEquals(Estados.PAGADO, detalleTasasPagadorDto.getEstadoTasa());
  }

  @Test
  public void testGetNombreCompletoPagador() {
    detalleTasasPagadorDto.setNombrePagador("Juan");
    detalleTasasPagadorDto.setApellidoUnoPagador("Pérez");
    detalleTasasPagadorDto.setApellidoDosPagador("Gómez");
    detalleTasasPagadorDto.setTipoPersonaPagador(0);

    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoPagador();
    assertEquals("Juan Pérez Gómez", nombreCompleto);
  }

  @Test
  public void testGetNombreCompletoRepresentante() {
    detalleTasasPagadorDto.setNombreRepresentante("Ana");
    detalleTasasPagadorDto.setApellidoUnoRepresentante("López");
    detalleTasasPagadorDto.setApellidoDosRepresentante("Martínez");
    detalleTasasPagadorDto.setTipoPersonaPagador(1);

    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoRepresentante();
    assertEquals("Ana López Martínez", nombreCompleto);
  }

  @Test
  public void testGetFechaPago() {
    detalleTasasPagadorDto.setFhPago(null);

    String fechaPago = detalleTasasPagadorDto.getFechaPago();
    assertNull(fechaPago);

    LocalDateTime fhPago = LocalDateTime.of(2023, 12, 31, 23, 59);
    detalleTasasPagadorDto.setFhPago(fhPago);

    fechaPago = detalleTasasPagadorDto.getFechaPago();
    assertEquals(Utils.timeToString(fhPago), fechaPago);
  }

  @Test
  public void testGetImporte() {
    detalleTasasPagadorDto.setImporteTasa(null);

    String importe = detalleTasasPagadorDto.getImporte();
    assertNull(importe);
    BigDecimal importeTasa = new BigDecimal("123.45");
    detalleTasasPagadorDto.setImporteTasa(importeTasa);

    importe = detalleTasasPagadorDto.getImporte();
    assertEquals(Utils.bigDecimalToString(importeTasa), importe);
  }

  @Test
  public void testToJson() {
    detalleTasasPagadorDto.setFhPago(null);
    detalleTasasPagadorDto.setIdPago(123L);
    detalleTasasPagadorDto.setIdLocalizador(456L);
    detalleTasasPagadorDto.setDescTasa("Descripción de la Tasa");
    detalleTasasPagadorDto.setCodTasa("TASA001");
    detalleTasasPagadorDto.setImporteTasa(new BigDecimal("123.45"));
    detalleTasasPagadorDto.setNrc("NRC123");
    detalleTasasPagadorDto.setEstadoTasa("VALIDO");
    detalleTasasPagadorDto.toJson();
    detalleTasasPagadorDto.setFhPago(LocalDateTime.of(2023, 12, 31, 23, 59));

    String json = detalleTasasPagadorDto.toJson();
    assertTrue(json.contains("\"Id_detallepagotasa\":123"));
    assertTrue(json.contains("\"Localizador\":456"));
    assertTrue(json.contains("\"Tasa\":\"Descripción de la Tasa\""));
    assertTrue(json.contains("\"CodTasa\":\"TASA001\""));
    assertTrue(json.contains("\"Importe\":123.45"));
    assertTrue(json.contains("\"NRC\":\"NRC123\""));
    // assertTrue(json.contains("\"Estado\":\"VALIDO\""));
    assertTrue(json.contains("\"FechaPago\":\"2023-12-31T23:59\""));
  }

  @Test
  public void testOthers() {
    detalleTasasPagadorDto.getApellidoDosRepresentante();
    detalleTasasPagadorDto.getApellidoUnoRepresentante();

    detalleTasasPagadorDto.getNombreRepresentante();
    detalleTasasPagadorDto.setDocumentoRepresentante("nombre");
    detalleTasasPagadorDto.getDocumentoRepresentante();

    detalleTasasPagadorDto.setDocumentoPagador("nombre");
    detalleTasasPagadorDto.getDocumentoPagador();

    detalleTasasPagadorDto.setExpediente("nombre");
    detalleTasasPagadorDto.getExpediente();

    detalleTasasPagadorDto.getCodTasa();

    detalleTasasPagadorDto.getTipoPersonaPagador();

    detalleTasasPagadorDto.setRazonSocial("nombre");
    detalleTasasPagadorDto.getRazonSocial();

    detalleTasasPagadorDto.getApellidoUnoPagador();
    detalleTasasPagadorDto.getApellidoDosPagador();
    detalleTasasPagadorDto.getNombrePagador();

  }

  @Test
  public void testGetNombreCompletoPagador_IndividualConApellidos() {
    // Configurar datos de prueba para una persona individual
    detalleTasasPagadorDto.setNombrePagador("Juan");
    detalleTasasPagadorDto.setApellidoUnoPagador("Pérez");
    detalleTasasPagadorDto.setApellidoDosPagador("Gómez");
    detalleTasasPagadorDto.setTipoPersonaPagador(0); // 0 indica persona individual

    // Ejecutar método
    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoPagador();

    // Verificar resultados
    assertEquals("Juan Pérez Gómez", nombreCompleto);
  }

  @Test
  public void testGetNombreCompletoPagador_IndividualSinSegundoApellido() {
    // Configurar datos de prueba para una persona individual sin segundo apellido
    detalleTasasPagadorDto.setNombrePagador("Ana");
    detalleTasasPagadorDto.setApellidoUnoPagador("López");
    detalleTasasPagadorDto.setApellidoDosPagador(null);
    detalleTasasPagadorDto.setTipoPersonaPagador(0);

    // Ejecutar método
    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoPagador();

    // Verificar resultados
    assertEquals("Ana López", nombreCompleto);
  }

  @Test
  public void testGetNombreCompletoPagador_Juridica() {
    // Configurar datos de prueba para una persona jurídica
    detalleTasasPagadorDto.setRazonSocial("Empresa S.A.");
    detalleTasasPagadorDto.setTipoPersonaPagador(1); // 1 indica persona jurídica

    // Ejecutar método
    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoPagador();

    // Verificar resultados
    assertEquals("Empresa S.A.", nombreCompleto);
  }

  @Test
  public void testGetNombreCompletoPagador_TipoPersonaNull() {
    // Configurar datos de prueba con tipo de persona nulo
    detalleTasasPagadorDto.setNombrePagador("Carlos");
    detalleTasasPagadorDto.setApellidoUnoPagador("Martínez");
    detalleTasasPagadorDto.setTipoPersonaPagador(null);

    // Ejecutar método
    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoPagador();

    // Verificar resultados
    assertNull(nombreCompleto);
  }

  @Test
  public void testGetNombreCompletoPagador_TipoPersonaInvalido() {
    // Configurar datos de prueba con tipo de persona inválido
    detalleTasasPagadorDto.setNombrePagador("Luis");
    detalleTasasPagadorDto.setApellidoUnoPagador("García");
    detalleTasasPagadorDto.setTipoPersonaPagador(2); // Tipo inválido

    // Ejecutar método
    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoPagador();

    // Verificar resultados
    assertNull(nombreCompleto);
  }

  @Test
  public void testGetNombreCompletoRepresentante_IndividualConApellidos() {
    // Configurar datos de prueba para un representante individual
    detalleTasasPagadorDto.setNombreRepresentante("Ana");
    detalleTasasPagadorDto.setApellidoUnoRepresentante("López");
    detalleTasasPagadorDto.setApellidoDosRepresentante("Martínez");
    detalleTasasPagadorDto.setTipoPersonaPagador(1); // 1 indica persona jurídica

    // Ejecutar método
    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoRepresentante();

    // Verificar resultados
    assertEquals("Ana López Martínez", nombreCompleto);
  }

  @Test
  public void testGetNombreCompletoRepresentante_IndividualSinSegundoApellido() {
    // Configurar datos de prueba para un representante individual sin segundo
    // apellido
    detalleTasasPagadorDto.setNombreRepresentante("Carlos");
    detalleTasasPagadorDto.setApellidoUnoRepresentante("García");
    detalleTasasPagadorDto.setApellidoDosRepresentante(null);
    detalleTasasPagadorDto.setTipoPersonaPagador(1);

    // Ejecutar método
    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoRepresentante();

    // Verificar resultados
    assertEquals("Carlos García", nombreCompleto);
  }

  @Test
  public void testGetNombreCompletoRepresentante_TipoPersonaNull() {
    // Configurar datos de prueba con tipo de persona nulo
    detalleTasasPagadorDto.setNombreRepresentante("Luis");
    detalleTasasPagadorDto.setApellidoUnoRepresentante("Fernández");
    detalleTasasPagadorDto.setTipoPersonaPagador(null);

    // Ejecutar método
    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoRepresentante();

    // Verificar resultados
    assertNull(nombreCompleto);
  }

  @Test
  public void testGetNombreCompletoRepresentante_TipoPersonaInvalido() {
    // Configurar datos de prueba con tipo de persona inválido
    detalleTasasPagadorDto.setNombreRepresentante("María");
    detalleTasasPagadorDto.setApellidoUnoRepresentante("Rodríguez");
    detalleTasasPagadorDto.setTipoPersonaPagador(0); // Tipo inválido para representante

    // Ejecutar método
    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoRepresentante();

    // Verificar resultados
    assertNull(nombreCompleto);
  }

  @Test
  public void testGetNombreCompletoRepresentante_NombreRepresentanteNull() {
    // Configurar datos de prueba con nombre del representante nulo
    detalleTasasPagadorDto.setNombreRepresentante(null);
    detalleTasasPagadorDto.setApellidoUnoRepresentante("Sánchez");
    detalleTasasPagadorDto.setTipoPersonaPagador(1);

    // Ejecutar método
    String nombreCompleto = detalleTasasPagadorDto.getNombreCompletoRepresentante();

    // Verificar resultados
    assertNull(nombreCompleto);
  }
}
