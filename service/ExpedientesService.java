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

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.data.domain.Pageable;
import org.xml.sax.SAXException;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.ArchivoFtpDto;
import es.mjusticia.sinac.core.model.dto.BoeAnunciosDto;
import es.mjusticia.sinac.core.model.dto.BusquedaAvisosExpDto;
import es.mjusticia.sinac.core.model.dto.BusquedaExpedientesDto;
import es.mjusticia.sinac.core.model.dto.ComunicacionesExternasDto;
import es.mjusticia.sinac.core.model.dto.DatosSolicitudInformeMjuDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToSaveDto;
import es.mjusticia.sinac.core.model.dto.DocumentosTramiteDto;
import es.mjusticia.sinac.core.model.dto.EnviarEmailDto;
import es.mjusticia.sinac.core.model.dto.EstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteAvisoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteBoeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteComunicacionesExternasDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteEstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteFormularioValDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDgpDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDgpTramiteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeMdeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInsideDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteNotificacionesDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteSecuenciasDto;
import es.mjusticia.sinac.core.model.dto.ExpedientesPersonasDto;
import es.mjusticia.sinac.core.model.dto.ExpedientesVinculadosDto;
import es.mjusticia.sinac.core.model.dto.InsideEstadoDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.LocalidadesDto;
import es.mjusticia.sinac.core.model.dto.PaisesDto;
import es.mjusticia.sinac.core.model.dto.ParametrizacionDto;
import es.mjusticia.sinac.core.model.dto.PerCertificacionesDto;
import es.mjusticia.sinac.core.model.dto.PerFilNiesDto;
import es.mjusticia.sinac.core.model.dto.PerFiliacionesDto;
import es.mjusticia.sinac.core.model.dto.PerPadronDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesAccionesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosPlantillasCriteriosDto;
import es.mjusticia.sinac.core.model.dto.ProvinciasDto;
import es.mjusticia.sinac.core.model.dto.RenovacionDniDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaAvisosExpDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaExpedientesDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDto;
import es.mjusticia.sinac.core.model.dto.SolicitudesPersonasDto;
import es.mjusticia.sinac.core.model.dto.TiposViaDto;
import es.mjusticia.sinac.core.model.dto.TitulosDto;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;
import es.mjusticia.sinac.core.model.dto.ValidacionSemaforoDto;
import es.mjusticia.sinac.core.model.entity.ExpedienteInformesMjuFicherosEntity;
import es.mjusticia.sinac.core.model.entity.LdvMaestraEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientoEntity;
import es.mjusticia.sinac.core.model.entity.RegistroEntity;
import es.mjusticia.sinac.dgp.dto.TitularDto;
import es.mjusticia.sinac.filiaciones.dto.RespuestaAltaFiliacionDto;
import jakarta.activation.DataHandler;
import jakarta.validation.Valid;

/**
 * Componente de Negocio para la Interfaz del Servicio de Expediente.
 *
 * @author NTT Data.
 */
public interface ExpedientesService {

  /**
   * Realiza la inserción de los datos del expediente.
   *
   * @param solicitudDto              contiene la información de la solicitud
   *                                  guardada anteriomente.
   * @param identificadorExpedienteGD Identificador ENI del Expediente en el
   *                                  Gestor Documental.
   * @return DTO con la Información del Expediente.
   * @throws SinacException Si se produce un error al guardar el expediente.
   */
  ExpedienteDto saveExpediente(final SolicitudDto solicitudDto, final String identificadorExpedienteGD,
      final String codigoExpediente) throws SinacException;

  /**
   * Realiza la inserción de los datos de los campos que son exclusivos del
   * procedimiento.
   *
   * @param expediente            contiene la información del expediente guardada
   *                              anteriomente.
   * @param formularioCampoValida contiene la lista de campos exclusivos del
   *                              procedimiento.
   * @throws SinacException Si se produce un error al guardar los campos
   *                        exclusivos.
   */
  void saveCamposFormularioExpediente(final ExpedienteDto expediente, SolicitudDto solicitud) throws SinacException;

  /**
   * Realiza la inserción de datos para enlazar expedientes y personas.
   *
   * @param solicitudesPersonasDto contiene la lista de personas asignadas a una
   *                               solicitud.
   * @param expediente             contiene la información del expediente guardada
   *                               anteriomente.
   * @throws SinacException Si se produce un error al guardar la unión entre
   *                        expedientes y persona.
   */
  void saveExpedientePersonas(final List<SolicitudesPersonasDto> solicitudesPersonasDto, final ExpedienteDto expediente)
      throws SinacException;

  /**
   * Realiza la inserción de datos para enlazar expedientes y estados.
   *
   * @param expediente contiene la información del expediente guardada
   *                   anteriomente.
   * @throws SinacException Si se produce un error al guardar la unión entre
   *                        expedientes y estados.
   */
  /*
   * void saveEstadoExpediente(final ExpedienteDto expediente, String
   * codEstadoIni, String codPro, String codFase, String codTramite, String
   * codOpe, String codAccion, boolean primerReg) throws SinacException;
   */
  /**
   * Obtiene el Expediente asociado al Identificador de Expediente establecido
   * como parámetro.
   *
   * @param idExpediente Identificador del Expediente.
   * @return DTO con la Información del Expediente.
   * @throws SinacException Si se produce un error al obtener el Expediente.
   */
  ExpedienteDto getExpedienteByIdExpediente(final BigInteger idExpediente) throws SinacException;

  /**
   * asignamos un usuario al expediente mediente el id de expediente y de usuario
   * comprobamos que no este ya asignado a este u otro usuario
   *
   * @param idExp, idUsu
   * @return int
   */
  void setUsuarioToExpediente(BigInteger idExp) throws SinacException;

  /**
   * desasignamos un usuario al expediente mediente el id de expediente y de
   * usuario comprobamos que sigue estando asignado a este usuario por eso
   * necesitamos ideUsu
   *
   * @param idExp, idUsu
   * @return int
   */
  void unsetUsuarioToExpediente(BigInteger idExp, Integer idUsu) throws SinacException;

  /**
   * Metodo que recupera el detalle del expediente
   */
  ExpedienteDto getExpedientebyId(BigInteger idExp) throws SinacException;

  void updateDetalleExpediente(ExpedienteDto detalleExpedienteDto, String segmentoActualizar) throws SinacException;

  List<PaisesDto> getPaises() throws SinacException;

  List<ProvinciasDto> getProvincias() throws SinacException;

  List<LocalidadesDto> getLocalidades() throws SinacException;

  List<LdvMaestraDto> getTiposIdentificacionDetalleExp() throws SinacException;

  List<LdvMaestraDto> getSexoDetalleExp() throws SinacException;

  List<LdvMaestraDto> getEstCivilDetalleExp() throws SinacException;

  /**
   * Metodo que recupera un procedimiento para el cambio de estado
   */
  ProcedimientoEntity recuperarIdPro(BigInteger idExp) throws SinacException;

  /**
   * Metodo que guarda la comunicación externa.
   *
   * @param idExp                     Identificador del expediente.
   * @param comunicacionesExternasDto formulario comunicaciones externas.
   * 
   * @throws SinacException Si se produce un error al guardar el Expediente.
   */
  void saveComunicacionExterna(final BigInteger idExp, final ComunicacionesExternasDto comunicacionesExternasDto,
      Map<String, Object> valores) throws SinacException;

  /**
   * cambia la ldvmaestra a favorable/desfavorable.
   *
   * @param idExp     Identificador del expediente.
   * @param idInforme Identificador del expedienteInforme.
   * @param operacion Identificador del operación a realizar.
   * @return int 1 si funciona bien.
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   * @throws SQLException
   */
  void informeFavorable(BigInteger idExp, BigInteger idInforme) throws SinacException;

  void informeDesfavorable(BigInteger idExp, BigInteger idInforme) throws SinacException;

  Map<String, ExpedienteInformeDto> getListaExpedienteInformeByExpId(BigInteger idExp) throws SinacException;

  List<ExpedienteComunicacionesExternasDto> getListaExpedienteComunicacionesExternasByExpId(BigInteger idExp)
      throws SinacException;

  /**
   * Metodo que se encarga de enviar el email
   * 
   * @param idExpediente
   * @param enviarEmailDto
   * @throws SinacException
   */
  void sendEmail(BigInteger idExpediente, @Valid EnviarEmailDto enviarEmailDto) throws SinacException;

  List<TiposViaDto> getTiposVia() throws SinacException;

  ExpedienteNotificacionesDto getExpedienteNotificacionesbyIdSolSun(String idSolSun) throws SinacException;

  ExpedienteNotificacionesDto saveExpedienteNotificaciones(ExpedienteNotificacionesDto expedienteNotificacionesDto,
      ExpedienteDocumentoDto expedienteDto) throws SinacException;

  ExpedientesPersonasDto getExpPersonasPorNifPerIdExp(String nif, BigInteger idExp) throws SinacException;

  /**
   * cambia la ldvmaestra a solicitado
   *
   * @param idExp          Identificador del expediente.
   * @param tipoInforme    tipo de informe.
   * @param fechaSolicitud fecha de solicitud
   * @return int con el resultado de la operación
   * @throws SinacException Si se produce un error al cambiar el valor
   */
  void informeSolicitado(BigInteger idExp, String tipoInforme, BigInteger idExpInf) throws SinacException;

  /**
   * cambia la ldvmaestra a recibido
   *
   * @param idExp                 Identificador del expediente.
   * @param idExpInforme          Identificador del expediente informe.
   * @param documentosEntradaDto  objeto con los campos del formulario.
   * @param fechaEmision          fecha de emisión.
   * @param fechaRecepcion        fecha de recepción.
   * @param sentido               sentido ldv de informe
   * @param idExpedienteDocumento idExpedienteDocumento
   * @return int con el resultado de la operación
   * @throws SinacException Si se produce un error al cambiar el valor
   */
  void informeRecibido(BigInteger idExp, BigInteger idExpInforme, ExpedienteInformeDgpDto expedienteInformeDgpDto,
      Date fechaEmision, Date fechaRecepcion, String sentido, BigInteger idExpedienteDocumento) throws SinacException;

  /**
   * obtiene ldvMaestra by codLdv
   *
   * @param codLdv codLdv.
   * @return LdvMaestraDto con el resultado de la operación
   * @throws SinacException Si se produce un error al cambiar el valor
   */
  LdvMaestraDto getLdvByCod(String codLdv) throws SinacException;

  /**
   * valida todos los informes de un expediente
   * 
   * @param idExp
   * @return ModelAndView
   * @throws SinacException
   */
  void validarTodosInformes(BigInteger idExp) throws SinacException;

  /**
   * Metodo para recuperar los expedientes con los filtros proporcionados
   */

  Map<Integer, List<ResultadoBusquedaExpedientesDto>> getExpedientesPaginated(BusquedaExpedientesDto busquedaExpDto,
      Pageable pageable) throws SinacException;

  List<ResultadoBusquedaExpedientesDto> getExpedientesFiltrados(BusquedaExpedientesDto busquedaExpDto,
      Pageable pageable) throws SinacException;

  List<DocumentosTramiteDto> getDocumentosConsejoMinistros(BigInteger idExp) throws SinacException;

  EnviarEmailDto setCamposPredefEmailAcuerdoConMin(EnviarEmailDto enviarEmailDto, String interesado,
      ExpedienteDto expediente) throws SinacException;

  List<ResultadoBusquedaExpedientesDto> getExpedientesAsignadosPublicacionBoe(BigInteger idExp) throws SinacException;

  List<ExpedienteEstadoDto> getExpedienteEstadoByIdExp(BigInteger idExp) throws SinacException;

  List<ExpedienteEstadoDto> getHistoricoAccionesEjecutadas(final BigInteger idExp) throws SinacException;

  // TODO getContadorDocumentosByEstado
//  Map<String, Integer> getContadorDocumentosByEstado(BigInteger idExp) throws SinacException;

  String getPlazoArchivoElectronico(BigInteger idExp) throws SinacException;

  /**
   * Recupera el resumen de datos de un expediente
   * 
   * @param idExpediente
   * @return
   */
  Map<String, Object> getResumenExpediente(BigInteger idExpediente) throws SinacException;

  /**
   * Devuelve una lista de documentos del expediente a partir de una lista de
   * códigos
   * 
   * @param codigos
   * @param idExpediente
   * @throws SinacException
   */
  List<ExpedienteDocumentoDto> obtenerDocumentosExpedientesPorCodigos(List<String> codigos, BigInteger idExpediente)
      throws SinacException;

  /**
   * Metodo que permite guardar las fechas de cierre y archivo del expediente
   * indicado por su idExp
   * 
   * @param idExp
   * @param fechaCierre
   * @param fechaArchivo
   * @throws SinacException
   */
  void saveExpedienteFechaCierreArchivo(BigInteger idExp, Date fechaCierre, Date fechaArchivo) throws SinacException;

  /**
   * Devuelve el expediente a partir de su idExp.
   * 
   * @param idExp
   * @return
   * @throws SinacException
   */
  ExpedienteDto getExpedientebyIdExp(BigInteger idExp) throws SinacException;

  int getSecuenciaExpediente(final short idProcedimiento, final short anio) throws SinacException;

  public List<String> validateErroresPlantillas(final PlantillaDto plantillaDto) throws SinacException;

  public DocumentoToSaveDto reintentoSubidaGestorDocumental(DocumentoToSaveDto documentoToSaveDto,
      final ExpedienteDto expedienteDto, final ExpedienteDocumentoDto expedienteDocumentoDto,
      final String nfsPathDocumentosSolicitudes, final Boolean esEntrada, LdvMaestraDto estadoDoc);

  public DocumentoToSaveDto getDocumentoToSaveDtoReintentoGD(ExpedienteDocumentoDto expedienteDocumentoDto,
      Boolean recuperarContenido) throws SinacException;

  public ExpedienteDocumentoDto saveDocumentoSalida(ExpedienteDocumentoDto expedienteDocumentoDto,
      DataHandler contenido) throws SinacException;

  public void saveDatosResolucion(BigInteger idExpediente, Date fechaCertificacion, Date fechaPublicacionBoe,
      Date fechaRecepcionAcuerdo, Integer resultadoAcuerdo, LdvMaestraEntity ldvMaestraEntity, String estadoOrigen)
      throws SinacException;

  RegistroEntity getRegistroExpediente(BigInteger idExp) throws SinacException;

  public void updateFechaEfectosExp(Date fechaEfectos, BigInteger idExp) throws SinacException;

  /**
   * Resuelve el filtro de las tarjetas de inicio de la pantalla de Busqueda de
   * Expediente. Dependiendo del id de la tarjeta, realizará un filtro u otro.
   * 
   * 
   * @param busquedaDto
   * @return BusquedaExpedientesDto
   * @throws SinacException
   */
  BusquedaExpedientesDto resolverFiltroTarjetaInicio(BusquedaExpedientesDto busquedaDto) throws SinacException;

  /**
   * Obtiene un mapa de valores para ejecutar las acciones de los informes
   * 
   * @param codExpediente Código de expediente
   * @param tipoInforme   Tipo de informe
   * @return
   * @throws SinacException
   */
  Map<String, Object> getIdExpCodProceByCodExpediente(String codExpediente, String tipoInforme) throws SinacException;

  void saveExpedientesPersonas(ExpedienteDto expediente, PersonaDto persona, LdvMaestraDto tipo, Boolean isNotificar)
      throws SinacException;

  ExpedienteDto saveExpediente(ExpedienteDto expedienteDto) throws SinacException;

  void desactivarExpedienteFormularioValAnterioresByIdExp(BigInteger idExp) throws SinacException;

  void saveExpedienteFormularioVal(ExpedienteFormularioValDto expedienteFormularioValDto) throws SinacException;

  DatosSolicitudInformeMjuDto obtenerDatosSolicitudInformeMju(BigInteger idExpInf) throws SinacException;

  void cambiarEstadoInformesAsolicitado(String nombreArchivo, String codigoEstado, String tipoInforme, BigInteger idExp)
      throws SinacException;

  boolean existeExpedienteInformesMjuFichero(String nombreArchivo) throws SinacException;

  Boolean existeDocumentoExpediente(String tipoDoc, BigInteger idExp) throws SinacException;

  ExpedienteInformesMjuFicherosEntity guardaExpedienteInformesMjuFicheros(String nombreArchivo, String codigoEstado)
      throws SinacException;

  void guardaRespuestaInformeMjuPenados(ArchivoFtpDto archivoFtpDto) throws SinacException;

  BigInteger getIdExpedienteByCodExpediente(String codigoExpediente) throws SinacException;

  void actualizarEstadoArchivoFtp(String nombreArchivo, String codigoEstado) throws SinacException;

  List<BigInteger> getIdsExpedienteByCodEstadoCodTipoInforme(String codEstado, String codTipoInforme, Integer maxItem)
      throws SinacException;

  /**
   * Actualiza el informe de la dgp cuando el webservice devuelve error
   * 
   * @param idExp                   id del expediente
   * @param codigoEstado            Código de estado
   * @param codigoEstadoSec         Código de estado secundario
   * @param literalError            SMS del error
   * @param alta                    true si el error es del alta del informe
   * @param codigoPeticionRespuesta Código de la petición
   * @throws SinacException
   */
  boolean actualizaInformeDgpRechazado(BigInteger idExp, String codigoEstado, String codigoEstadoSec,
      String literalError, String codigoPeticionRespuesta, boolean alta) throws SinacException;

  void desactivarInformesActivosError(BigInteger idExp, String tipoInforme) throws SinacException;

  void saveRenovacionDni(RenovacionDniDto renovacionDniDto) throws SinacException;

  void saveExpedienteInfomeDgpTramite(ExpedienteInformeDgpTramiteDto expedienteInformeDgpTramite) throws SinacException;

  ExpedienteInformeDgpDto saveExpedienteInformeDgp(ExpedienteInformeDgpDto expedienteInformeDgp) throws SinacException;

  void saveExpedienteInformeDgp(BigInteger idExpInf, ExpedienteDocumentoDto expedienteDocumentoDto)
      throws SinacException;

  void informeRecibido(BigInteger idExp, BigInteger idExpInforme, Date fechaEmision, Date fechaRecepcion,
      String sentido, BigInteger idExpedienteDocumento) throws SinacException;

  TitularDto getDatosSolicitudInformeDgp(BigInteger idExpInforme);

  List<BigInteger> getIdsExpedienteInformesByCodEstadoCodTipoInforme(String codEstado, String codTipoInforme);

  void informeSolicitadoDgp(BigInteger idExp, String tipoInforme, Date fechaSolicitud, String idSolicitudInforme,
      BigInteger idExpInf) throws SinacException;

  /**
   * Guarda el expediente informe.
   * 
   * @param ExpedienteInformeDto
   * @throws SinacException
   */
  void saveExpedienteInforme(ExpedienteInformeDto expedienteInformeDto) throws SinacException;

  boolean checkUsarioAsignadoExpediente(BigInteger idExp, Integer idUsuario) throws SinacException;

  ProcedimientosFasesTramitesOperacionesAccionesDto getPftoaResponderCniByidExp(BigInteger idExp) throws SinacException;

  ProcedimientosFasesTramitesOperacionesDto getPftobyCod(BigInteger idExpediente, String codFase, String codTramite,
      String codOpe);

  TitulosDto obtenerTitulosEducacion(BigInteger idPersona) throws SinacException;

  TitulosDto consultarTitulosEducacion(ExpedienteDto expedienteDto);

  /**
   * Recupera el ldv por id, actualmente se usa por que de los selects de
   * identidad nos llega informado el id y no el cod, asi luego podemos comparar
   * por el cod
   * 
   * @param idLdvMae id del ldvMaestra
   * @return LdvMaestraDto
   * @throws SinacException
   */
  LdvMaestraDto getLdvById(Integer idLdvMae) throws SinacException;

  /**
   * @param idExp
   * @param idPersona
   */
  void desactivarRepresentante(BigInteger idExp, BigInteger idPersona) throws SinacException;

  /**
   * Guarda los datos de notificacion, personaContactoElectronico y
   * personaDomicilio
   * 
   * @param ExpedienteDto
   * @return ExpedienteDto
   * @throws SinacException
   */
  ExpedienteDto saveExpedientesCorDom(ExpedienteDto expediente);

  List<ExpedienteDto> getExpedientesByIdPerInteresadoCodCortoPro(BigInteger idPer, String codCortoPro)
      throws SinacException;

  LinkedList<DocumentoToSaveDto> acumularExpediente(ExpedienteDto expediente);

  ExpedienteDto getExpedienteSimpleByCodExpediente(String codExpediente) throws SinacException;

  void saveExpedientesViculados(ExpedientesVinculadosDto expedientesVinculadosDto);

  void saveExpedienteSecuencias(ExpedienteSecuenciasDto expSecDto);

  List<LdvMaestraDto> getTipoRelacionesExpediente() throws SinacException;

  void desactivarRepresentanteExpediente(ExpedienteDto expediente, PersonaDto persona);

  void acumularPersonasFromSolicitudToExpediente(ExpedienteDto expediente, SolicitudDto solicitud);

  List<ExpedienteDto> getExpedientesByIdPerInteresadoCodCortoProDistinto(BigInteger idPer, String codCortoPro)
      throws SinacException;

  void consultarCertificaciones(ExpedienteDto expedienteDto);

  List<PerCertificacionesDto> getPerCertificacionesByIdPerTipoCertificacion(BigInteger idPersona,
      String tipoCertificacion) throws SinacException;

  List<PerCertificacionesDto> getPerCertificacionesByIdPer(BigInteger idPersona) throws SinacException;

  /**
   * Obtiene los expedienteAviso por idExpediente y se filtra por admin
   *
   * @param BigInteger idExpediente
   * @return List<ExpedienteAvisoDto> lista de expedienteAviso
   * @throws SinacException Si se produce un error al obtener la lista
   */
  List<ExpedienteAvisoDto> getAvisosExpedienteByIdExp(BigInteger idExpediente) throws SinacException;

  /**
   * Obtiene los resultados de busqueda para Page, filtrando por Busqueda,
   * Pageable y si es admin
   *
   * @param BusquedaAvisosExpDto, pageable, isAdmin
   * @return Map<Integer, List<ResultadoBusquedaAvisosExpDto>>
   * @throws SinacException Si se produce un error al obtener el map
   */
  Map<Integer, List<ResultadoBusquedaAvisosExpDto>> getAvisosExpPaginated(BusquedaAvisosExpDto busquedaAvisosExpDto,
      Pageable pageable, Boolean isAdmin) throws SinacException;

  /**
   * Obtiene los ultimos avisos por idUsuario (asignado), id procedimiento y si es
   * o no admin
   *
   * @param Integer idUsuario, Short idPro, Boolean isAdmin
   * @return List<ExpedienteAvisoDto> lista de expedienteAviso
   * @throws SinacException Si se produce un error al obtener la lista
   */
  List<ExpedienteAvisoDto> getUltimosAvisosAsignados(Integer idUsuario, Short idPro, Boolean isAdmin)
      throws SinacException;

  void consultarPadron(ExpedienteDto expedienteDto);

  PerPadronDto getPerPadronByIdPer(BigInteger idPersona) throws SinacException;

//FUTURE: Método "gesdocObtenerExpedienteEni" a usar cuando se tenga disponible en el conector de milano
//  DescargaDeDocumentoDto getExpedienteENI(BigInteger idExp) throws SinacException;

  void validarDocumentosEntradaByIdExp(BigInteger idExp);

  /**
   * Obtiene ExpedienteDto con los campos requeridos por indice electronico
   *
   * @param idExpediente Identificador del expediente
   * @return ExpedienteDto
   * @throws SinacException Si se produce un error al completar el expediente
   */
  ExpedienteDto getExpedienteInteresadoByIdExpediente(BigInteger idExpediente) throws SinacException;

  void saveDatosBoe(ExpedienteBoeDto expedienteBoeDto) throws SinacException;

  List<String> getIdsEnvioJobBoe() throws SinacException;

  ExpedienteBoeDto getExpedienteBoeByIdEnvio(String idEnvio) throws SinacException;

  List<BoeAnunciosDto> getBoeAnunciosByIdExpBoe(BigInteger idExpBoe) throws SinacException;

  PersonaDto getInteresadoByIdExp(BigInteger idExpediente) throws SinacException;

  void updateInactivaDocSalida(BigInteger expediente, String codLdvMae);

  Boolean isEstadoRetroaccion(BigInteger expediente);

  ExpedienteFormularioValDto getExpedienteFormularioCampo(BigInteger expediente, String codForm);

  List<EstadoDto> getEstadosByidProcedimientoidTramiteFaseCodAccion(Short idPro, String codAccion)
      throws SinacException;

  short getProcedimientoOrigenExp(BigInteger idExp) throws SinacException;

  void updateBorrarResolucionExp(BigInteger expediente);

  BigInteger saveDocumentoExpedienteDgp(ExpedienteDocumentoDto expedienteDocumento, ExpedienteDto expedienteDto,
      BigInteger idExpInf);

  void saveDocumentoInterno(ExpedienteDocumentoDto expedienteDocumentoDto, ExpedienteDto expedienteDto,
      BigInteger idExpInf) throws SinacException;

  List<PaisesDto> getPaisesPrefijo() throws SinacException;

  List<ExpedienteInformeDto> getExpedienteInformesByIdExp(BigInteger idExp) throws SinacException;

  ExpedienteDto getExpedienteByIdExpedienteInforme(BigInteger idExpedienteInforme) throws SinacException;

  void informeSolicitadoMde(BigInteger idExp, String tipoInforme, Date fechaSolicitud, BigInteger idExpInf,
      String codLdvEjercito) throws SinacException;

  /**
   * Obtiene los Informes del Expediente categorizados por Código de Tipo de
   * Informe.
   *
   * @param idExpediente Identificador del Expediente.
   * @return Informes del Expediente categorizados por Código de Tipo de Informe.
   * @throws SinacException Si se produce un error al obtener los Informes del
   *                        Expediente categorizados por Código de Tipo de
   *                        Informe.
   */
  Map<String, ExpedienteInformeDto> getInformesByIdExpediente(BigInteger idExpediente) throws SinacException;

  /**
   * Actualiza en la Tabla "EXP_INFORMES" el Estado del Informe.
   *
   * @param idInforme     Identificador del Informe a actualizar el Estado.
   * @param ldvMaestraDto Nuevo Estado a actualizar.
   * @throws SinacException Si se produce un error al actualizar en la Tabla
   *                        "EXP_INFORMES" el Estado del Informe.
   */
  void updateEstadoInforme(BigInteger idInforme, LdvMaestraDto ldvMaestraDto) throws SinacException;

  void updateInformeDgpDocumento(BigInteger idExpInf, BigInteger idExpedienteDocumento) throws SinacException;

  /**
   * Actualiza el expediente añadiendo el sentido de la resolucion a partir de
   * ldvMaestraEntity.
   *
   * @param ldvMaestraEntity
   * @param idExp
   * @throws SinacException
   */
  void updateExpedienteSentResolByIdDoc(LdvMaestraEntity ldvMaestraEntity, BigInteger idExp) throws SinacException;

  ProcedimientosFasesTramitesOperacionesAccionesDto getPftoaResponderMdeByIdExp(BigInteger idExp);

  void informeRecibido(BigInteger idExp, ExpedienteInformeDto expedienteInformeDto) throws SinacException;

  ExpedienteInformeDto getExpedienteInformesByIdExpCodTipoInformeActivo(BigInteger idExp, String codTipoInformeLdv);

  ExpedienteInformeMdeDto getExpedienteInformeMdeByIdExpedienteInforme(BigInteger idExpedienteInforme);

  InsideEstadoDto enviarExpedienteInside(InsideEstadoDto insideEstadoDto,
      ProcedimientosFasesTramitesOperacionesDto pfto, UsuarioDto usuario);

  void remitiarAJusticia(InsideEstadoDto insideEstadoDto, UsuarioDto usuario) throws SinacException;

  void consultarEstadoRemisionAJusticia(InsideEstadoDto insideEstadoDto, UsuarioDto usuario) throws SinacException;

  void saveInsideConfig(BigInteger idExp, List<ExpedienteInsideDto> expedienteInsideDtos);

  void saveAltaFiliaciones(RespuestaAltaFiliacionDto respuestaAltaFiliacionDto, PersonaDto personaDto,
      ExpedienteDto expedienteDto);

  void savePermitirAltaFiliaciones(PersonaDto personaDto, ExpedienteDto expedienteDto);

  List<PerFiliacionesDto> getPerFiliacionesByIdPer(BigInteger idPersona);

  List<PerFilNiesDto> getPerFilNiesByIdPer(BigInteger idPersona);

  void desactivarFiliacionesByIdPersona(BigInteger idPersona);

  void desactivarPerFilNiesByIdPersonaMenosNie(BigInteger idPersona, String nie);

  void peticionConsultaNieFiliacion(String nie, PersonaDto personaDto);

  void peticionConsultaReferenciaFiliacion(String referencia, PersonaDto personaDto, ExpedienteDto expedienteDto);

  void saveCopyDatosFiliacionEnPersona(PersonaDto personaDto, PerFiliacionesDto perFiliacionesDto);

  /**
   * Devuelve una lista de validaciones, necesarias para el semaforo de
   * validaciones, a partir de idExp.
   * 
   * @param idExp
   * @return
   * @throws SinacException
   */
  List<ValidacionSemaforoDto> getListaValidacionesByIdExp(BigInteger idExp, List<String> listCodValLdvEntMae)
      throws SinacException;

  /**
   * Realiza la inserción de los estados por defecto de las validaciones.
   * 
   * @param expedienteDto
   * @param interesadoDto
   * @throws SinacException
   */
  void saveExpedienteValSemaforo(ExpedienteDto expedienteDto, PersonaDto interesadoDto) throws SinacException;

  /**
   * Realiza un update del estado de las validaciones a partir del idExp y el tipo
   * de la validacion(codLdvEntMae) cambiandolo por el código del nuevo estado que
   * va a tener la validacion(codValSem)
   * 
   * @param idExp
   * @param codLdvEntMae
   * @param codValSem
   * @throws SinacException
   */
  void updateValidacionSemaforo(BigInteger idExp, String codLdvEntMae, String codValSem) throws SinacException;

  /**
   * Recalcula las validaciones del apartado de Cumplimiento de Integración para
   * el expediente indicado.
   * 
   * @param idExp
   * @param codValLvdEntMae
   */
  void recalcularValidadionesIntegracion(BigInteger idExp, List<String> listCodValLdvEntMae);

  /**
   * Recalcula las validaciones del apartado de Conducta Cívica para el expediente
   * indicado.
   * 
   * @param idExp
   * @param codValLvdEntMae
   */
  void recalcularValidadionesConducta(BigInteger idExp, List<String> listCodValLdvEntMae);

  /**
   * Recalcula las validaciones tanto de Conducta Cívica como de Cumplimiento de
   * integracion para el expediente indicado.
   * 
   * @param idExp
   * @param codValLvdEntMae
   */
  void recalcularValidadionesSemaforo(BigInteger idExp, List<String> listaValidacionesInt,
      List<String> listaValidacionesCon);

  List<ExpedienteDto> listaExpedientesPorEstado(List<String> listaEstados);

  List<ExpedienteDto> listaExpedientesDocPendienteValidar(List<String> listaEstadosIn, List<String> listaEstadosNotIn);

  List<ExpedienteDto> listaExpedientesDgpRechazo(List<String> listaEstadosInforme);

  ExpedienteDto getExpedienteByCodExpediente(String codExpediente, String numeroIdentificacion) throws SinacException;

  List<ExpedienteDto> getListaExpDocumentosNotificar(List<String> listaEstadoDoc, List<String> listaParam, short idPro,
      Date fechaFirmaFormateada);

  /**
   * Recoge la lista de documentos entity que tengan código gestor documental nulo
   * y lo convierte en lista de dtos
   * 
   */
  List<ExpedienteDocumentoDto> getListaExpedienteDocumentosCodGDNull();

  List<ParametrizacionDto> getParametrizacionByNombre(String string);

  ParametrizacionDto getParametrizacionByNombreAndProcedimiento(String nomParam, String codPro);

  /**
   * Devuelve una lista de expedientes con todos los informes que tengan el estado
   * que se le pasa.
   * 
   * @param codEstInforme
   * @return
   */
  List<ExpedienteInformeDto> getListaExpedientesInformesByCodEstInforme(String codEstInforme) throws SinacException;

  List<ExpedienteDto> getListaExpedienteByFase(String codFase);

  /**
   * Recoge la lista de expedientes entity que tengan código gestor documental
   * nulo y lo convierte en lista de dtos
   * 
   */
  List<ExpedienteDto> getListaExpedientesCodGDNull();

  List<ExpedienteDto> getListaExpedientesIncompletos(List<String> estados);

  void reintentoDocumentosExpediente(List<ExpedienteDocumentoDto> listaDocsExp, SolicitudDto solicitudDto,
      ExpedienteDto expedienteDto);

  List<PersonaDto> getExpedienteAcumular(String numAcreditacion, List<String> listaEstados, String codPro);

  ExpedienteDto guardarEntidadesExpedientes(Map<String, Object> valores, BigInteger idExpOri, SolicitudDto solicitudDto,
      PersonaDto interesadoDto, List solicitudesPersonasDtoList, String idenExpGD, String codExp);

  void comprobarSolicitudPenCompletada(BigInteger idExpInf);

  void actualizarExpedienteInformesMjuFicherosDatos(String nombreArchivo);

  List<ExpedienteDto> getExpedienteAcumularPorIdPer(BigInteger idPer, String codPro, List<String> listaEstados);

  List<ExpedienteDto> getListaExpedientesResolver(String codPro, List<String> listaEstadosExp);

  Integer calcularTiempoResidenciaExigido(Integer motivoSolicitud, Integer nacionalidad, Integer segundaNacionalidad,
      Integer paisNacimiento);

  ExpedienteDto getExpedientesByIdPerInteresado(BigInteger idPer, List<String> listaEstados);

  List<ExpedienteDto> getListaExpedientesPropuesta(List<String> listaEstados, String valor,
      List<String> listaNombresNotIn);

  List<ExpedienteDto> getListaExpedientesConsultaTitulaciones(Date fechaComunicacion, String codPro,
      List<String> listaEstadosNotIn, List<String> listaCodigosEstados);

  List<PerCertificacionesDto> getPerCertificacionesByIdPerCodigosEstados(BigInteger idPer,
      List<String> listaCodigosEstados);

  List<ProcedimientosPlantillasCriteriosDto> getListaProcedimientosPlantillasCriteriosByIdPro(Short idPro);

}
