package es.mjusticia.sinac.core.business.facade.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.facade.ExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.SedeExpedientesFacade;
import es.mjusticia.sinac.core.business.facade.SolicitudesFacade;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.PersonasService;
import es.mjusticia.sinac.core.business.service.ProcedimientosService;
import es.mjusticia.sinac.core.eis.MotorTramitacionComponent;
import es.mjusticia.sinac.core.model.dto.AdjuntarDocumentoResultDto;
import es.mjusticia.sinac.core.model.dto.AltaExpedienteSedeResultDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToSaveDto;
import es.mjusticia.sinac.core.model.dto.ErrorDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.PersonaIdentificaDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDto;
import es.mjusticia.sinac.core.model.dto.SolicitudFormularioValDto;
import es.mjusticia.sinac.core.model.mapper.SedeExpedienteMapper;
import es.mjusticia.sinac.core.persistence.ProcedimientosDocumentosTipoDao;
import es.mjusticia.sinac.core.utils.CamposEspecificosValidator;
import es.mjusticia.sinac.sede.expedientes.AdjuntarDocumentoRequest;
import es.mjusticia.sinac.sede.expedientes.ExpedienteRequest;
import es.mjusticia.sinac.sede.expedientes.types.CampoEspecifico;
import es.mjusticia.sinac.sede.expedientes.types.DireccionInternacional;
import es.mjusticia.sinac.sede.expedientes.types.DireccionNacional;
import es.mjusticia.sinac.sede.expedientes.types.DocumentoAdjunto;
import es.mjusticia.sinac.sede.expedientes.types.Interesado;
import es.mjusticia.sinac.sede.expedientes.types.Notificaciones;

/**
 * Implementación de la fachada de negocio para operaciones de SEDE Electrónica.
 *
 * @author NTT Data
 */
@Service
public class SedeExpedientesFacadeImpl implements SedeExpedientesFacade {

  private static final Logger LOG = LoggerFactory.getLogger(SedeExpedientesFacadeImpl.class);

  @Autowired
  private PersonasService personasService;

  @Autowired
  private ProcedimientosService procedimientosService;

  @Autowired
  private MotorTramitacionComponent motorTramitacionComponent;

  @Autowired
  private SedeExpedienteMapper sedeExpedienteMapper;

  @Autowired
  private CamposEspecificosValidator camposEspecificosValidator;
  @Autowired
  private ExpedientesService expedientesService;
  @Autowired
  private SolicitudesFacade solicitudesFacade;
  @Autowired
  private ExpedientesFacade expedientesFacade;
  @Autowired
  private ProcedimientosDocumentosTipoDao procedimientosDocumentosTipoDao;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AltaExpedienteSedeResultDto crearExpedienteSedeElectronica(ExpedienteRequest request) throws SinacException {

    String numeroIdentificador = obtenerNumeroIdentificador(request);
    LOG.info("=== INICIO TRANSACCIÓN: Creación expediente SEDE CodOrigen={}, IdSolicitudOrigen{} ===",
        request.getSolicitud().getCodOrigenSolicitud(), request.getSolicitud().getIdSolicitudOrigen());

    try {
      validarRequest(request, numeroIdentificador);

      // Obtener y validar procedimiento
      ProcedimientoDto procedimiento = getProcedimientoDto(request);

      // Validar campos específicos del procedimiento (si existen)
      List<CampoEspecifico> camposRequest = getCampoEspecificos(request, procedimiento);

      validarNotificaciones(request.getNotificaciones());
      validarDireccion(request.getInteresado());

      // Convertir datos XML a DTOs del sistema para Persona primero
      LOG.debug("Convirtiendo datos XML a DTOs del sistema");
      PersonaDto interesado = sedeExpedienteMapper.xmlToPersonaDto(request.getInteresado());

      // Guardar Persona (Interesado)
      LOG.debug("Procesando persona interesada - numeroIdentificador={}", numeroIdentificador);
      PersonaDto interesadoBD = validarIdentificadorPersonaDto(interesado);

      SolicitudDto solicitudDto = sedeExpedienteMapper.xmlToSolicitudDto(request);
      if (interesadoBD != null) {
        solicitudDto.setInteresado(interesadoBD);

        LOG.info("DTOs convertidos - numeroIdentificador={}, contactos={}, domicilios={}", numeroIdentificador,
            interesadoBD.getPersonasContactosElectronicosDtos() != null
                ? interesadoBD.getPersonasContactosElectronicosDtos().size()
                : 0,
            interesadoBD.getPersonasDomiciliosDto() != null ? interesadoBD.getPersonasDomiciliosDto().size() : 0);
      }
      // Mapear campos específicos a la solicitud
      if (camposRequest != null && !camposRequest.isEmpty()) {
        List<SolicitudFormularioValDto> valoresCampos = sedeExpedienteMapper.mapearCamposEspecificos(camposRequest,
            procedimiento, solicitudDto);
        solicitudDto.setSolicitudFormularioValDtos(valoresCampos);
        LOG.info("Campos específicos mapeados: {}", valoresCampos.size());
      }

      // Guardar Solicitud
      LOG.debug("Guardando solicitud - numeroIdentificador={}, motivoSolicitud={}", numeroIdentificador,
          request.getSolicitud().getCodMotivoSolicitud());
      SolicitudDto solicitudCreada = solicitudesFacade.saveSolicitud(solicitudDto, null);
      LOG.info("Solicitud creada exitosamente - idSolicitud={}", solicitudCreada.getIdSol());

      // Crear Expediente mediante Motor de Tramitación
      LOG.debug("Iniciando creación de expediente mediante motor - idSolicitud={}", solicitudCreada.getIdSol());
      ExpedienteDto expedienteCreado = crearExpedienteDesdeMotorTramitacion(solicitudCreada);

      LOG.info("=== FIN TRANSACCIÓN: Expediente creado exitosamente - idExpediente={}, codigo={}",
          expedienteCreado.getIdExp(), expedienteCreado.getCodExp());

      return new AltaExpedienteSedeResultDto(expedienteCreado, AltaExpedienteSedeResultDto.EstadoCreacion.EXITO,
          "Expediente creado exitosamente: " + expedienteCreado.getCodExp());

    } catch (SinacException e) {
      LOG.error("=== ROLLBACK TRANSACCIÓN: Error de negocio - CodOrigen={}, IdSolicitudOrigen{}, mensaje={}",
          request.getSolicitud().getCodOrigenSolicitud(), request.getSolicitud().getIdSolicitudOrigen(), e.getMessage(),
          e);
      throw e;
    } catch (Exception e) {
      LOG.error("=== ROLLBACK TRANSACCIÓN: Error inesperado - CodOrigen={}, IdSolicitudOrigen{}, mensaje={}",
          request.getSolicitud().getCodOrigenSolicitud(), request.getSolicitud().getIdSolicitudOrigen(), e.getMessage(),
          e);
      throw new SinacException(e, SinacExceptionMessageType.SINAC_SEDE_12)
          .logMessageParams(numeroIdentificador, e.getMessage()).type(SinacExceptionType.BUSINESS);
    }
  }

  /**
   * Valida que la dirección del interesado contenga todos los campos
   * obligatorios.
   *
   * @param interesado Datos del interesado con su dirección
   * @throws SinacException
   */
  private void validarDireccion(Interesado interesado) throws SinacException {
    var direccion = interesado.getDireccion();

    if (direccion == null) {
      LOG.error("Dirección nula");
      throw new SinacException(SinacExceptionMessageType.SINAC_SEDE_2).logMessageParams("Dirección es obligatoria")
          .type(SinacExceptionType.DATA);
    }

    if (direccion.getDireccionNacional() != null) {
      validarDireccionNacional(direccion.getDireccionNacional());
    } else if (direccion.getDireccionInternacional() != null) {
      validarDireccionInternacional(direccion.getDireccionInternacional());
    } else {
      LOG.error("Dirección sin datos");
      throw new SinacException(SinacExceptionMessageType.SINAC_SEDE_2)
          .logMessageParams("Debe proporcionar una dirección nacional o internacional").type(SinacExceptionType.DATA);
    }
  }

  /**
   * Valida campos obligatorios de dirección nacional.
   *
   * @param direccion Dirección nacional a validar
   * @throws SinacException
   */
  private void validarDireccionNacional(DireccionNacional direccion) throws SinacException {

    List<String> errores = new ArrayList<>();

    if (direccion.getCodTipoVia() == null || direccion.getCodTipoVia().trim().isEmpty()) {
      errores.add("Código de Tipo de Via es obligatorio");
    }

    if (direccion.getNombreVia() == null || direccion.getNombreVia().trim().isEmpty()) {
      errores.add("Nombre de Via es obligatorio");
    }

    if (direccion.getCodLocalidad() == null || direccion.getCodLocalidad().trim().isEmpty()) {
      errores.add("Código de Localidad es obligatorio");
    }

    if (direccion.getCodProvincia() == null || direccion.getCodProvincia().trim().isEmpty()) {
      errores.add("Código de Provincia es obligatorio");
    }

    if (direccion.getPoblacion() == null || direccion.getPoblacion().trim().isEmpty()) {
      errores.add("Poblacion es obligatoria");
    }

    if (direccion.getCodigoPostal() == null || direccion.getCodigoPostal().trim().isEmpty()) {
      errores.add("Código Postal es obligatorio");
    }

    if (!errores.isEmpty()) {
      String mensajeError = "Dirección nacional incompleta: " + String.join(", ", errores);
      LOG.error("Validación de dirección nacional fallida, errores={}", mensajeError);
      throw new SinacException(SinacExceptionMessageType.SINAC_SEDE_2).logMessageParams(mensajeError)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("Dirección nacional validada correctamente");
  }

  /**
   * Valida campos obligatorios de dirección internacional.
   *
   * @param direccion Dirección internacional a validar
   * @throws SinacException
   */
  private void validarDireccionInternacional(DireccionInternacional direccion) throws SinacException {
    List<String> errores = new ArrayList<>();

    if (direccion.getCodPais() == null || direccion.getCodPais().trim().isEmpty()) {
      errores.add("Código de País es obligatorio");
    }

    if (direccion.getNombreViaInternacional() == null || direccion.getNombreViaInternacional().trim().isEmpty()) {
      errores.add("Nombre de Via Internacional es obligatorio");
    }

    if (direccion.getCodigoPostalInternacional() == null || direccion.getCodigoPostalInternacional().trim().isEmpty()) {
      errores.add("Código Postal Internacional es obligatorio");
    }

    if (direccion.getPoblacionInternacional() == null || direccion.getPoblacionInternacional().trim().isEmpty()) {
      errores.add("Poblacion Internacional es obligatoria");
    }

    if (!errores.isEmpty()) {
      String mensajeError = "Dirección internacional incompleta: " + String.join(", ", errores);
      LOG.error("Validación de dirección internacional fallida, errores={}", mensajeError);
      throw new SinacException(SinacExceptionMessageType.SINAC_SEDE_2).logMessageParams(mensajeError)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("Dirección internacional validada correctamente");
  }

  /**
   * Valida las notificaciones según reglas de negocio condicionales. Si
   * consentimiento = true, ciertos campos son obligatorios.
   */
  private void validarNotificaciones(Notificaciones notificaciones) throws SinacException {

    if (notificaciones == null) {
      LOG.error("Notificaciones de solicitud nulas");
      throw new SinacException(SinacExceptionMessageType.SINAC_SEDE_2)
          .logMessageParams("Notificaciones son obligatorias").type(SinacExceptionType.DATA);
    }

    boolean consentimiento = notificaciones.isConsentimientoNotificaciones();
    if (!consentimiento) {
      LOG.debug("Sin consentimiento de notificaciones - validación omitida");
      return;
    }

    // Con consentimiento: validar campos obligatorios
    List<String> errores = new ArrayList<>();
    if (StringUtils.isBlank(notificaciones.getDestinatarioNotificaciones())) {
      errores.add("Destinatario Notificaciones es obligatorio cuando hay consentimiento");
    }

    if (notificaciones.getDireccionNotificaciones() == null) {
      errores.add("Dirección Notificaciones es obligatoria cuando hay consentimiento");
    } else {
      // Validar que tenga al menos una dirección
      var direccion = notificaciones.getDireccionNotificaciones();
      if (direccion.getDireccionNacionalNotificaciones() == null
          && direccion.getDireccionInternacionalNotificaciones() == null) {
        errores.add("Debe proporcionar Dirección Nacional o Dirección Es Internacional para Notificaciones");
      }
    }

    if (!errores.isEmpty()) {
      String mensajeError = String.join("; ", errores);
      LOG.error("Validación de notificaciones fallida, errores={}", mensajeError);
      throw new SinacException(SinacExceptionMessageType.SINAC_SEDE_2).logMessageParams(mensajeError)
          .type(SinacExceptionType.DATA);
    }

    LOG.info("Notificaciones validadas correctamente");
  }

  private PersonaDto validarIdentificadorPersonaDto(PersonaDto interesado) {
    String numAcreditacion = obtenerNumeroAcreditacionPrincipal(interesado);

    List<PersonaDto> personasEncontradas = personasService.getPersonasRastreoVea(numAcreditacion);

    PersonaDto personaDto = null;

    if (personasEncontradas == null || personasEncontradas.isEmpty()) {
      // No existe -> crear
      LOG.info("No se encontró persona, creando - numeroIdentificador={}", numAcreditacion);
      personaDto = personasService.savePersona(interesado);
      LOG.info("Persona creada exitosamente - idPer={}", personaDto != null ? personaDto.getIdPer() : "null");
    } else {
      // Existe una -> reutilizar
      personaDto = personasEncontradas.get(0);
      LOG.info("Se encontraron {} coincidencias para identificador={}, usando la primera idPer={}",
          personasEncontradas.size(), numAcreditacion, personaDto.getIdPer());
    }
    return personaDto;

  }

  /**
   * Obtiene el número de identificación principal del interesado.
   */
  private String obtenerNumeroAcreditacionPrincipal(PersonaDto personaDto) {
    if (personaDto == null || personaDto.getPersonasIdentificaDtos() == null) {
      return null;
    }

    // Buscar la identificación marcada como principal
    return personaDto.getPersonasIdentificaDtos().stream().filter(PersonaIdentificaDto::getFlgPrincipal)
        .map(PersonaIdentificaDto::getNumAcreditacion).findFirst().orElse(null);
  }

  private List<CampoEspecifico> getCampoEspecificos(ExpedienteRequest request, ProcedimientoDto procedimiento) {
    List<CampoEspecifico> camposRequest = obtenerCamposEspecificos(request);
    if (camposRequest != null && !camposRequest.isEmpty()) {
      LOG.info("Validando {} campos específicos del procedimiento", camposRequest.size());
      camposEspecificosValidator.validarCamposEspecificos(camposRequest, procedimiento);
      LOG.info("Campos específicos validados correctamente");
    }
    return camposRequest;
  }

  private ProcedimientoDto getProcedimientoDto(ExpedienteRequest request) {
    String codProcedimiento = request.getSolicitud().getCodProcedimientoSolicitud();
    ProcedimientoDto procedimiento = procedimientosService.getProcedimientoByCodPro(codProcedimiento);
    LOG.debug("Obteniendo procedimiento: {}", codProcedimiento);

    if (procedimiento == null) {
      LOG.error("Procedimiento no encontrado: {}", codProcedimiento);
      throw new SinacException(SinacExceptionMessageType.SINAC_SEDE_5).logMessageParams(codProcedimiento)
          .type(SinacExceptionType.BUSINESS);
    }
    return procedimiento;
  }

  /**
   * Crea el expediente mediante el motor de tramitación ejecutando la acción
   * CRES.
   *
   * @param solicitudCreada Solicitud previamente creada
   * @return Expediente creado por el motor
   * @throws SinacException Si el motor falla o no retorna un expediente
   */
  private ExpedienteDto crearExpedienteDesdeMotorTramitacion(SolicitudDto solicitudCreada) throws SinacException {

    Map<String, Object> valores = new HashMap<>();
    valores.put("idSol", solicitudCreada.getIdSol());
    valores.put("idPro", solicitudCreada.getProcedimientoDto().getIdPro());
    valores.put("flgNoValidar", true);

    // Obtener el ID de la acción CRES (Crear Expediente SEDE)
    Long idPftoa = obtenerIdAccionCrearExpedienteSede(solicitudCreada);

    LOG.debug("Ejecutando motor de tramitación - idPftoa={}, idSolicitud={}", idPftoa, solicitudCreada.getIdSol());

    // Ejecutar el motor de tramitación
    motorTramitacionComponent.ejecutarAccion(idPftoa, valores);

    ExpedienteDto expedienteCreado = validarCreacionExpediente(solicitudCreada, valores);

    String codigoExpediente = valores.get("codExp") != null ? valores.get("codExp").toString()
        : expedienteCreado.getCodExp();
    expedienteCreado.setCodExp(codigoExpediente);

    LOG.info("Motor de tramitación ejecutado exitosamente - idExpediente={}, codigo={}", expedienteCreado.getIdExp(),
        codigoExpediente);

    return expedienteCreado;
  }

  private ExpedienteDto validarCreacionExpediente(SolicitudDto solicitudCreada, Map<String, Object> valores) {
    // Verificar que el motor creó el expediente exitosamente
    Boolean expedienteCreadoFlag = (Boolean) valores.get("expedienteCreado");
    if (expedienteCreadoFlag == null || !expedienteCreadoFlag) {
      LOG.error("Motor de tramitación indicó que no se creó expediente para idSolicitud={}",
          solicitudCreada.getIdSol());
      throw new SinacException(SinacExceptionMessageType.SINAC_SEDE_11)
          .logMessageParams(solicitudCreada.getIdSol(), "Motor de tramitación indicó que no se creó expediente")
          .type(SinacExceptionType.BUSINESS);
    }

    BigInteger idExpediente = (BigInteger) valores.get("idExp");
    if (idExpediente == null) {
      LOG.error("Motor de tramitación no retornó idExpediente para solicitud, idSolicitud={}",
          solicitudCreada.getIdSol());
      throw new SinacException(SinacExceptionMessageType.SINAC_SEDE_11)
          .logMessageParams(solicitudCreada.getIdSol(), "Motor de tramitación no retornó idExpediente")
          .type(SinacExceptionType.BUSINESS);
    }

    ExpedienteDto expedienteCreado;
    try {
      expedienteCreado = expedientesService.getExpedienteByIdExpediente(idExpediente);
    } catch (Exception e) {
      LOG.error("Error obteniendo expediente creado - idExpediente={}", idExpediente, e);
      throw new SinacException(e, SinacExceptionMessageType.SINAC_SEDE_11)
          .logMessageParams(solicitudCreada.getIdSol(), "Error obteniendo expediente creado")
          .type(SinacExceptionType.BUSINESS);
    }
    return expedienteCreado;
  }

  /**
   * Obtiene el identificador de la acción CRES (Crear Expediente SEDE).
   *
   * @param solicitudDto Solicitud con el procedimiento
   * @return ID de la acción CRES
   * @throws SinacException Si no se encuentra la acción
   */
  private Long obtenerIdAccionCrearExpedienteSede(SolicitudDto solicitudDto) throws SinacException {
    String codProcedimiento = solicitudDto.getProcedimientoDto().getCodPro();

    try {
      return procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(codProcedimiento,
          "INI", "INI", "SOL", "CRES");
    } catch (Exception e) {
      LOG.error("Error obteniendo idPftoa para acción CRES - codProcedimiento={}", codProcedimiento, e);
      throw new SinacException(e, SinacExceptionMessageType.SINAC_SEDE_11)
          .logMessageParams(codProcedimiento, "Error obteniendo idPftoa para CRES").type(SinacExceptionType.BUSINESS);
    }
  }

  /**
   * Extrae el número de identificación del request para trazabilidad.
   */
  private String obtenerNumeroIdentificador(ExpedienteRequest request) {
    return (request.getInteresado() != null) ? request.getInteresado().getNumeroIdentificador() : "desconocido";
  }

  /**
   * Valida que el request tenga los datos mínimos requeridos.
   *
   * @throws SinacException Si faltan datos obligatorios
   */
  private void validarRequest(ExpedienteRequest request, String numeroIdentificador) throws SinacException {

    if (request == null || request.getSolicitud() == null) {
      LOG.error("Request o solicitud nulos - numeroIdentificador={}", numeroIdentificador);
      throw new SinacException(SinacExceptionMessageType.SINAC_SEDE_1).logMessageParams(numeroIdentificador)
          .type(SinacExceptionType.DATA);
    }

    if (request.getInteresado() == null) {
      LOG.error("Interesado nulo - numeroIdentificador={}", numeroIdentificador);
      throw new SinacException(SinacExceptionMessageType.SINAC_SEDE_2).logMessageParams(numeroIdentificador)
          .type(SinacExceptionType.DATA);
    }
  }

  /**
   * Extrae los campos específicos del procedimiento del request.
   */
  private List<CampoEspecifico> obtenerCamposEspecificos(ExpedienteRequest request) {
    if (request.getSolicitud() != null && request.getSolicitud().getCamposEspecificosProcedimiento() != null) {
      return request.getSolicitud().getCamposEspecificosProcedimiento().getCampo();
    }
    return null;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AdjuntarDocumentoResultDto adjuntarDocumentoExpediente(AdjuntarDocumentoRequest request)
      throws SinacException {

    LOG.info("=== INICIO: Adjuntar documentos a expediente ===");
    LOG.info("Expediente: {}/{}, Documentos: {}", request.getNumeroExpediente(), request.getAnioExpediente(),
        request.getDocumentos().getDocumento().size());

    try {
      // Validar y obtener expediente
      String codExpediente = request.getNumeroExpediente() + "/" + request.getAnioExpediente();

      ExpedienteDto expediente = expedientesService.getExpedienteSimpleByCodExpediente(codExpediente);

      if (expediente == null) {
        LOG.warn("Expediente no encontrado: {}", codExpediente);
        return new AdjuntarDocumentoResultDto(AdjuntarDocumentoResultDto.EstadoAdjunto.ERROR_EXPEDIENTE_NO_ENCONTRADO,
            0, "Expediente no encontrado o no pertenece al interesado");
      }

      validarTipoDocumentoByExpediente(request, expediente, codExpediente);

      // Mapear documentos XML a DocumentoToSaveDto usando el mapper
      LinkedList<DocumentoToSaveDto> documentos = sedeExpedienteMapper.mapearDocumentosAdjuntar(request.getDocumentos(),
          expediente);

      List<DocumentoToSaveDto> documentosGuardados = expedientesFacade
          .saveDocumentosEntradaExpediente(expediente.getIdExp(), documentos);

      // Contar documentos guardados exitosamente
      int documentosExitosos = (int) documentosGuardados.stream()
          .filter(d -> Boolean.TRUE.equals(d.getDocumentoFlagsToSaveDto().getInsertadoBaseDatos())).count();

      // Coleccionar documentos exitosos
      List<String> exitosos = documentosGuardados.stream()
          .filter(d -> Boolean.TRUE.equals(d.getDocumentoFlagsToSaveDto().getInsertadoBaseDatos()))
          .map(DocumentoToSaveDto::getNombre).collect(Collectors.toList());

      // Coleccionar errores individuales en ErrorDocumentoDto
      List<ErrorDocumentoDto> erroresIndividuales = documentosGuardados.stream()
          .filter(d -> !Boolean.TRUE.equals(d.getDocumentoFlagsToSaveDto().getInsertadoBaseDatos()))
          .map(d -> new ErrorDocumentoDto(d.getNombre(), d.getError())).collect(Collectors.toList());

      LOG.info("=== FIN: {} documentos adjuntados exitosamente, {} errores ===", documentosExitosos,
          erroresIndividuales.size());

      // Determinar estado: OK si al menos 1 documento se adjuntó, KO si ninguno
      AdjuntarDocumentoResultDto.EstadoAdjunto estado = (documentosExitosos > 0)
          ? AdjuntarDocumentoResultDto.EstadoAdjunto.EXITO
          : AdjuntarDocumentoResultDto.EstadoAdjunto.ERROR_VALIDACION;

      AdjuntarDocumentoResultDto resultado = new AdjuntarDocumentoResultDto(estado, documentosExitosos,
          String.format("Se adjuntaron %d de %d documentos", documentosExitosos, documentos.size()),
          erroresIndividuales);
      resultado.setTotalDocumentos(documentos.size());
      resultado.setDocumentosExitosos(exitosos);

      return resultado;

    } catch (SinacException e) {
      LOG.error("=== ERROR SINAC: Adjuntando documentos ===", e);
      throw e;

    } catch (Exception e) {
      LOG.error("=== ERROR INESPERADO: Adjuntando documentos ===", e);
      throw new SinacException(e, SinacExceptionMessageType.SINAC_SEDE_12).type(SinacExceptionType.BUSINESS)
          .logMessageParams(request.getNumeroExpediente());
    }
  }

  private void validarTipoDocumentoByExpediente(AdjuntarDocumentoRequest request, ExpedienteDto expediente,
      String codExpediente) {
    List<String> tipoDocumentoValidos = procedimientosDocumentosTipoDao
        .getTipoDocPorProMDocTipoFlgEntrada(expediente.getProcedimientoDto().getIdPro()).stream()
        .map(tipoDocumento -> tipoDocumento.getDocumentosTipo().getCodTipo()).toList();

    for (DocumentoAdjunto documento : request.getDocumentos().getDocumento()) {
      if (!tipoDocumentoValidos.contains(documento.getCodTipoDocumento())) {
        LOG.warn("Tipo de documento no válido para adjuntar: {} en expediente {}", documento.getCodTipoDocumento(),
            codExpediente);
        throw new SinacException(SinacExceptionMessageType.SINAC_TIPO_DOCUMENTO_PROCEDIMIENTO)
            .type(SinacExceptionType.BUSINESS).logMessageParams(request.getNumeroExpediente());
      }
    }
  }

}
