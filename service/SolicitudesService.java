package es.mjusticia.sinac.core.business.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.BusquedaSolicitudesDto;
import es.mjusticia.sinac.core.model.dto.EstadoSolicitudDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaSolicitudesDto;
import es.mjusticia.sinac.core.model.dto.SolOpoForDatosDto;
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
import es.mjusticia.sinac.core.model.dto.SolicitudFormularioValDto;
import es.mjusticia.sinac.core.model.dto.SolicitudesPersonasDto;
import es.mjusticia.sinac.core.model.entity.RegistroEntity;

public interface SolicitudesService {

  void borrarSolicitud(BigInteger idSol) throws SinacException;

  SolicitudDto saveSolicitud(SolicitudDto solicitudDto) throws SinacException;

  void saveSolicitudFormularioVal(SolicitudFormularioValDto solicitudFormularioValDto) throws SinacException;

  void saveSolicitudDocumento(SolicitudDocumentoDto solicitudFormularioValDto) throws SinacException;

  SolicitudDto getSolicitudPorId(BigInteger idSolicitud) throws SinacException;

  void deleteSolicitudDocumento(SolicitudDocumentoDto solicitudDocumentoDto) throws SinacException;

  List<SolicitudDocumentoDto> getSolicitudDocumentosBySolicitudId(BigInteger idSolicitud) throws SinacException;

  List<SolicitudesPersonasDto> getPersonasSolicitudBySolicitudId(BigInteger idSolicitud) throws SinacException;

  List<SolicitudDocumentoDto> getDocsSolBySolicitudId(BigInteger idSolicitud) throws SinacException;

  Map<Integer, List<ResultadoBusquedaSolicitudesDto>> getSolicitudesPaginated(BusquedaSolicitudesDto busquedaSolDto,
      Pageable pageable, String rol) throws SinacException;

  void saveSolicitudesPersonas(SolicitudDto solicitud, PersonaDto interesado, LdvMaestraDto catalogoByCod)
      throws SinacException;

  /**
   * 
   * @param idPro
   * @param idSol
   * @return lista documentos por procedimiento de pro_m_doc_tipo
   */
  List<SolicitudDocumentoDto> getDocumentosPromDocTipo(Short idPro, BigInteger idSol);

  void deleteSolicitudPersona(SolicitudDto solicitud, PersonaDto persona) throws SinacException;

  void desactivarSolicitudFormularioValAnterioresByIdSol(BigInteger idSol) throws SinacException;

  RegistroEntity getRegistroSolicitud(BigInteger idSol);

  /**
   * 
   * @param idSol
   * @param codTipo
   * @return lista documentos por id solicitud y opcionalmente el tipo de doc
   */
  List<SolicitudDocumentoDto> getDocumentosSolicitud(BigInteger idSol, String codTipo) throws SinacException;

  /**
   * Guarda los datos de notificacion, personaContactoElectronico y
   * personaDomicilio
   * 
   * @param ExpedienteDto
   * @return ExpedienteDto
   * @throws SinacException
   */
  SolicitudDto saveSolicitudesCorDom(SolicitudDto solicitud);

  /**
   * Recoge los datos del expediente y se los traspasa a la solicitud
   * 
   * @param ExpedienteDto
   * @param SolicitudDto
   * @return SolicitudDto
   */
  SolicitudDto transferDatosExpedienteSolicitud(ExpedienteDto exp, SolicitudDto sol);

  List<SolicitudDocumentoDto> getDocumentosSolicitudObligatorios(BigInteger idSol, String codPro);

  /**
   * Recupera el estado de la solicitud en el momento de la creación del
   * expediente
   * 
   * @return
   */
  EstadoSolicitudDto getEstadoSolicitud();

  /**
   * Asigna el estado de la solicitud en el momento de la creación
   * 
   * @param estado
   */
  void setEstadoSolicitud(String estado);

  /**
   * Limpia el estado de la solicitud en el momento de la creación
   * 
   * @param estado
   */
  void clearEstadoSolicitud();
  
  void saveSolOpoForDatos(SolOpoForDatosDto solOpoForDato, BigInteger idSol) throws SinacException;
  
  List<SolicitudDto> getListaSolicitudesSede();

}
