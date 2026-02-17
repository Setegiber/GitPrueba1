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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.impl.ExpedientesFacadeImpl;
import es.mjusticia.sinac.core.business.service.DocumentosService;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.PlantillasService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.eis.MotorTramitacionComponent;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.MaquinaEstadosDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;
import es.mjusticia.sinac.core.utils.UtilError;

@Component
public class SinacJobReintentoAltaExpediente extends SinacJob<ExpedienteDto> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobReintentoAltaExpediente.class);

  private String jobName;

  private String jobDescription;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private ExpedientesService expedientesService;

  @Autowired
  private DocumentosService documentosService;

  @Autowired
  private PlantillasService plantillasService;

  @Autowired
  private MotorTramitacionComponent motorTramitacionComponent;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  private static final String INICIACION = "INI";
  private static final String ACCION = "GEND";
  private static final String OPERACION = "ALT";
  private static final String TRAMITE = "REC";
  private static final String ADPV = "ADPV"; // codAccion para Avanzar a Documentación pendiente de validar

  public SinacJobReintentoAltaExpediente() {
    super();
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_AUT_REINTENTO_ALTA_EXP";
  }

  @Override
  public List<ExpedienteDto> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobReintentoAltaExpediente.recuperarItems - Init");

    jobName = (String) contextData.get("jobName");
    jobDescription = (String) contextData.get("descripcion");

    List<ExpedienteDto> expedienteDtoList = expedientesService.getListaExpedienteByFase(INICIACION);

    LOG.info("SinacJobReintentoAltaExpediente.recuperarItems - End");

    return expedienteDtoList;
  }

  @Override
  public void procesarItem(ExpedienteDto item, Map<String, Object> contextData) {
    LOG.info("SinacJobReintentoAltaExpediente.procesarItem : expediente {}", item.getCodExp());

    List<MaquinaEstadosDto> listaAccionesDisponibles = motorTramitacionComponent
        .getListaAccionesDisponiblesPorIdExp(item.getIdExp(), null, item.getProcedimientoDto().getIdPro());

    ExpedienteDocumentoDto expDocumento = documentosService.getExpedienteDocumentosByAccionOperacionTramiteIdExp(
        item.getExpedienteDocumentoDtos(), ACCION, OPERACION, TRAMITE, item.getIdExp());

    for (MaquinaEstadosDto accion : listaAccionesDisponibles) {
      if (accion.getAccion().getProFasesTraOpe().getProFasesTra().getProFases().getFaseDto().getCodFase()
          .equals(INICIACION) && !accion.getAccion().getAccionDto().getCodAccion().equals(ADPV)) {
        try {
          contextData.put("idPro", item.getProcedimientoDto().getIdPro());
          contextData.put("idExp", item.getIdExp());
          if (expDocumento != null) {
            contextData.put("idExpDoc", expDocumento.getIdExpDoc());
          }

          List<PlantillaDto> listaPlantillas = plantillasService.getListaPlantillas(item.getIdExp(), TRAMITE, OPERACION,
              ACCION);
          PlantillaDto plantilla = plantillasService.selectPlantillaByCod(listaPlantillas, item);
          if (plantilla != null) {
            contextData.put("idPlantilla", plantilla.getIdPla());
          }

          contextData.put("idProFasTraOpeAcc", accion.getAccion().getIdProFaseTraOpeAcc());

          LOG.info("Se va a ejecutar la acción {} del expediente {}", accion.getAccion().getAccionDto().getCodAccion(),
              item.getIdExp());
          expedientesFacade.ejecutarAccion(accion.getAccion().getIdProFaseTraOpeAcc(), contextData);

        } catch (Exception exception) {
          LOG.error("SinacJobReintentoAltaExpediente.procesarItem - Error: ", exception);
          try {
            addError();
            guardaErrorJob(exception, jobDescription, jobName);
          } catch (Exception e1) {
            LOG.error("Error guardando el error del job en la tabla TRI_ERRORES: {}", e1.getMessage());
          }
        }
      }
    }
    LOG.info("SinacJobReintentoAltaExpediente.procesarItem {} en el expediente {}");

  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    LOG.info("SinacJobReintentoAltaExpediente.guardaErrorJob - Init");

    List<String> list = List.of(this.getClass().getName(), ExpedientesFacadeImpl.class.getName());

    UtilError.guardaErrorJob(e, list, jobFacade, jobDescripcion, this.getClass().getName(), jobName);

    LOG.info("SinacJobReintentoAltaExpediente.guardaErrorJob - End");
  }
}
