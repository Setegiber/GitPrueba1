package es.mjusticia.sinac.core.business.facade.impl;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.mjusticia.sinac.core.batch.SinacJobAuditoria;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.facade.JobFacade;
import es.mjusticia.sinac.core.business.service.JobsService;
import es.mjusticia.sinac.core.model.dto.TriggerErroresDto;

@Service
@Transactional(readOnly = true)
public class JobFacadeImpl implements JobFacade {
	
	@Autowired
	  private JobsService jobService;

	@Override
	@Transactional(readOnly = false)
	public void guardaJobError(TriggerErroresDto triggerErrorDto) throws SinacException {
		jobService.guardaJobError(triggerErrorDto);
	}
	
	 @Override
	  @Transactional(readOnly = false)
	  public void guardaJobErrorDgpRecibir(TriggerErroresDto triggerErrorDto) throws SinacException {
	    jobService.guardaJobErrorDgpRecibir(triggerErrorDto);
	  }

  @Override
  public void envioReporte(Map<String, Object> contextData, SinacJobAuditoria sinacJobAuditoria) throws SinacException {
    jobService.envioReporte(contextData, sinacJobAuditoria);
  }

}
