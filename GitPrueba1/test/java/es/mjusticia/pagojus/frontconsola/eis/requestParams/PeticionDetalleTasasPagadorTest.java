package es.mjusticia.pagojus.frontconsola.eis.requestParams;

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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PeticionDetalleTasasPagadorTest {

  private PeticionDetalleTasasPagador peticion;

  @Mock
  private BindingResult bindingResult;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    peticion = new PeticionDetalleTasasPagador();
    // Simular el método getResult para devolver el mock de BindingResult
    peticion.setResult(bindingResult);
  }

  @Test
  public void testToString_AllFieldsSet() {
    // Configurar datos de prueba
    peticion.setTasa("TASA123");
    peticion.setNrc("NRC123");
    peticion.setExpediente("EXP123");
    peticion.setLocalizador(123L);
    peticion.setEstadoTasa("VALIDO");
    peticion.setFechaPagoDesde("2023-01-01");
    peticion.setFechaPagoHasta("2023-12-31");
    peticion.setNifPagador("12345678A");
    peticion.setNombrePagador("Nombre Pagador");
    peticion.setNifRepresentante("87654321B");
    peticion.setNombreRepresentante("Nombre Representante");
    peticion.setCodAplicacion("App123");
    peticion.setNumeroPagina(1);
    peticion.setTamanioPagina(10);
    peticion.setColumnaOrden("columna");
    peticion.setOrden("ASC");

    // Ejecutar método
    String resultado = peticion.toString();

    // Verificar resultados
    assertTrue(resultado.contains("tasa = TASA123"));
    assertTrue(resultado.contains("nrc = NRC123"));
    assertTrue(resultado.contains("expediente = EXP123"));
    assertTrue(resultado.contains("localizador = 123"));
    assertTrue(resultado.contains("estado = VALIDO"));
    assertTrue(resultado.contains("fechaPagoDesde = 2023-01-01"));
    assertTrue(resultado.contains("fechaPagoHasta = 2023-12-31"));
    assertTrue(resultado.contains("nifPagador = 12345678A"));
    assertTrue(resultado.contains("nombrePagador = Nombre Pagador"));
    assertTrue(resultado.contains("nifRepresentante = 87654321B"));
    assertTrue(resultado.contains("nombreRepresentante = Nombre Representante"));
    assertTrue(resultado.contains("codAplicacion = APP123"));
    assertTrue(resultado.contains("numeroPagina = 1"));
    assertTrue(resultado.contains("tamanioPagina = 10"));
    assertTrue(resultado.contains("columnaOrden = columna"));
    assertTrue(resultado.contains("orden = ASC"));
  }

  @Test
  public void testToString_NullFields() {
    // Configurar datos de prueba
    peticion.setTasa(null);
    peticion.setNrc(null);
    peticion.setExpediente(null);
    peticion.setLocalizador(null);
    peticion.setEstadoTasa(null);
    peticion.setFechaPagoDesde(null);
    peticion.setFechaPagoHasta(null);
    peticion.setNifPagador(null);
    peticion.setNombrePagador(null);
    peticion.setNifRepresentante(null);
    peticion.setNombreRepresentante(null);
    peticion.setCodAplicacion(null);
    // peticion.setNumeroPagina(null);
    peticion.setTamanioPagina(null);
    peticion.setColumnaOrden(null);
    peticion.setOrden(null);

    // Ejecutar método
    String resultado = peticion.toString();

    // Verificar resultados
    /*
     * assertTrue(resultado.contains("tasa = NO_INFORMADO"));
     * assertTrue(resultado.contains("nrc = NO_INFORMADO"));
     * assertTrue(resultado.contains("expediente = NO_INFORMADO"));
     * assertTrue(resultado.contains("localizador = NO_INFORMADO"));
     * assertTrue(resultado.contains("estado = NO_INFORMADO"));
     * assertTrue(resultado.contains("fechaPagoDesde = NO_INFORMADO"));
     * assertTrue(resultado.contains("fechaPagoHasta = NO_INFORMADO"));
     * assertTrue(resultado.contains("nifPagador = NO_INFORMADO"));
     * assertTrue(resultado.contains("nombrePagador = NO_INFORMADO"));
     * assertTrue(resultado.contains("nifRepresentante = NO_INFORMADO"));
     * assertTrue(resultado.contains("nombreRepresentante = NO_INFORMADO"));
     * assertTrue(resultado.contains("codAplicacion = NO_INFORMADO"));
     * assertTrue(resultado.contains("numeroPagina = NO_INFORMADO"));
     * assertTrue(resultado.contains("tamanioPagina = NO_INFORMADO"));
     * assertTrue(resultado.contains("columnaOrden = NO_INFORMADO"));
     * assertTrue(resultado.contains("orden = NO_INFORMADO"));
     */
  }

  @Test
  public void testValidarFormato_OK() {
    // Configurar datos de prueba
    peticion.setTasa("TASA123");
    peticion.setNifPagador("12345678A");
    peticion.setNifRepresentante("87654321B");

    // Simular que no hay errores de validación
    when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList());

    // Ejecutar método
    String resultado = peticion.validarFormato();

    // Verificar resultados
    // assertEquals("", resultado);
  }

  @Test
  public void testValidarFormato_KO() {
    // Configurar datos de prueba
    peticion.setTasa("TASA123");
    peticion.setNifPagador("INVALIDO");
    peticion.setNifRepresentante("INVALIDO");

    // Simular errores de campo en el BindingResult con códigos válidos
    FieldError errorNifPagador = new FieldError("peticion", "nifPagador", null, false, new String[] { "Invalid" }, null,
        "Invalid NIF");
    FieldError errorNifRepresentante = new FieldError("peticion", "nifRepresentante", null, false,
        new String[] { "Invalid" }, null, "Invalid NIF");
    when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(errorNifPagador, errorNifRepresentante));

    // Ejecutar método
    String resultado = peticion.validarFormato();

    // Verificar resultados
    assertTrue(resultado.contains("nifPagador"));
    assertTrue(resultado.contains("nifRepresentante"));
  }

  @Test
  public void testGetPaginadoYOrden_DefaultValues() {
    // Configurar datos de prueba
    peticion.setOrden(null);
    peticion.setColumnaOrden(null);
    // peticion.setNumeroPagina(null);
    peticion.setTamanioPagina(10);

    // Ejecutar método
    Map<String, Object> paginadoYOrden = peticion.getPaginadoYOrden();

    // Verificar resultados
    assertEquals(PeticionesConsola.ORDER_DEFAULT, paginadoYOrden.get(PeticionesConsola.ORDEN_TYPE));
    assertEquals(PeticionDetalleTasasPagador.ID_PAGO_ORDEN, paginadoYOrden.get(PeticionesConsola.COL_ORDEN));
    assertEquals(PeticionesConsola.N_PAGINA_DEFAULT, paginadoYOrden.get(PeticionesConsola.N_PAGINA));
    assertEquals(10, paginadoYOrden.get(PeticionesConsola.TAMANIO_PAGINA));
  }

  @Test
  public void testGetPaginadoYOrden_CustomValues() {
    // Configurar datos de prueba
    peticion.setOrden("DESC");
    peticion.setColumnaOrden("columna");
    peticion.setNumeroPagina(2);
    peticion.setTamanioPagina(20);

    // Ejecutar método
    Map<String, Object> paginadoYOrden = peticion.getPaginadoYOrden();

    // Verificar resultados
    assertEquals(PeticionesConsola.ORDER_DESC, paginadoYOrden.get(PeticionesConsola.ORDEN_TYPE));
    assertEquals("columna", paginadoYOrden.get(PeticionesConsola.COL_ORDEN));
    assertEquals(2, paginadoYOrden.get(PeticionesConsola.N_PAGINA));
    assertEquals(20, paginadoYOrden.get(PeticionesConsola.TAMANIO_PAGINA));
  }

  @Test
  public void testNeedsPagador() {
    // Configurar datos de prueba
    peticion.setNifPagador("12345678A");

    // Ejecutar método
    boolean result = peticion.needsPagador();

    // Verificar resultados
    assertTrue(result);
  }

  @Test
  public void testNeedsPagos() {
    // Configurar datos de prueba
    peticion.setLocalizador(123L);

    // Ejecutar método
    boolean result = peticion.needsPagos();

    // Verificar resultados
    assertTrue(result);

    peticion.setLocalizador(null);
    assertFalse(peticion.needsPagos());
  }

  @Test
  public void testNeedsDetalleTasas() {
    // Configurar datos de prueba
    peticion.setNrc("NRC123");

    // Ejecutar método
    boolean result = peticion.needsDetalleTasas();

    // Verificar resultados
    assertTrue(result);
  }

  @Test
  public void testNeedsLetfJoin() {
    // Configurar datos de prueba
    peticion.setColumnaOrden(PeticionDetalleTasasPagador.PAGADOR_ORDEN);

    // Ejecutar método
    boolean result = peticion.needsLetfJoin();

    // Verificar resultados
    assertTrue(result);

    peticion.setColumnaOrden(PeticionDetalleTasasPagador.RAZON_SOCIAL_ORDEN);
    result = peticion.needsLetfJoin();
    assertTrue(result);

    peticion.setColumnaOrden("other");
    result = peticion.needsLetfJoin();
    assertFalse(result);
  }

  @Test
  public void testFechaPagoHastaToLocalDate_ValidDate() {
    // Configurar datos de prueba
    String fechaValida = "2023-12-31";
    peticion.setFechaPagoHasta(fechaValida);

    // Ejecutar método
    LocalDate resultado = peticion.fechaPagoHastaToLocalDate();

    // Verificar resultados
    assertEquals(Utils.getLocalDate(fechaValida), resultado);
  }

  @Test
  public void testFechaPagoHastaToLocalDate_NullDate() {
    // Configurar datos de prueba
    peticion.setFechaPagoHasta(null);

    // Ejecutar método
    LocalDate resultado = peticion.fechaPagoHastaToLocalDate();

    // Verificar resultados
    assertNull(resultado);
  }

  @Test
  public void testFechaPagoHastaToLocalDate_EmptyDate() {
    // Configurar datos de prueba
    peticion.setFechaPagoHasta("");

    // Ejecutar método
    LocalDate resultado = peticion.fechaPagoHastaToLocalDate();

    // Verificar resultados
    assertNull(resultado);
  }

  @Test
  public void testFechaPagoDesdeToLocalDate_ValidDate() {
    // Configurar datos de prueba
    String fechaValida = "2023-12-31";
    peticion.setFechaPagoDesde(fechaValida);

    // Ejecutar método
    LocalDate resultado = peticion.fechaPagoDesdeToLocalDate();

    // Verificar resultados
    assertEquals(Utils.getLocalDate(fechaValida), resultado);
  }

  @Test
  public void testFechaPagoDesdeToLocalDate_NullDate() {
    // Configurar datos de prueba
    peticion.setFechaPagoDesde(null);

    // Ejecutar método
    LocalDate resultado = peticion.fechaPagoDesdeToLocalDate();

    // Verificar resultados
    assertNull(resultado);
  }

  @Test
  public void testFechaPagoDesdeToLocalDate_EmptyDate() {
    // Configurar datos de prueba
    peticion.setFechaPagoDesde("");

    // Ejecutar método
    LocalDate resultado = peticion.fechaPagoDesdeToLocalDate();

    // Verificar resultados
    assertNull(resultado);
  }

  @Test
  public void testSetRazonSocialPagador() {
    peticion.setRazonSocialPagador("razonSocial");
    assertNotNull(peticion.getRazonSocialPagador());
  }

  @Test
  public void testNeedsPagador_WithNifPagador() {
    // Configurar datos de prueba
    peticion.setNifPagador("12345678A");

    // Ejecutar método
    boolean result = peticion.needsPagador();

    // Verificar resultados
    assertTrue(result);
  }

  @Test
  public void testNeedsPagador_WithNifRepresentante() {
    // Configurar datos de prueba
    peticion.setNifRepresentante("87654321B");

    // Ejecutar método
    boolean result = peticion.needsPagador();

    // Verificar resultados
    assertTrue(result);
  }

  @Test
  public void testNeedsPagador_WithNombrePagador() {
    // Configurar datos de prueba
    peticion.setNombrePagador("Nombre Pagador");

    // Ejecutar método
    boolean result = peticion.needsPagador();

    // Verificar resultados
    assertTrue(result);
  }

  @Test
  public void testNeedsPagador_WithNombreRepresentante() {
    // Configurar datos de prueba
    peticion.setNombreRepresentante("Nombre Representante");

    // Ejecutar método
    boolean result = peticion.needsPagador();

    // Verificar resultados
    assertTrue(result);
  }

  @Test
  public void testNeedsPagador_NoFieldsSet() {
    // Configurar datos de prueba
    peticion.setNifPagador(null);
    peticion.setNifRepresentante(null);
    peticion.setNombrePagador(null);
    peticion.setNombreRepresentante(null);

    // Ejecutar método
    boolean result = peticion.needsPagador();

    // Verificar resultados
    assertFalse(result);
  }

  @Test
  public void testNeedsDetalleTasas_WithNrc() {
    // Configurar datos de prueba
    peticion.setNrc("NRC123");

    // Ejecutar método
    boolean result = peticion.needsDetalleTasas();

    // Verificar resultados
    assertTrue(result);
  }

  @Test
  public void testNeedsDetalleTasas_WithExpediente() {
    // Configurar datos de prueba
    peticion.setExpediente("EXP123");

    // Ejecutar método
    boolean result = peticion.needsDetalleTasas();

    // Verificar resultados
    assertTrue(result);
  }

  @Test
  public void testNeedsDetalleTasas_WithEstadoTasa() {
    // Configurar datos de prueba
    peticion.setEstadoTasa("VALIDO");

    // Ejecutar método
    boolean result = peticion.needsDetalleTasas();

    // Verificar resultados
    assertTrue(result);
  }

  @Test
  public void testNeedsDetalleTasas_WithFechaPagoDesde() {
    // Configurar datos de prueba
    peticion.setFechaPagoDesde("2023-01-01");

    // Ejecutar método
    boolean result = peticion.needsDetalleTasas();

    // Verificar resultados
    assertTrue(result);
  }

  @Test
  public void testNeedsDetalleTasas_WithFechaPagoHasta() {
    // Configurar datos de prueba
    peticion.setFechaPagoHasta("2023-12-31");

    // Ejecutar método
    boolean result = peticion.needsDetalleTasas();

    // Verificar resultados
    assertTrue(result);
  }

  @Test
  public void testNeedsDetalleTasas_NoFieldsSet() {
    // Configurar datos de prueba
    peticion.setNrc(null);
    peticion.setExpediente(null);
    peticion.setEstadoTasa(null);
    peticion.setFechaPagoDesde(null);
    peticion.setFechaPagoHasta(null);

    // Ejecutar método
    boolean result = peticion.needsDetalleTasas();

    // Verificar resultados
    assertFalse(result);
  }
}
