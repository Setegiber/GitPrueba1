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

import org.quartz.Job;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de un Job simple de sinac
 */
@Configuration
public class SinacJobGeiserConfiguration extends SinacJobConfiguration {

	/**
	 * Expresión cron parametrizada
	 */
	@Value("${sinac.quartz.sinacJobGeiser.cronExpression}")
	private String cronExpression;

	private static final Class<SinacJobGeiser> clase = SinacJobGeiser.class;

	private static final String DESCRIPCION = "Sinac Job Geiser";
	
	private static final String JOB_NAME = "sinacJobGeiserDetail";

	/**
	 * Factoria de detalle del Job simple de sinax
	 */
	@Bean
	public JobDetailFactoryBean sinacJobGeiserDetail() {
		return getJobDetailFactoryBean();
	}

	/**
	 * Disparador del Job simple de sinax
	 */
	@Bean
	public CronTriggerFactoryBean sinacJobGeiserTrigger(@Qualifier(JOB_NAME) JobDetail sinacJobGeiserDetail) {
		return getCronTriggerFactoryBean(sinacJobGeiserDetail);
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
		Map<String, Object> mapGeiser = new HashMap<>();
		mapGeiser.put("descripcion", DESCRIPCION);
		mapGeiser.put("jobName", JOB_NAME);
		mapGeiser.put("jobClassName", clase.getName());
		return mapGeiser;
	}

	@Override
	protected String getCronExpression() {
		return cronExpression;
	}
}
