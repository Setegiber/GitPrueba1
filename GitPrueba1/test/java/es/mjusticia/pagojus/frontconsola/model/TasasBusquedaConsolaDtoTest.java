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
import java.util.List;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TasasBusquedaConsolaDtoTest {
  private TasasBusquedaConsolaDto tasasBusquedaConsolaDto;

  @BeforeEach
  public void setUp() {
    tasasBusquedaConsolaDto = new TasasBusquedaConsolaDto();
  }

  @Test
  public void testGetSetIdPago() {
    Long idPago = 123L;
    tasasBusquedaConsolaDto.setIdPago(idPago);
    assertEquals(idPago, tasasBusquedaConsolaDto.getIdPago());
  }

  @Test
  public void testGetSetIdLocalizador() {
    Long idLocalizador = 456L;
    tasasBusquedaConsolaDto.setIdLocalizador(idLocalizador);
    assertEquals(idLocalizador, tasasBusquedaConsolaDto.getIdLocalizador());
  }

  @Test
  public void testGetSetDescTasa() {
    String descTasa = "Descripción de la Tasa";
    tasasBusquedaConsolaDto.setDescTasa(descTasa);
    assertEquals(descTasa, tasasBusquedaConsolaDto.getDescTasa());
  }

  @Test
  public void testGetSetImporteTasa() {
    String importeTasa = "123.45";
    tasasBusquedaConsolaDto.setImporteTasa(importeTasa);
    assertEquals(importeTasa, tasasBusquedaConsolaDto.getImporteTasa());
  }

  @Test
  public void testGetSetNrc() {
    String nrc = "NRC123";
    tasasBusquedaConsolaDto.setNrc(nrc);
    assertEquals(nrc, tasasBusquedaConsolaDto.getNrc());
  }

  @Test
  public void testGetSetFhPago() {
    String fhPago = "2023-12-31";
    tasasBusquedaConsolaDto.setFhPago(fhPago);
    assertEquals(fhPago, tasasBusquedaConsolaDto.getFhPago());
  }

  @Test
  public void testGetSetEstadoTasa() {
    String estadoTasa = "VALIDO";
    tasasBusquedaConsolaDto.setEstadoTasa(estadoTasa);
    assertEquals(estadoTasa, tasasBusquedaConsolaDto.getEstadoTasa());
  }

  @Test
  public void testGetSetNombreCompleto() {
    String nombreCompleto = "Juan Pérez";
    tasasBusquedaConsolaDto.setNombreCompleto(nombreCompleto);
    assertEquals(nombreCompleto, tasasBusquedaConsolaDto.getNombreCompleto());
  }

  @Test
  public void testGetSetRazonSocial() {
    String razonSocial = "Empresa S.A.";
    tasasBusquedaConsolaDto.setRazonSocial(razonSocial);
    assertEquals(razonSocial, tasasBusquedaConsolaDto.getRazonSocial());
  }

  @Test
  public void testGetPropertyNames() throws Exception {
    // Ejecutar método
    List<String> propertyNames = TasasBusquedaConsolaDto.getPropertyNames();

    // Verificar resultados
    assertEquals(9, propertyNames.size());
    assertTrue(propertyNames.contains("Id_detallepagotasa"));
    assertTrue(propertyNames.contains("Localizador"));
    assertTrue(propertyNames.contains("Tasa"));
    assertTrue(propertyNames.contains("Importe"));
    assertTrue(propertyNames.contains("NRC"));
    assertTrue(propertyNames.contains("Fecha pago"));
    assertTrue(propertyNames.contains("Estado"));
    assertTrue(propertyNames.contains("Pagador"));
    assertTrue(propertyNames.contains("Razon Social"));
  }

}
