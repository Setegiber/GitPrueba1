package es.mjusticia.sinac.core.batch;

import java.util.ArrayList;
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
import java.util.List;
import java.util.Map;
import org.apache.cxf.common.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.impl.ExpedientesFacadeImpl;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.business.service.impl.CatalogosServiceImpl;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.utils.UtilError;

/**
 * Job para reintentar la subida al Gestor Documental de aquellos documentos que
 * no tengan código gestor documental.
 *
 * @author NTT Data.
 */
@Component
public class SinacJobReintentoDocumentoGd extends SinacJob<Object> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacVencimientoPlazosJob.class);

  private String jobName;

  private String jobDescription;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private ExpedientesService expedientesService;

  @Autowired
  private UsuariosService usuariosService;

  public SinacJobReintentoDocumentoGd() {
    super();
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_AUT_REINTENTO_DOC_GD";
  }

  @Override
  public List<Object> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobReintentoDocumentoGd.recuperarItems - Init");

    jobName = (String) contextData.get("jobName");
    jobDescription = (String) contextData.get("descripcion");

    // recuperamos la lista de exps que tienen codGD nulo
    List<ExpedienteDto> expedientesDtoList = expedientesService.getListaExpedientesCodGDNull();
    LOG.info("Se han recuperado {} expedientes sin codGD.", expedientesDtoList.size());

    // recuperamos la lista de expDocs que tienen codGD nulo
    List<ExpedienteDocumentoDto> expedienteDocumentosDtoList = expedientesService
        .getListaExpedienteDocumentosCodGDNull();
    LOG.info("Se han recuperado {} documentos sin codGD.", expedienteDocumentosDtoList.size());

    if (CollectionUtils.isEmpty(expedientesDtoList)) {
      LOG.info(
          "SinacJobReintentoDocumentoGd.recuperarItems - No se han encontrado Expedientes sin código gestor documental .");
    } else if (CollectionUtils.isEmpty(expedienteDocumentosDtoList)) {
      LOG.info(
          "SinacJobReintentoDocumentoGd.recuperarItems - No se han encontrado Documentos sin código gestor documental .");
    }

    List<Object> listaObjetos = new ArrayList<Object>();
    listaObjetos.addAll(expedientesDtoList);
    listaObjetos.addAll(expedienteDocumentosDtoList);

    LOG.info("SinacJobReintentoDocumentoGd.recuperarItems - End");

    // la retornamos
    return listaObjetos;
  }

  @Override
  public void procesarItem(Object item, Map<String, Object> contextData) {
    LOG.info("SinacJobReintentoDocumentoGd.procesarItem - Init");
    LOG.info("Se va a intentar subir al GD un {} ", item.getClass());
    // si el item es un expediente sin cod gd
    if (item instanceof ExpedienteDto) {
      ExpedienteDto expediente = (ExpedienteDto) item;
      try {
        LOG.info("Se va a abrir el expediente {} en el GD", expediente.getCodExp());
        expedientesFacade.abrirExpedienteGd(expediente.getIdExp());
        LOG.info("Se ha abierto correctamente el expediente {} en el GD", expediente.getCodExp());
      } catch (SinacException exception) {
        LOG.error("SinacJobReintentoDocumentoGd.procesarItem - Error: ", exception);
        try {
          addError();
          guardaErrorJob(exception, jobDescription, jobName);
        } catch (Exception e1) {
          LOG.error("Error guardando el error del job en la tabla TRI_ERRORES: {}", e1.getMessage());
        }
      }
    }

    // si el item es un documento sin cod gd
    if (item instanceof ExpedienteDocumentoDto) {
      ExpedienteDocumentoDto documento = (ExpedienteDocumentoDto) item;
      try {
        LOG.info("Se va a subir al gestor documental el documento {} del expediente {}", documento.getNomDoc(),
            documento.getExpedienteDto().getCodExp());
        expedientesFacade.reintentoSubidaGestorDocumental(documento.getExpedienteDto().getIdExp(),
            ((ExpedienteDocumentoDto) item).getIdExpDoc());
        LOG.info("Se ha subido correctamente al gestor documental el documento {} del expediente {}",
            documento.getNomDoc(), documento.getExpedienteDto().getCodExp());
      } catch (SinacException exception) {
        LOG.error("SinacJobReintentoDocumentoGd.procesarItem - Error: ", exception);
        try {
          addError();
          guardaErrorJob(exception, jobDescription, jobName);
        } catch (Exception e1) {
          LOG.error("Error guardando el error del job en la tabla TRI_ERRORES: {}", e1.getMessage());
        }
      }
    }

    LOG.info("SinacJobReintentoDocumentoGd.procesarItem - End");
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    LOG.info("SinacJobReintentoDocumentoGd.guardaErrorJob - Init");

    List<String> list = List.of(this.getClass().getName(), ExpedientesFacadeImpl.class.getName(),
        CatalogosServiceImpl.class.getName());

    UtilError.guardaErrorJob(e, list, jobFacade, jobDescripcion, this.getClass().getName(), jobName);

    LOG.info("SinacJobReintentoDocumentoGd.guardaErrorJob - End");
  }
}
