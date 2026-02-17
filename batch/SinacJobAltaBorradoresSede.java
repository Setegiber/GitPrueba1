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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.SolicitudesFacade;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.eis.connector.VeaConnector;
import es.mjusticia.sinac.core.model.dto.PersonaDomicilioDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDto;
import es.mjusticia.sinac.core.utils.Validaciones;
import es.mjusticia.sinac.vea.model.dto.SedeSolicitudCompletaDto;
import es.mjusticia.sinac.vea.model.entity.SedeSolicitudGlobalEntity;

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
public class SinacJobAltaBorradoresSede extends SinacJob<BigInteger> {

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobAltaBorradoresSede.class);

  private String descripcion;

  private String jobName;

  @Autowired
  private UsuariosService usuariosService;

  @Autowired
  private VeaConnector veaConnector;

  @Autowired
  private SolicitudesFacade solicitudesFacade;

  @Autowired
  private ExpedientesFacade expedientesFacade;

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
  public List<BigInteger> recuperarItems(Map<String, Object> contextData) {
    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    List<BigInteger> lista = veaConnector.getIdsSolicitudesSinProcesar();
    LOG.info("Tamaño lista: {}", lista.size());
    return lista;
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
  public void procesarItem(BigInteger item, Map<String, Object> contextData) {
    try {
      LOG.info("ProcesarItem - Init. ID Solicitud VEA: {}", item);
      List<PersonaDto> listaPersonas = new ArrayList<>();
      List<PersonaDto> listaPersonasRep = new ArrayList<>();
      SedeSolicitudGlobalEntity sedeSolicitudGlobalEntity = veaConnector.getSolicitudGlobal(item);
      LOG.debug("Solicitud global recuperada: {}", sedeSolicitudGlobalEntity);

      SedeSolicitudCompletaDto sedeSolicitudCompletaDto = veaConnector
          .formarSolicitudCompleta(sedeSolicitudGlobalEntity);
      LOG.debug("Solicitud completa formada: {}", sedeSolicitudCompletaDto);

      SolicitudDto sinacSolicitudDto = veaConnector.transformarSedeSolicitudToSinacSolicitud(sedeSolicitudCompletaDto);
      LOG.debug("Solicitud transformada a SINAC: {}", sinacSolicitudDto);

      sinacSolicitudDto.setSolicitudDocumentoDtos(veaConnector.formarDocumentacion(sedeSolicitudCompletaDto));
      LOG.debug("Documentación formada y añadida a la solicitud SINAC. Documentos: {}",
          sinacSolicitudDto.getSolicitudDocumentoDtos());

      if (sinacSolicitudDto.getInteresado() != null && validarCamposTextoVea(sinacSolicitudDto)
          && (sinacSolicitudDto.getRepresentanteMandato() != null
              && !sinacSolicitudDto.getInteresado().getPersonasIdentificaDtos().get(0).getNumAcreditacion().equals(
                  sinacSolicitudDto.getRepresentanteMandato().getPersonasIdentificaDtos().get(0).getNumAcreditacion())
              || sinacSolicitudDto.getRepresentanteMandato() == null)) {
        listaPersonas = expedientesFacade.getPersonasRastreo(
            sinacSolicitudDto.getInteresado().getPersonasIdentificaDtos().get(0).getNumAcreditacion());

        if (listaPersonas != null && listaPersonas.size() == 1) {
          LOG.debug("ListaPersonas contiene {} elementos", listaPersonas.size());
          sinacSolicitudDto.getInteresado().setIdPer(listaPersonas.get(0).getIdPer());
        }

        if (sinacSolicitudDto.getRepresentanteMandato() != null) {
          listaPersonasRep = expedientesFacade.getPersonasRastreo(
              sinacSolicitudDto.getRepresentanteMandato().getPersonasIdentificaDtos().get(0).getNumAcreditacion());
        }

        if (listaPersonasRep != null && listaPersonasRep.size() == 1) {
          LOG.debug("ListaPersonasRep contiene {} elementos", listaPersonasRep.size());
          sinacSolicitudDto.getRepresentanteMandato().setIdPer(listaPersonasRep.get(0).getIdPer());
          sinacSolicitudDto.getRepresentanteMandato().getPersonasContactosElectronicosDtos().get(0).setIdPerPerConEle(
              listaPersonasRep.get(0).getPersonasContactosElectronicosDtos().get(0).getIdPerPerConEle());

          sinacSolicitudDto.getRepresentanteMandato().getPersonasContactosElectronicosDtos().get(0)
              .getPersonaContactoElectronicoDto().setIdPerConEle(listaPersonasRep.get(0)
                  .getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto().getIdPerConEle());

          sinacSolicitudDto.getRepresentanteMandato().getPersonasDomiciliosDto().get(0)
              .setIdPerPerDom(listaPersonasRep.get(0).getPersonasDomiciliosDto().get(0).getIdPerPerDom());
          sinacSolicitudDto.getRepresentanteMandato().getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto()
              .setIdPerDom(
                  listaPersonasRep.get(0).getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getIdPerDom());

        }

        sinacSolicitudDto = solicitudesFacade.saveSolicitud(sinacSolicitudDto, null);
        LOG.info("Solicitud guardada en SINAC. ID_SOL en SINAC: {}", sinacSolicitudDto.getIdSol());

        if (sinacSolicitudDto.getLdvMaestraDtoByIdEstSolLdv() != null
            && "SOL-BOR".equals(sinacSolicitudDto.getLdvMaestraDtoByIdEstSolLdv().getCodLdvMae())) {
          veaConnector.updateEstadoSolicitud(sedeSolicitudGlobalEntity.getIdSol(), false);
          LOG.info("Estado PROCESADO de la solicitud actualizado en VEA para ID: {}",
              sedeSolicitudGlobalEntity.getIdSol());
        }
      } else {
        veaConnector.updateEstadoSolicitud(sedeSolicitudGlobalEntity.getIdSol(), true);
        LOG.info("Estado RECHAZADO de la solicitud actualizado en VEA para ID: {}",
            sedeSolicitudGlobalEntity.getIdSol());
      }

      LOG.info("Procesamiento finalizado para solicitud VEA: {}", sedeSolicitudGlobalEntity.getIdSol());

    } catch (Exception e) {
      LOG.error("Error en SinacJobAltaBorradoresSede.procesarItem para ID {}: {}", item, e.getMessage(), e);
      try {
        LOG.info("Registrando error en la tabla de errores para ID: {}", item);
        addError();
        contextData.put("errorJob", e.getMessage());
        guardaErrorJob(e, descripcion, jobName);
        LOG.info("Error registrado correctamente para ID: {}", item);
      } catch (Exception e1) {
        LOG.error("Error guardando el error del job en la tabla TRI_ERRORES para ID {}: {}", item, e1.getMessage(), e1);
      }
    }
  }

  private static boolean validarCamposTextoVea(SolicitudDto sinacSolicitudDto) {
    PersonaDto datosInteresado = sinacSolicitudDto.getInteresado();
    PersonaDomicilioDto datosNotifica = sinacSolicitudDto.getPersonaDomicilioDtoNotificacion();

    Predicate<String> soloLetras100 = v -> Validaciones.validaCampoSoloLetrasLongitud(v, 100);
    Predicate<String> soloLetras150 = v -> Validaciones.validaCampoSoloLetrasLongitud(v, 150);

    if (isInvalid(datosInteresado.getNombre(), soloLetras100)) {
      LOG.error("Error validando la solicitud de VEA {}: Error validando nombre del interesado {}",
          sinacSolicitudDto.getIdSolVea(), datosInteresado.getNombre());
      return false;
    }
    if (isInvalid(datosInteresado.getApellido1(), soloLetras100)) {
      LOG.error("Error validando la solicitud de VEA {}: Error validando apellido 1 del interesado {}",
          sinacSolicitudDto.getIdSolVea(), datosInteresado.getApellido1());
      return false;
    }
    if (isInvalid(datosInteresado.getApellido2(), soloLetras150)) {
      LOG.error("Error validando la solicitud de VEA {}: Error validando apellido 2 del interesado {}",
          sinacSolicitudDto.getIdSolVea(), datosInteresado.getApellido2());
      return false;
    }
    if (isInvalid(datosInteresado.getLugarNacimiento(), soloLetras100)) {
      LOG.error("Error validando la solicitud de VEA {}: Error validando lugar de nacimiento del interesado {}",
          sinacSolicitudDto.getIdSolVea(), datosInteresado.getLugarNacimiento());
      return false;
    }

    return validarCamposTextoDomicilioVea(sinacSolicitudDto, datosNotifica);

  }

  /**
   * @param sinacSolicitudDto
   * @param datosNotifica
   * @param validacion
   */
  private static Boolean validarCamposTextoDomicilioVea(SolicitudDto sinacSolicitudDto,
      PersonaDomicilioDto datosNotifica) {
    Predicate<String> alfa50 = v -> Validaciones.validaAlfanumericoLongitud(v, 50);
    Predicate<String> alfa3 = v -> Validaciones.validaAlfanumericoLongitud(v, 3);

    if (Boolean.FALSE.equals(sinacSolicitudDto.getFlgPersonaConsiente())) {
      if (isInvalid(datosNotifica.getNomVia(), alfa50)) {
        LOG.error(
            "Error validando la solicitud de VEA {}: Error validando nombre de la via del domicilio de notificación {}",
            sinacSolicitudDto.getIdSolVea(), datosNotifica.getNomVia());
        return false;
      }
      if (isInvalid(datosNotifica.getBloque(), alfa3)) {
        LOG.error("Error validando la solicitud de VEA {}: Error validando bloque del domicilio de notificación {}",
            sinacSolicitudDto.getIdSolVea(), datosNotifica.getBloque());
        return false;
      }
      if (isInvalid(datosNotifica.getEscalera(), alfa3)) {
        LOG.error("Error validando la solicitud de VEA {}: Error validando escalera del domicilio de notificación {}",
            sinacSolicitudDto.getIdSolVea(), datosNotifica.getEscalera());
        return false;
      }
      if (isInvalid(datosNotifica.getLetra(), alfa3)) {
        LOG.error("Error validando la solicitud de VEA {}: Error validando letra del domicilio de notificación {}",
            sinacSolicitudDto.getIdSolVea(), datosNotifica.getLetra());
        return false;
      }
      if (isInvalid(datosNotifica.getPiso(), alfa3)) {
        LOG.error("Error validando la solicitud de VEA {}: Error validando piso del domicilio de notificación {}",
            sinacSolicitudDto.getIdSolVea(), datosNotifica.getPiso());
        return false;
      }
      if (isInvalid(datosNotifica.getPortal(), alfa3)) {
        LOG.error("Error validando la solicitud de VEA {}: Error validando portal del domicilio de notificación {}",
            sinacSolicitudDto.getIdSolVea(), datosNotifica.getPortal());
        return false;
      }
    }
    return true;
  }

  private static boolean isInvalid(String value, Predicate validator) {
    return value != null && !value.trim().isEmpty() && !validator.test(value.trim());
  }

  /**
   * Devuelve el nombre del usuario asociado al job.
   * 
   * @return Nombre del usuario asociado al job.
   */
  @Override
  protected String getUsuarioJusticia() {
    return "JOB_SEDE_BORRADORES_ALTA";
  }
}
