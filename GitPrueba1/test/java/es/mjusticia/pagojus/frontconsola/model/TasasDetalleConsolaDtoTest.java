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
public class TasasDetalleConsolaDtoTest {

  private TasasDetalleConsolaDto tasasDetalleConsolaDto;

  @BeforeEach
  public void setUp() {
    tasasDetalleConsolaDto = new TasasDetalleConsolaDto();
  }

  @Test
  public void testGetSetCodTasa() {
    String codTasa = "TASA001";
    tasasDetalleConsolaDto.setCodTasa(codTasa);
    assertEquals(codTasa, tasasDetalleConsolaDto.getCodTasa());
  }

  @Test
  public void testGetSetExpediente() {
    String expediente = "EXP123";
    tasasDetalleConsolaDto.setExpediente(expediente);
    assertEquals(expediente, tasasDetalleConsolaDto.getExpediente());
  }

  @Test
  public void testGetSetResultado() {
    String resultado = "Éxito";
    tasasDetalleConsolaDto.setResultado(resultado);
    assertEquals(resultado, tasasDetalleConsolaDto.getResultado());
  }

  @Test
  public void testGetSetDocumentoPagador() {
    String documentoPagador = "12345678A";
    tasasDetalleConsolaDto.setDocumentoPagador(documentoPagador);
    assertEquals(documentoPagador, tasasDetalleConsolaDto.getDocumentoPagador());
  }

  @Test
  public void testGetSetDocumentoRepresentante() {
    String documentoRepresentante = "87654321B";
    tasasDetalleConsolaDto.setDocumentoRepresentante(documentoRepresentante);
    assertEquals(documentoRepresentante, tasasDetalleConsolaDto.getDocumentoRepresentante());
  }

  @Test
  public void testGetSetNombreCompletoRepresentante() {
    String nombreCompletoRepresentante = "Ana López";
    tasasDetalleConsolaDto.setNombreCompletoRepresentante(nombreCompletoRepresentante);
    assertEquals(nombreCompletoRepresentante, tasasDetalleConsolaDto.getNombreCompletoRepresentante());
  }

  @Test
  public void testGetSetCuentaPJ() {
    Boolean cuentaPJ = true;
    tasasDetalleConsolaDto.setCuentaPJ(cuentaPJ);
    assertEquals(cuentaPJ, tasasDetalleConsolaDto.getCuentaPJ());
  }

  @Test
  public void testInitialValues() {
    // Verificar que los valores iniciales son null
    assertNull(tasasDetalleConsolaDto.getCodTasa());
    assertNull(tasasDetalleConsolaDto.getExpediente());
    assertNull(tasasDetalleConsolaDto.getResultado());
    assertNull(tasasDetalleConsolaDto.getDocumentoPagador());
    assertNull(tasasDetalleConsolaDto.getDocumentoRepresentante());
    assertNull(tasasDetalleConsolaDto.getNombreCompletoRepresentante());
    assertNull(tasasDetalleConsolaDto.getCuentaPJ());
  }
}
