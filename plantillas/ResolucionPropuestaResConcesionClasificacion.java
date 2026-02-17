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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.entity.ExpedienteDocumentoEntity;
import es.mjusticia.sinac.core.persistence.ExpedienteDocumentoDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosFasesTramitesOperacionesAccionesDao;

@Component
public class ResolucionPropuestaResConcesionClasificacion implements EvaluadorClasificacion {

  @Autowired
  private ProcedimientosFasesTramitesOperacionesAccionesDao pftoaDao;
  @Autowired
  private ExpedienteDocumentoDao expedientesDocDao;

  @Override
  public boolean cumpleClasificacion(BigInteger idExp) throws SinacException {
    // Verificar si el expediente tiene un trámite de resolución
    boolean tieneTramiteResOpeProp = tieneTramiteResolucionOperacionPropuesta(idExp);
    ExpedienteDocumentoEntity expDoc = expedientesDocDao.getExpedienteDocumentosByCodTipoDocumentoIdExpediente(idExp,
        "PRORC");
    // si tiene trámite de resolución
    return tieneTramiteResOpeProp || expDoc != null;
  }

  /**
   * Verifica si el expediente tiene un trámite de resolución.
   *
   * @param idExp Identificador del expediente.
   * @return Verdadero si el expediente tiene un trámite de resolución, falso de
   *         lo contrario.
   */
  private boolean tieneTramiteResolucionOperacionPropuesta(BigInteger idExp) {
    List<Object[]> acciones = pftoaDao.getAccionesDisponiblesExpediente(idExp);
    for (Object[] accionOpe : acciones) {
      if (accionOpe[3] != null && "RES".equals(accionOpe[3].toString()) && "GDP".equals(accionOpe[1].toString())) {
        return true;
      }
    }
    return false;
  }

}
