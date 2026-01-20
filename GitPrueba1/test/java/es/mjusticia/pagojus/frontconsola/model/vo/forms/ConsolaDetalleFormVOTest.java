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
public class ConsolaDetalleFormVOTest {
  private ConsolaDetalleFormVO consolaDetalleFormVO;

  @BeforeEach
  public void setUp() {
    consolaDetalleFormVO = new ConsolaDetalleFormVO();
  }

  @Test
  public void testGetSetCodApp() {
    // Configurar datos de prueba
    String codApp = "App123";
    consolaDetalleFormVO.setCodApp(codApp);

    // Ejecutar método y verificar resultados
    assertEquals(codApp, consolaDetalleFormVO.getCodApp());
  }

  @Test
  public void testGetSetIdPago() {
    // Configurar datos de prueba
    String idPago = "Pago456";
    consolaDetalleFormVO.setIdPago(idPago);

    // Ejecutar método y verificar resultados
    assertEquals(idPago, consolaDetalleFormVO.getIdPago());
  }

  @Test
  public void testInitialValues() {
    // Verificar que los valores iniciales son null
    assertNull(consolaDetalleFormVO.getCodApp());
    assertNull(consolaDetalleFormVO.getIdPago());
  }

  @Test
  public void testSetCodApp_NullValue() {
    // Configurar datos de prueba
    consolaDetalleFormVO.setCodApp(null);

    // Ejecutar método y verificar resultados
    assertNull(consolaDetalleFormVO.getCodApp());
  }

  @Test
  public void testSetIdPago_NullValue() {
    // Configurar datos de prueba
    consolaDetalleFormVO.setIdPago(null);

    // Ejecutar método y verificar resultados
    assertNull(consolaDetalleFormVO.getIdPago());
  }

}
