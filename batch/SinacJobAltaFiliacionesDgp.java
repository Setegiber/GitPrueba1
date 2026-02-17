package es.mjusticia.sinac.core.batch;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.JobFacade;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.PersonaContactoElectronicoDto;
import es.mjusticia.sinac.core.model.dto.PersonaDomicilioDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.PersonasContactosElectronicosDto;
import es.mjusticia.sinac.core.model.dto.PersonasDomiciliosDto;
import es.mjusticia.sinac.core.utils.UtilError;
import es.mjusticia.sinac.filiaciones.dto.RespuestaAltaFiliacionDto;
import es.mjusticia.sinac.filiaciones.exception.SinacFiliacionesException;
import es.mjusticia.sinac.filiaciones.service.FiliacionesService;
import es.mjusticia.sinac.filiaciones.service.impl.FiliacionesServiceImpl;
import ws_alta_fili_domi.com.adexttra.Datos;
import ws_alta_fili_domi.com.adexttra.Direccion;
import ws_alta_fili_domi.com.adexttra.Organismo;
import ws_alta_fili_domi.com.adexttra.Peticion;
import ws_alta_fili_domi.com.adexttra.Solicitante;
import ws_alta_fili_domi.com.adexttra.Titular;
import ws_alta_fili_domi.com.adexttra.Via;

/**
 * Job para solicitar a la Dgp el alta de filiaciones
 */
@Component
public class SinacJobAltaFiliacionesDgp extends SinacJob<BigInteger> {

  // private static final String CONTADOR_DGP_ALTA = "contadorDgpAlta";

  private static final Logger LOG = LoggerFactory.getLogger(SinacJobAltaFiliacionesDgp.class);

  /*
   * @Value(value = "${sinac.quartz.sinacJobDgp.solicitar.maxItem}") private
   * Integer maxItem;
   */

  @Value(value = "${es.mjusticia.filiaciones.codOrganismo}")
  private String codOrganismo;

  @Value(value = "${es.mjusticia.filiaciones.nombreOrganismo}")
  private String nombreOrganismo;

  @Value(value = "${es.mjusticia.filiaciones.identificacionPuesto}")
  private String identificacionPuesto;

  @Value(value = "${es.mjusticia.filiaciones.equipo}")
  private String equipo;

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

  @Autowired
  private FiliacionesService filiacionesService;

  public SinacJobAltaFiliacionesDgp() {
    super();
  }

  @Override
  protected UsuariosService getUsuariosService() {
    return usuariosService;
  }

  @Override
  public List<BigInteger> recuperarItems(Map<String, Object> contextData) {
    LOG.debug("SinacJobAltaFiliacionesDgp.recuperarItems - Init");
    jobName = (String) contextData.get("jobName");
    descripcion = (String) contextData.get("descripcion");
    List<BigInteger> lista = expedientesFacade.getIdsInteresadosAltaFiliaciones();
    LOG.info("Tamaño lista de los interesados para el alta de NIEs en las filiaciones: {}", lista.size());
    LOG.debug("SinacJobAltaFiliacionesDgp.recuperarItems - End");
    return lista;
  }

  @Override
  public void procesarItem(BigInteger item, Map<String, Object> contextData) {
    try {
      LOG.info("SinacJobAltaFiliacionesDgp.procesarItem - Init");
      LOG.info("ID del interesado para el alta del NIE: {}", item);
      List<String> listaEstados = Arrays.asList("EXPC", "ARCE", "DRNT");
      ExpedienteDto expedienteDto = expedientesFacade.getExpedientesByIdPerInteresado(item, listaEstados);

      PersonaDto interesado = expedientesFacade.getPersonaByIdPer(item);
      var peticionAlta = new ws_alta_fili_domi.com.adexttra.Peticion();
      cargarDatosPeticionAlta(interesado, peticionAlta);
      peticionSaveAltaFiliacion(interesado, peticionAlta, expedienteDto);

      LOG.info("SinacJobAltaFiliacionesDgp.procesarItem - End");
    } catch (SinacException e) {
      addError();
      guardaErrorJob(e, descripcion, jobName);
    }
  }

  private void peticionSaveAltaFiliacion(PersonaDto interesado, Peticion peticionAlta, ExpedienteDto expedienteDto) {
    try {
      RespuestaAltaFiliacionDto respuestaAltaFiliacionDto = filiacionesService.peticionAltaFiliacion(peticionAlta);
      expedientesFacade.saveAltaFiliaciones(respuestaAltaFiliacionDto, interesado, expedienteDto);
    } catch (SinacFiliacionesException e) {
      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_102);
    }
  }

  private void cargarDatosPeticionAlta(PersonaDto interesado, Peticion peticionAlta) {
    Datos datos = new Datos();
    List<PersonasDomiciliosDto> personasDomiciliosDtos = interesado.getPersonasDomiciliosDto();
    if (!CollectionUtils.isEmpty(personasDomiciliosDtos)) {
      PersonaDomicilioDto personaDomicilioDto = personasDomiciliosDtos.get(0).getPersonaDomicilioDto();
      Direccion direccion = new Direccion();
      direccion.setCodPostal(personaDomicilioDto.getCodigoPostal());
      if (personaDomicilioDto.getLocalidadDto() != null) {
        direccion.setLocalidadDom(personaDomicilioDto.getLocalidadDto().getNomMunicipio());
      }
      direccion.setEntidadMenorDom("");
      Via via = new Via();
      setNombreViaProvincia(personaDomicilioDto, direccion, via);
      via.setBloque(personaDomicilioDto.getBloque());
      via.setEscalera(personaDomicilioDto.getEscalera());
      via.setKm(personaDomicilioDto.getKm());
      // Si existe km, el número de vía irá en blanco
      if (StringUtils.isEmpty(personaDomicilioDto.getKm())) {
          via.setNumeroVia(personaDomicilioDto.getNumVia());
        }
      if (personaDomicilioDto.getPiso() != null) {
        via.setPlanta("P".concat(String.format("%2s", personaDomicilioDto.getPiso()).replace(' ', '0')));
      }
      via.setPortal(personaDomicilioDto.getPortal());
      via.setPuerta(personaDomicilioDto.getLetra());
      if (personaDomicilioDto.getTipoVia() != null) {
        via.setTipoVia(personaDomicilioDto.getTipoVia().getNumDgp());
      }
      direccion.setVia(via);
      datos.setDireccion(direccion);
    }
    datos.setEquipo(equipo);
    datos.setForzarAlta("0");
    Titular titular = new Titular();
    titular.setNombre(interesado.getNombre());
    titular.setApellido1(interesado.getApellido1());
    titular.setApellido2(interesado.getApellido2());
    List<PersonasContactosElectronicosDto> personasContactosElectronicosDtos = interesado
        .getPersonasContactosElectronicosDtos();
    if (!CollectionUtils.isEmpty(personasContactosElectronicosDtos)) {
      PersonaContactoElectronicoDto personaContactoElectronicoDto = personasContactosElectronicosDtos.get(0)
          .getPersonaContactoElectronicoDto();
      titular.setEmail(personaContactoElectronicoDto.getEmail());
      titular.setNumMovil(personaContactoElectronicoDto.getTelMovil());
      titular.setNumtelefono(personaContactoElectronicoDto.getTelFijo());
    }
    String estadoCivil = "";
    if (interesado.getEstadoCivil() != null) {
      estadoCivil = interesado.getEstadoCivil().getNomLdvMae();
      if (interesado.getEstadoCivil().getNomLdvMae().equals("J")) {
    	  estadoCivil = "X";
      }
    } else {
      estadoCivil = "I";
    }
    titular.setEstadoCivil(estadoCivil);
    if (interesado.getFechaNacimiento() != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      titular.setFechaNacimiento(sdf.format(interesado.getFechaNacimiento()));
    }
    titular.setLugarNacimiento(interesado.getLugarNacimiento());
    titular.setMadre(interesado.getProgenitor2());
    // Se pasa el nombre del país sin tilde
    titular.setNacionalidad(interesado.getNacionalidad().getNomPaisMju());
    titular.setPaisNacimiento(interesado.getPaisNacimiento().getNomPaisMju());
    titular.setPadre(interesado.getProgenitor1());
    if (interesado.getSexo() != null) {
      if (interesado.getSexo().getCodLdvMae().equals("SEX-HOM")) {
        titular.setSexo("M");
      } else if (interesado.getSexo().getCodLdvMae().equals("SEX-MUJ")) {
        titular.setSexo("F");
      }
    } else {
    	titular.setSexo("X");
    }
    datos.setTitular(titular);
    peticionAlta.setDatos(datos);
    Solicitante solicitante = new Solicitante();
    solicitante.setCodigoPeticion(
        "nac" + new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(java.util.Calendar.getInstance().getTime()));
    solicitante.setIdentificacionPuesto(identificacionPuesto);
    Organismo organismo = new Organismo();
    organismo.setCodigoOrganismo(codOrganismo);
    organismo.setNombreOrganismo(nombreOrganismo);
    solicitante.setOrganismo(organismo);
    solicitante.setUsuario(usuario);
    peticionAlta.setSolicitante(solicitante);
  }

  private void setNombreViaProvincia(PersonaDomicilioDto personaDomicilioDto, Direccion direccion, Via via) {
    if (personaDomicilioDto.getPaisDto() != null && !personaDomicilioDto.getPaisDto().getCodPais().equals("724")) {
      direccion.setProvinciaDom(personaDomicilioDto.getPaisDto().getNomPaisMju());
      via.setNombreVia("PAIS ORIGEN");
    } else {
      if (personaDomicilioDto.getProvinciaDto() != null) {
        direccion.setProvinciaDom(personaDomicilioDto.getProvinciaDto().getNomProvincia());
      }
      via.setNombreVia(personaDomicilioDto.getNomVia());
    }
  }

  @Override
  protected String getUsuarioJusticia() {
    return "JOB_INT_DGP_FILIACIONES_ALTA";
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
