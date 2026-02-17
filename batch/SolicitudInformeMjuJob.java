package es.mjusticia.sinac.core.batch;

import java.io.IOException;
import java.io.InputStream;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.sshtools.client.SshClient;
import com.sshtools.client.sftp.SftpClient;
import com.sshtools.client.sftp.TransferCancelledException;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.sftp.SftpStatusException;
import com.sshtools.common.ssh.SshException;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.impl.ExpedientesFacadeImpl;
import es.mjusticia.sinac.core.business.service.SshClienteService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.DatosSolicitudInformeMjuDto;
import es.mjusticia.sinac.core.security.impl.NoSessionBean;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.NFSManager;
import es.mjusticia.sinac.core.utils.UtilError;
import jakarta.activation.DataSource;

/**
 * Job para enviar solicitudes de infomes al MJU
 */
@Component
public class SolicitudInformeMjuJob extends SinacJob<BigInteger> {

  private static final Logger LOG = LoggerFactory.getLogger(SolicitudInformeMjuJob.class);

  @Value("${sinac.quartz.solicitudInformeMjuJob.maxItemFile}")
  private Integer maxItemFile;
  @Value("${sinac.quartz.solicitudInformeMjuJob.maxItem}")
  private Integer maxItem;
  @Value("${sinac.ftp.solicitudInformeMjuJob.host}")
  private String host;
  @Value("${sinac.ftp.solicitudInformeMjuJob.port}")
  private Integer port;
  @Value("${sinac.ftp.solicitudInformeMjuJob.pathEntrada}")
  private String pathEntrada;
  @Value("${sinac.ftp.solicitudInformeMjuJob.pathSalida}")
  private String pathSalida;
  @Value("${sinac.ftp.solicitudInformeMjuJob.user}")
  private String user;
  @Value("${sinac.ftp.solicitudInformeMjuJob.pass}")
  private String pass;
  @Value("${sinac.quartz.solicitudInformeMjuJob.nfs.solicitados}")
  private String nfsSolicitados;

  private Integer idUsu;
  private String descripcion;
  private String jobName;

  private int numLineas = 0;
  private int numArchivo = 0;

  private List<DataSource> archivosGuardadosNFS = new ArrayList<>();
  private DataSource actualFile = null;
  private int cantidadArchivos = 0;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private SshClienteService sshClienteService;

  @Autowired
  private ExpedientesFacade expedientesFacade;
  

  @Autowired
  private NFSManager nfsManager;

  public SolicitudInformeMjuJob() {
    super();
  }

  @Override
  public List<BigInteger> recuperarItems(Map<String, Object> contextData) {
    LOG.info("solicitudInformeMjuJob.recuperarItems - Init");
    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    idUsu = getUsuariosService().getUsuarioByUsuarioJusticia(getUsuarioJusticia()).getIdUsu();
    List<BigInteger> listado = expedientesFacade.getIdsExpedienteByCodEstadoCodTipoInforme(
        Constantes.EstadosInforme.ESTADO_INFORME_PENDIENTE, Constantes.TiposInforme.TIPO_INFORME_MJU, null);
    if (listado.size() > maxItem) {
      listado = listado.subList(0, maxItem);
    }
    LOG.info("solicitudInformeMjuJob.recuperarItems - End");
    return listado;
  }

  @Override
  public void procesarItem(BigInteger item, Map<String, Object> contextData) {
    LOG.info("solicitudInformeMjuJob.procesarItems solicitud idExp={} - Init", item);
    try {
      if (actualFile == null)
        obtenerArchivoActual();
      if (actualFile != null && numLineas == maxItemFile) {
        archivosGuardadosNFS.add(actualFile);
        crearArchivoActual();
      }
      if (actualFile != null) {
        DatosSolicitudInformeMjuDto datosSolicitud = ejecutarAccionExpediente(idUsu, expedientesFacade, item,
            actualFile.getName());
        addNuevaSolicitud(datosSolicitud);
      }
    } catch (SinacException | SQLException | ParserConfigurationException | SAXException | IOException e) {
      LOG.error("solicitudInformeMjuJob.procesarItem - Error: ", e);
      try {
        addError();
        guardaErrorJob(e, descripcion, jobName);
      } catch (Exception e1) {
        LOG.error("Error guardando el error del job en la tabla TRI_ERRORES: {}", e1.getMessage());
      }
    }

    LOG.info("solicitudInformeMjuJob.procesarItems solicitud idExp={} - End", item);
  }

  private void obtenerArchivoActual() throws SinacException {
    Calendar fechaInforme = Calendar.getInstance();
    String pattern = "yyyyMMdd";
    SimpleDateFormat df = new SimpleDateFormat(pattern);
    String fechaInformeTexto = df.format(fechaInforme.getTime());
    Pattern patron = Pattern.compile("(?=.*28079AP002_" + fechaInformeTexto + ")(?=.*.PEN)");
    try {
      List<DataSource> archivosSolicitados = expedientesFacade.obtenerTodosLosArchivos(nfsSolicitados);

      Optional<DataSource> op = archivosSolicitados.stream().filter(a -> patron.matcher(a.getName()).find())
          .map(a -> new AbstractMap.SimpleEntry<>(a, extraerNumeroFinal(a.getName()))).filter(e -> e.getValue() >= 0)
          .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);

      if (op.isPresent()) {
        numArchivo = extraerNumeroFinal(op.get().getName());
      }
      crearArchivoActual();


    } catch (SinacException e) {
      LOG.error("Se ha producido un error buscando o creando el archivo .PEN en el NFS");
      crearArchivoActual();
      throw e;
    }
  }

	
  private static final Pattern NOMBRE_FICHERO_PATTERN =
		    Pattern.compile("^[^_]+_(\\d+)(?:\\.[^.]+)?$");
  
	private int extraerNumeroFinal(String nombre) {
		if (nombre == null)
			return -1;
		Matcher matcher = NOMBRE_FICHERO_PATTERN.matcher(nombre);
		if (matcher.matches()) {
		    return Integer.parseInt(matcher.group(1));
		}

		return -1;
	}


  private void crearArchivoActual() {
    Calendar fechaInforme = Calendar.getInstance();
    String pattern = "yyyyMMdd";
    SimpleDateFormat df = new SimpleDateFormat(pattern);
    String fechaInformeTexto = df.format(fechaInforme.getTime());
    numArchivo++;
    numLineas = 0;
    String nombreArchivo = "28079AP002_" + fechaInformeTexto + "_" + numArchivo + ".PEN";
    boolean copiadoNFS = expedientesFacade.copyArchivoFtpNFS(nombreArchivo, new byte[0], nfsSolicitados);
    if (!copiadoNFS) {
      LOG.error("No se ha podido crear un nuevo archivo con nombre {} en la ruta {} del NFS.", nombreArchivo,
          nfsSolicitados);
    } else {
      actualFile = expedientesFacade.obtenerArchivoByNombre(nombreArchivo, nfsSolicitados);
      expedientesFacade.guardaExpedienteInformesMjuFicheros(actualFile.getName(),
          Constantes.EstadosInforme.ESTADO_INFORME_SOLICITADO);
      cantidadArchivos++;
    }
  }


  private void addNuevaSolicitud(DatosSolicitudInformeMjuDto datosSolicitud) throws SinacException, IOException {
    String solicitud = obtenerNuevaLinea(datosSolicitud);
    addLinea(solicitud);
    numLineas++;
  }

  private void addLinea(String linea) throws SinacException, IOException {
    String nombreArchivo = actualFile.getName();
    if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
      LOG.error("No está presente el archivoActual.");
    }

    String rutaTemp = nfsManager.getTemporalPath("solicitud_mju_.pen");
    Path temp = Path.of(rutaTemp); // 

    try {
      try (InputStream actual = actualFile.getInputStream()) {
        if (actual != null) {
          Files.copy(actual, temp, StandardCopyOption.REPLACE_EXISTING);
        }
      } catch (IOException e) {
        LOG.warn("No se pudo leer el DataSource remoto {}, continuamos con temp vacío: {}", nombreArchivo,
            e.getMessage());
      }

      byte[] bytesLinea = (linea + System.lineSeparator()).getBytes(StandardCharsets.UTF_8);
      Files.write(temp, bytesLinea, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

      byte[] contenido = Files.readAllBytes(temp);
      boolean copiado = expedientesFacade.copyArchivoFtpNFS(nombreArchivo, contenido, nfsSolicitados);
      if (!copiado) {
        String msg = String.format("No se ha podido subir %s al NFS %s", nombreArchivo, nfsSolicitados);
        LOG.error(msg);
      }
      actualFile = expedientesFacade.obtenerArchivoByNombre(nombreArchivo, nfsSolicitados);
    } finally {
      try {
        Files.deleteIfExists(temp);
      } catch (IOException e) {
        LOG.warn("No se pudo borrar el temporal {}: {}", temp, e.getMessage());
      }
    }
  }

  /**
   * Devuelve la información necesaria del expediente para poder rellenar los
   * datos de solicitud del Informe y ejecuta la acción asociada para pasar el
   * Informe a estado solicitado
   */
  public DatosSolicitudInformeMjuDto ejecutarAccionExpediente(Integer idUsuario, ExpedientesFacade expedientesFacade,
      BigInteger idExpediente, String fileName)
      throws SinacException, SQLException, ParserConfigurationException, SAXException, IOException {
    DatosSolicitudInformeMjuDto datosSolicitudInformeMjuDto = expedientesFacade
        .obtenerDatosSolicitudInformeMju(idExpediente);
    ejecutarAccionExpediente(idUsuario, idExpediente, expedientesFacade, fileName,
        datosSolicitudInformeMjuDto.getCodigoProcedimiento());
    return datosSolicitudInformeMjuDto;
  }

  private void ejecutarAccionExpediente(Integer idUsuario, BigInteger idExp, ExpedientesFacade expedientesFacade,
      String nombreArchivo, String codigoProcedimiento)
      throws SinacException, SQLException, ParserConfigurationException, SAXException, IOException {
    Long idProFasTraOpeAcc = expedientesFacade.getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(
        codigoProcedimiento, "INF", "IMJU", "SMJU");
    Map<String, Object> modelMap = new HashMap<>();
    modelMap.put("idExp", idExp);
    modelMap.put("idUsu", idUsuario);
    modelMap.put("flgProceso", true);
    modelMap.put("tipoInformeMju", Constantes.TiposInforme.TIPO_INFORME_MJU);
    modelMap.put("estadoInformeMju", Constantes.EstadosInforme.ESTADO_INFORME_SOLICITADO);
    modelMap.put("nombreArchivo", nombreArchivo);
    expedientesFacade.ejecutarAccion(idProFasTraOpeAcc, modelMap);
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_INT_MJU_ALTA";
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  private String obtenerNuevaLinea(DatosSolicitudInformeMjuDto datosSolicitudInformeMjuDto) {
    return emptyOnNull(datosSolicitudInformeMjuDto.getPrimerApellido()) + "|"
        + emptyOnNull(datosSolicitudInformeMjuDto.getSegundoApellido()) + "|"
        + emptyOnNull(datosSolicitudInformeMjuDto.getNombre()) + "|"
        + emptyOnNullFecha(datosSolicitudInformeMjuDto.getFechaNacimiento()) + "|"
        + emptyOnNull(datosSolicitudInformeMjuDto.getNombrePadre()) + "|"
        + emptyOnNull(datosSolicitudInformeMjuDto.getNombreMadre()) + "|"
        + emptyOnNull(datosSolicitudInformeMjuDto.getPaisNacimiento()) + "|"
        + emptyOnNullDocumento(datosSolicitudInformeMjuDto.getDniPasaporte()) + "|"
        + emptyOnNullDocumento(datosSolicitudInformeMjuDto.getNie()) + "|"
        + emptyOnNull(datosSolicitudInformeMjuDto.getDomicilio()) + "|"
        + emptyOnNullNumeros(datosSolicitudInformeMjuDto.getProvinciaNacimiento()) + "|"
        + emptyOnNull(datosSolicitudInformeMjuDto.getLocalidadNacimiento()) + "|"
        + emptyOnNullDocumento(datosSolicitudInformeMjuDto.getReferencia()) + "|"
        + emptyOnNull(datosSolicitudInformeMjuDto.getPaisNacionalidad());
  }

  private String emptyOnNullNumeros(String valor) {
    if (valor == null) {
      return "";
    }
    StringBuilder resultado = new StringBuilder();
    for (Character caracter : valor.toCharArray()) {
      if (Character.isDigit(caracter)) {
        resultado.append(caracter);
      }
    }
    return resultado.toString();
  }

  private String emptyOnNullDocumento(String valor) {
    if (valor == null) {
      return "";
    }
    StringBuilder resultado = new StringBuilder();
    for (Character caracter : valor.toCharArray()) {
      if (Character.isDigit(caracter) || Character.isLetter(caracter) || caracter.equals(' ')) {
        resultado.append(caracter);
      }
    }
    return resultado.toString();
  }

  private String emptyOnNullFecha(String valor) {
    if (valor == null) {
      return "";
    }
    StringBuilder resultado = new StringBuilder();
    for (Character caracter : valor.toCharArray()) {
      if (Character.isDigit(caracter) || caracter.equals('/')) {
        resultado.append(caracter);
      }
    }
    return resultado.toString();
  }

  private String emptyOnNull(String valor) {
    if (valor == null) {
      return "";
    }
    StringBuilder resultado = new StringBuilder();
    for (Character caracter : valor.toCharArray()) {
      if (Character.isAlphabetic(caracter) || caracter.equals('/') || caracter.equals('-') || caracter.equals(' ')
          || caracter.equals('\'')) {
        resultado.append(caracter);
      }
    }
    return resultado.toString();
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    listaNombresClaseGuardarError.add(ExpedientesFacadeImpl.class.getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

  @Override
  public void postEjecucion(Map<String, Object> contextData, List<BigInteger> items, NoSessionBean noSessionBean)
      throws JobExecutionException {
    if (items.isEmpty())
      return;
    SshClient sshClient = null;
    SftpClient sftpClient = null;

    try {
      sshClient = sshClienteService.loginSsh(host, port, user, pass);
      sftpClient = sshClienteService.createSftpClient(sshClient);
      sftpClient.cd(pathEntrada);
      subirArchivosSFTP(sftpClient);
    } catch (SinacException | IOException | SshException | PermissionDeniedException | SftpStatusException e) {
      try {
        addError();
        guardaErrorJob(e, descripcion, jobName);
      } catch (Exception e1) {
        LOG.error("Error guardando el error del job en la tabla TRI_ERRORES: {}", e1.getMessage());
      }
    } finally {
      desconectarSftp(sftpClient, sshClient);
      limpiarDatos();
    }
  }

  private void subirArchivosSFTP(SftpClient sftpClient) {
    int success = 0;
    int failures = 0;
    if ((archivosGuardadosNFS.isEmpty() && actualFile != null) || archivosGuardadosNFS.size() != cantidadArchivos) {
      archivosGuardadosNFS.add(actualFile);
    }

    for (DataSource ds : archivosGuardadosNFS) {

      if (ds == null) {
        LOG.warn("DataSource nulo en archivosGuardadosNFS, se omite");
        failures++;
        continue;
      }

      String nombreRemoto = ds.getName();

      expedientesFacade.actualizarExpedienteInformesMjuFicherosDatos(nombreRemoto);

      if (nombreRemoto == null || nombreRemoto.trim().isEmpty()) {
        LOG.warn("Se omite DataSource sin nombre válido");
        failures++;
        continue;
      }

      try {
        try (InputStream probe = ds.getInputStream()) {
          if (probe == null) {
            LOG.warn("DataSource {} devuelve InputStream nulo; se omite", nombreRemoto);
            failures++;
            continue;
          }
          int first = probe.read();
          if (first == -1) {
            LOG.warn("DataSource {} está vacío; se omite", nombreRemoto);
            failures++;
            continue;
          }
        }
        try (InputStream in = ds.getInputStream()) {
          sftpClient.put(in, nombreRemoto);
          LOG.info("Archivo subido correctamente al SFTP: {}", nombreRemoto);
          success++;
        }
      } catch (TransferCancelledException | SftpStatusException e) {
        LOG.error("Error SFTP subiendo {}: {}", nombreRemoto, e.getMessage(), e);
        failures++;
      } catch (IOException e) {
        LOG.error("IO error leyendo DataSource {}: {}", nombreRemoto, e.getMessage(), e);
        failures++;
      } catch (Exception e) {
        LOG.error("Error inesperado subiendo {}: {}", nombreRemoto, e.getMessage(), e);
        failures++;
      }
    }

    LOG.info("Subida a SFTP completada. Éxitos: {}, Fallos: {}", success, failures);
  }

  private void desconectarSftp(SftpClient sftpClient, SshClient sshClient) {
    try {
      if (sshClient != null && sftpClient != null) {
        sftpClient.quit();
        sshClient.disconnect();
      }
    } catch (SshException e) {
      LOG.error("Error al desconectar SFTP {}", e.getMessage());
    }

  }

  private void limpiarDatos() {
    numLineas = 0;
    numArchivo = 0;
    archivosGuardadosNFS = new ArrayList<>();
    actualFile = null;
    cantidadArchivos = 0;
  }
}
