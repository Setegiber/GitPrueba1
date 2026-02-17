package es.mjusticia.sinac.core.batch;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.JobFacade;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.PerFiliacionesDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.PersonaIdentificaDto;
import es.mjusticia.sinac.core.utils.UtilError;
import es.mjusticia.sinac.filiaciones.service.impl.FiliacionesServiceImpl;

/**
 * Job para solicitar a la Dgp el alta de filiaciones
 */
@Component
public class SinacJobConsultaFiliacionesDgp extends SinacJob<BigInteger> {

  // private static final String CONTADOR_DGP_ALTA = "contadorDgpAlta";

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobConsultaFiliacionesDgp.class);

  @Value("${sinac.quartz.sinacJobConsultaFiliacionesDgp.maxItem}")
  private String maxItemConsultaFiliaciones;

  @Value(value = "${es.mjusticia.filiaciones.codOrganismo}")
  private String codOrganismo;

  @Value(value = "${es.mjusticia.filiaciones.nombreOrganismo}")
  private String nombreOrganismo;

  @Value(value = "${es.mjusticia.filiaciones.identificacionPuesto}")
  private String identificacionPuesto;

  @Value(value = "${es.mjusticia.filiaciones.usuario}")
  private String usuario;

  private String descripcion;

  private String jobName;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private ExpedientesFacade expedientesFacade;

  @Autowired
  private JobFacade jobFacade;

  public SinacJobConsultaFiliacionesDgp() {
    super();
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  public List<BigInteger> recuperarItems(Map<String, Object> contextData) {
    LOG.info("SinacJobConsultaFiliacionesDgp.recuperarItems - Init");
    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    List<BigInteger> lista = expedientesFacade.getIdsInteresadosConsultaFiliaciones(maxItemConsultaFiliaciones);
    LOG.info("Tamaño lista: {}", lista.size());
    LOG.info("SinacJobConsultaFiliacionesDgp.recuperarItems - End");
    return lista;
  }

  @Override
  public void procesarItem(BigInteger item, Map<String, Object> contextData) {
    try {
      LOG.info("Consulta NIE Filiación, consulta por referencia de filiación, Procesando item con ID: {}", item);

      LOG.info("Obteniendo datos de la persona con ID: {}", item);
      List<String> listaEstados = Arrays.asList("EXPC", "ARCE", "DRNT");
      ExpedienteDto expedienteDto = expedientesFacade.getExpedientesByIdPerInteresado(item, listaEstados);

      PersonaDto personaDto = expedienteDto.getExpedientesPersonasDtos().get(0).getPersonaDto();
      LOG.info("Datos de la persona obtenidos: {}", personaDto);

      LOG.info("Formateando NIE para la persona con ID: {}", item);
      String nieConsulta = "";
      nieConsulta = setNieFormatoDgp(personaDto, nieConsulta);
      LOG.info("NIE formateado: {}", nieConsulta);

      LOG.info("Realizando consulta de filiación para el NIE: {}", nieConsulta);
      expedientesFacade.peticionConsultaNieFiliacion(nieConsulta, personaDto);

      LOG.info("Obteniendo filiaciones para la persona con ID: {}", item);
      List<PerFiliacionesDto> perFiliacionesDtos = expedientesFacade.getPerFiliacionesByIdPer(item);
      LOG.info("Filiaciones obtenidas: {}", perFiliacionesDtos);

      if (perFiliacionesDtos != null && !CollectionUtils.isEmpty(perFiliacionesDtos)) {
        LOG.info("Validando estado de la primera filiación obtenida.");
        String codEstado = perFiliacionesDtos.get(0).getCodEstado();
        LOG.info("Estado de la filiación: {}", codEstado);

        if ("00".equals(codEstado) || "01".equals(codEstado)) {
          LOG.info("Estado válido ({}). Realizando consulta de referencia de filiación.", codEstado);

          expedientesFacade.peticionConsultaReferenciaFiliacion(perFiliacionesDtos.get(0).getReferencia(), personaDto,
              expedienteDto);
          LOG.info("Consulta de referencia de filiación realizada con éxito.");
        } else {
          LOG.error(
              "Se ha producido un error en la consulta por NIE ({}). No se realizará la consulta de referencia de filiación.",
              codEstado);
        }
      } else {
        LOG.warn("No se encontraron filiaciones para la persona con ID: {}", item);
      }

      LOG.info("SinacJobConsultaFiliacionesDgp.procesarItem - End");
    } catch (SinacException e) {
      LOG.error("Error en SinacJobConsultaFiliacionesDgp.procesarItem: {}", e.getMessage(), e);
      try {
        LOG.info("Registrando error en la tabla de errores.");
        addError();
        guardaErrorJob(e, descripcion, jobName);
        LOG.info("Error registrado correctamente.");
      } catch (Exception e1) {
        LOG.error("Error guardando el error del job en la tabla TRI_ERRORES: {}", e1.getMessage(), e1);
      }
    }
  }

  private String setNieFormatoDgp(PersonaDto interesado, String nieConsulta) {
    for (PersonaIdentificaDto personaIdentificaDto : interesado.getPersonasIdentificaDtos()) {
      if (personaIdentificaDto.getLdvMaestraDto().getCodLdvMae().equals("DID-NIE")) {
        LOG.info("Formateando NIE para la persona con ID: {} - NIE sin formatear: {}", interesado.getIdPer(),
            personaIdentificaDto.getNumAcreditacion());
        nieConsulta = personaIdentificaDto.getNumAcreditacion().replace("X", "0").replace("Y", "1").replace("Z", "2")
            .substring(0, 8);
        break;
      }
    }
    return nieConsulta;
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_INT_DGP_FILIACIONES_CONSULTA";
  }

  @Override
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {
    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    listaNombresClaseGuardarError.add(FiliacionesServiceImpl.class.getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);
  }

}
