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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ParametrizacionDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;

/**
 * Job encargado de procesar solicitudes desde el sistema VEA y transformarlas
 * en solicitudes del sistema SINAC. Este job recupera las solicitudes sin
 * procesar, las transforma y las guarda como borradores en el sistema SINAC.
 * 
 * <p>
 * Extiende la clase genérica {@link SinacJob} para implementar la lógica
 * específica del procesamiento de solicitudes.
 * </p>
 */
@Component
public class SinacJobPropuestaConcesionResidencia extends SinacJob<ExpedienteDto> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobPropuestaConcesionResidencia.class);

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private ExpedientesService expedientesService;

  /**
   * Devuelve el servicio de usuarios utilizado por el job.
   * 
   * @return {@link UsuariosService} utilizado para obtener información de
   *         usuarios.
   */
  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  /**
   * Recupera los IDs de las solicitudes sin procesar desde el sistema VEA.
   * 
   * @param contextData Datos de contexto proporcionados al job.
   * @return Lista de IDs de solicitudes sin procesar.
   */
  @Override
  public List<ExpedienteDto> recuperarItems(Map<String, Object> contextData) {
    List<String> listaEstados = Arrays.asList("INFC");
    List<ParametrizacionDto> listaParam = expedientesService.getParametrizacionByNombre("RESOLUCION_AUTOMATICA_R");
    List<ExpedienteDto> listaExpedientes = new ArrayList<>();
    List<String> listaNombresNotIn = Arrays.asList("DGP", "MJU", "CNI", "MDE", "Dispensa IC concedida", "CCSE", "DELE",
        "Justificante de estudios");
    for (ParametrizacionDto parametrizacionDto : listaParam) {

      listaExpedientes.addAll(expedientesFacade.getListaExpedientesPropuesta(listaEstados,
          parametrizacionDto.getValor(), listaNombresNotIn));
    }
    LOG.info("Tamaño lista: {}", listaExpedientes.size());
    return listaExpedientes;
  }

  /**
   * Procesa un ítem (solicitud) recuperado desde el sistema VEA.
   * 
   * <p>
   * El procesamiento incluye:
   * </p>
   * <ul>
   * <li>Recuperar la solicitud global desde VEA.</li>
   * <li>Formar la solicitud completa con sus datos relacionados.</li>
   * <li>Transformar la solicitud al formato SINAC.</li>
   * <li>Guardar la solicitud como borrador en el sistema SINAC.</li>
   * <li>Actualizar el estado de la solicitud en VEA si se guarda
   * correctamente.</li>
   * </ul>
   * 
   * @param item        ID de la solicitud a procesar.
   * @param contextData Datos de contexto proporcionados al job.
   */
  @Override
  public void procesarItem(ExpedienteDto item, Map<String, Object> contextData) {

    contextData.put("idPro", item.getProcedimientoDto().getIdPro());
    contextData.put("idExp", item.getIdExp());
    contextData.put("idUsu", usuariosService.getUsuarioByUsuarioJusticia(getUsuarioJusticia()).getIdUsu());

    PlantillaDto plantilla = expedientesFacade.getPlantillaPorCod("PRORCR");

    contextData.put("idExp", item.getIdExp());
    contextData.put("idPlantilla", plantilla.getIdPla());
    expedientesFacade.generarFirmarAuto(item, contextData, plantilla);
  }

  /**
   * Devuelve el nombre del usuario asociado al job.
   * 
   * @return Nombre del usuario asociado al job.
   */
  @Override
  protected String getUsuarioJusticia() {
    return "JOB_AUT_PROPUESTA_RESIDENCIA";
  }
}
