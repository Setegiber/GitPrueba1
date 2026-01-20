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
import es.mjusticia.pagojus.frontconsola.enums.CodigosError;
import es.mjusticia.pagojus.frontconsola.enums.EstadosPermitidos;
import es.mjusticia.pagojus.frontconsola.model.TasasDetalleConsolaDto;
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
public class ServicesResponseTest {

  private ServicesResponse servicesResponse;

  @BeforeEach
  public void setUp() {
    servicesResponse = new ServicesResponse();
  }

  @Test
  public void testConstructorDefault() {
    // Verificar valores iniciales
    assertNull(servicesResponse.getCodigoRespuesta());
    assertNull(servicesResponse.getRespuesta());
  }

  @Test
  public void testConstructorWithCodigo() {
    // Configurar datos de prueba
    CodigosError codigo = CodigosError.CODE_200;
    servicesResponse = new ServicesResponse(codigo);

    // Verificar resultados
    assertEquals(codigo, servicesResponse.getCodigoRespuesta());
    assertEquals(codigo.getMensajeRespuesta(), servicesResponse.getRespuesta());
  }

  @Test
  public void testConstructorWithCodigoAndRespuesta() {
    // Configurar datos de prueba
    CodigosError codigo = CodigosError.CODE_409_1;
    String respuesta = "Conflicto detectado";
    servicesResponse = new ServicesResponse(codigo, respuesta);

    // Verificar resultados
    assertEquals(codigo, servicesResponse.getCodigoRespuesta());
    assertEquals(respuesta, servicesResponse.getRespuesta());
  }

  @Test
  public void testConstructorWithCodigoAndStrings() {
    // Configurar datos de prueba
    CodigosError codigo = CodigosError.CODE_500;
    String[] strings = { "Error", "Interno" };
    servicesResponse = new ServicesResponse(codigo, strings);

    // Verificar resultados
    assertEquals(codigo, servicesResponse.getCodigoRespuesta());
    assertEquals("Error Interno", servicesResponse.getRespuesta());
  }

  @Test
  public void testErrorGenerico() {
    // Configurar datos de prueba
    String errorMsg = "Error genérico";
    servicesResponse.errorGenerico(errorMsg);

    // Verificar resultados
    assertEquals(CodigosError.CODE_500, servicesResponse.getCodigoRespuesta());
    assertEquals(errorMsg, servicesResponse.getRespuesta());
  }

  @Test
  public void testErrorServicio() {
    // Configurar datos de prueba
    CodigosError codigo = CodigosError.CODE_409_1;
    servicesResponse.errorServicio(codigo);

    // Verificar resultados
    assertEquals(codigo, servicesResponse.getCodigoRespuesta());
    assertEquals(codigo.getMensajeRespuesta(), servicesResponse.getRespuesta());
  }

  @Test
  public void testOk() {
    // Configurar datos de prueba
    String responseData = "Operación exitosa";
    servicesResponse.ok(responseData);

    // Verificar resultados
    assertEquals(CodigosError.CODE_200, servicesResponse.getCodigoRespuesta());
    assertEquals(responseData, servicesResponse.getRespuesta());
  }

  @Test
  public void testSetGetCodigoRespuesta() {
    // Configurar datos de prueba
    CodigosError codigo = CodigosError.CODE_200;
    servicesResponse.setCodigoRespuesta(codigo);

    // Verificar resultados
    assertEquals(codigo, servicesResponse.getCodigoRespuesta());
  }

  @Test
  public void testSetGetRespuesta() {
    // Configurar datos de prueba
    String respuesta = "Respuesta de prueba";
    servicesResponse.setRespuesta(respuesta);

    // Verificar resultados
    assertEquals(respuesta, servicesResponse.getRespuesta());
  }
}
