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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PeticionConsolaDetallePagoTasaEditarTest {

  private PeticionConsolaDetallePagoTasaEditar peticion;

  @Mock
  private EstadosPermitidos estadosPermitidos = mock(EstadosPermitidos.class);

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    peticion = new PeticionConsolaDetallePagoTasaEditar();
  }

  @Test
  public void testGetUpdates_OK() {
    // Configurar datos de prueba
    peticion.setCodAplicacion("App123");
    peticion.setIdPago(123L);
    peticion.setNrc("NRC123");
    peticion.setExpediente("EXP123");
    peticion.setEstado("VALIDO");

    // Ejecutar método
    Map<String, Object> updates = peticion.getUpdates();

    // Verificar resultados
    assertEquals(4, updates.size());
    assertEquals("App123", updates.get(PeticionConsolaDetallePagoTasaEditar.UPDATE_COD_APP));
    assertEquals("NRC123", updates.get(PeticionConsolaDetallePagoTasaEditar.UPDATE_NRC));
    assertEquals("EXP123", updates.get(PeticionConsolaDetallePagoTasaEditar.UPDATE_EXPEDIENTE));
    assertEquals("VALIDO", updates.get(PeticionConsolaDetallePagoTasaEditar.UPDATE_ESTADO));
  }

  @Test
  public void testValidarFormato_KO() {
    peticion.setEstado("NO_VALIDO");

    // Ejecutar método
    String resultado = peticion.validarFormato();

    // Verificar resultados
    assertEquals(PeticionConsolaDetallePagoTasaEditar.UPDATE_ESTADO, resultado);
  }

  @Test
  public void testValidarFormato_OK() {
    // Usamos un estado que sabemos que es permitido
    peticion.setEstado("PAGADO");

    // Ejecutar método
    String resultado = peticion.validarFormato();

    // Verificar resultados
    assertEquals("", resultado);
  }

  @Test
  public void testValidarFormato_KO2() {
    // Usamos un estado que sabemos que es permitido
    peticion.setEstado(null);

    // Ejecutar método
    String resultado = peticion.validarFormato();

    // Verificar resultados
    assertEquals("", resultado);
  }

  @Test
  public void testToString_AllFieldsSet() {
    // Configurar datos de prueba
    peticion.setCodAplicacion("App123");
    peticion.setIdPago(123L);
    peticion.setNrc("NRC123");
    peticion.setExpediente("EXP123");
    peticion.setEstado("VALIDO");

    // Ejecutar método
    String resultado = peticion.toString();

    // Verificar resultados
    assertTrue(resultado.contains("codAplicacion = 'App123'"));
    assertTrue(resultado.contains("idPago = 123"));
    assertTrue(resultado.contains("nrc = 'NRC123'"));
    assertTrue(resultado.contains("expediente = 'EXP123'"));
    assertTrue(resultado.contains("estado = 'VALIDO'"));
  }

  @Test
  public void testToString_NullFields() {
    // Configurar datos de prueba
    peticion.setCodAplicacion(null);
    peticion.setIdPago(null);
    peticion.setNrc(null);
    peticion.setExpediente(null);
    peticion.setEstado(null);

    // Ejecutar método
    String resultado = peticion.toString();

    // Verificar resultados
    // assertTrue(resultado.contains("codAplicacion = 'NO_INFORMADO'"));
    // assertTrue(resultado.contains("idPago = NO_INFORMADO"));
    // assertTrue(resultado.contains("nrc = 'NO_INFORMADO'"));
    // assertTrue(resultado.contains("expediente = 'NO_INFORMADO'"));
    // assertTrue(resultado.contains("estado = 'NO_INFORMADO'"));
  }

  @Test
  public void testToString_EmptyFields() {
    // Configurar datos de prueba
    peticion.setCodAplicacion("");
    peticion.setIdPago(123L); // Mantener un valor válido para idPago
    peticion.setNrc("");
    peticion.setExpediente("");
    peticion.setEstado("");

    // Ejecutar método
    String resultado = peticion.toString();

    // Verificar resultados
    // assertTrue(resultado.contains("codAplicacion = 'NO_INFORMADO'"));
    // assertTrue(resultado.contains("idPago = 123"));
    // assertTrue(resultado.contains("nrc = 'NO_INFORMADO'"));
    // assertTrue(resultado.contains("expediente = 'NO_INFORMADO'"));
    // assertTrue(resultado.contains("estado = 'NO_INFORMADO'"));
  }
}
