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
public class PeticionDetalleTasasPagadorDetalleTest {

  private PeticionDetalleTasasPagadorDetalle peticion;

  @BeforeEach
  public void setUp() {
    peticion = new PeticionDetalleTasasPagadorDetalle();
  }

  @Test
  public void testGetSetCodAplicacion() {
    // Configurar datos de prueba
    String codAplicacion = "App123";
    peticion.setCodAplicacion(codAplicacion);

    // Ejecutar método y verificar resultados
    assertEquals(codAplicacion, peticion.getCodAplicacion());
  }

  @Test
  public void testGetSetIdPago() {
    // Configurar datos de prueba
    Long idPago = 123L;
    peticion.setIdPago(idPago);

    // Ejecutar método y verificar resultados
    assertEquals(idPago, peticion.getIdPago());
  }

  @Test
  public void testToString_AllFieldsSet() {
    // Configurar datos de prueba
    peticion.setCodAplicacion("App123");
    peticion.setIdPago(123L);

    // Ejecutar método
    String resultado = peticion.toString();

    // Verificar resultados
    assertEquals("PeticionDetalleTasasPagadorDetalle { codAplicacion = App123idPago = 123 }", resultado);
  }

  @Test
  public void testToString_NullFields() {
    // Configurar datos de prueba
    peticion.setCodAplicacion(null);
    peticion.setIdPago(null);

    // Ejecutar método
    String resultado = peticion.toString();

    // Verificar resultados
    assertEquals("PeticionDetalleTasasPagadorDetalle { codAplicacion = No InformadoidPago = No Informado }", resultado);
  }

  @Test
  public void testValidarFormato() {
    peticion.validarFormato();
  }
}
