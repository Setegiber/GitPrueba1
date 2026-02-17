package es.mjusticia.sinac.core.batch;

import java.util.HashMap;
import java.util.Map;

/*-
 * #%L
 * sinac-core
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
import org.quartz.Job;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

/**
 * Configuración de un Job simple de sinac
 */
@Configuration
public class SinacJobResolverExpedientesConfiguration extends SinacJobConfiguration {

  /**
   * Expresión cron parametrizada
   */
  @Value("${sinac.quartz.sinacJobResolverExpedientes.cronExpression}")
  private String cronExpression;

  private static final Class<SinacJobResolverExpedientes> clase = SinacJobResolverExpedientes.class;

  private static final String DESCRIPCION = "Sinac Job Resolver Expedientes";

  private static final String JOB_NAME = "sinacJobResolverExpedientesDetail";

  /**
   * Factoria de detalle del Job simple de sinax
   */
  @Bean
  public JobDetailFactoryBean sinacJobResolverExpedientesDetail() {
    return getJobDetailFactoryBean();
  }

  /**
   * Disparador del Job simple de sinax
   */
  @Bean
  public CronTriggerFactoryBean sinacJobResolverExpedientesTrigger(
      @Qualifier(JOB_NAME) JobDetail sinacJobResolverExpedientesDetail) {
    return getCronTriggerFactoryBean(sinacJobResolverExpedientesDetail);
  }

  @Override
  protected Class<? extends Job> getJobClass() {
    return clase;
  }

  @Override
  protected String getJobDescription() {
    return DESCRIPCION;
  }

  @Override
  protected Map<String, Object> getJobDataMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("descripcion", DESCRIPCION);
    map.put("jobName", JOB_NAME);
    map.put("jobClassName", clase.getName());
    return map;
  }

  @Override
  protected String getCronExpression() {
    return cronExpression;
  }
}