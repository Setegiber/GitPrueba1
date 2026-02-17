package es.mjusticia.sinac.core.batch;

import java.util.HashMap;
import java.util.Map;
import org.quartz.Job;
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
public class SinacJobMdeSolicitarConfiguration extends SinacJobConfiguration {

	/**
	 * Expresión cron parametrizada
	 */
	@Value("${sinac.quartz.sinacJobMde.solicitar.cronExpression}")
	private String cronExpression;

	private static final Class<SinacJobMdeSolicitar> clase = SinacJobMdeSolicitar.class;

	private static final String DESCRIPCION = "Sinac Job Mde Solicitar";
	
	private static final String JOB_NAME = "sinacJobMdeSolicitarDetail";

	/**
	 * Factoria de detalle del Job simple de sinax
	 */
	@Bean
	public JobDetailFactoryBean sinacJobMdeSolicitarDetail() {
		return getJobDetailFactoryBean();
	}

	/**
	 * Disparador del Job simple de sinax
	 */
	@Bean
	public CronTriggerFactoryBean sinacJobMdeSolicitarTrigger(@Qualifier(JOB_NAME) JobDetail sinacJobMdeSolicitarDetail) {
		return getCronTriggerFactoryBean(sinacJobMdeSolicitarDetail);
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
		Map<String, Object> mapMde = new HashMap<>();
		mapMde.put("descripcion", DESCRIPCION);
		mapMde.put("jobName", JOB_NAME);
		mapMde.put("jobClassName", clase.getName());
		return mapMde;
	}

	@Override
	protected String getCronExpression() {
		return cronExpression;
	}
}
