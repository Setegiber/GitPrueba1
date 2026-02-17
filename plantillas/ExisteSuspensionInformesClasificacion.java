package es.mjusticia.sinac.core.business.plantillas;

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

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.entity.ExpedienteDocumentoEntity;
import es.mjusticia.sinac.core.persistence.ExpedienteDocumentoDao;

@Component
public class ExisteSuspensionInformesClasificacion implements EvaluadorClasificacion {

  @Autowired
  private ExpedienteDocumentoDao expedientesDocDao;

  @Override
  public boolean cumpleClasificacion(BigInteger idExp) throws SinacException {
    ExpedienteDocumentoEntity expDoc = expedientesDocDao.getExpedienteDocumentosByCodTipoDocumentoIdExpediente(idExp,
        "OSINF");
    ExpedienteDocumentoEntity expDocOr = expedientesDocDao.getExpedienteDocumentosByCodTipoDocumentoIdExpediente(idExp,
        "ORINF");
    return expDocOr != null || (expDoc != null && expDoc.getLdvMaestraEntityByIdEstDocLdv() != null
        && expDoc.getLdvMaestraEntityByIdEstDocLdv().getCodLdvMae() != null
        && ("EDOC-COM".equals(expDoc.getLdvMaestraEntityByIdEstDocLdv().getCodLdvMae())
            || "EDOC-ECO".equals(expDoc.getLdvMaestraEntityByIdEstDocLdv().getCodLdvMae())));
  }

}
