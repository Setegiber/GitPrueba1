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
public class InformarTasasDtoTest {

  private InformarTasasDto informarTasasDto;

  @BeforeEach
  public void setUp() {
    // Inicializa el objeto con valores de prueba
    informarTasasDto = new InformarTasasDto("TASA001", "Descripción de la Tasa", false);
  }

  @Test
  public void testGetSetCodTasa() {
    // Configurar datos de prueba
    String nuevoCodTasa = "TASA002";
    informarTasasDto.setCodTasa(nuevoCodTasa);

    // Ejecutar método y verificar resultados
    assertEquals(nuevoCodTasa, informarTasasDto.getCodTasa());
  }

  @Test
  public void testGetSetDescTasa() {
    // Configurar datos de prueba
    String nuevaDescTasa = "Nueva Descripción";
    informarTasasDto.setDescTasa(nuevaDescTasa);

    // Ejecutar método y verificar resultados
    assertEquals(nuevaDescTasa, informarTasasDto.getDescTasa());
  }

  @Test
  public void testIsSetFlgBorrado() {
    // Verificar valor inicial
    assertFalse(informarTasasDto.isFlgBorrado());

    // Cambiar el valor y verificar
    informarTasasDto.setFlgBorrado(true);
    assertTrue(informarTasasDto.isFlgBorrado());
  }

  @Test
  public void testConstructor() {
    // Verificar que el constructor inicializa correctamente los campos
    assertEquals("TASA001", informarTasasDto.getCodTasa());
    assertEquals("Descripción de la Tasa", informarTasasDto.getDescTasa());
    assertFalse(informarTasasDto.isFlgBorrado());
  }
}
