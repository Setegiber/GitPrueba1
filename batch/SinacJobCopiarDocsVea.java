package es.mjusticia.sinac.core.batch;

/*-
 * #%L
 * sinac-core
 * %%
 * Copyright (C) 2023 - 2025 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.DocumentoToSaveDto;
import es.mjusticia.sinac.core.security.impl.NoSessionBean;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.NFSManager;
import es.mjusticia.sinac.core.utils.UtilError;

/**
 * Job para copiar los documentos de VEA a SINAC de las solicitudes iniciadas.
 */
@Component
public class SinacJobCopiarDocsVea extends SinacJob<String> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobCopiarDocsVea.class);

  private static final String NOMBRE_CLASE = "SinacJobCopiarDocsVea";

  @Value("${sinac.copiarDocsVea.fecha}") // formato esperado: yyyy-MM-dd, si vacía => hoy
  private String fechaProp;

  @Value("${sinac.copiarDocsVea.veaRoot}")
  private String veaRoot;

  @Value("#{'${sinac.copiarDocsVea.codTramites:DIC}'.split(',')}")
  private List<String> codTramites;

  private String descripcion;
  private String jobName;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private NFSManager nfsManager;

  @Value("${nfs.ruta}")
  private String nfsPath;

  @Value("${nfs.ruta.solicitudes:DIC}")
  private String nfsPathDocumentosSolicitudes;

  @Value("${sinac.copiarDocsVea.nombreDirAdj:pdfSolicitud}")
  private String nombreDirAdjuntos;

  @Value("${sinac.copiarDocsVea.nombreDocJus:02_Pdffirmadoministerio}")
  private String nombreDocJustificante;

  private int numDocsACopiar = 0;
  private int numDocsCopiadosOk = 0;

  public SinacJobCopiarDocsVea() {
    super();
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  /**
   * Recupera la lista de ficheros justificantes generados en la fecha indicada
   * por la propiedad (o en la fecha actual si no se indica).
   *
   * Devuelve la lista de rutas absolutas (String) para su posterior
   * procesamiento.
   */
  @Override
  public List<String> recuperarItems(Map<String, Object> contextData) {

    final String nombreClaseMetodo = NOMBRE_CLASE + "." + Thread.currentThread().getStackTrace()[1].getMethodName()
        + " -";
    LOG.info(Constantes.LOG_INIT, nombreClaseMetodo);

    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");

    // Cargar fecha sobre la que realizar el proceso (valor propiedad o actual)
    LocalDate fechaObjetivoTmp;
    if (fechaProp == null || fechaProp.isBlank()) {
      fechaObjetivoTmp = LocalDate.now();
      LOG.info("{} No se proporcionó propiedad de fecha; usando fecha actual: {}", nombreClaseMetodo, fechaObjetivoTmp);
    } else {
      try {
        fechaObjetivoTmp = LocalDate.parse(fechaProp, DateTimeFormatter.ISO_LOCAL_DATE);
      } catch (DateTimeParseException e) {
        LOG.warn("{} Formato de fecha inválido en propiedad sinac.copiarDocsVea.fecha='{}'. Se usa fecha actual.",
            nombreClaseMetodo, fechaProp, e);
        fechaObjetivoTmp = LocalDate.now();
      }
    }
    final LocalDate fechaObjetivo = fechaObjetivoTmp;

    final String anho = String.valueOf(fechaObjetivo.getYear());
    
    boolean dosMeses = false;
    String mesAnterior = null;
    if (fechaObjetivo.getDayOfMonth() == 1) {
    	dosMeses = true;
    	mesAnterior = String.format("%02d", fechaObjetivo.getMonthValue() - 1);
    }
    String mes = String.format("%02d", fechaObjetivo.getMonthValue());
    
    List<String> encontrados = new ArrayList<>();

    // Obtiene justificantes de la lista de tramites parametrizada.
    for (String tramite : codTramites) {
      Path dirRaizMesDia = Paths.get(veaRoot, tramite, anho, mes);
      LOG.info("{} Buscando en directorio: {}", nombreClaseMetodo, dirRaizMesDia);
      if (dosMeses) {
    	  mes = mesAnterior;
      }
  	  Path dirRaizMesDiaAnt = Paths.get(veaRoot, tramite, anho, mes);
      LOG.info("{} Buscando en directorio: {}", nombreClaseMetodo, dirRaizMesDiaAnt);
      
      if (nfsManager.existeDir(dirRaizMesDia.toString())) {
        List<String> encontradosParaTramite = nfsManager.obtenerRutaDocsPorNombreFecha(dirRaizMesDia.toString(),
            nombreDocJustificante, fechaObjetivo);
        LOG.info("{} Encontrados {} documentos para el tramite {} en la fecha {}", nombreClaseMetodo,
            encontradosParaTramite.size(), tramite, fechaObjetivo);
  
        encontrados.addAll(encontradosParaTramite);
      } else {
        LOG.error("{} Directorio origen no existe: {}", nombreClaseMetodo, dirRaizMesDia);
      }
      if (nfsManager.existeDir(dirRaizMesDiaAnt.toString()) && dosMeses) {
          List<String> encontradosParaTramiteAnt = nfsManager.obtenerRutaDocsPorNombreFecha(dirRaizMesDiaAnt.toString(),
              nombreDocJustificante, fechaObjetivo);
          LOG.info("{} Encontrados {} documentos para el tramite {} en la fecha {}", nombreClaseMetodo,
              encontradosParaTramiteAnt.size(), tramite, fechaObjetivo);
          
          encontrados.addAll(encontradosParaTramiteAnt);
        } 


    }

    encontrados.forEach(p -> LOG.info("{} Documento de solicitud encontrado: {}", nombreClaseMetodo, p));

    LOG.info(Constantes.LOG_END, nombreClaseMetodo);
    return encontrados;
  }

  @Override
  public void procesarItem(String item, Map<String, Object> contextData) {
    final String nombreClaseMetodo = NOMBRE_CLASE + "." + Thread.currentThread().getStackTrace()[1].getMethodName()
        + " -";
    LOG.info(Constantes.LOG_INIT, nombreClaseMetodo);

    LOG.info("{} Documento de solicitud a procesar: {}", nombreClaseMetodo, item);

    Path rutaSolicitud = Paths.get(item).getParent().getParent();

    if (rutaSolicitud == null || !nfsManager.existeDir(rutaSolicitud.toString())) {
      LOG.error("{} No se ha podido determinar la ruta '{}}' o no es válida para el documento de solicitud: {}",
          nombreClaseMetodo, nombreDirAdjuntos, item);
      LOG.info(Constantes.LOG_END, nombreClaseMetodo);
      return;
    }

    LOG.info("{} Ruta '{}' determinada: {}", nombreClaseMetodo, nombreDirAdjuntos,
        rutaSolicitud.toAbsolutePath().toString());

    int nameCount = rutaSolicitud.getNameCount();
    if (nameCount < 5) { // Comprueba número directorios suficiente
      LOG.error("{} Ruta de Adjuntos no tiene la profundidad esperada: {}", nombreClaseMetodo, rutaSolicitud);
      LOG.info(Constantes.LOG_END, nombreClaseMetodo);
      return;
    }

    // Obtiene los documentos PDF asociados a la solicitud
    String adjuntosPath = rutaSolicitud.toString();
    List<DocumentoToSaveDto> todosLosDocs = new ArrayList<>();
    todosLosDocs.addAll(nfsManager.obtenerDocsDeDirectorio(adjuntosPath));
    numDocsACopiar = numDocsACopiar + todosLosDocs.size();

    LOG.info("{} Documentos asociados encontrados en '{}': {}", nombreClaseMetodo, adjuntosPath, todosLosDocs.size());
    if (todosLosDocs.isEmpty()) {
      LOG.error("{} Ningún PDF encontrado asociado a la ruta {}", nombreClaseMetodo, adjuntosPath);
    } else {
      todosLosDocs.forEach(d -> LOG.info("{} -> {}", nombreClaseMetodo, d.getNombre()));
    }

    for (DocumentoToSaveDto doc : todosLosDocs) {
      String rutaDocVea = doc.getRutaNFS();
      String nombreDoc = doc.getNombre();
      boolean copiado = nfsManager.copiarDocNfsVeaASinac(rutaDocVea, nombreDoc);
      if (copiado) {
        numDocsCopiadosOk++;
        LOG.info("{} Documento copiado correctamente: {}/{}", nombreClaseMetodo, rutaDocVea, nombreDoc);
      } else {
        LOG.error("Error al copiar el documento: {}/{}", nombreClaseMetodo, rutaDocVea, nombreDoc);
        addError();
        guardaErrorJob(new Exception(nombreClaseMetodo + " Error al copiar " + nombreDoc), descripcion, jobName);
      }
    }

    LOG.info(Constantes.LOG_END, nombreClaseMetodo);
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_SEDE_DOCS_VEA";
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

  @Override
  public void postEjecucion(Map<String, Object> contextData, List<String> items, NoSessionBean noSessionBean)
      throws JobExecutionException {
    final String nombreClaseMetodo = NOMBRE_CLASE + "." + Thread.currentThread().getStackTrace()[1].getMethodName()
        + " -";
    LOG.info("{} Copiados correctamente {} de {} documentos", nombreClaseMetodo, numDocsCopiadosOk, numDocsACopiar);
  }

}
