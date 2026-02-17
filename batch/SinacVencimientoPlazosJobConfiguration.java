package es.mjusticia.sinac.core.batch;

/*-
 * #%L
 * sinac-core
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

import java.util.HashMap;
import java.util.Map;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

/**
 * Configuración del Job para vencer aquellos Plazos que estén "En Curso" y su
 * Fecha de Finalización sea anterior a la Fecha actual.
 *
 * @author NTT Data.
 */
@Configuration
public class SinacVencimientoPlazosJobConfiguration extends SinacJobConfiguration {

  /**
   * Expresión Cron Parametrizada.
   */
  @Value("${sinac.quartz.sinacVencimientoPlazosJob.cronExpression}")
  private String cronExpression;

  private static final Class<SinacVencimientoPlazosJob> JOB_CLASS = SinacVencimientoPlazosJob.class;

  private static final String JOB_NAME = "sinacVencimientoPlazosJobDetail";

  private static final String JOB_DESCRIPTION = "Sinac Vencimiento Plazos Job";

  /**
   * Obtiene Factoría de Detalle del Job.
   *
   * @return Factoría de Detalle del Job.
   */
  @Bean
  public JobDetailFactoryBean sinacVencimientoPlazosJobDetail() {
    return getJobDetailFactoryBean();
  }

  /**
   * Obtiene Disparador del Job.
   *
   * @param sinacVencimientoPlazosJobDetail Job Detail.
   * @return Disparador del Job.
   */
  @Bean
  public CronTriggerFactoryBean sinacVencimientoPlazosJobTrigger(
      @Qualifier(JOB_NAME) JobDetail sinacVencimientoPlazosJobDetail) {
    return getCronTriggerFactoryBean(sinacVencimientoPlazosJobDetail);
  }

  @Override
  protected Class<? extends Job> getJobClass() {
    return JOB_CLASS;
  }

  @Override
  protected String getJobDescription() {
    return JOB_DESCRIPTION;
  }

  @Override
  protected Map<String, Object> getJobDataMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("descripcion", JOB_DESCRIPTION);
    map.put("jobName", JOB_NAME);
    map.put("jobClassName", JOB_CLASS.getName());

    return map;
  }

  @Override
  protected String getCronExpression() {
    return cronExpression;
  }

}
