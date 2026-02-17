package es.mjusticia.sinac.core.batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.PlazosService;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ParametrizacionDto;
import es.mjusticia.sinac.core.utils.UtilError;
import es.mjusticia.sinac.dgp.service.impl.InformeDgpServiceImpl;

/**
 * Job para solicitar a la Dgp los informes en estado pendiente
 */
@Component
public class SinacJobConsultaTitulaciones extends SinacJob<ExpedienteDto> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobConsultaTitulaciones.class);

  @Autowired
  private ExpedientesService expedientesService;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private PlazosService plazosService;

  /**
   * Constructor por defecto
   */
  public SinacJobConsultaTitulaciones() {
    super();
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    listaNombresClaseGuardarError.add(InformeDgpServiceImpl.class.getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_AUT_CONSULTA_TITULACIONES";
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  public List<ExpedienteDto> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobConsultaTitulaciones.recuperarItems - Init");

    List<String> listaEstadosNotIn = Arrays.asList("EXPC");
    List<ExpedienteDto> listaExpedientesCer = new ArrayList<>();
    Date fechaActual = new Date();
    Date fechaComunicacion = plazosService.getDateByDaysToTakeOff(fechaActual, Integer.parseInt(String.valueOf("20")));
    List<ParametrizacionDto> listaParam = expedientesService.getParametrizacionByNombre("CONSULTA_AUTO_CCSE_DELE");
    List<String> listaCodigosEstados = Arrays.asList("0", "1", "2", "3", "4");
    for (ParametrizacionDto parametrizacionDto : listaParam) {

      listaExpedientesCer.addAll(expedientesService.getListaExpedientesConsultaTitulaciones(fechaComunicacion,
          parametrizacionDto.getValor(), listaEstadosNotIn, listaCodigosEstados));
    }

    LOG.info("Se van a procesar {} expedientes", listaExpedientesCer.size());
    return listaExpedientesCer;
  }

  @Override
  public void procesarItem(ExpedienteDto item, Map<String, Object> contextData) {
    LOG.info("SinacJobConsultaTitulaciones.procesarItem - Init");
    LOG.info("Item: {}", item.getIdExp());
    item.setInteresado(item.getExpedientesPersonasDtos().get(0).getPersonaDto());
    expedientesService.consultarCertificaciones(item);

  }

}
