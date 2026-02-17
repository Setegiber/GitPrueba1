package es.mjusticia.sinac.core.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.InsideEstadosService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.business.service.impl.ExpedientesServiceImpl;
import es.mjusticia.sinac.core.model.dto.InsideEstadoDto;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;
import es.mjusticia.sinac.core.model.enums.InsideEnviosEstadosEnum;
import es.mjusticia.sinac.core.utils.UtilError;

/**
 * Job para comprobar el último estado de todos los asientos enviados a GEISER
 * que esten EN_CURSO
 */
@Component
public class SinacJobInside extends SinacJob<InsideEstadoDto> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobInside.class);

  private String descripcion;

  private String jobName;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private ExpedientesService expedientesService;

  @Autowired
  private InsideEstadosService insideEstadosService;

  public SinacJobInside() {
    super();
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  public List<InsideEstadoDto> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobInside.recuperarItems - Init");
    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    List<InsideEstadoDto> lista = insideEstadosService.getEnviosPendientesAlta();
    LOG.info("Tamaño lista: {}", lista.size());
    LOG.info("SinacJobInside.recuperarItems - End");
    return lista;
  }

  @Override
  public void procesarItem(InsideEstadoDto insideEstadoDto, Map<String, Object> contextData) {
    try {
      LOG.info("SinacJobInside.procesarItem - Init");
      LOG.info("InsideEstadoDto: {}", insideEstadoDto);

      UsuarioDto usuario = this.getUsuariosService().getUsuarioByUsuarioJusticia(getUsuarioJusticia());

      if (insideEstadoDto.getEstado().equals(InsideEnviosEstadosEnum.PENDIENTE_ALTA_EXPEDIENTE)
          || insideEstadoDto.getEstado().equals(InsideEnviosEstadosEnum.ERROR_ALTA_EXPEDIENTE)) {
        insideEstadoDto = expedientesService.enviarExpedienteInside(insideEstadoDto, null, usuario);
      }

      if (insideEstadoDto.getEstado().equals(InsideEnviosEstadosEnum.ALTA_EXPEDIENTE)
          || insideEstadoDto.getEstado().equals(InsideEnviosEstadosEnum.ERROR_REMISION_JUSTICIA)) {
        expedientesService.remitiarAJusticia(insideEstadoDto, usuario);
      }

      LOG.info("SinacJobInside.procesarItem - End");
    } catch (SinacException e) {
      try {
        addError();
        guardaErrorJob(e, descripcion, jobName);
      } catch (Exception e1) {
        LOG.error("Error guardando el error del job en la tabla TRI_ERRORES: {}", e1.getMessage());
      }
    }
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_INT_INSIDE_CONSULTA";
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    listaNombresClaseGuardarError.add(ExpedientesServiceImpl.class.getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

}
