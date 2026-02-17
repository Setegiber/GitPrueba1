package es.mjusticia.sinac.core.batch;

import java.math.BigInteger;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sshtools.common.logger.Log;

import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.PlantillasService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.eis.MotorTramitacionComponent;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.MaquinaEstadosDto;
import es.mjusticia.sinac.core.model.dto.ParametrizacionDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosPlantillasCriteriosDto;

/**
 * 
 * Job para solicitar todos los informes de expedientes que tengan el estado
 * 
 * informes pendientes de solicitar.
 * 
 */

@Component

public class SinacJobResolverExpedientes extends SinacJob<ExpedienteDto> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobResolverExpedientes.class);
  private String descripcion;
  private String jobName;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private ExpedientesService expedientesService;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private MotorTramitacionComponent motorTramitacionComponent;

  @Autowired
  private PlantillasService plantillasService;

  public SinacJobResolverExpedientes() {
    super();
  }

  @Override
  public List<ExpedienteDto> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobResolverExpedientes recuperarItems INIT");

    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    List<ExpedienteDto> listaExpedientes = new ArrayList<>();
    List<String> listaEstadosExp = Arrays.asList("DCPV", "DCVA");
    List<ExpedienteDto> listaExpedientesAux = new ArrayList<>();
    Map<BigInteger, PlantillaDto> expedientesPlantillas = new HashMap<>();
    List<ParametrizacionDto> listaParam = expedientesService.getParametrizacionByNombre("RESOLUCION_AUTOMATICA");
    Map<Short, Map<Short, Map<Short, List<Long>>>> mapaProcedimientos = new HashMap<>();
    Map<Short, PlantillaDto> mapaPlantillas = new HashMap<>();
    LOG.info("SinacJobResolverExpedientes recuperarItems la lista de procedimientos configurados son {}", listaParam);
    for (ParametrizacionDto parametrizacionDto : listaParam) {
      LOG.info(
          "SinacJobResolverExpedientes recuperarItems para el procedimiento {} se recuperan los criterios de las diferentes plantillas",
          parametrizacionDto.getProcedimiento().getIdPro());
      List<ProcedimientosPlantillasCriteriosDto> listaProPlaCri = expedientesService
          .getListaProcedimientosPlantillasCriteriosByIdPro(parametrizacionDto.getProcedimiento().getIdPro());

      Map<Short, Map<Short, List<Long>>> mapaPrioridad = new HashMap<>();

      for (ProcedimientosPlantillasCriteriosDto proPlaCri : listaProPlaCri) {
        LOG.info("Se guarda en el mapa el criterio {} para la plantilla {} con orden de prioridad {} y la condición {}",
            proPlaCri.getIdProPlaCriterio(), proPlaCri.getPlantilla().getIdPla(), proPlaCri.getOrdenPrioridad(),
            proPlaCri.getCriterio().getIdCondicion());
        Map<Short, List<Long>> mapaInterno = mapaPrioridad.get(proPlaCri.getOrdenPrioridad());

        if (mapaInterno == null) {
          mapaInterno = new HashMap<>();
        }

        List<Long> listaCond = mapaInterno.get(proPlaCri.getPlantilla().getIdPla());

        if (listaCond == null)
          listaCond = new ArrayList<>();
        listaCond.add(proPlaCri.getCriterio().getIdCondicion());
        mapaInterno.put(proPlaCri.getPlantilla().getIdPla(), listaCond);
        if (mapaPlantillas.get(proPlaCri.getPlantilla().getIdPla()) == null) {
          mapaPlantillas.put(proPlaCri.getPlantilla().getIdPla(), proPlaCri.getPlantilla());
        }
        mapaPrioridad.put(proPlaCri.getOrdenPrioridad(), mapaInterno);
      }
      mapaProcedimientos.put(parametrizacionDto.getProcedimiento().getIdPro(), mapaPrioridad);

      LOG.info("Se recuperan los expedientes que podrían ser resueltos para el procedimiento {}",
          parametrizacionDto.getProcedimiento().getIdPro());
      listaExpedientes
          .addAll(expedientesFacade.getListaExpedientesResolver(parametrizacionDto.getValor(), listaEstadosExp));
    }

    for (ExpedienteDto expediente : listaExpedientes) {
      encuentraPlantilla(listaExpedientesAux, expedientesPlantillas, mapaProcedimientos, mapaPlantillas, expediente);
    }

    contextData.put("plantillasResoluciones", expedientesPlantillas);
    LOG.info("Se van a procesar {} items", listaExpedientes.size());
    return listaExpedientesAux;
  }

  /**
   * @param listaExpedientesAux
   * @param expedientesPlantillas
   * @param mapaProcedimientos
   * @param mapaPlantillas
   * @param expediente
   */
  private void encuentraPlantilla(List<ExpedienteDto> listaExpedientesAux,
      Map<BigInteger, PlantillaDto> expedientesPlantillas,
      Map<Short, Map<Short, Map<Short, List<Long>>>> mapaProcedimientos, Map<Short, PlantillaDto> mapaPlantillas,
      ExpedienteDto expediente) {
    LOG.info("Se va a comprobar si el expediente {}-{} cumple los criterios para resolverse", expediente.getIdExp(),
        expediente.getCodExp());
    Map<Short, Map<Short, List<Long>>> mapaCriterios = mapaProcedimientos
        .get(expediente.getProcedimientoDto().getIdPro());

    for (Entry<Short, Map<Short, List<Long>>> mapaPlantillasCondiciones : mapaCriterios.entrySet()) {
      for (Entry<Short, List<Long>> plantillaCondiciones : mapaPlantillasCondiciones.getValue().entrySet()) {

        PlantillaDto plantilla = mapaPlantillas.get(plantillaCondiciones.getKey());
        LOG.info(
            "Se va a comprobar si el expediente {}-{} cumple las clasificaciones para la plantilla {} y el orden de prioridad {}",
            expediente.getIdExp(), expediente.getCodExp(), plantilla.getCodPlantilla(),
            mapaPlantillasCondiciones.getKey());
        if (plantillasService.comprobarClasificacionesPlantilla(expediente.getIdExp(),
            plantilla.getPlantillasClasificacionDtos())) {

          LOG.info(
              "Se cumple la clasificacion de la plantilla con código {} para el expediente {}-{} y el orden de prioridad {}. Van a comprobarse los criterios definidos.",
              plantilla.getCodPlantilla(), expediente.getIdExp(), expediente.getCodExp(),
              mapaPlantillasCondiciones.getKey());

          if (expedientesFacade.cumpleCriterios(plantillaCondiciones.getValue(), expediente.getIdExp(),
              expediente.getProcedimientoDto().getIdPro())) {
            LOG.info(
                "Se cumplen los criterios de resolución de la plantilla con código {} para el expediente {}-{} y el orden de prioridad {}",
                plantilla.getCodPlantilla(), expediente.getIdExp(), expediente.getCodExp(),
                mapaPlantillasCondiciones.getKey());

            expedientesPlantillas.put(expediente.getIdExp(), plantilla);
            listaExpedientesAux.add(expediente);
            return;
          }
        }
      }
    }
  }

  @Override

  public void procesarItem(ExpedienteDto item, Map<String, Object> contextData) {

    Map<BigInteger, PlantillaDto> expPlantillas = (Map<BigInteger, PlantillaDto>) contextData
        .get("plantillasResoluciones");

    LOG.info("Se va a procesar el expediente {}-{} y la plantilla {}", item.getIdExp(), item.getCodExp(),
        expPlantillas.get(item.getIdExp()).getIdPla());

    List<MaquinaEstadosDto> listaAccionesDisponibles = motorTramitacionComponent
        .getListaAccionesDisponiblesPorIdExp(item.getIdExp(), null, item.getProcedimientoDto().getIdPro());

    boolean valDocumentacion = false;
    Long pftoa = null;

    for (MaquinaEstadosDto maquinaEstadosDto : listaAccionesDisponibles) {
      if ("VALD".equals(maquinaEstadosDto.getAccion().getAccionDto().getCodAccion())) {
        LOG.info("El expediente {}-{} tiene disponible la acción de validar documentación", item.getIdExp(),
            item.getCodExp());
        valDocumentacion = true;
        pftoa = maquinaEstadosDto.getAccion().getIdProFaseTraOpeAcc();
        break;
      }
    }

    if (valDocumentacion) {
      LOG.info(
          "La acción de validar documentación está disponible para el expediente {}-{}, se va a ejecutar antes de generar la resolución",
          item.getIdExp(), item.getCodExp());

      try {
        contextData.put("idPro", item.getProcedimientoDto().getIdPro());
        contextData.put("idExp", item.getIdExp());
        expedientesFacade.ejecutarAccion(pftoa, contextData);
      } catch (Exception e) {

        Log.error("Se ha producido un error ejecutando la accion {} ", pftoa, e.getMessage());
        LOG.info("Registrando error en la tabla de errores para expediente con ID: {}", item.getIdExp());
        addError();
        contextData.put("errorJob", e.getMessage());
        guardaErrorJob(e, descripcion, jobName);
        LOG.info("Error registrado correctamente para expediente con ID: {}", item.getIdExp());
      }
    }

    expedientesFacade.generarFirmarAuto(item, contextData, expPlantillas.get(item.getIdExp()));
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_AUT_RES_EXPEDIENTES";
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

}
