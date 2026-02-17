package es.mjusticia.sinac.core.business.facade;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.xml.sax.SAXException;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.ArchivoFtpDto;
import es.mjusticia.sinac.core.model.dto.AsientoDto;
import es.mjusticia.sinac.core.model.dto.BoeAnunciosDto;
import es.mjusticia.sinac.core.model.dto.BusquedaAvisosExpDto;
import es.mjusticia.sinac.core.model.dto.BusquedaExpedientesDto;
import es.mjusticia.sinac.core.model.dto.DatosSolicitudInformeMjuDto;
import es.mjusticia.sinac.core.model.dto.DatosTramiteDto;
import es.mjusticia.sinac.core.model.dto.DescargaDeDocumentoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToRequerirDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToSaveDto;
import es.mjusticia.sinac.core.model.dto.DocumentosEntradaDto;
import es.mjusticia.sinac.core.model.dto.DocumentosTramiteDto;
import es.mjusticia.sinac.core.model.dto.EnviarEmailDto;
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
import es.mjusticia.sinac.core.model.dto.ExpedienteRequerimientoDto;
import es.mjusticia.sinac.core.model.dto.ExpedientesPlazosDto;
import es.mjusticia.sinac.core.model.dto.InformesDgpRecibidosDto;
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
import es.mjusticia.sinac.core.model.dto.PlazoDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesAccionesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesDto;
import es.mjusticia.sinac.core.model.dto.ProvinciasDto;
import es.mjusticia.sinac.core.model.dto.RegistroDto;
import es.mjusticia.sinac.core.model.dto.RenovacionDniDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaAvisosExpDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaExpedientesDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDocumentoDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDto;
import es.mjusticia.sinac.core.model.dto.TipoOficioDto;
import es.mjusticia.sinac.core.model.dto.TiposViaDto;
import es.mjusticia.sinac.core.model.dto.TitulosDto;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;
import es.mjusticia.sinac.core.model.dto.ValidacionSemaforoDto;
import es.mjusticia.sinac.core.model.entity.LdvMaestraEntity;
import es.mjusticia.sinac.core.model.enums.TipoRespuestaEnviarDocumentoPortafirmasEnum;
import es.mjusticia.sinac.dgp.dto.TitularDto;
import es.mjusticia.sinac.filiaciones.dto.RespuestaAltaFiliacionDto;
import es.mjusticia.sinac.geiser.model.dto.DocumentoDto;
import jakarta.activation.DataSource;
import jakarta.validation.Valid;

/**
 * Fachada de Negocio para la Interfaz del Servicio de Expediente.
 *
 * @author NTT Data.
 */
public interface ExpedientesFacade {

  /**
   * Guarda Documentos en un Expediente.
   *
   * @param idExpediente           Identificador del Expediente.
   * @param documentoToSaveDtoList Lista de Documentos a guardar en el Expediente.
   * @return Lista de Documentos guardados y los Errores surgidos para aquellos
   *         Documentos que no hayan podido ser guardados.
   * @throws SinacException Si se produce un error al guardar los Documentos en el
   *                        Expediente.
   */
  List<DocumentoToSaveDto> saveDocumentosEntradaExpediente(final BigInteger idExpediente,
      LinkedList<DocumentoToSaveDto> documentoToSaveDtoList) throws SinacException;

  public DocumentoToSaveDto reintentoSubidaGestorDocumental(final BigInteger idExpediente, final BigInteger idExpDoc)
      throws SinacException;

  List<ResultadoBusquedaExpedientesDto> getExpedientesAsignadosPublicacionBoe(BigInteger idExp) throws SinacException;

  /**
   * asigna un usuario al expediente
   *
   * @param idExp Identificador del expediente.
   * @return int con resultado de la operación.
   * @throws SinacException Si se produce un error al obtener el Documento de
   *                        Expediente.
   */
  void setUsuarioToExpediente(BigInteger idExp) throws SinacException;

  /**
   * desasigna un usuario al expediente
   *
   * @param idExp Identificador del expediente.
   * @return int con resultado de la operación.
   * @throws SinacException Si se produce un error al obtener el Documento de
   *                        Expediente.
   */
  void unsetUsuarioToExpediente(BigInteger idExp, Integer idUsu) throws SinacException;

  ExpedienteDto getDetalleExpediente(BigInteger idExp) throws SinacException;

  void updateDetalleExpediente(ExpedienteDto detalleExpedienteDto, String segmentoActualizar) throws SinacException;

  void anadirObservacion(ExpedienteDto expedienteDto, String mensaje, String titulo) throws SinacException;

  List<PlantillaDto> getPlantillas();

  /**
   * Obtiene el Documento de Expediente asociado al Identificador de Documento
   * establecido como parámetro.
   *
   * @param idDocumento Identificador del Documento.
   * @return DTO con la Información del Documento de Expediente.
   * @throws SinacException Si se produce un error al obtener el Documento de
   *                        Expediente.
   */
  ExpedienteDocumentoDto getExpedienteDocumentoByIdDocumento(BigInteger bigInteger) throws SinacException;

  PlantillaDto getPlantillaById(short idPlantilla) throws SinacException;

  List<String> validateErroresPlantillas(PlantillaDto plantillaDto, ExpedienteDto expedienteDto) throws SinacException;

  String getUrlDocumentoByIdDocumento(BigInteger idDocumento) throws SinacException;

  /*
   * descarga un documento
   *
   * @param idDocExp Identificador del documento de Expediente
   *
   * @return DescargaDeDocumentoDto Objeto con los datos
   *
   * @throws SinacException Si se produce un error al convertir archivo
   */
  DescargaDeDocumentoDto getArchivoByIdDocExp(BigInteger idDocExp) throws SinacException;

  /*
   * descarga un documento con la firma de copia autentica
   *
   * @param idDocExp Identificador del documento de Expediente
   *
   * @return DescargaDeDocumentoDto Objeto con los datos
   *
   * @throws SinacException Si se produce un error al convertir archivo
   */
  DescargaDeDocumentoDto descargarDocumentoCopiaAutentica(BigInteger idDocExp) throws SinacException;

  /*
   * obtiene un documento y lo convierte a pdf este metodo junto sera borrado de
   * aqui ya que nuna se le llamara desde una ventana
   *
   * @param idDocExp Identificador del documento de Expediente
   *
   * @return boolean
   *
   * @throws SinacException Si se produce un error al convertir archivo
   */
  public boolean convertirDocumentoEditableEnPdf(ExpedienteDocumentoDto expDoc) throws SinacException;

  /*
   * obtiene la lista de docstipos
   */
  public List<DocumentoTipoDto> getComboDocumentoTipo();

  List<LdvMaestraDto> getComboLdvMaestraByCodLdvEntidadMaestra(String codLdvEntMae) throws SinacException;

  List<PaisesDto> getPaises() throws SinacException;

  List<ProvinciasDto> getProvincias() throws SinacException;

  List<LocalidadesDto> getLocalidades() throws SinacException;

  List<LdvMaestraDto> getTiposIdentificacionDetalleExp() throws SinacException;

  List<LdvMaestraDto> getSexoDetalleExp() throws SinacException;

  List<LdvMaestraDto> getEstCivilDetalleExp() throws SinacException;

  /**
   * cambia el estado del documento a validado o rechazadp establecido como
   * parámetro.
   *
   * @param idDocExp Identificador del Documento, operacion validar/rechazar.
   * @return int con el resultado de la operación
   * @throws SinacException Si se produce un error al cambiar el valor
   */
  void validarRechazarDoc(BigInteger idDocExp, Integer operacion) throws SinacException;

  /**
   * cambia la ldvmaestra a favorable/desfavorable.
   *
   * @param idExp     Identificador del expediente.
   * @param idInforme Identificador del expedienteInforme.
   * @param operacion Identificador del operación a realizar.
   * @return int con el resultado de la operación
   * @throws SinacException               Si se produce un error al cambiar el
   *                                      valor
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   * @throws SQLException
   */

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

  List<LdvMaestraDto> getComboTipoRc() throws SinacException;

  /**
   * cambia la ldvmaestra a solicitado
   *
   * @param idExp       Identificador del expediente.
   * @param tipoInforme tipo de informe.
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
   * @throws SinacException Si se produce un error al cambiar el valor
   */
  void informeRecibido(BigInteger idExp, BigInteger idExpInforme, ExpedienteInformeDgpDto expedienteInformeDgpDto,
      Date fechaEmision, Date fechaRecepcion, String sentido, BigInteger idExpedienteDocumento) throws SinacException;

  ExpedienteNotificacionesDto sincronizarEnvio(String estado, String identificador, boolean acusePDF,
      byte[] contenidoPDF, String hashPDF, String accion, String idNotifica, BigInteger modoNot, Date fechaCambioEstado)
      throws SinacException;

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
   * Envío de un Documento PDF a Portafirmas.
   *
   * @param idDocumentoExpediente Identificador del Documento.
   * @param idProFaseTraOpe       Identificador ProFaseTraOpe.
   * @return Enumerado que determina el Tipo de Respuesta del envío de un
   *         Documento PDF a Portafirmas.
   * @throws SinacException Si se produce un error al enviar el Documento PDF a
   *                        Portafirmas.
   */
  TipoRespuestaEnviarDocumentoPortafirmasEnum sendDocumentoToPortafirmas(final BigInteger idDocumentoExpediente,
      final long idProFaseTraOpe) throws SinacException;

  /**
   * Metodo que permite recuperar los documentos necesarios para el Acuerdo de
   * Consejo de Ministros
   *
   * @param idExp
   * @return
   * @throws SinacException
   */
  List<DocumentosTramiteDto> getDocumentosConsejoMinistros(BigInteger idExp) throws SinacException;

  /**
   * Metodo que se encarga de introducir los datos por defecto en la pantalla de
   * Enviar Email del acuerdo del consejo de minitros
   *
   * @param enviarEmailDto
   * @param interesado
   * @return
   * @throws SinacException
   */
  EnviarEmailDto setCamposPredefEmailAcuerdoConMin(EnviarEmailDto enviarEmailDto, String interesado,
      ExpedienteDto expediente) throws SinacException;

  /**
   * Ejecuta una accion del expediente
   *
   * @param idProFasTraOpeAcc
   * @param valores
   * @throws SinacException
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   * @throws SQLException
   */
  DatosTramiteDto ejecutarAccion(long idProFasTraOpeAcc, Map<String, Object> valores)
      throws SinacException, SQLException, ParserConfigurationException, SAXException, IOException;

  /**
   * Recupera el resumen de datos de un expediente
   *
   * @param idExpediente
   * @return
   * @throws SinacException
   */
  Map<String, Object> getResumenExpediente(BigInteger idExpediente) throws SinacException;

  /**
   * Verifica que un expediente tiene habilitado un tramite
   *
   *
   * @param idExpediente<
   * @param codTramite
   * @throws SinacException
   */
  void verificarExpedienteTramite(BigInteger idExpediente, String codTramite) throws SinacException;

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

  PlantillaDto getPlantillaPorCod(String codPlantilla);

  List<ExpedienteEstadoDto> getExpedienteEstadoByIdExp(BigInteger idExp) throws SinacException;

  String getPlazoArchivoElectronico(BigInteger idExp) throws SinacException;

  ExpedienteDto getExpedienteById(BigInteger idExp) throws SinacException;

  void saveDatosResolucion(BigInteger idExpediente, Date fechaCertificacion, Date fechaPublicacionBoe,
      Date fechaRecepcionAcuerdo, Integer resultadoAcuerdo, LdvMaestraEntity ldvMaestraEntity, String estadoRetroaccion)
      throws SinacException;

  ExpedienteDocumentoDto saveDocumentoPlantilla(PlantillaDto plantillaDto, ExpedienteDto expedienteDto)
      throws SinacException;

  LinkedList<DocumentoToSaveDto> transformMultipartToDocumentoToSaveExp(DocumentosEntradaDto documentosEntrada);

  /**
   * Devuelve una documentoTipoDto mediante el codigo de tipo de doc
   *
   * @param cod_tipo
   * @throws SinacException
   */
  DocumentoTipoDto getExpedienteDocumentoByTipoDocCod(String cod_tipo) throws SinacException;

  List<BigInteger> getIdsExpedienteInformesByCodEstadoCodTipoInforme(String codEstado, String codTipoInforme)
      throws SinacException;

  TitularDto getDatosSolicitudInformeDgp(BigInteger idExpInforme) throws SinacException;

  /**
   * Obtiene un mapa de valores para ejecutar las acciones de los informes
   *
   * @param codExpediente Código de expediente
   * @param tipoInforme   Tipo de informe
   * @return
   * @throws SinacException
   */
  Map<String, Object> getIdExpCodProceByCodExpediente(String codExpediente, String tipoInforme) throws SinacException;

  void cambiarEstadoInformesAsolicitado(String nombreArchivo, String codigoEstado, String tipoInforme, BigInteger idExp)
      throws SinacException;

  void guardaExpedienteInformesMjuFicheros(String nombreArchivo, String codigoEstado) throws SinacException;

  DatosSolicitudInformeMjuDto obtenerDatosSolicitudInformeMju(BigInteger idExpInf) throws SinacException;

  boolean copyArchivoFtpNFS(String nombreArchivo, byte[] contenido, String ruta) throws SinacException;

  boolean borrarArchivoFtpNFS(String nombreArchivo, String ruta) throws SinacException;

  boolean existeExpedienteInformesMjuFichero(String nombreArchivo) throws SinacException;

  void guardaRespuestaInformeMjuPenados(ArchivoFtpDto archivoFtpDto) throws SinacException;

  void actualizarEstadoArchivoFtp(String nombreArchivo, String codigoEstado) throws SinacException;

  List<BigInteger> getIdsExpedienteByCodEstadoCodTipoInforme(String codEstado, String codTipoInforme, Integer maxItem)
      throws SinacException;

  Long getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(String codPro, String codTramite,
      String codOpe, String codAccion) throws SinacException;

  Long getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(long idProFasTraOpe, String codAcc)
      throws SinacException;

  DocumentoToSaveDto getDocumentoToSaveDtoMju(ArchivoFtpDto archivoFtpDto) throws SinacException, IOException;

  /**
   * Actualiza el informe de la dgp cuando el webservice devuelve error
   *
   * @param idExp                   id del expediente
   * @param codigoEstado            Código de estado
   * @param codigoEstadoSec         Código de estado secundario
   * @param literalError            SMS del error
   * @param codigoPeticionRespuesta Código de la petición
   * @param alta                    true si el error es del alta del informe
   * @throws SinacException
   */
  boolean actualizaInformeDgpRechazado(BigInteger idExp, String codigoEstado, String codigoEstadoSec,
      String literalError, String codigoPeticionRespuesta, boolean alta) throws SinacException;

  void desactivarInformesActivosError(BigInteger idExp, String tipoInforme) throws SinacException;

  void informeSolicitadoDgp(BigInteger idExp, String tipoInforme, Date date, String codigoPeticionRespuesta,
      BigInteger idExpInforme) throws SinacException;

  void saveDatosInformeDgp(ExpedienteInformeDgpDto expedienteInformeDgpDto,
      List<ExpedienteInformeDgpTramiteDto> expedienteInformeDgpTramites, List<RenovacionDniDto> renovacionesDniDto)
      throws SinacException;

  ExpedienteInformeDto getExpedienteInformeById(BigInteger id) throws SinacException;

  Page<ResultadoBusquedaExpedientesDto> getExpedientesPaginated(BusquedaExpedientesDto busquedaDto,
      org.springframework.data.domain.Pageable pageable) throws SinacException;

  List<ResultadoBusquedaExpedientesDto> getExpedientesFiltrados(BusquedaExpedientesDto busquedaDto,
      org.springframework.data.domain.Pageable pageable);

  ProcedimientosFasesTramitesOperacionesAccionesDto getPftoaResponderCniByidExp(BigInteger idExp) throws SinacException;

  PersonaDto getPersonaByIdPer(BigInteger idPer) throws SinacException;

  boolean checkUsarioAsignadoExpediente(BigInteger idExp, Integer idUsuario) throws SinacException;

  ProcedimientosFasesTramitesOperacionesDto getPftobyCod(BigInteger idExpediente, String codFase, String codTramite,
      String codOpe);

  /**
   * Recupera la lista de plantillas de una acción de generar concreta. Esta lista
   * vendrá filtrada por la clasificación del expediente
   *
   * @param idExp      id del expediente
   * @param codTramite Código de trámite
   * @param codOpe     Código de operación
   * @param codAccion  Código de acción
   * @throws SinacException
   */
  List<PlantillaDto> getListaPlantillas(BigInteger idExp, String codTramite, String codOpe, String codAccion)
      throws SinacException;

  /**
   * Recupera la lista de títulos y peticiones PID de una persona
   *
   * @param idPer id de la persona
   * @throws SinacException
   */
  TitulosDto obtenerTitulosEducacion(BigInteger idPersona) throws SinacException;

  /**
   * Consulta la lista de títulos al servicio web PID de una persona Guarda los
   * datos de la peticion y la información recibida de centros y títulos
   *
   * @param expedienteDto expediente
   * @return
   * @throws SinacException
   */
  TitulosDto consultarTitulosEducacion(ExpedienteDto expedienteDto) throws SinacException;

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
   * Recupera todos los archivos de uan carpeta nfs
   *
   * @param nfsPath carpeta nfs
   * @return listado de archivos tipo Datasource
   */
  List<DataSource> obtenerTodosLosArchivos(String nfsPath) throws SinacException;

  /**
   *
   * Desactiva el representante del expediente
   *
   * @param idExp
   * @param idPersona
   * @return el expediente actualizado
   */
  ExpedienteDto desactivarRepresentante(BigInteger idExp, BigInteger idPersona) throws SinacException;

  /**
   * Método que recupera las acciones agrupadas por operación para cargar los
   * botones y el stepper. Se filtrará por condición de ejecución para que las
   * acciones que no se pueden ejecutar no se visibilicen
   *
   * @param idExp
   * @return mapa con las accionesOperaciones que están disponibles en este
   *         momento en la pantalla que se desea cargar
   */
  List<Map<String, Object>> getAccionesOperacionesPorExpedienteUsuario(BigInteger idExp) throws SinacException;

  /**
   * Obtiene los Tipos de Oficios y sus Documentos a requerir para Requerimientos
   * de Subsanación y Trámite de Audiencias.
   *
   * @param idProcedimiento Identificador del Procedimiento.
   * @param idExpediente    Identificador del Expediente.
   * @param codTramite      Código del Trámite.
   * @param codAccion       Código de la Acción.
   * @return Tipos de Oficios y sus Documentos a requerir para Requerimientos de
   *         Subsanación y Trámite de Audiencias.
   * @throws SinacException Si se produce un error al obtener los Tipos de Oficios
   *                        y sus Documentos a requerir para Requerimientos de
   *                        Subsanación y Trámite de Audiencias.
   */
  Map<String, List<TipoOficioDto>> getTiposOficiosAndDocumentosToRequerirForRequerimientosAndAudiencias(
      BigInteger idExpediente, short idProcedimiento, String codTramite, String codAccion) throws SinacException;

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

  void acumularExpediente(ExpedienteDto expediente) throws SinacException;

  Page<PersonaDto> getPersonasRastreo(String identificador, String nombre, String apellido1, String apellido2,
      Date fechaNacimiento, Pageable pageable, String tipoOrdenacion, String columnaOrdenar) throws SinacException;

  void relacionarExpedientes(ExpedienteDto expediente) throws SinacException;

  BigInteger getIdExpedienteByCodExpediente(String parameter);

  void consultarCertificaciones(ExpedienteDto expedienteDto);

  List<PerCertificacionesDto> getPerCertificacionesByIdPerTipoCertificacion(BigInteger idPersona,
      String tipoCertificacion) throws SinacException;

  List<PerCertificacionesDto> getPerCertificacionesByIdPer(BigInteger idPersona) throws SinacException;

  /**
   * Obtiene los Avisos asociados al Identificador de Expediente, procedimiento y
   * usuario establecido como parámetro, y filtrando si es administrador o no
   *
   * @param idExpediente Identificador del Expediente.
   * @param proDto       Procedimiento.
   * @param idUsuario    Usuario.
   * @param isAdmin      Boolean si es administrador o no
   * @return String Avisos
   * @throws SinacException Si se produce un error al obtener los Avisos asociados
   *                        al Expediente.
   */
  List<String> getAvisosExpediente(BigInteger idExp, ProcedimientoDto proDto, Integer idUsuario, Boolean isAdmin)
      throws SinacException;

  /**
   * Obtiene los Avisos asociados al Identificador de Expediente y se filtra si es
   * administrador o no
   *
   * @param idExpediente Identificador del Expediente.
   * @param isAdmin      Boolean si es administrador o no
   * @return Lista de expedienteAviso
   * @throws SinacException Si se produce un error al obtener los Avisos asociados
   *                        al Expediente.
   */
  List<ExpedienteAvisoDto> getAvisosExpedienteByIdExp(BigInteger idExp, Boolean isAdmin) throws SinacException;

  /**
   * Se habilita/deshabilita el aviso asociado a un expediente
   *
   * @param idExpAvisos Identificador del expedienteAviso.
   * @throws SinacException Si se produce un error al guardar el cambio
   */
  void cambiarEstadoAvisoExp(BigInteger idExpAvisos) throws SinacException;

  /**
   * Obtiene los ultimos expedienteAviso asociados a un usuario y procedimiento, y
   * se filtra por rol administrador o no
   *
   * @param idUsuario Identificador del Usuario.
   * @param idPro     Identificador del Procedimiento.
   * @param Boolean   rol administrador
   * 
   * @return Lista de expedienteAviso
   * @throws SinacException Si se produce un error al obtener los Avisos asociados
   *                        al Procedimiento Usuario.
   */
  List<ExpedienteAvisoDto> getUltimosAvisosByUserId(Integer idUsuario, Short idPro, Boolean isAdmin)
      throws SinacException;

  /**
   * Obtiene los ajustes de procedimientos avisos
   * 
   * @return Map<List<String>, LdvMaestraDto>
   * @throws SinacException Si se produce un error al obtener el Map.
   */
  Map<List<String>, LdvMaestraDto> getAvisosUnicosLdvMaestra() throws SinacException;

  /**
   * Metodo que actualiza flgHabilitado de un procedimientoAviso
   * 
   * @throws SinacException Si se produce un error al actualizar el flgHabilitado
   */
  void actualizarHabilitadoProAvi(Long idProAvisos, Boolean habilitado) throws SinacException;

  /**
   * Metodo que obtiene la busqueda de avisos expediente en tipo Page
   * 
   * @throws SinacException Si se produce un error al obtener el Page
   */
  Page<ResultadoBusquedaAvisosExpDto> getAvisosExpPaginated(BusquedaAvisosExpDto busquedaDto,
      org.springframework.data.domain.Pageable pageable, Boolean isAdmin) throws SinacException;

  void consultarPadron(ExpedienteDto expedienteDto) throws SinacException;

  PerPadronDto getPerPadronByIdPer(BigInteger idPersona) throws SinacException;

  void updateEstadoDocumento(BigInteger idExpDoc, LdvMaestraDto ldvMaestra) throws SinacException;

  void saveRegistroAux(RegistroDto registroDto) throws SinacException;

  /**
   * Obtiene el Histórico del Plazo del Expediente asociado al Identificador de
   * Expediente, al Identificador de Plazo y al Identificador de Requerimiento
   * establecidos como parámetros.
   *
   * @param idExpediente    Identificador del Expediente.
   * @param idPlazo         Identificador del Tipo de Plazo.
   * @param idRequerimiento Identificador del Requerimiento.
   * @return Histórico del Plazo del Expediente.
   * @throws SinacException Si se produce un error al obtener el Histórico del
   *                        Plazo del Expediente asociado al Identificador de
   *                        Expediente, al Identificador de Plazo y al
   *                        Identificador de Requerimiento establecidos como
   *                        parámetros.
   */
  List<ExpedientesPlazosDto> getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazoAndIdRequerimiento(
      BigInteger idExpediente, short idPlazo, BigInteger idRequerimiento) throws SinacException;

  /**
   * Crea el Plazo del Expediente.
   *
   * @param idExpediente    Identificador del Expediente.
   * @param idRequerimiento Identificador del Requerimiento.
   * @param plazoDto        Tipo de Plazo.
   * @throws SinacException Si se produce un error al crear el Plazo del
   *                        Expediente.
   */
  void crearPlazoExpediente(BigInteger idExpediente, BigInteger idRequerimiento, PlazoDto plazoDto)
      throws SinacException;

  /**
   * Suspende el Plazo del Expediente.
   *
   * @param idExpediente    Identificador del Expediente.
   * @param idPlazo         Identificador del Tipo de Plazo.
   * @param idRequerimiento Identificador del Requerimiento.
   * @throws SinacException Si se produce un error al suspender el Plazo del
   *                        Expediente.
   */
  void suspenderPlazoExpediente(BigInteger idExpediente, short idPlazo, BigInteger idRequerimiento)
      throws SinacException;

  /**
   * Reanuda el Plazo del Expediente. El Plazo de Resolución del Expediente sólo
   * se puede reanudar si no hay otros Plazos en curso.
   *
   * @param idExpediente    Identificador del Expediente.
   * @param idPlazo         Identificador del Tipo de Plazo.
   * @param idRequerimiento Identificador del Requerimiento.
   * @param isManual        Flag que determina el Tipo de Reanudación (true:
   *                        Manual, false: Automática).
   * @throws SinacException Si se produce un error al reanudar el Plazo del
   *                        Expediente.
   */
  void reanudarPlazoExpediente(BigInteger idExpediente, short idPlazo, BigInteger idRequerimiento, boolean isManual)
      throws SinacException;

  /**
   * Reanuda el Plazo del Expediente. El Plazo de Resolución del Expediente sólo
   * se puede reanudar si no hay otros Plazos en curso.
   *
   * @param expedientesPlazosDto Plazo del Expediente.
   * @param isManual             Flag que determina el Tipo de Reanudación (true:
   *                             Manual, false: Automática).
   * @throws SinacException Si se produce un error al reanudar el Plazo del
   *                        Expediente.
   */
  void reanudarPlazoExpediente(ExpedientesPlazosDto expedientesPlazosDto, boolean isManual) throws SinacException;

  /**
   * Finaliza el Plazo del Expediente.
   *
   * @param idExpediente    Identificador del Expediente.
   * @param idPlazo         Identificador del Tipo de Plazo.
   * @param idRequerimiento Identificador del Requerimiento.
   * @throws SinacException Si se produce un error al finalizar el Plazo del
   *                        Expediente.
   */
  void finalizarPlazoExpediente(BigInteger idExpediente, short idPlazo, BigInteger idRequerimiento)
      throws SinacException;

  /**
   * Vence el Plazo del Expediente.
   *
   * @param expedientesPlazosDto Plazo del Expediente.
   * @throws SinacException Si se produce un error al vencer el Plazo del
   *                        Expediente.
   */
  void vencerPlazoExpediente(ExpedientesPlazosDto expedientesPlazosDto) throws SinacException;

  /**
   * Metodo para recuperar la notificación.
   *
   * @param idSolSun Identificador del Sun.
   * @throws SinacException Si se produce un error al finalizar el Plazo del
   *                        Expediente.
   */
  ExpedienteNotificacionesDto getExpedienteNotificacionesbyIdSolSun(String idSolSun) throws SinacException;

  AsientoDto enviarDocumentoAGeiser(BigInteger idExpDoc, String orgDestino, String asunto,
      ProcedimientosFasesTramitesOperacionesDto pfto, UsuarioDto usuario);

  DocumentoDto obtenerJustificanteGeiser(BigInteger idAsiento);

  /**
   * Obtiene el expediente ENI del expediente en cuestión
   *
   * @param idExp Identificador del Expediente.
   * 
   * @return Expediente ENI (documento pdf)
   * @throws SinacException Si se produce un error al obtener el expediente ENI
   */
//FUTURE: Método "gesdocObtenerExpedienteEni" a usar cuando se tenga disponible en el conector de milano
//  DescargaDeDocumentoDto getArchivoExpedienteENIByIdExp(BigInteger idExp) throws SinacException;

  /**
   * Obtiene el indice del expediente
   *
   * @param idExp Identificador del Expediente.
   * 
   * @return Indice del expediente (documento zip)
   * @throws SinacException Si se produce un error al obtener el expediente ENI
   */
  ExpedienteDocumentoDto generarIndiceElectronico(BigInteger idExp) throws SinacException;

  void saveDatosBoe(ExpedienteBoeDto expedienteBoeDto) throws SinacException;

  List<String> getIdsEnvioJobBoe() throws SinacException;

  ExpedienteBoeDto getExpedienteBoeByIdEnvio(String idEnvio) throws SinacException;

  List<BoeAnunciosDto> getBoeAnunciosByIdExpBoe(BigInteger idExpBoe) throws SinacException;

  void abrirExpedienteGd(BigInteger idExpediente) throws SinacException;

  /**
   * Metodo que devuelve una lista de documentos dependiendo del Tramite, la
   * Operacion y la Acción que se le pasa
   * 
   * @param idExp
   * @param codTra
   * @param codOpe
   * @param codAcc
   * @return
   * @throws SinacException
   */
  List<DocumentosTramiteDto> getDocumentosTramite(BigInteger idExp, String codTra, String codOpe, String codAcc)
      throws SinacException;

  DocumentoTipoDto getExpedienteDocumentoByTipoDocId(short idTipoDoc) throws SinacException;

  Boolean existeDocumentoExpediente(String tipoDoc, BigInteger idExp) throws SinacException;

  List<DocumentosTramiteDto> getDocumentosTramiteSinOpe(BigInteger idExp, String codTra, String codAcc)
      throws SinacException;

  PlantillaDto getPlantillaPorTipoDocAndPro(short idPro, String codTipo);

  BigInteger generarPlantillaDgp(BigInteger idExp, BigInteger idExpInf);

  BigInteger saveDocumentoExpedienteDgp(ExpedienteDocumentoDto expedienteDocumento, ExpedienteDto expedienteDto,
      BigInteger idExpInf);

  ExpedienteDocumentoDto saveDocPlantillaDgp(ExpedienteDto expedienteDto, ExpedienteDocumentoDto expDoc)
      throws SinacException;

  void getListaAccionesDisponiblesPorUsuario(BigInteger idExp, Integer idUsu, Long idProFasTraOpeAcc, short idPro)
      throws SinacException;

  void solicitarInformesDisponibles(BigInteger idExp, Map<String, Object> valores) throws SinacException;

  ExpedienteDto getExpedienteByIdExpedienteInforme(BigInteger idExpInf) throws SinacException;

  ExpedienteFormularioValDto getExpedienteFormularioCampo(BigInteger idExp, String codForm);

  void informeSolicitadoMde(BigInteger idExp, String tipoInforme, Date date, BigInteger idExpInforme,
      String codLdvEjercito) throws SinacException;

  /**
   * Obtiene los Plazos Vigentes Vencidos en el Estado especificado.
   *
   * @param estado Estado.
   * @return Plazos Vigentes Vencidos en el Estado especificado.
   * @throws SinacException Si se produce un error al obtener los Plazos Vigentes
   *                        Vencidos en el Estado especificado.
   */
  List<ExpedientesPlazosDto> getPlazosVigentesVencidosByEstado(String estado) throws SinacException;

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
   * Obtiene el Plazo de Resolución Vigente asociado al Identificador de
   * Expediente establecido como parámetro.
   *
   * @param idExpediente Identificador del Expediente.
   * @return DTO con la Información del Plazo de Resolución Vigente.
   * @throws SinacException Si se produce un error al obtener el Plazo de
   *                        Resolución Vigente asociado al Identificador de
   *                        Expediente establecido como parámetro.
   */
  ExpedientesPlazosDto getPlazoResolucionVigenteByIdExpediente(BigInteger idExpediente) throws SinacException;

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

  void reintentoGenerarDocDgp(BigInteger idExpediente, BigInteger idExpInf) throws SinacException;

  ProcedimientosFasesTramitesOperacionesAccionesDto getPftoaResponderMdeByIdExp(BigInteger idExp) throws SinacException;

  ExpedienteInformeDto getExpedienteInformesByIdExpCodTipoInformeActivo(BigInteger idExp, String codTipoInformeLdv);

  void informeRecibido(BigInteger idExp, ExpedienteInformeDto expedienteInformeDto);

  void saveDatosDefensa(Map<String, Object> valores);

  List<ExpedienteDocumentoDto> getExpedientesDocumentosMdeByIdInforme(BigInteger idInforme);

  ExpedienteInformeMdeDto getExpedienteInformeMdeByIdExpedienteInforme(BigInteger idExpInf);

  void saveInsideConfig(BigInteger idExp, List<ExpedienteInsideDto> expedienteInsideDtos);

  List<ExpedienteDto> listaExpedientesPorEstado(List<String> listaEstados);

  List<ExpedienteDto> listaExpedientesDocPendienteValidar(List<String> listaEstadosIn, List<String> listaEstadosNotIn);

  List<BigInteger> getIdsInteresadosAltaFiliaciones();

  void saveAltaFiliaciones(RespuestaAltaFiliacionDto respuestaAltaFiliacionDto, PersonaDto personaDto,
      ExpedienteDto expedienteDto);

  void savePermitirAltaFiliaciones(PersonaDto personaDto, ExpedienteDto expedienteDto);

  List<PerFiliacionesDto> getPerFiliacionesByIdPer(BigInteger idPer);

  List<PerFilNiesDto> getPerFilNiesByIdPer(BigInteger idPersona);

  void desactivarFiliacionesByIdPersona(BigInteger idPersona);

  void desactivarPerFilNiesByIdPersonaMenosNie(BigInteger idPersona, String nie);

  void peticionConsultaNieFiliacion(String nie, PersonaDto personaDto);

  void peticionConsultaReferenciaFiliacion(String referencia, PersonaDto personaDto, ExpedienteDto expedienteDto);

  void saveCopyDatosFiliacionEnPersona(PersonaDto personaDto, PerFiliacionesDto perFiliacionesDto);

  List<BigInteger> getIdsInteresadosConsultaFiliaciones(String maxItemConsultaFiliaciones);

  /**
   * Devuelve una lista de validaciones, necesarias para el semaforo de
   * validaciones, a partir de idExp.
   * 
   * @param idExp
   * @return
   * @throws SinacException
   */
  List<ValidacionSemaforoDto> getListaValidacionesByIdExp(BigInteger idExp) throws SinacException;

  /**
   * Identifica la validacion de los certificados (DELE, CCSE) mediante el codigo
   * de la entidad maestra y la calificacion que recibe de la persona que tiene
   * estos certificados.
   * 
   * @param calificacion
   * @param codLdvEntMae
   * @return
   * @throws SinacException
   */
  LdvMaestraDto identificarValCertificadoByCalificacion(String calificacion, String codLdvEntMae) throws SinacException;

  /**
   * Realiza un Update en la tabla Val_Semaforo, utilizando el idExp donde se
   * encuentra la validacion a cambiar, codLdvEntMae para identificar el tipo de
   * validacion y codValSem para indentificar el estado al que va a pasar la
   * validacion.
   * 
   * @param idExp
   * @param codLdvEntMae
   * @param codValSem
   * @throws SinacException
   */
  void updateValidacionSemaforo(BigInteger idExp, String codLdvEntMae, String codValSem) throws SinacException;

  /**
   * Recalcula la validación que se le pasa por código de expediente indicado por
   * si es posible realizar un cambio de estado a dicha validación.
   * 
   * @param idExp
   * @param codValLvdEntMae
   */
  void recalcularValidadionesIntegracion(BigInteger idExp, List<String> listCodValLdvEntMae) throws SinacException;

  /**
   * Recalcula las validación (cumplimiento integracion y conducta civica) que se
   * le pasa por código de expediente indicado por si es posible realizar un
   * cambio de estado a dichas validaciones.
   * 
   * @param idExp
   * @param codValLvdEntMae
   */
  void recalcularValidadionesSemaforo(BigInteger idExp, List<String> listaValidacionesInt,
      List<String> listaValidacionesCon);

  /**
   * Obtiene el codigo de LdvEntidadesMaestras a partir de codLdvMae
   * 
   * @param codVal
   * @throws SinacException
   */
  String getCodLdvEntMaeByCodLdvMae(String codVal) throws SinacException;

  /**
   * Devuelve una lista de expedientes con todos los informes que tengan el estado
   * que se le pasa.
   * 
   * @param codEstInforme
   * @return
   */
  List<ExpedienteInformeDto> getListaExpedientesInformesByCodEstInforme(String codEstInforme) throws SinacException;

  void saveInformeDgpRecibido(String numExp, String fechaAlta, String tipoPeticion, String estado);

  List<InformesDgpRecibidosDto> getAllInformesDgpRecibidosNoProcesados();

  InformesDgpRecibidosDto findByNumExpAndFechaAlta(String numExp, String fechaAlta);

  void updateInformeDgpRecibidoEntity(InformesDgpRecibidosDto entityToUpdate, String estado);

  void saveExpedientesRelacionadosAutomaticamente(ExpedienteDto expedienteDto,
      List<ExpedienteDto> expedientesRelacionados, BigInteger idExpOrigen);

  /**
   * Realiza el guardado de los datos necesarios para la creacion del expedientes
   * (saveExpediente, saveExpedientesRelacionados, saveCamposFormularioExpediente,
   * saveExpedientePersonas, saveExpedienteValSemaforo, saveSolicitud)
   * 
   * 
   * @param valores
   * @param idExpOri
   * @param solicitudDto
   * @param interesadoDto
   * @param solicitudesPersonasDtoList
   * @param idenExpGD
   * @param codExp
   * @return
   */
  ExpedienteDto guardarEntidadesExpediente(Map<String, Object> valores, BigInteger idExpOri, SolicitudDto solicitudDto,
      PersonaDto interesadoDto, List solicitudesPersonasDtoList, String idenExpGD, String codExp);

  LinkedList<DocumentoToSaveDto> obtenerTodosLosDocumentosSede(List<SolicitudDocumentoDto> listaDocs);

  List<ExpedienteDto> getListaExpedientesIncompletos(List<String> estados);

  void reintentoDocumentosExpediente(List<ExpedienteDocumentoDto> listaDocsExp, SolicitudDto solicitudDto,
      ExpedienteDto expedienteDto);

  List<PersonaDto> getExpedienteAcumular(String numAcreditacion, List<String> listaEstados, String string);

  ParametrizacionDto getParametrizacionByNombreAndProcedimiento(String nomParam, String codPro);

  void descargarJustificanteGeiser(AsientoDto asientoDto, UsuarioDto usuarioDto);

  DataSource obtenerArchivoByNombre(String nombre, String ruta) throws SinacException;

  void comprobarSolicitudPenCompletada(BigInteger idExpInf);

  void actualizarExpedienteInformesMjuFicherosDatos(String nombreArchivo) throws SinacException;

  List<ExpedienteDto> getExpedienteAcumularPorIdPer(BigInteger idPer, String codPro, List<String> listaEstados);

  List<PersonaDto> getPersonasRastreo(String numAcreditacion);

  List<ExpedienteDto> getListaExpedientesResolver(String codPro, List<String> listaEstadosExp);

  void generarFirmarAuto(ExpedienteDto item, Map<String, Object> contextData, PlantillaDto plantilla);

  ExpedienteDto getExpedientesByIdPerInteresado(BigInteger idPer, List<String> listaEstados);

  void saveExpedienteInformeDgp(ExpedienteInformeDgpDto expedienteInformeDgpDto);

  List<ExpedienteDto> getListaExpedientesPropuesta(List<String> listaEstados, String valor,
      List<String> listaNombresNotIn);

  List<PerCertificacionesDto> getPerCertificacionesByIdPerCodigosEstados(BigInteger idPer,
      List<String> listaCodigosEstados);

  boolean cumpleCriterios(List<Long> listaCondiciones, BigInteger idExp, Short idPro);

}
