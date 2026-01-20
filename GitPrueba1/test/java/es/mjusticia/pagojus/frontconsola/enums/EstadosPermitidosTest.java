package es.mjusticia.pagojus.frontconsola.enums;

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
public class EstadosPermitidosTest {

  @Test
  public void testGetPermitidos() {
    // Verificar que el método getPermitidos devuelve la cadena correcta
    assertEquals("INICIADO PAGADOR_INFORMADO", EstadosPermitidos.INFORMAR_PAGADOR.getPermitidos());
    assertEquals("PAGADOR_INFORMADO", EstadosPermitidos.HACER_PAGO.getPermitidos());
  }

  @Test
  public void testValidarEstado_ValidState() {
    // Configurar datos de prueba
    String estadosPermitidos = "PAGADOR_INFORMADO NO_PAGADO PAGADO";
    String estadoActual = "PAGADO";

    // Ejecutar método
    String resultado = EstadosPermitidos.validarEstado(estadosPermitidos, estadoActual);

    // Verificar resultados
    assertNull(resultado);
  }

  @Test
  public void testValidarEstado_InvalidState() {
    // Configurar datos de prueba
    String estadosPermitidos = "PAGADOR_INFORMADO NO_PAGADO";
    String estadoActual = "PAGADO";

    // Ejecutar método
    String resultado = EstadosPermitidos.validarEstado(estadosPermitidos, estadoActual);

    // Verificar resultados
    assertEquals(
        "Operacion no permitida, el Estado del pago PAGADO es incorrecto; los estados permitidos son: PAGADOR_INFORMADO NO_PAGADO. ",
        resultado);
  }

  @Test
  public void testValidarEstado_NullPermitidos() {
    // Configurar datos de prueba
    String estadosPermitidos = null;
    String estadoActual = "PAGADO";

    // Ejecutar método
    String resultado = EstadosPermitidos.validarEstado(estadosPermitidos, estadoActual);

    // Verificar resultados
    assertEquals("Error en la validacicion de estados permitidos, argumento de entrada nulo", resultado);
  }

  @Test
  public void testValidarEstado_NullEstadoActual() {
    // Configurar datos de prueba
    String estadosPermitidos = "PAGADOR_INFORMADO NO_PAGADO";
    String estadoActual = null;

    // Ejecutar método
    String resultado = EstadosPermitidos.validarEstado(estadosPermitidos, estadoActual);

    // Verificar resultados
    assertEquals("Error en la validacicion de estados permitidos, argumento de entrada nulo", resultado);
  }

  @Test
  public void testValidarEstado() {
    // Configurar datos de prueba
    String estadosPermitidos = "PAGADOR_INFORMADO NO_PAGADO";

    // Ejecutar método
    assertNull(EstadosPermitidos.validarEstado(estadosPermitidos, Estados.NO_PAGADO));
  }
}
