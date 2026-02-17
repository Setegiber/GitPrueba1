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

import java.math.BigInteger;
import java.util.List;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.EstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.FormularioOposicionDatosDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesAccionesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesDto;
import es.mjusticia.sinac.core.model.dto.TramiteDto;

public interface ProcedimientosService {

  List<ProcedimientoDto> getProcedimientos() throws SinacException;

  List<EstadoDto> getEstados() throws SinacException;

  ProcedimientoDto getProcedimientoDtoById(short id) throws SinacException;

  List<EstadoDto> getEstadoByProcedimientoId(Short idProcedimiento) throws SinacException;

  /**
   * Obtiene el Código SIA asociado al Identificador de Procedimiento establecido
   * como parámetro.
   *
   * @param idProcedimiento Identificador del Procedimiento.
   * @return Código SIA asociado al Procedimiento.
   * @throws SinacException Si se produce un error al obtener el Código SIA
   *                        asociado al Procedimiento.
   */
  String getCodSiaByIdProcedimiento(final short idProcedimiento) throws SinacException;

  ProcedimientosFasesTramitesOperacionesAccionesDto getProcedimientosFasesTramitesOperacionesAccionesByIdProFasesTraOpeCodAccion(
      Long id, String codAccion) throws SinacException;

  ProcedimientosFasesTramitesOperacionesDto getProcedimientosFasesTramitesOperacionesById(Long id)
      throws SinacException;

  ProcedimientoDto getProcedimientoCompleto(short idPro) throws SinacException;

  List<ExpedienteDocumentoDto> getDocumentosRequeridos(BigInteger idExp) throws SinacException;

  /**
   * Obtiene el Procedimiento, Fase, Trámite y Operación para el Identificador
   * establecido como parámetro.
   *
   * @param idProFaseTraOpe Identificador de Procedimiento, Fase, Trámite y
   *                        Operación.
   * @return DTO con la Información del Procedimiento, Fase, Trámite y Operación.
   * @throws SinacException Si se produce un error al obtener la Información del
   *                        Procedimiento, Fase, Trámite y Operación.
   */
  ProcedimientosFasesTramitesOperacionesDto getProcedimientosFasesTramitesOperacionesByIdProFaseTraOpe(
      final long idProFaseTraOpe) throws SinacException;

  /**
   * Obtiene el Procedimiento, Fase, Trámite, Operación y Acción para el
   * Identificador de Procedimiento, Fase, Trámite y Operación y el Identificador
   * de Acción establecidos como parámetro.
   *
   * @param idProFaseTraOpe Identificador de Procedimiento, Fase, Trámite y
   *                        Operación.
   * @param codAccion       Código de la Acción.
   * @return DTO con la Información del Procedimiento, Fase, Trámite, Operación y
   *         Acción.
   * @throws SinacException Si se produce un error al obtener la Información del
   *                        Procedimiento, Fase, Trámite, Operación y Acción.
   */
  ProcedimientosFasesTramitesOperacionesAccionesDto getProcedimientosFasesTramitesOperacionesAccionesByIdProFaseTraOpeAndIdAccion(
      final long idProFaseTraOpe, final String codAccion) throws SinacException;

  Long getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(final String codPro, final String codFase,
      final String codTra, final String codOpe, final String codAcc) throws SinacException;

  List<ProcedimientosFasesDto> getListaProcedimientoFases();

  List<TramiteDto> getListaTramiteByIdProIdFase(Short procId, Short faseId);

  List<EstadoDto> getEstadosByidProcedimientoidTramiteIdFase(Short procId, Short faseId, Short tramiteId);

  Long getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(String codPro, String codTra, String codOpe,
      String codAcc) throws SinacException;

  Long getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(long idProFasTraOpe, String codAcc)
      throws SinacException;

  ProcedimientoDto getProcedimientoByCodPro(String codPro);

  /**
   * Obtiene el IdProFaseTraOpeAcc en base al Código de Procedimiento, al Código
   * de Trámite, al Código de Operación y al Código de Acción.
   *
   * @param codProcedimiento Código del Procedimiento.
   * @param codTramite       Código del Trámite.
   * @param codOperacion     Código de la Operación.
   * @param codAccion        Código de la Acción.
   * @return IdProFaseTraOpeAcc en base al Código de Procedimiento, al Código de
   *         Trámite, al Código de Operación y al Código de Acción.
   * @throws SinacException Si se produce un error al obtener el
   *                        IdProFaseTraOpeAcc en base al Código de Procedimiento,
   *                        al Código de Trámite, al Código de Operación y al
   *                        Código de Acción,
   */
  Long getIdProFaseTraOpeAccByCodProcedimientoAndCodTramiteAndCodOperacionAndCodAccion(String codProcedimiento,
      String codTramite, String codOperacion, String codAccion) throws SinacException;

  /**
   * Recupera el procedimiento del expediente
   * 
   * @param idExp
   * @return
   * @throws SinacException
   */
  ProcedimientoDto getProcedimientoByIdExp(BigInteger idExp) throws SinacException;

  Boolean validarProcedimientoConsulta(String codCorto) throws SinacException;

  FormularioOposicionDatosDto getFormularioOposicionDatosByCodCampo(String codCampo) throws SinacException;
  
//  Map<String, List<ProcedimientoDto>> getProcedimientosValidadosGestAvi() throws SinacException;
}
