package es.mjusticia.sinac.core.business.service;

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

import java.util.List;
import java.util.Map;

import es.mjusticia.sinac.core.batch.SinacJobAuditoria;
import es.mjusticia.sinac.core.batch.SinacJobDto;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.TriggerErroresDto;

/**
 * Servicio para la gestion de tareas en segundo plano de SINAC
 * 
 * @author NTTData
 */
public interface JobsService {

  /**
   * Recupera la lista de tareas planificadas en SINAC
   * 
   * @return
   * @throws SinacException
   */
  List<SinacJobDto> getSinacJobs() throws SinacException;

  /**
   * Ejecuta un Job de forma inmediata
   * 
   * @param sinacJob
   * @param model
   * @throws SinacException
   */
  void ejecutarJob(SinacJobDto sinacJob, Map<String, Object> model) throws SinacException;

  /**
   * Lanza una ejecución sobre un item concreto para un Job existente
   * 
   * @param sinacJob
   * @param parámetros
   * @throws SinacException
   */
//  void ejecutarLlamada(SinacJobDto sinacJob, Map<String, Object> parámetros) throws SinacException;

  /**
   * Guarda los errores ocurridos en los Jobs
   * 
   * @param siqrErrorDto
   * @throws SinacException
   */
  void guardaJobError(TriggerErroresDto siqrErrorDto) throws SinacException;

  void guardaJobErrorDgpRecibir(TriggerErroresDto triggerErrorDto) throws SinacException;

  void envioReporte(Map<String, Object> contextData, SinacJobAuditoria sinacJobAuditoria) throws SinacException;

  void pauseTrigger(String jobName) throws SinacException;

  void resumeTrigger(String jobName) throws SinacException;

  String getTriggerState(String jobName) throws SinacException;

}
