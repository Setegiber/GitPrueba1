package es.mjusticia.sinac.core.batch;

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

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

/**
 * Clase generica para configurar Jobs en Sinac
 */
public abstract class SinacJobConfiguration {

  /**
   * Recupera el Job de Sinac
   * @return
   */
  protected JobDetailFactoryBean getJobDetailFactoryBean() {
    JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
    jobDetailFactory.setJobClass(getJobClass());
    jobDetailFactory.setDescription(getJobDescription());
    jobDetailFactory.setDurability(true);
    Map<String,Object> jobDataMap = getJobDataMap();
    if(jobDataMap != null) {
      jobDetailFactory.getJobDataMap().putAll(getJobDataMap());
    }
    return jobDetailFactory;
  }

  /**
   * Recupera el planificador para el Job
   * @param job
   * @return
   */
  protected CronTriggerFactoryBean getCronTriggerFactoryBean(JobDetail job) {
    String cronExpression = getCronExpression();
    if(cronExpression == null) {
      return null;
    }
    CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
    trigger.setJobDetail(job);
    trigger.setCronExpression(getCronExpression());
    return trigger;
  }

  /**
   * Recupera la clase que implementa el Job
   * @return
   */
  protected abstract Class<? extends Job> getJobClass();

  /**
   * Recupera la descripción del Job
   * @return
   */
  protected abstract String getJobDescription();

  /**
   * Recupera la parametrizacion del job
   * @return
   */
  protected abstract Map<String, Object> getJobDataMap();

  /**
   * Recupera la expresion de planificacion
   * @return
   */
  protected abstract String getCronExpression();

}
