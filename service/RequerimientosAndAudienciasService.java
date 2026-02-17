package es.mjusticia.sinac.core.business.service;

/*-
 * #%L
 * sinac-core
 * %%
 * Copyright (C) 2023 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
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
import java.util.Date;
import java.util.List;
import java.util.Map;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToRequerirDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteRequerimientoDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.TipoOficioDto;

/**
 * Componente de Negocio para la Interfaz del Servicio de Requerimientos de
 * Subsanación y Trámite de Audiencias.
 *
 * @author NTT Data.
 */
public interface RequerimientosAndAudienciasService {

  /**
   * Obtiene los Tipos de Oficios y sus Documentos a requerir en el Procedimiento.
   *
   * @param idProcedimiento Identificador del Procedimiento.
   * @return Tipos de Oficios y sus Documentos a requerir en el Procedimiento.
   * @throws SinacException Si se produce un error al obtener los Tipos de Oficios
   *                        y sus Documentos a requerir en el Procedimiento.
   */
  List<TipoOficioDto> getTiposOficiosAndDocumentosToRequerirByIdProcedimiento(short idProcedimiento)
      throws SinacException;

  /**
   * Obtiene los Documentos a requerir para el Procedimiento.
   *
   * @param idProcedimiento Identificador del Procedimiento.
   * @return Documentos a requerir para el Procedimiento.
   * @throws SinacException Si se produce un error al obtener los Documentos a
   *                        requerir para el Procedimiento.
   */
  List<DocumentoToRequerirDto> getDocumentosToRequerirByIdProcedimiento(short idProcedimiento) throws SinacException;

  /**
   * Obtiene los Requerimientos asociados al Identificador de Expediente
   * establecido como parámetro.
   *
   * @param idExpediente Identificador del Expediente.
   * @return Requerimientos asociados al Expediente.
   * @throws SinacException Si se produce un error al obtener los Requerimientos
   *                        asociados al Expediente.
   */
  List<ExpedienteRequerimientoDto> getRequerimientosByIdExpediente(BigInteger idExpediente) throws SinacException;

  /**
   * Valida y obtiene los Documentos Requeridos para la generación del
   * Requerimiento.
   *
   * @param values Mapa con los Parámetros asociados a la Acción "Generar
   *               Documento".
   * @return Documentos Requeridos para la generación del Requerimiento.
   * @throws SinacException Si se produce un error al validar u obtener los
   *                        Documentos Requeridos para la generación del
   *                        Requerimiento.
   */
  List<DocumentoTipoDto> validateAndGetDocumentosRequeridos(final Map<String, Object> values) throws SinacException;

  /**
   * Genera el Requerimiento en Estado "Borrador" con los Documentos Requeridos.
   *
   * @param expedienteDocumentoDto Documento asociado al Expediente.
   * @param documentoTipoDtoList   Lista de Documentos Requeridos.
   * @param isAudiencia            true, si es una Audiencia. false, en caso
   *                               contrario.
   * @return DTO con la Información del Requerimiento generado.
   * @throws SinacException Si se produce un error al generar el Requerimiento en
   *                        Estado "Borrador" con los Documentos Requeridos.
   */
  ExpedienteRequerimientoDto generateRequerimientoWithDocumentosRequeridos(
      final ExpedienteDocumentoDto expedienteDocumentoDto, final List<DocumentoTipoDto> documentoTipoDtoList,
      final boolean isAudiencia) throws SinacException;

  /**
   * Obtiene el Requerimiento asociado al Identificador de Documento establecido
   * como parámetro.
   *
   * @param idDocumento Identificador del Documento.
   * @return DTO con la Información del Requerimiento.
   * @throws SinacException Si se produce un error al obtener el Requerimiento.
   */
  ExpedienteRequerimientoDto getRequerimientoByIdDocumento(final BigInteger idDocumento) throws SinacException;

  /**
   * Actualiza en la Tabla "EXP_REQUERIMIENTOS" el Estado del Requerimiento.
   *
   * @param idRequerimiento Identificador del Requerimiento a actualizar el
   *                        Estado.
   * @param ldvMaestraDto   Nuevo Estado a actualizar.
   * @throws SinacException Si se produce un error al actualizar en la Tabla
   *                        "EXP_REQUERIMIENTOS" el Estado del Requerimiento.
   */
  void updateEstadoRequerimiento(final BigInteger idRequerimiento, final LdvMaestraDto ldvMaestraDto)
      throws SinacException;

  /**
   * Actualiza en la Tabla "EXP_REQUERIMIENTOS" el Estado y la Fecha de
   * Finalización del Requerimiento.
   *
   * @param idRequerimiento   Identificador del Requerimiento a actualizar el
   *                          Estado.
   * @param ldvMaestraDto     Nuevo Estado a actualizar.
   * @param fechaFinalizacion Nueva Fecha de Finalización a actualizar.
   * @throws SinacException Si se produce un error al actualizar en la Tabla
   *                        "EXP_REQUERIMIENTOS" el Estado y la Fecha de
   *                        Finalización del Requerimiento.
   */
  void updateEstadoAndFechaFinalizacionRequerimiento(BigInteger idRequerimiento, LdvMaestraDto ldvMaestraDto,
      Date fechaFinalizacion) throws SinacException;

  /**
   * Obtiene los Requerimientos asociados al Expediente en el Estado especificado.
   *
   * @param idExpediente Identificador del Expediente.
   * @param estado       Código del Estado.
   * @return Requerimientos asociados al Expediente en el Estado especificado.
   * @throws SinacException Si se produce un error al obtener los Requerimientos
   *                        asociados al Expediente en el Estado especificado.
   */
  List<ExpedienteRequerimientoDto> getRequerimientosByIdExpedienteAndEstado(BigInteger idExpediente,
      List<String> estado) throws SinacException;

  /**
   * Obtiene el Identificador de la Plantilla asociado al Identificador de
   * Expediente y al Identificador de Requerimiento establecidos como parámetros.
   * 
   * @param idExpediente Identificador del Expediente.
   * @param idExpReq     Identificador del Requerimiento.
   * @return Identificador de la Plantilla.
   * @throws SinacException Si se produce un error al obtener el Identificador de
   *                        la Plantilla.
   */
  short getIdPlantillaByIdExpedienteAndIdExpReq(BigInteger idExpediente, BigInteger idExpReq) throws SinacException;

  /**
   * Obtiene el Requerimiento asociado al Identificador de Requerimiento
   * establecido como parámetro.
   *
   * @param idRequerimiento Identificador del Requerimiento.
   * @return DTO con la Información del Requerimiento.
   * @throws SinacException Si se produce un error al obtener el Requerimiento.
   */
  ExpedienteRequerimientoDto getRequerimientoByIdRequerimiento(BigInteger idRequerimiento) throws SinacException;

}
