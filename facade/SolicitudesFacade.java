package es.mjusticia.sinac.core.business.facade;

import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.BusquedaSolicitudesDto;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToSaveDto;
import es.mjusticia.sinac.core.model.dto.DocumentosEntradaDto;
import es.mjusticia.sinac.core.model.dto.EstadoSolicitudDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.PaisesDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaSolicitudesDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDocumentoDto;
/*-
 * #%L
 * sinac-core
 * %%
 * Copyright (C) 2022 - 2023 Ministerio de Justicia
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
import es.mjusticia.sinac.core.model.dto.SolicitudDto;

public interface SolicitudesFacade {

  void borrarSolicitud(BigInteger idSol) throws SinacException;

  LdvMaestraDto getCatalogoById(int id) throws SinacException;

  SolicitudDto getSolicitudPorId(BigInteger idSol) throws SinacException;

  SolicitudDto saveSolicitud(SolicitudDto solicitud, List<DocumentoToSaveDto> documentosToSave) throws SinacException;

  Page<ResultadoBusquedaSolicitudesDto> getSolicitudesPaginated(BusquedaSolicitudesDto busquedaDto, Pageable pageable,
      String rol) throws SinacException;

  /**
   * 
   * @param idPro
   * @param idSol
   * @return lista documentos por procedimiento de pro_m_doc_tipo
   * @throws SinacException
   */
  List<SolicitudDocumentoDto> getDocumentosPromDocTipo(Short idPro, BigInteger idSol) throws SinacException;

  Long getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(final String codPro, final String codFase,
      final String codTra, final String codOpe, final String codAcc) throws SinacException;

  LinkedList<DocumentoToSaveDto> transformMultipartToDocumentoToSave(SolicitudDto documentosEntrada);

  /**
   * Recoge los datos del expediente y se los traspasa a la solicitud
   * 
   * @param ExpedienteDto
   * @param SolicitudDto
   * @return SolicitudDto
   */
  SolicitudDto transferDatosExpedienteSolicitud(ExpedienteDto exp, SolicitudDto sol);

  /**
   * @param codPais
   * @return PaisesDto
   * @throws SinacException
   */
  PaisesDto getPaisPorCodigo(String codPais);

  /**
   * 
   * @param idSol
   * @param codTipo
   * @return lista documentos por id solicitud y opcionalmente el tipo de doc
   * @throws SinacException
   */
  List<SolicitudDocumentoDto> getDocumentosSolicitud(BigInteger idSol, String codTipo) throws SinacException;

  List<SolicitudDocumentoDto> getDocumentosSolicitudObligatorios(BigInteger idSol, String codPro) throws SinacException;

  List<DocumentoTipoDto> getDocumentosSolicitudObligatorios(String codPro) throws SinacException;

  PersonaDto getPersonaByIdPer(BigInteger idPer) throws SinacException;

  List<ExpedienteDto> getExpedientesByIdPerInteresadoCodCortoPro(BigInteger idPer, String codCortoPro)
      throws SinacException;

  Page<PersonaDto> getPersonasRastreo(String identificador, String nombre, String apellido1, String apellido2,
      Date fechaNacimiento, Pageable pageable) throws SinacException;

  List<ExpedienteDto> getExpedientesByIdPerInteresadoCodCortoProDistinto(BigInteger idPer, String codCortoPro)
      throws SinacException;

  /**
   * Recupera el estado de la solicitud en el momento de la creacion del
   * expediente
   * 
   * @return
   */
  EstadoSolicitudDto getEstadoSolicitud();

  /**
   * Limpia el estado de la solicitud en el momento de creacion del expediente
   */
  void clearEstadoSolicitud();

  /**
   * Devuelve la lista de solicitudes que tengan el estado Borrador y el flgSede
   * activo.
   * 
   * @return
   */
  List<SolicitudDto> getListaSolicitudesSede();

  /**
   * Calcula el codigo del expediente utilizando para ello SolicitudDto
   * 
   * @return
   */
  String calcularCodigoExpediente(SolicitudDto solicitudDto);
}
