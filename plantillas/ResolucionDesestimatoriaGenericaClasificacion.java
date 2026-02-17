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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.entity.ExpedienteEstadoEntity;
import es.mjusticia.sinac.core.model.mapper.LdvMaestraMapper;
import es.mjusticia.sinac.core.persistence.ExpedienteDocumentoDao;
import es.mjusticia.sinac.core.persistence.ExpedienteEstadoDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosFasesTramitesOperacionesAccionesDao;

@Component
public class ResolucionDesestimatoriaGenericaClasificacion implements EvaluadorClasificacion {

  @Autowired
  private ExpedienteEstadoDao expedientesEstadoDao;
  @Autowired
  private LdvMaestraMapper ldvMaestraMapper;
  @Autowired
  private ProcedimientosFasesTramitesOperacionesAccionesDao pftoaDao;

  @Autowired
  private ExpedienteDocumentoDao expedientesDocDao;

  @Override
  public boolean cumpleClasificacion(BigInteger idExp) throws SinacException {
    List<String> listaEstados = Arrays.asList("DCVA");

    List<ExpedienteEstadoEntity> expEstados = obtenerEstadosFiltrados(idExp, listaEstados);

    return !expEstados.isEmpty();
  }

  /**
   * Obtiene y filtra los estados del expediente.
   *
   * @param idExp Identificador del expediente.
   * @return Lista de estados filtrados del expediente.
   */
  private List<ExpedienteEstadoEntity> obtenerEstadosFiltrados(BigInteger idExp, List<String> listaEstados) {
    List<ExpedienteEstadoEntity> expEstados = expedientesEstadoDao.getEstadosExpedienteExiste(idExp);
    if (expEstados != null) {
      return expEstados.stream().filter(c -> listaEstados.contains(c.getEstado().getEstadoFin().getCodEstado()))
          .toList();
    }
    return Collections.emptyList();
  }
}
