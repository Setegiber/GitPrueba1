package es.mjusticia.sinac.core.batch;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.sshtools.client.SshClient;
import com.sshtools.client.sftp.SftpClient;
import com.sshtools.client.sftp.SftpFile;
import com.sshtools.client.sftp.TransferCancelledException;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.sftp.SftpStatusException;
import com.sshtools.common.ssh.SshException;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.service.SshClienteService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.ArchivoFtpDto;
import es.mjusticia.sinac.core.model.dto.ArchivoMjuDto;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.UtilError;
import jakarta.activation.DataSource;

@Component
public class RecepcionInformeMjuJob extends SinacJob<ArchivoMjuDto> {

  private static final Logger LOG = LoggerFactory.getLogger(RecepcionInformeMjuJob.class);
  private static final int ANYO_LENGTH = 4;
  private static final int ESP_RES_PART = 2;
  private static final int ES_MOTIVO_RECHAZO_PART = 3;
  private static final int EU_RES_PART = 4;
  private static final int EU_MOTIVO_RECHAZO_PART = 5;
  @Value("${sinac.quartz.recepcionInformeMjuJob.nfs.respondidos}")
  private String nfsRespondidos;
  @Value("${sinac.quartz.recepcionInformeMjuJob.nfs.en_tratamiento}")
  private String nfsEnTratamiento;
  @Value("${sinac.quartz.recepcionInformeMjuJob.nfs.tratados}")
  private String nfsTratados;
  @Value("${sinac.quartz.recepcionInformeMjuJob.nfs.error}")
  private String nfsError;
  @Value("${sinac.ftp.solicitudInformeMjuJob.host}")
  private String host;
  @Value("${sinac.ftp.solicitudInformeMjuJob.port}")
  private Integer port;
  @Value("${sinac.ftp.solicitudInformeMjuJob.pathSalida}")
  private String pathSalida;
  @Value("${sinac.ftp.solicitudInformeMjuJob.user}")
  private String user;
  @Value("${sinac.ftp.solicitudInformeMjuJob.pass}")
  private String pass;

  private boolean contieneErrores = false;

  private String descripcion;

  private String jobName;

  private Integer idUsu;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  SshClienteService sshClienteService;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  public RecepcionInformeMjuJob() {
    super();
  }

  @Override
  public List<ArchivoMjuDto> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobMjuRecibir.recuperarItems - Init");
    SshClient sshClient = null;
    SftpClient sftpClient = null;
    List<SftpFile> listaAchivos = new ArrayList<>();
    List<ArchivoMjuDto> items = new ArrayList<>();
    idUsu = getUsuariosService().getUsuarioByUsuarioJusticia(getUsuarioJusticia()).getIdUsu();
    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    if (host != null && !host.isEmpty() && port != null && user != null && !user.isEmpty() && pass != null
        && !pass.isEmpty()) {
      try {
        LOG.info("contextData: {}", contextData);
        sshClient = sshClienteService.loginSsh(host, port, user, pass);
        sftpClient = sshClienteService.createSftpClient(sshClient);
        sftpClient.lcd(System.getProperty("user.dir"));
        sftpClient.cd(pathSalida);
        listaAchivos.addAll(Arrays.asList(sftpClient.getFiles("*.zip")));
        Collections.sort(listaAchivos, (SftpFile o1, SftpFile o2) -> {
          try {
            return o1.getAttributes().getModifiedDateTime().compareTo(o2.getAttributes().getModifiedDateTime());
          } catch (SftpStatusException | SshException e) {
            LOG.info("sftpClient - Collections.sort {}", e.getMessage());
          }
          return 0;
        });
        for (SftpFile sftpFile : listaAchivos) {
          LOG.info("nombre: {}", sftpFile.getFilename());
          String itemName = recuperarItemSftp(sftpFile, sftpClient);
          if (itemName != null) {
            items.add(new ArchivoMjuDto(itemName, nfsRespondidos));
            expedientesFacade.actualizarEstadoArchivoFtp(sftpFile.getFilename().trim().toUpperCase(),
              Constantes.EstadosFichero.ESTADO_FICHERO_EN_TRATAMIENTO);
          }
        }
        items.addAll(recuperarErroneos());
      } catch (PermissionDeniedException | TransferCancelledException | SshException | SftpStatusException
          | IOException e) {
        addError();
        contextData.put("errorJob", e.getMessage());
        guardaErrorJob(e, descripcion, jobName);
      } finally {
        cerrarTodo(sshClient, sftpClient, listaAchivos);
      }
    }
    LOG.info("SinacJobMjuRecibir.recuperarItems - End");
    return items;
  }

  private String recuperarItemSftp(SftpFile sftpFile, SftpClient sftpClient) {
    try {
      String name = guardarNfsSiNoExisteFichero(sftpFile, sftpClient);
      sftpFile.delete();
      return name;
    } catch (SftpStatusException | SshException | IOException e) {
      LOG.error("Error al recuperar y guardar el archivo zip {} : {}", sftpFile.getFilename(), e.getMessage());
      return null;
    }
  }

  private String guardarNfsSiNoExisteFichero(SftpFile sftpFile, SftpClient sftpClient)
      throws SftpStatusException, SshException, IOException {
    String fileName = sftpFile.getFilename();
    if (!expedientesFacade.existeExpedienteInformesMjuFichero(sftpFile.getFilename().trim().toUpperCase())) {
      File file = new File(fileName);
      InputStream byteArrayOutputStream;
      byteArrayOutputStream = sftpClient.getInputStream(sftpFile.getFilename());
      Files.write(file.toPath(), byteArrayOutputStream.readAllBytes());
      expedientesFacade.copyArchivoFtpNFS(fileName, FileUtils.readFileToByteArray(file), nfsRespondidos);
      return fileName;
    }
    LOG.info("El fichero ya ha sido tratado: {}", fileName);
    return null;
  }

  private List<ArchivoMjuDto> recuperarErroneos() {
    List<DataSource> error = expedientesFacade.obtenerTodosLosArchivos(nfsError);
    List<ArchivoMjuDto> itemsError = new ArrayList<>();
    for (DataSource ds : error)
      itemsError.add(new ArchivoMjuDto(ds.getName(), nfsError));
    return itemsError;
  }

  /**
   * Cierra y limpia los recursos asociados a la ejecución del job.
   * - Cierra las conexiones SFTP/SSH si están abiertas.
   * - Elimina archivos temporales creados durante el procesamiento de los ZIP
   * recibidos.
   * - Manejo de errores aislado para no interrumpir el flujo de otros archivos.
   */
  private void cerrarTodo(SshClient sshClient, SftpClient sftpClient, List<SftpFile> listaAchivos) {
    for (SftpFile file : listaAchivos) {
      borrarArchivosTemporales(null, file.getFilename());
    }
    try {
      if (sshClient != null && sftpClient != null) {
        desconectarSftp(sftpClient, sshClient);
      }
    } catch (SshException e) {
      LOG.error(MessageFormat.format("error al desconectar {0}", e.getMessage()), e);
    }
  }


  private void desconectarSftp(SftpClient sftpClient, SshClient sshClient) throws SshException {
    sftpClient.quit();
    sshClient.disconnect();

  }

  @Override
  public void procesarItem(ArchivoMjuDto item, Map<String, Object> contextData) {
    LOG.info("SinacJobDgpRecibir.procesarItems - Init");
    LOG.info("Item: {} - {}", item.getFileName(), item.getRuta());
    DataSource dataSource = expedientesFacade.obtenerArchivoByNombre(item.getFileName(), item.getRuta());
    try {
        File file = copiarYEliminar(dataSource,null, item.getRuta(), nfsEnTratamiento);
        procesarFicheroZip(idUsu, file);
    }catch (Exception e){
      try {
        copiarYEliminar(dataSource, null, nfsEnTratamiento, nfsError);
      } catch (IOException e1) {
        LOG.error("No se ha podido mover el archivo {} a la ruta de error del NFS.", dataSource.getName());
      }
      addError();
      contextData.put("errorJob", e.getMessage());
      guardaErrorJob(e, descripcion, jobName);
    }
    LOG.info("SinacJobDgpRecibir.procesarItems - End");
  }
  
  private void procesarContenidoZip (File file, Map<String, ArchivoFtpDto> listaCodExpedienteArchivoFtpDto) throws IOException{
    ZipFile zip;
    try {
      zip = new ZipFile(file);
      List<? extends ZipEntry> listaArchivosRpr = zip.stream()
        .filter(entry -> entry.getName().toUpperCase().contains(".RPR")).toList();
      List<? extends ZipEntry> listaArchivosPdf = zip.stream()
          .filter(entry -> entry.getName().toUpperCase().contains(".PDF")).toList();
      rellenaDatosPdfs(listaArchivosPdf, listaCodExpedienteArchivoFtpDto, zip);
      rellenaDatosRpr(listaArchivosRpr, listaCodExpedienteArchivoFtpDto, zip);
      zip.close();
    } catch (IOException e) {
      LOG.error("Se ha producido un error procesando los datos del zip {}", file.getName());
      throw e;
    }
  }

  public void procesarFicheroZip(Integer idUsu, File file) throws IOException {
    contieneErrores = false;
    String nombre = file.getName();
    Map<String, ArchivoFtpDto> listaCodExpedienteArchivoFtpDto = new HashMap<>();
    procesarContenidoZip(file, listaCodExpedienteArchivoFtpDto);
    try {
	    listaCodExpedienteArchivoFtpDto.forEach((String codExpediente, ArchivoFtpDto archivoFtpDto) -> {
	      Map<String, Object> mapa = expedientesFacade.getIdExpCodProceByCodExpediente(codExpediente,
	          Constantes.TiposInforme.TIPO_INFORME_MJU);
	      if (mapa != null && !mapa.isEmpty()) {
	        try {
	          archivoFtpDto.setIdExpediente((BigInteger) mapa.get("idExp"));
	          archivoFtpDto.setIdExpInfMju((BigInteger) mapa.get("idExpInfMju"));
	          Long idProFasTraOpeAcc = expedientesFacade
	              .getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
	                  mapa.get("codProcedimiento").toString(), "INF", "IMJU", "RMJU");
	          expedientesFacade.ejecutarAccion(idProFasTraOpeAcc,
	              getModelMap(idUsu, Short.valueOf(mapa.get("idPro").toString()), archivoFtpDto));
	        } catch (NumberFormatException | SinacException | SQLException | ParserConfigurationException | SAXException
	            | IOException e) {
	          contieneErrores = true;
	          addError();
	          guardaErrorJob(e, descripcion, jobName);
	          LOG.error("Se ha producido un error ejecutando la acción: {}", e.getMessage());
	        }
	      } else {
	        LOG.info("No se ha encontrado el expediente o el informe: {}", codExpediente);
	      }
	    });
    }catch (Exception e) {
    	contieneErrores = true;
    	addError();
        guardaErrorJob(e, descripcion, jobName);
    	LOG.error("Se ha producido un error durante el procesamiento: {}", e.getMessage());
    }
    if (contieneErrores) {
      copiarYEliminar(null, file, nfsEnTratamiento, nfsError);
      expedientesFacade.actualizarEstadoArchivoFtp(nombre.trim().toUpperCase(),
          Constantes.EstadosFichero.ESTADO_FICHERO_ERROR);
    } else {
      copiarYEliminar(null, file, nfsEnTratamiento, nfsTratados);
      expedientesFacade.actualizarEstadoArchivoFtp(nombre.trim().toUpperCase(),
          Constantes.EstadosFichero.ESTADO_FICHERO_TRATADO);
      listaCodExpedienteArchivoFtpDto.forEach((key, dto) -> {
          BigInteger idExpInfMju = dto!= null ? dto.getIdExpInfMju() : null;
          try {expedientesFacade.comprobarSolicitudPenCompletada(idExpInfMju);
          } catch (Exception e) {
            LOG.error("No se ha podido actualizar el estado del archivo .pen de solicitud asociado de forma correcta.");
          }
      });
    }
    
    listaCodExpedienteArchivoFtpDto.values().forEach(archivoFtpDto -> borrarArchivosTemporales(archivoFtpDto.getPdf(), archivoFtpDto.getNomFichZip()));
      
  }

  /**
   * Construye el modelo de datos para la acción de negocio asociada al informe
   * MJU.
   *
   * @param idUsu         Identificador del usuario que ejecuta la acción.
   * @param idPro         Identificador de la fase/proceso asociado al expediente.
   * @param archivoFtpDto DTO con los datos del archivo FTP enlazado al
   *                      expediente.
   * @return Un mapa con claves necesarias por la fachada de expedientes para
   *         ejecutar la acción.
   */
  private static Map<String, Object> getModelMap(Integer idUsu, short idPro, ArchivoFtpDto archivoFtpDto) {
    Map<String, Object> modelMap = new HashMap<>();
    modelMap.put("idExp", archivoFtpDto.getIdExpediente());
    modelMap.put("idUsu", idUsu);
    modelMap.put("idPro", idPro);
    modelMap.put("flgProceso", true);
    modelMap.put("tipoInforme", Constantes.TiposInforme.TIPO_INFORME_MJU);
    modelMap.put("sentido", archivoFtpDto.getSentidoInforme());
    modelMap.put("idExpInf", archivoFtpDto.getIdExpInfMju());
    modelMap.put("archivoFtpDto", archivoFtpDto);
    return modelMap;
  }

  private void rellenaDatosPdfs(List<? extends ZipEntry> listaArchivosPdf,
      Map<String, ArchivoFtpDto> listaCodExpedienteArchivoFtpDto, ZipFile zip) {
    for (ZipEntry entry : listaArchivosPdf) {
      try {
        String currentEntry = entry.getName();
        String codExpediente = currentEntry.toUpperCase().replace(".PDF", "");
        int anyoIndex = codExpediente.length() - ANYO_LENGTH;
        String cod = codExpediente.substring(0, anyoIndex);
        String anyo = codExpediente.substring(anyoIndex);
        codExpediente = cod + "/" + anyo;
        ArchivoFtpDto archivoFtpDto = new ArchivoFtpDto();
        archivoFtpDto.setCodExpediente(codExpediente);
        archivoFtpDto.setNomFichZip(zip.getName().toUpperCase());
        InputStream is = zip.getInputStream(entry);
        File pdf = new File(currentEntry);
        FileUtils.copyInputStreamToFile(is, pdf);
        archivoFtpDto.setPdf(pdf);
        BasicFileAttributes attr = Files.readAttributes(pdf.toPath(), BasicFileAttributes.class,
            LinkOption.NOFOLLOW_LINKS);
        FileTime time = attr.creationTime();
        Date fechaCreacionMju = new Date(time.toMillis());
        archivoFtpDto.setFechaCreacionPdfMju(fechaCreacionMju);
        listaCodExpedienteArchivoFtpDto.put(codExpediente, archivoFtpDto);
      } catch (IOException e) {
        guardaErrorJob(e, descripcion, jobName);
      }
    }
  }

  private void rellenaDatosRpr(List<? extends ZipEntry> listaArchivosRpr,
      Map<String, ArchivoFtpDto> listaCodExpedienteArchivoFtpDto, ZipFile zip) {
    for (ZipEntry entry : listaArchivosRpr) {
      try {
    	String folder = zip.getName().replace(".zip", "");
        String currentEntry = entry.getName().replace(folder +"/", "");
        InputStream is = zip.getInputStream(entry);
        File fileRpr = new File(currentEntry);
        FileUtils.writeByteArrayToFile(fileRpr, is.readAllBytes());
        List<String> lineas = Files.readAllLines(fileRpr.toPath());
        for (String linea : lineas) {
          String[] partes = linea.split(";");
          if (partes.length > 0) {
            String codExpediente = partes[0];
            int anyoIndex = codExpediente.length() - ANYO_LENGTH;
            String cod = codExpediente.substring(0, anyoIndex);
            String anyo = codExpediente.substring(anyoIndex);
            codExpediente = cod + "/" + anyo;
            if (listaCodExpedienteArchivoFtpDto.containsKey(codExpediente)) {
              ArchivoFtpDto archivoFtpDto = listaCodExpedienteArchivoFtpDto.get(codExpediente);
              BasicFileAttributes attr;
              attr = Files.readAttributes(fileRpr.toPath(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
              FileTime time = attr.creationTime();
              Date fechaCreacionMju = new Date(time.toMillis());
              archivoFtpDto.setFechaCreacionRprMju(fechaCreacionMju);
              listaCodExpedienteArchivoFtpDto.replace(codExpediente,
                  calculaLinea(partes, archivoFtpDto, currentEntry, zip.getName().toUpperCase(), codExpediente));
            } else {
              ArchivoFtpDto archivoFtpDto = new ArchivoFtpDto();
              listaCodExpedienteArchivoFtpDto.put(codExpediente,
                  calculaLinea(partes, archivoFtpDto, currentEntry, zip.getName().toUpperCase(), codExpediente));
            }
          }
        }
        is.close();
        Files.deleteIfExists(fileRpr.toPath());
      } catch (IOException e) {
        guardaErrorJob(e, descripcion, jobName);
      }
    }
  }

  private ArchivoFtpDto calculaLinea(String[] partes, ArchivoFtpDto archivoFtpDto, String nomFichRespuesta,
      String nomZip, String codExpediente) {
    archivoFtpDto.setCodExpediente(codExpediente);
    archivoFtpDto.setNomFichRespuesta(nomFichRespuesta);
    archivoFtpDto.setNomFichZip(nomZip);
    String codigoRespuesta = partes[1];
    LOG.info("Código respuesta: {}", codigoRespuesta);
    archivoFtpDto.setCodRespuesta(codigoRespuesta);
    String valor = "";
    if (partes.length > ESP_RES_PART) {
      valor = partes[ESP_RES_PART];
      valor = valor.replace("ESP_RES", "");
      valor = valor.trim();
      if (!valor.isEmpty()) {
        archivoFtpDto.setEspRes(valor.trim());
      }
      LOG.info("Respuesta: {}", partes[ESP_RES_PART]);
    }
    if (partes.length > ES_MOTIVO_RECHAZO_PART) {
      valor = partes[ES_MOTIVO_RECHAZO_PART];
      valor = valor.replace("ES_MOTIVO_RECHAZO", "");
      valor = valor.trim();
      if (!valor.isEmpty()) {
        archivoFtpDto.setEspMotRechazo(valor);
      }
    }
    if (partes.length > EU_RES_PART) {
      valor = partes[EU_RES_PART];
      valor = valor.replace("EU_RES", "");
      valor = valor.trim();
      if (!valor.isEmpty()) {
        archivoFtpDto.setEuRes(valor);
      }
    }
    if (partes.length > EU_MOTIVO_RECHAZO_PART) {
      valor = partes[EU_MOTIVO_RECHAZO_PART];
      valor = valor.replace("EU_MOTIVO_RECHAZO", "");
      valor = valor.trim();
      if (!valor.isEmpty()) {
        archivoFtpDto.setEuMotRechazo(valor);
      }
    }
    String sentidoInforme = null;
    switch (codigoRespuesta) {
      case "A":
        sentidoInforme = Constantes.SentidoInformeMju.SENTIDO_INF_ANTECEDENTES;
        break;
      case "R":
        sentidoInforme = Constantes.SentidoInformeMju.SENTIDO_INF_RECHAZADO;
        break;
      case "N":
        sentidoInforme = Constantes.SentidoInformeMju.SENTIDO_INF_NO_ANTECEDENTES;
        break;
      case "P":
        sentidoInforme = Constantes.SentidoInformeMju.SENTIDO_INF_PENDIENTE;
        break;
      default:

    }
    archivoFtpDto.setSentidoInforme(sentidoInforme);
    return archivoFtpDto;
  }

  private void borrarArchivosTemporales(File archivo, String nombreTemp) {
    try {
      if (archivo != null && archivo.exists()) {
        Files.deleteIfExists(archivo.toPath());
        LOG.info("Archivo temporal borrado: {}", archivo.getAbsolutePath());
      }

      if (nombreTemp != null && !nombreTemp.isBlank()) {

    	String userDir = System.getProperty("user.dir");
        File dirUser = new File(userDir);
        borrarPorNombreEnDirectorio(dirUser, nombreTemp);
      }

    } catch (IOException e) {
      LOG.error("Error borrando temporales: {}", e.getMessage(), e);
    }
  }

  private void borrarPorNombreEnDirectorio(File directorio, String nombre) throws IOException {
    if (directorio == null || !directorio.exists() || !directorio.isDirectory()) {
      return;
    }

    for (File f : directorio.listFiles()) {
      if (f.getName().equals(nombre.toLowerCase())) {
        Files.deleteIfExists(f.toPath());
        LOG.info("Archivo temporal borrado: {}", f.getAbsolutePath());
      }
    }
  }
  
  /**
   * Valida y procesa los ficheros de error disponibles en el repositorio NFS de
   * errores.
   *
   * @param idUsu Identificador del usuario asociado al procesamiento.
   */
  public void comprobarArchivosError(Integer idUsu) {
    List<DataSource> listado = expedientesFacade.obtenerTodosLosArchivos(nfsError);
    for (DataSource dataSource : listado) {
      try {
        File file = copiarYEliminar(dataSource, null, nfsError, nfsEnTratamiento);
        procesarFicheroZip(idUsu, file);
      } catch (IOException e) {
        LOG.error("Se ha producido un error tratando el fichero de la carpeta de errores : {}", e.getMessage());
      }
    }
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_INT_MJU_REC";
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }
  
 private File copiarYEliminar(DataSource dataSource, File fileToDelete, String nfsOrigen, String nfsDestino) throws IOException {
  boolean copiado = false;
    if (fileToDelete == null && dataSource != null){
      File file = null; 
      try (InputStream in = dataSource.getInputStream()) {
          file = new File(dataSource.getName());
          byte[] content = in.readAllBytes();
          Files.write(file.toPath(), content);
          copiado = expedientesFacade.copyArchivoFtpNFS(dataSource.getName(), content, nfsDestino);
      }catch (IOException e){
        LOG.error("Error movimiento el archivo {} - Origen {} - Destino {}", dataSource.getName(), nfsOrigen, nfsDestino);
        throw e;
      }finally{
        if (copiado){ 
          LOG.debug("Cerrando inputstream y eliminado archivo del origen.");
          expedientesFacade.borrarArchivoFtpNFS(dataSource.getName(), nfsOrigen);
        }
      }
      return file;
    }else if (fileToDelete != null && dataSource == null){
      try{
        copiado = expedientesFacade.copyArchivoFtpNFS(fileToDelete.getName(), FileUtils.readFileToByteArray(fileToDelete), nfsDestino);
      }catch (IOException e){
        LOG.error("Error movimiento el archivo {} - Origen {} - Destino {}", fileToDelete.getName(), nfsOrigen, nfsDestino);
        throw e;
      }finally{
        if (copiado){ 
          LOG.debug("Cerrando inputstream y eliminado archivo del origen.");
          expedientesFacade.borrarArchivoFtpNFS(fileToDelete.getName(), nfsOrigen);
        }
      }
      return null;
    }
    return null;
  }

}