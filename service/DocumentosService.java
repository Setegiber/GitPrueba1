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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.model.dto.AsientoDto;
import es.mjusticia.sinac.core.model.dto.DescargaDeDocumentoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToSaveDto;
import es.mjusticia.sinac.core.model.dto.DocumentosEntradaDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoInformeMdeDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteFirmaDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDgpDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDgpTramiteDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteInformeDto;
import es.mjusticia.sinac.core.model.dto.FirmanteDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;
import es.mjusticia.sinac.core.model.dto.PlantillasPlantillasCamposDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesDto;
import es.mjusticia.sinac.core.model.dto.RegistroDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDocumentoDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDto;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;
import es.mjusticia.sinac.core.model.entity.LdvMaestraEntity;
import es.mjusticia.sinac.core.model.enums.TipoRegistroRegageEnum;
import es.mjusticia.sinac.geiser.model.dto.DocumentoDto;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;

/**
 * Componente de Negocio para la Interfaz del Servicio de Documento.
 *
 * @author NTT Data.
 */
public interface DocumentosService {

  /**
   * Obtiene el Tipo de Documento asociado al Identificador de Tipo de Documento
   * establecido como parámetro.
   *
   * @param idDocumentoTipo Identificador del Tipo de Documento.
   * @return DTO con la Información del Tipo de Documento.
   * @throws SinacException Si se produce un error al obtener el Tipo de
   *                        Documento.
   */
  DocumentoTipoDto getDocumentoTipoByIdDocumentoTipo(final Short idDocumentoTipo) throws SinacException;

  String copyDocumentosSolicitudesNFS(String codSolicitud, String codProcedimiento,
      List<DocumentoToSaveDto> documentoToSaveDtoList);

  void deleteDocumentosSolicitudesNFS(List<DocumentoToSaveDto> documentoToSaveDtoList);

  ExpedienteDocumentoDto saveExpedienteDocumento(ExpedienteDocumentoDto expedienteDocumentoDto,
      ExpedienteDto expedienteDto) throws SinacException;

  List<PlantillaDto> getPlantillas();

  PlantillaDto getPlantillaById(short id) throws SinacException;

  List<PlantillasPlantillasCamposDto> getPlantillasPlantillasCamposByIdPlantilla(short idPlantilla);

  /**
   * Obtiene el Documento de Expediente asociado al Identificador de Documento
   * establecido como parámetro.
   *
   * @param idDocumento Identificador del Documento.
   * @return DTO con la Información del Documento de Expediente.
   * @throws SinacException Si se produce un error al obtener el Documento de
   *                        Expediente.
   */
  ExpedienteDocumentoDto getExpedienteDocumentoByIdDocumento(final BigInteger idDocumento) throws SinacException;

  String getUrlDocumentoByIdDocumento(BigInteger idDocumento) throws SinacException;

  /**
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

  /**
   * obtiene la lista de docstipos
   */
  public List<DocumentoTipoDto> getComboDocumentoTipo(Short procedimiento);

  /**
   * descarga un documento
   *
   * @param idDocExp Identificador del documento de Expediente
   *
   * @return DescargaDeDocumentoDto Objeto con los datos
   *
   * @throws SinacException Si se produce un error al convertir archivo
   */
  DescargaDeDocumentoDto getArchivoByIdDocExp(BigInteger idDocExp) throws SinacException;

  /**
   * descarga un documento con la firma de copia autentica
   *
   * @param idDocExp Identificador del documento de Expediente
   *
   * @return DescargaDeDocumentoDto Objeto con los datos
   *
   * @throws SinacException Si se produce un error al convertir archivo
   */
  DescargaDeDocumentoDto descargarDocumentoCopiaAutentica(BigInteger idDocExp) throws SinacException;

  List<ExpedienteInformeDto> getExpedienteInformesByIdExpediente(BigInteger idExpediente, int idLdvCaducado);

  DocumentoTipoDto getTipoDocumentoPorCodGdCodReg(int idLdvGd, int idLdvReg) throws SinacException;

  void enviarNotificacion(ExpedienteDto expedienteDto, BigInteger idDocumento, String codTipoEnvio,
      ProcedimientosFasesTramitesOperacionesDto procedimientosFasesTramitesOperacionesDto, Map<String, Object> valores)
      throws SinacException;

  ExpedienteDocumentoDto getExpedienteDocumentoByIdDocumentoIdExpediente(BigInteger idDocumento,
      BigInteger idExpediente) throws SinacException;

  /**
   * 
   * cambia el estado del documento a validado o rechazadp establecido como
   * parámetro.
   *
   * @param idDocExp Identificador del Documento, operacion validar/rechazar.
   * @return int con el resultado de la operación
   * @throws SinacException Si se produce un error al cambiar el valor
   */
  int validarRechazarDoc(BigInteger idDocExp, Integer operacion) throws SinacException;

  /**
   * Obtiene el Documento asociado al Identificador de Documento establecido como
   * parámetro.
   *
   * @param idDocumentoExpediente Identificador del Documento.
   * @return DTO con la Información del Documento.
   * @throws SinacException Si se produce un error al obtener el Documento.
   */
  ExpedienteDocumentoDto getExpedienteDocumentoByIdDocumentoExpediente(final BigInteger idDocumentoExpediente)
      throws SinacException;

  /**
   * Obtiene la Lista de Firmantes del Documento asociado al Identificador de
   * Procedimiento y al Identificador de Tipo de Documento establecidos como
   * parámetros.
   *
   * @param idProcedimiento Identificador del Procedimiento.
   * @param idTipoDocumento Identificador del Tipo de Documento.
   * @return Lista de DTOs con la Información de los Firmantes.
   * @throws SinacException Si se produce un error al obtener la Lista de DTOs con
   *                        la Información de los Firmantes.
   */
  List<FirmanteDto> getFirmantesDocumentoByIdProcedimientoAndIdTipoDocumento(final short idProcedimiento,
      final short idTipoDocumento) throws SinacException;

  /**
   * Inserta en la Tabla "EXP_FIRMAS" el Registro con los Datos del Documento
   * enviado o recibido de Portafirmas.
   *
   * @param expedienteFirmaDto Datos del Documento.
   * @param valores
   * @throws SinacException Si se produce un error al insertar en la Tabla
   *                        "EXP_FIRMAS" el Registro con los Datos del Documento.
   */
  void saveExpedienteFirma(final ExpedienteFirmaDto expedienteFirmaDto) throws SinacException;

  /**
   * Actualiza en la Tabla "EXP_DOCUMENTOS" el Estado del Documento.
   *
   * @param idDocumento   Identificador del Documento a actualizar el Estado.
   * @param ldvMaestraDto Nuevo Estado a actualizar.
   * @throws SinacException Si se produce un error al actualizar en la Tabla
   *                        "EXP_DOCUMENTOS" el Estado del Documento.
   */
  void updateEstadoDocumento(final BigInteger idDocumento, final LdvMaestraDto ldvMaestraDto) throws SinacException;

  /**
   * Obtiene el Registro con los Datos del Documento enviado a Portafirmas
   * asociado al Identificador de la Solicitud de Firma establecido como
   * parámetro.
   *
   * @param idSolicitudFirma Identificador de la Solicitud de Firma.
   * @return DTO con los Datos del Documento enviado a Portafirmas.
   * @throws SinacException Si se produce un error al obtener el Registro con los
   *                        Datos del Documento enviado a Portafirmas.
   */
  ExpedienteFirmaDto getExpedienteFirmaByIdSolicitudFirma(final String idSolicitudFirma) throws SinacException;

  /**
   * Actualiza en la Tabla "EXP_FIRMAS" a no vigente el Registro con los Datos del
   * Documento enviado o recibido de Portafirmas asociado al Identificador
   * establecido como parámetro.
   *
   * @param idExpedienteFirma Identificador del Registro a actualizar.
   * @param modificadoPor     Quien actualiza en la Tabla "EXP_FIRMAS" el Registro
   *                          a no vigente (Persona o Proceso).
   * @throws SinacException Si se produce un error al actualizar en la Tabla
   *                        "EXP_FIRMAS" el Registro a no vigente.
   */
  void updateExpedienteFirmaNoVigente(final BigInteger idExpedienteFirma, final UsuarioDto modificadoPor)
      throws SinacException;

  /**
   * Obtiene la Información del Documento requerida para guardar un Documento de
   * Salida.
   *
   * @param idDocumentoExpediente Identificador del Documento.
   * @return DTO con la Información del Documento.
   * @throws SinacException Si se produce un error al obtener la Información del
   *                        Documento.
   */
  ExpedienteDocumentoDto getInfoToSaveDocumentoSalidaByIdDocumentoExpediente(final BigInteger idDocumentoExpediente)
      throws SinacException;

  /**
   * Firma el Documento.
   *
   * @param expedienteDocumentoDto Documento a firmar.
   * @return Contenido Firmado del Documento.
   * @throws SinacException Si se produce un error al firmar el Documento.
   */
  DataHandler signDocumento(final ExpedienteDocumentoDto expedienteDocumentoDto) throws SinacException;

  /**
   * Genera un nuevo Registro de Salida para el Documento.
   *
   * @param tipoRegistro           Tipo de Registro.
   * @param expedienteDocumentoDto Documento para el que se ha de generar un nuevo
   *                               Registro de Salida.
   * @param contenido              Contenido del Documento.
   * @return Nuevo Registro de Salida para el Documento.
   * @throws SinacException Si se produce un error al generar un nuevo Registro de
   *                        Salida para el Documento.
   */
  RegistroDto generateRegistroDocumento(final TipoRegistroRegageEnum tipoRegistro,
      final ExpedienteDocumentoDto expedienteDocumentoDto, final DataHandler contenido) throws SinacException;

  /**
   * Inserta en la Tabla "REGISTROS" los Datos del Apunte Registral.
   *
   * @param registroDto Datos del Apunte Registral.
   * @throws SinacException Si se produce un error al insertar en la Tabla
   *                        "REGISTROS" los Datos del Apunte Registral.
   */
  void saveRegistro(final RegistroDto registroDto) throws SinacException;

  /**
   * Guarda el Documento Firmado en el Gestor Documental.
   *
   * @param tipoAsientoRegistral      Tipo Asiento Registral.
   * @param identificadorExpedienteGD Identificador ENI del Expediente en el
   *                                  Gestor Documental.
   * @param idProcedimiento           Identificador del Procedimiento necesario
   *                                  para determinar el Tipo de Firma.
   * @param expedienteDocumentoDto    Documento a guardar.
   * @param contenido                 Contenido Firmado del Documento.
   * @throws SinacException Si se produce un error al guardar el Documento firmado
   *                        en el Gestor Documental.
   */
  void saveDocumentoGestorDocumental(final TipoRegistroRegageEnum tipoAsientoRegistral,
      final String identificadorExpedienteGD, final short idProcedimiento,
      final ExpedienteDocumentoDto expedienteDocumentoDto, final DataHandler contenido) throws SinacException;

  /**
   * Copia en NFS (Network File System) el Documento.
   *
   * @param expedienteDocumentoDto Documento a copiar en NFS (Network File
   *                               System).
   * @param contenido              Contenido del Documento.
   */
  void copyDocumentoNFS(final ExpedienteDocumentoDto expedienteDocumentoDto, final DataHandler contenido);

  /**
   * Elimina en NFS (Network File System) el Documento del Expediente.
   *
   * @param expedienteDocumentoDto Documento a eliminar en NFS (Network File
   *                               System).
   * @throws SinacException Si se produce un error al eliminar en NFS (Network
   *                        File System) el Documento del Expediente.
   */
  void deleteDocumentoNFS(final ExpedienteDocumentoDto expedienteDocumentoDto) throws SinacException;

  /**
   * Añade el Identificador ENI del Documento en el Gestor Documental al Campo
   * "COD_GD" y establece a NULL el Campo "NFS_RUTA" en la Tabla "EXP_DOCUMENTOS".
   * 
   * @param idDocumento Identificador del Documento a actualizar.
   * @param codGd       Identificador ENI del Documento en el Gestor Documental a
   *                    añadir al Campo "COD_GD".
   * @throws SinacException Si se produce un error al actualizar en la Tabla
   *                        "EXP_DOCUMENTOS" el Documento.
   */
  void updateCodGdAndSetNfsRutaToNullForDocumento(final BigInteger idDocumento, final String codGd)
      throws SinacException;

  ExpedienteDocumentoDto saveExpedienteDocumentoHistorico(ExpedienteDocumentoDto expedienteDocumentoDto,
      ExpedienteDto expedienteDto) throws SinacException;

  boolean cambiarFormatoDocumento(ExpedienteDocumentoDto expDoc, String formatoOriginal, String formatoObjetivo)
      throws SinacException;

  RegistroDto getRegistroByIdSolicitudDocumento(BigInteger idSolicitudDocumento);

  DocumentoTipoDto getDocumentoTipoEntityByCod(String cod);

  /**
   * Obtiene el estado inicial del documento basado en el procedimiento y tipo de
   * documento. Consulta la configuración de ProcedimientosDocumentosTipo para
   * obtener el estado correspondiente desde el catálogo.
   *
   * @param idProcedimiento Identificador del procedimiento
   * @param idDocumentoTipo Identificador del tipo de documento
   * @return DTO con el estado del documento, o null si no se encuentra
   *         configuración
   * @throws SinacException Si hay error al obtener el estado
   */
  LdvMaestraDto getEstadoDocumentoPorProcedimientoYTipo(Short idProcedimiento, Short idDocumentoTipo)
      throws SinacException;

  void deleteRegistro(RegistroDto registroDto) throws SinacException;

  boolean copyArchivoFtpNFS(String nombreArchivo, byte[] contenido, String ruta) throws SinacException;

  boolean borrarArchivoFtpNFS(String nombreArchivo, String ruta) throws SinacException;

  ExpedienteInformeDgpDto getExpedienteInformeDgpByCodExpediente(String codExpediente);

  List<ExpedienteInformeDgpTramiteDto> getExpedienteInformesDgpTramitesByIdExpedienteInformeDgp(
      BigInteger idExpedienteInformeDgp);

  ExpedienteInformeDto getExpedienteInformeById(BigInteger id) throws SinacException;

  byte[] cambiarFormatoDocumentoPlantilla(byte[] contenido, String formatoOriginal, String formatoObjetivo)
      throws SinacException;

  /**
   * Comprueba con el Antivirus ofrecido desde el Framework que los Documentos son
   * válidos.
   *
   * @param documentoToSaveDtoList Lista de Documentos a comprobar si son válidos.
   * @throws SinacException Si se produce un error al validar el Documento con el
   *                        Antivirus.
   */
  DocumentoToSaveDto validateDocumentoAntivirus(DocumentoToSaveDto documentoToSaveDto) throws SinacException;

  /**
   * Genera un nuevo Registro de Entrada para los Documentos validados.
   *
   * @param tipoRegistro           Tipo de Registro.
   * @param documentoToSaveDtoList Lista de Documentos para los que se ha de
   *                               generar un nuevo Registro de Entrada.
   * @throws SinacException Si se produce un error al generar un nuevo Registro de
   *                        Entrada para los Documentos.
   */
  DocumentoToSaveDto generateRegistroDocumentoV2(TipoRegistroRegageEnum tipoRegistro,
      DocumentoToSaveDto documentoToSaveDto) throws SinacException;

  /**
   * Firma los Documentos validados.
   *
   * @param documentoToSaveDtoList Lista de Documentos a firmar.
   * @throws SinacException Si se produce un error al firmar los Documentos.
   */
  DocumentoToSaveDto signDocumento(DocumentoToSaveDto documentoToSaveDto) throws SinacException;

  /**
   * Guarda los Documentos firmados en el Gestor Documental.
   *
   * @param tipoAsientoRegistral      Tipo Asiento Registral.
   * @param identificadorExpedienteGD Identificador ENI del Expediente en el
   *                                  Gestor Documental.
   * @param idProcedimiento           Identificador del Procedimiento necesario
   *                                  para determinar el Tipo de Firma.
   * @param documentoToSaveDtoList    Lista de Documentos a guardar.
   * @throws SinacException Si se produce un error al guardar los Documentos
   *                        firmados en el Gestor Documental.
   */
  DocumentoToSaveDto saveDocumentoGestorDocumental(TipoRegistroRegageEnum tipoAsientoRegistral,
      String identificadorExpedienteGD, short idProcedimiento, DocumentoToSaveDto documentoToSaveDto)
      throws SinacException;

  /**
   * Copia en NFS (Network File System) los Documentos asociados al Expediente.
   *
   * @param codExpediente          Código del Expediente.
   * @param codProcedimiento       Código del Procedimiento.
   * @param documentoToSaveDtoList Lista de Documentos a copiar en NFS (Network
   *                               File System).
   */
  DocumentoToSaveDto copyDocumentoNFS(String codExpediente, String codProcedimiento, Date fechaEfectos,
      DocumentoToSaveDto documentoToSaveDto) throws SinacException;

  /**
   * Inserta en Base de Datos los Documentos guardados asociados al Expediente.
   *
   * @param expedienteDto          Expediente.
   * @param documentoToSaveDtoList Lista de Documentos a insertar.
   * @param isCreateExpediente     Flag que determina si la Operación de origen es
   *                               la creación del expediente.
   * @throws SinacException Si se produce un error al insertar en Base de Datos
   *                        los Documentos guardados asociados al Expediente.
   */
  DocumentoToSaveDto saveDocumentoV2(BigInteger idExpediente, DocumentoToSaveDto documentoToSaveDto,
      boolean isCreateExpediente) throws SinacException;

  /**
   * Valida el Tamaño, la Extensión y los Campos requeridos (Tipo Documento,
   * Origen, Estado Elaboración, Órgano, Número Registro y Fecha Registro) para
   * los Documentos comprobados como válidos por el Antivirus.
   *
   * @param documentoToSaveDtoList Lista de Documentos a validar.
   * @throws SinacException Si se produce un error al validar el Tamaño, la
   *                        Extensión y los Campos requeridos del Documento.
   */
  DocumentoToSaveDto validateDocumento(DocumentoToSaveDto documentoToSaveDto) throws SinacException;

  /**
   * Inserta en Base de Datos los Documentos guardados asociados al Expediente.
   *
   * @param expedienteDto          Expediente.
   * @param documentoToSaveDtoList Lista de Documentos a insertar.
   * @param isCreateExpediente     Flag que determina si la Operación de origen es
   *                               la creación del expediente.
   * @throws SinacException Si se produce un error al insertar en Base de Datos
   *                        los Documentos guardados asociados al Expediente.
   */
  DocumentoToSaveDto saveDocumento(ExpedienteDto expedienteDto, DocumentoToSaveDto documentoToSaveDto,
      boolean isCreateExpediente) throws SinacException;

  LinkedList<DocumentoToSaveDto> transformMultipartToDocumentoToSave(SolicitudDto documentoEntrada);

  LinkedList<DocumentoToSaveDto> transformMultipartToDocumentoToSaveExp(DocumentosEntradaDto documentoEntrada);

  /**
   * Recupera todos los archivos de una carpeta nfs
   * 
   * @param nfsPath carpeta nfs
   * @return listado de archivos tipo Datasource
   */
  List<DataSource> obtenerTodosLosArchivos(String nfsPath) throws SinacException;

  ExpedienteDocumentoDto createExpedienteDocumentoPlantillaContent(PlantillaDto plantillaDto,
      ExpedienteDto expedienteDto, ExpedienteDocumentoDto expedienteDocumentoDto, Map<String, String> valoresPlantillas)
      throws SinacException;

  /**
   * Recoge los datos documentos obligatorios por procedimiento y los pasa como
   * solDoc
   * 
   * @param codPro
   * @return List<DocumentoTipoDto>
   */
  List<DocumentoTipoDto> getDocumentosSolicitudObligatorios(String codPro);

  DataHandler getContenido(final ExpedienteDocumentoDto expedienteDocumentoDto) throws SinacException;

  void descargarJustificanteGeiser(AsientoDto asientoDto, UsuarioDto usuarioDto);

  DocumentoDto obtenerJustificanteGeiser(AsientoDto asientoDto);

  AsientoDto guardarJustificanteGeiser(AsientoDto asientoDto, DocumentoDto justificante, UsuarioDto usuarioDto);

  List<AsientoDto> getAsientosEnCurso();

  AsientoDto consultarEstadoDocumentoEnviadoAGeiser(AsientoDto asiento, UsuarioDto usuario);

  /**
   * Genera el indice electronico del expediente en cuestión
   * 
   * @param idExp Identificador del expediente
   * @return ExpedienteDocumentoDto
   */
  ExpedienteDocumentoDto generarIndiceElectronico(BigInteger idExp) throws SinacException;

  /**
   * Busca el sentido de la resolucion por el idDocumento.
   * 
   * @param idDocumento
   * @return
   * @throws SinacException
   */
  LdvMaestraEntity findIdSentidoResolucionLdvByIdExpDoc(BigInteger idExpDoc) throws SinacException;

  void saveExpedienteDocumentoInformeMde(ExpedienteDocumentoInformeMdeDto expedienteDocumentoInformeMdeDto);

  List<ExpedienteDocumentoDto> getExpedientesDocumentosMdeByIdInforme(BigInteger idExpedienteInforme)
      throws SinacException;

  void desactivarExpedienteDocumentosMdeByIdDocumento(BigInteger idDocumento) throws SinacException;

  short getIdTipoDocByCodTipo(String codTipo) throws SinacException;

  LinkedList<DocumentoToSaveDto> obtenerTodosLosDocumentosSede(List<SolicitudDocumentoDto> listaDocs);

  DocumentoToSaveDto generarDocumentosExpediente(SolicitudDocumentoDto docSol, byte[] contenido);

  ExpedienteDocumentoDto generarExpedienteDocumento(SolicitudDocumentoDto docSol, byte[] contenido);

  ExpedienteDocumentoDto getExpedienteDocumentosByAccionOperacionTramiteIdExp(List<ExpedienteDocumentoDto> expDocs,
      String codAccion, String codOpe, String codTramite, BigInteger idExp);

  boolean getListDocsGeneradosPostFirma(BigInteger idExp, Short idPro, String tramite, Date fechaRecepcion);

  DataSource obtenerArchivoByNombre(String nombre, String ruta) throws SinacException;
}
