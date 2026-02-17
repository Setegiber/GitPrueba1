package es.mjusticia.sinac.core.business.facade;

import java.util.Map;

import es.mjusticia.sinac.core.batch.SinacJobAuditoria;

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

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.TriggerErroresDto;

public interface JobFacade {
	
	void guardaJobError(TriggerErroresDto triggerErrorDto) throws SinacException;

  void guardaJobErrorDgpRecibir(TriggerErroresDto triggerErrorDto) throws SinacException;

  void envioReporte(Map<String, Object> contextData, SinacJobAuditoria sinacJobAuditoria) throws SinacException;
}
