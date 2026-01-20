package es.mjusticia.pagojus.frontconsola;

/*-
 * #%L
 * pagojus-frontconsola
 * %%
 * Copyright (C) 2023 - 2024 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.web.servlet.ModelAndView;
@SpringJUnitWebConfig(TestConfiguration.class)
@ActiveProfiles(profiles = "test")
public class ApplicationTest {
  
  /*
   * Incluir los métodos de prueba utilizando los servicios mockeados definidos en TestConfiguration.
   * 
   * @Autowired
   * private MockedService mockedService;
   *
   * @Test
   * public void testMockedService() {
   *   Mockito.when(mockedService.methodToTest("Test Parameter")).thenReturn("Mock response");
   *   String testResult = userService.methodToTest("Test Parameter");
   *   assertEquals("Mock response", testResult);
   * }
   *
   */

}
