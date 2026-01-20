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

import es.mjusticia.milano.config.EnableTestContext;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
@Profile("test")
@Configuration
//Carga los componentes de test del arquetipo
@EnableTestContext
//Carga los componentes aplicativos 
@ComponentScan("es.mjusticia.pagojus.frontconsola")
public class TestConfiguration {

  /*
   * Incluir la definicion de los Mocks de los servicios a inyectar en la clase.
   * 
   * @Bean
   * @Primary
   * public MockedService mockedService() {
   *   return Mockito.mock(MockedService.class);
   * }
   * 
   */

}
