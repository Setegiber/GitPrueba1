package es.mjusticia.sinac.core.business.facade.impl;

/*-
 * #%L
 * sinac-core
 * %%
 * Copyright (C) 2022 - 2023 Ministerio de Justicia
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.facade.SolicitudesFacade;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.DocumentosService;
import es.mjusticia.sinac.core.business.service.ExpedientesService;
import es.mjusticia.sinac.core.business.service.PaisesService;
import es.mjusticia.sinac.core.business.service.PersonasService;
import es.mjusticia.sinac.core.business.service.PlantillasService;
import es.mjusticia.sinac.core.business.service.ProcedimientosService;
import es.mjusticia.sinac.core.business.service.SolicitudesService;
import es.mjusticia.sinac.core.model.dto.BusquedaSolicitudesDto;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToSaveDto;
import es.mjusticia.sinac.core.model.dto.EstadoSolicitudDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.FormularioCamposValidaDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.LocalidadesDto;
import es.mjusticia.sinac.core.model.dto.PaisesDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.PersonaFamDto;
import es.mjusticia.sinac.core.model.dto.PlantillaDto;
import es.mjusticia.sinac.core.model.dto.ProvinciasDto;
import es.mjusticia.sinac.core.model.dto.RegistroDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaSolicitudesDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDocumentoDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDto;
import es.mjusticia.sinac.core.model.dto.SolicitudFormularioValDto;
import es.mjusticia.sinac.core.model.dto.TiposViaDto;
import es.mjusticia.sinac.core.persistence.ProcedimientoDao;
import es.mjusticia.sinac.core.utils.Constantes;
import es.mjusticia.sinac.core.utils.UtilFiltro;
import es.mjusticia.sinac.core.utils.Utilidades;

@Service
@Transactional(readOnly = true)
public class SolicitudesFacadeImpl implements SolicitudesFacade {

  private static final Logger LOG = LoggerFactory.getLogger(ExpedientesFacadeImpl.class);

  @Autowired
  private SolicitudesService solicitudService;

  @Autowired
  private PersonasService personaService;

  @Autowired
  private DocumentosService documentoService;

  @Autowired
  private CatalogosService catalogoService;

  @Autowired
  private PlantillasService plantillasService;

  @Autowired
  private ProcedimientoDao procedimientoDao;

  @Autowired
  private ProcedimientosService procedimientosService;

  @Autowired
  private PaisesService paisesService;

  @Autowired
  private ExpedientesService expedientesService;

  private void saveSolicitudDocumentos(SolicitudDto solicitudDto, List<DocumentoToSaveDto> documentosToSave,
      String rutaNfs) throws SinacException {
    for (DocumentoToSaveDto solicitudDocumento : documentosToSave) {

      if (Boolean.TRUE.equals(solicitudDocumento.getDocumentoFlagsToSaveDto().getCopiadoNFS())) {
        SolicitudDocumentoDto documento = new SolicitudDocumentoDto();
        DocumentoTipoDto dtd = new DocumentoTipoDto();
        dtd.setIdDocTipo(solicitudDocumento.getTipoDocumento());
        documento.setDocumentoTipoDto(dtd);
        LdvMaestraDto estadoDocumento = catalogoService.getCatalogoByCod("EDOC-BOR");
        LdvMaestraDto estadoElaboracion = new LdvMaestraDto();
        estadoElaboracion.setIdLdvMae(solicitudDocumento.getEstadoElaboracion());
        LdvMaestraDto organo = new LdvMaestraDto();
        organo.setIdLdvMae(solicitudDocumento.getOrgano());
        LdvMaestraDto origen = new LdvMaestraDto();
        origen.setIdLdvMae(solicitudDocumento.getOrigen());
        documento.setLdvMaestraDtoByIdEstDocLdv(estadoDocumento);
        documento.setLdvMaestraDtoByIdEstElaLdv(estadoElaboracion);
        documento.setLdvMaestraDtoByIdOrgLdv(organo);
        documento.setLdvMaestraDtoByIdOriDocLdv(origen);
        documento.setNomDoc(solicitudDocumento.getNombre());
        documento.setFechaEntrada(solicitudDto.getRegistroDtos().get(0).getFechaReg());
        documento.setNfsRuta(rutaNfs);
        documento.setSolicitudDto(solicitudDto);
        solicitudService.saveSolicitudDocumento(documento);
      }
    }
  }

  /*
   * private void validarDocumentos(List<DocumentoToSaveDto> documentosToSave) {
   * documentoService.validateDocumentosSolicitudAntivirus(documentosToSave); for
   * (DocumentoToSaveDto docs : documentosToSave) { if
   * (Validaciones.validarExtensionDocumento(docs.getNombre()) &&
   * Validaciones.validarTamanyoDocumento(docs.getNombre(), docs.getContenido()))
   * { docs.getDocumentoFlagsToSaveDto().setValidado(true); } else {
   * documentosToSave.removeIf(n -> n.getNombre().equals(docs.getNombre())); } } }
   */

  @Override
  public LdvMaestraDto getCatalogoById(int id) throws SinacException {
    return catalogoService.getCatalogoById(id);
  }

  @Override
  public SolicitudDto getSolicitudPorId(BigInteger idSol) throws SinacException {
    return solicitudService.getSolicitudPorId(idSol);
  }

  @Transactional(readOnly = false)
  @Override
  public SolicitudDto saveSolicitud(SolicitudDto solicitud, List<DocumentoToSaveDto> documentosToSave)
      throws SinacException {
    LOG.info("Iniciando el proceso de guardado de la solicitud.");

    // Comprueba que las personas no tengan ciertos parámetros iguales
    LOG.debug("Comprobando que las personas no tengan parámetros iguales.");
    comprobarPersonasDistintas(solicitud);

    boolean actualizar = false;

    // Se cambian a nulo los campos vacíos o con valor 0
    LOG.debug("Limpiando campos vacíos o con valor 0.");
    limpiarCamposCombos(solicitud);

    // Se comprueba si es una edición
    if (solicitud.getIdSol() != null) {
      LOG.debug("La solicitud tiene un ID, se procederá a actualizar.");
      actualizar = true;
    }

    // Se guardan los datos del interesado
    if (solicitud.getInteresado() != null) {
      LOG.debug("Guardando los datos del interesado.");
      PersonaDto interesado = personaService.savePersona(solicitud.getInteresado());
      solicitud.setInteresado(interesado);
    }

    // Procesar representantes
    LOG.debug("Procesando los representantes de la solicitud.");
    procesarRepresentantes(solicitud, actualizar);

    // Configuración de estado inicial
    LOG.debug("Configurando el estado inicial de la solicitud.");
    solicitud.setLdvMaestraDtoByIdEstSolLdv(
        catalogoService.getCatalogoByCod(Constantes.EstadosSolicitud.ESTADO_SOLICITUD_BORRADOR));

    // Configuración de origen si no está definido
    if (solicitud.getLdvMaestraDtoByIdOriSolLdv() == null) {
      LOG.debug("Configurando el origen de la solicitud como 'EXP-GEISER'.");
      solicitud.setLdvMaestraDtoByIdOriSolLdv(catalogoService.getCatalogoByCod("EXP-GEISER"));
    }

    // Guardar relación de personaCorreoElec y personaDomicilio con la solicitud
    LOG.debug("Guardando la relación de personaCorreoElec y personaDomicilio con la solicitud.");
    solicitudService.saveSolicitudesCorDom(solicitud);

    // Configuración de estado acumulado si aplica
    if (solicitud.getIdExpedienteAcumular() != null) {
      LOG.debug("Configurando el estado de la solicitud como acumulado.");
      solicitud.setLdvMaestraDtoByIdEstSolLdv(
          catalogoService.getCatalogoByCod(Constantes.EstadosSolicitud.ESTADO_SOLICITUD_ACUMULADO));
    }

    // Configuración de fecha de efectos
    if (!solicitud.getRegistroDtos().isEmpty() && solicitud.getRegistroDtos().get(0).getFechaReg() != null) {
      LOG.debug("Configurando la fecha de efectos de la solicitud.");
      solicitud.setFechaEfectos(solicitud.getRegistroDtos().get(0).getFechaReg());
    }

    // Guardar la solicitud
    LOG.debug("Guardando la solicitud en la base de datos.");
    SolicitudDto solicitudR = solicitudService.saveSolicitud(solicitud);
    solicitud.setIdSol(solicitudR.getIdSol());

    // Configuración de notificaciones y consentimiento del interesado
    LOG.debug("Configurando notificaciones y consentimiento del interesado.");
    solicitud.getInteresado().setFlgNotificar(!StringUtils.isEmpty(solicitud.getTipoPersonaNotificar())
        && solicitud.getTipoPersonaNotificar().equals(Constantes.Personas.TIPO_INTERESADO));
    solicitud.getInteresado()
        .setFlgConsiente(!StringUtils.isEmpty(solicitud.getTipoPersonaNotificar())
            && solicitud.getTipoPersonaNotificar().equals(Constantes.Personas.TIPO_INTERESADO)
            && Boolean.TRUE.equals(solicitud.getFlgPersonaConsiente()));

    // Guardar la relación entre interesado y la solicitud
    LOG.debug("Guardando la relación entre el interesado y la solicitud.");
    solicitudService.saveSolicitudesPersonas(solicitud, solicitud.getInteresado(),
        catalogoService.getCatalogoByCod(Constantes.Personas.TIPO_INTERESADO));

    // Procesar representantes adicionales
    LOG.debug("Procesando representantes adicionales.");
    LdvMaestraDto ldvMotivoRepre = solicitud.getMotivoRepresentacion();
    String codLdvMotivoRepre = (ldvMotivoRepre != null) ? ldvMotivoRepre.getCodLdvMae() : "";

    if (solicitud.getRepresentante1() != null) {
      LOG.debug("Procesando representante 1.");
      solicitud.getRepresentante1().setFlgNotificar(!StringUtils.isEmpty(solicitud.getTipoPersonaNotificar())
          && solicitud.getTipoPersonaNotificar().equals(Constantes.Personas.TIPO_REPRESENTANTE_UNO));
      solicitud.getRepresentante1()
          .setFlgConsiente(!StringUtils.isEmpty(solicitud.getTipoPersonaNotificar())
              && solicitud.getTipoPersonaNotificar().equals(Constantes.Personas.TIPO_REPRESENTANTE_UNO)
              && Boolean.TRUE.equals(solicitud.getFlgPersonaConsiente()));
      saveOrDeleteUnionRepresentante(solicitudR, actualizar, codLdvMotivoRepre, solicitud.getRepresentante1());
    }

    if (solicitud.getRepresentante2() != null) {
      LOG.debug("Procesando representante 2.");
      solicitud.getRepresentante2().setFlgNotificar(!StringUtils.isEmpty(solicitud.getTipoPersonaNotificar())
          && solicitud.getTipoPersonaNotificar().equals(Constantes.Personas.TIPO_REPRESENTANTE_DOS));
      solicitud.getRepresentante2()
          .setFlgConsiente(!StringUtils.isEmpty(solicitud.getTipoPersonaNotificar())
              && solicitud.getTipoPersonaNotificar().equals(Constantes.Personas.TIPO_REPRESENTANTE_DOS)
              && Boolean.TRUE.equals(solicitud.getFlgPersonaConsiente()));
      saveOrDeleteUnionRepresentante(solicitud, actualizar, codLdvMotivoRepre, solicitud.getRepresentante2());
    }

    if (solicitud.getRepresentanteMandato() != null) {
      LOG.debug("Procesando representante mandato.");
      solicitud.getRepresentanteMandato().setFlgNotificar(!StringUtils.isEmpty(solicitud.getTipoPersonaNotificar())
          && solicitud.getTipoPersonaNotificar().equals(Constantes.Personas.TIPO_REPRESENTANTE_MANDATO));
      solicitud.getRepresentanteMandato()
          .setFlgConsiente(!StringUtils.isEmpty(solicitud.getTipoPersonaNotificar())
              && solicitud.getTipoPersonaNotificar().equals(Constantes.Personas.TIPO_REPRESENTANTE_MANDATO)
              && Boolean.TRUE.equals(solicitud.getFlgPersonaConsiente()));
      saveOrDeleteUnionRepresentante(solicitud, actualizar, Constantes.Personas.TIPO_REPRESENTANTE_MANDATO,
          solicitud.getRepresentanteMandato());
    }

    // Desactivar formularios anteriores si es una actualización
    if (actualizar) {
      LOG.debug("Desactivando formularios anteriores de la solicitud.");
      solicitudService.desactivarSolicitudFormularioValAnterioresByIdSol(solicitud.getIdSol());
    }

    // Guardar campos dinámicos, checks y eliminar documentos antiguos
    LOG.debug("Guardando campos dinámicos, checks de oposición y eliminando documentos antiguos.");
    saveCamposDinamicosByProcedimiento(solicitud);

    deleteSolicitudesDocumentosByIds(solicitud);

    // Procesar acumulación de expedientes
    LOG.debug("Procesando acumulación de expedientes.");
    procesarAcumulacionExpediente(solicitud);

    // Procesar documentos de la solicitud
    LOG.debug("Procesando documentos de la solicitud.");
    procesarDocumentosSolicitud(solicitud, documentosToSave);

    // Guardar registros de la solicitud
    LOG.debug("Guardando registros de la solicitud.");
    saveRegistrosSolicitud(solicitud);

    LOG.info("Finalizado el proceso de guardado de la solicitud.");
    return solicitud;
  }

  private void procesarDocumentosSolicitud(SolicitudDto solicitud, List<DocumentoToSaveDto> documentosToSave) {
    if ("EXP-SED".equals(solicitud.getLdvMaestraDtoByIdOriSolLdv().getCodLdvMae())) {
      if (solicitud.getSolicitudDocumentoDtos() != null && !solicitud.getSolicitudDocumentoDtos().isEmpty()) {
        for (SolicitudDocumentoDto doc : solicitud.getSolicitudDocumentoDtos()) {
          doc.setSolicitudDto(new SolicitudDto());
          doc.getSolicitudDto().setIdSol(solicitud.getIdSol());
          solicitudService.saveSolicitudDocumento(doc);
        }
      }
    } else {
      saveDocumentosSolicitudesNfs(solicitud, documentosToSave);
    }
  }

  private void procesarAcumulacionExpediente(SolicitudDto solicitud) {
    if (solicitud.getIdExpedienteAcumular() != null) {
      ExpedienteDocumentoDto expedienteDocumentoAcumulacion = new ExpedienteDocumentoDto();
      ExpedienteDto expedienteDestino = expedientesService.getExpedientebyId(solicitud.getIdExpedienteAcumular());

      // Acumular personas de la solicitud al expediente
      expedientesService.acumularPersonasFromSolicitudToExpediente(expedienteDestino, solicitud);

      // Crear contenido del documento de acumulación
      PlantillaDto plantilla = plantillasService.getPlantillaPorCod("OACUCN");
      documentoService.createExpedienteDocumentoPlantillaContent(plantilla, expedienteDestino,
          expedienteDocumentoAcumulacion, null);

      // Preparar y guardar el documento
      List<DocumentoToSaveDto> documentoToSaveDtoList = new ArrayList<>();
      DocumentoToSaveDto documentoToSaveDto = new DocumentoToSaveDto();
      documentoToSaveDto.setNombre(expedienteDocumentoAcumulacion.getNomDoc());
      documentoToSaveDto.setContenido(expedienteDocumentoAcumulacion.getContenido());
      documentoToSaveDtoList.add(documentoToSaveDto);

      documentoToSaveDto.getDocumentoFlagsToSaveDto().setValidado(true);
      documentoToSaveDto.getDocumentoFlagsToSaveDto().setGuardadoGestorDocumental(false);

      documentoToSaveDtoList.replaceAll(d -> documentoService.copyDocumentoNFS(expedienteDestino.getCodExp(),
          expedienteDestino.getProcedimientoDto().getCodPro(), expedienteDestino.getFechaEfectos(), d));

      expedienteDocumentoAcumulacion = documentoService.saveExpedienteDocumento(expedienteDocumentoAcumulacion,
          expedienteDestino);
      expedienteDocumentoAcumulacion.setNfsRuta(documentoToSaveDto.getRutaNFS());

      // Firmar y actualizar el estado del documento
      documentoService.signDocumento(expedienteDocumentoAcumulacion);
      documentoService.updateEstadoDocumento(expedienteDocumentoAcumulacion.getIdExpDoc(),
          catalogoService.getCatalogoByCod("EDOC-FIR"));

      // TODO: Comunicar documento
    }
  }

  private void procesarRepresentantes(SolicitudDto solicitud, boolean actualizar) {
    if (solicitud.getRepresentante1() != null) {
      solicitud.setRepresentante1(saveOrDeleteRepresentante(solicitud, actualizar, solicitud.getRepresentante1()));
    }
    if (solicitud.getRepresentante2() != null) {
      solicitud.setRepresentante2(saveOrDeleteRepresentante(solicitud, actualizar, solicitud.getRepresentante2()));
    }
    if (solicitud.getRepresentanteMandato() != null) {
      solicitud.setRepresentanteMandato(
          saveOrDeleteRepresentante(solicitud, actualizar, solicitud.getRepresentanteMandato()));
    }
  }

//  private void saveChecksOposicion(SolicitudDto solicitud) {
//    if (solicitud.getSolOpoForDatosDtos() != null && !solicitud.getSolOpoForDatosDtos().isEmpty()) {
//      solicitud.getSolOpoForDatosDtos().stream()
//          .forEach(solOpoForDato -> solicitudService.saveSolOpoForDatos(solOpoForDato, solicitud.getIdSol()));
//    }
//  }

  private void saveDocumentosSolicitudesNfs(SolicitudDto solicitud, List<DocumentoToSaveDto> documentosToSave) {
    String codPro = procedimientoDao.recuperarProcedimiento(solicitud.getProcedimientoDto().getIdPro()).getCodPro();
    if (documentosToSave != null && !documentosToSave.isEmpty()) {
      String rutaNfs = documentoService.copyDocumentosSolicitudesNFS(solicitud.getIdSol().toString(), codPro,
          documentosToSave);
      saveSolicitudDocumentos(solicitud, documentosToSave, rutaNfs);
    }
  }

  private void saveCamposDinamicosByProcedimiento(SolicitudDto solicitud) {
    for (FormularioCamposValidaDto formularioCampos : solicitud.getProcedimientoDto().getFormularioCamposValidaDtos()) {
      if (!formularioCampos.isFlgExpediente() && formularioCampos.getSolicitudFormularioValDtos() != null
          && !formularioCampos.getSolicitudFormularioValDtos().isEmpty()
          && formularioCampos.getSolicitudFormularioValDtos().get(0).getValor() != null
          && !formularioCampos.getSolicitudFormularioValDtos().get(0).getValor().isEmpty()) {
        saveFormularioCamposValida(solicitud, formularioCampos,
            formularioCampos.getSolicitudFormularioValDtos().get(0).getValor());
      }
    }
  }

  private void saveRegistrosSolicitud(SolicitudDto solicitud) {
    if (!CollectionUtils.isEmpty(solicitud.getRegistroDtos()) && solicitud.getRegistroDtos().get(0) != null) {
      Date fechaRegistro = solicitud.getRegistroDtos().get(0).getFechaReg();
      String numReg = solicitud.getRegistroDtos().get(0).getNumReg();
      if (fechaRegistro != null && numReg != null && !numReg.isEmpty()) {
        for (SolicitudDocumentoDto solicitudDocumentoDto : solicitudService
            .getDocsSolBySolicitudId(solicitud.getIdSol())) {
          RegistroDto registroDto = documentoService
              .getRegistroByIdSolicitudDocumento(solicitudDocumentoDto.getIdSolDoc());
          if (registroDto == null) {
            registroDto = new RegistroDto();
          }
          registroDto.setFechaReg(fechaRegistro);
          registroDto.setNumReg(numReg);
          registroDto.setSolicitudDocumentoDto(solicitudDocumentoDto);
          registroDto.setLdvMaestraDto(catalogoService.getCatalogoByCod("OREG-ENT"));
          documentoService.saveRegistro(registroDto);
        }
      }
    }
  }

  private void deleteSolicitudesDocumentosByIds(SolicitudDto solicitud) {
    List<Integer> listaIds = solicitud.getListaIdEliminarDocumento();
    List<DocumentoToSaveDto> documentosEliminar = new ArrayList<>();
    for (SolicitudDocumentoDto solicitudDocumentoDto : solicitudService.getDocsSolBySolicitudId(solicitud.getIdSol())) {
      Integer id = solicitudDocumentoDto.getIdSolDoc().intValue();
      if (listaIds.contains(id)) {
        RegistroDto registroDto = documentoService
            .getRegistroByIdSolicitudDocumento(solicitudDocumentoDto.getIdSolDoc());
        if (registroDto != null) {
          documentoService.deleteRegistro(registroDto);
        }
        DocumentoToSaveDto documentoEliminar = new DocumentoToSaveDto();
        documentoEliminar.setNombre(solicitudDocumentoDto.getNomDoc());
        documentoEliminar.setRutaNFS(solicitudDocumentoDto.getNfsRuta());
        documentosEliminar.add(documentoEliminar);
        solicitudService.deleteSolicitudDocumento(solicitudDocumentoDto);
      }
    }
    documentoService.deleteDocumentosSolicitudesNFS(documentosEliminar);
  }

  private void saveFormularioCamposValida(SolicitudDto solicitud, FormularioCamposValidaDto formularioCampos,
      String valorCampo) {
    if (valorCampo != null && !valorCampo.isEmpty()) {
      SolicitudFormularioValDto solicitudFormularioValDto = new SolicitudFormularioValDto();
      solicitudFormularioValDto.setFormularioCamposValidaDto(formularioCampos);
      solicitudFormularioValDto.setSolicitudDto(solicitud);
      if (valorCampo.equalsIgnoreCase("null")) {
        solicitudFormularioValDto.setValor("No");
      } else {
        solicitudFormularioValDto.setValor(valorCampo);
      }

      solicitudService.saveSolicitudFormularioVal(solicitudFormularioValDto);
    }
  }

  private void saveOrDeleteUnionRepresentante(SolicitudDto solicitud, boolean actualizar, String codLdv,
      PersonaDto personaDto) {
    // Si se está editando, el representante tiene id pero no tiene sus campos
    // obligatorios, se elimina
    if (actualizar && !isCamposObligatoriosPersona(personaDto) && personaDto.getIdPer() != null) {
      solicitudService.deleteSolicitudPersona(solicitud, personaDto);
    } else {
      LdvMaestraDto ldvMotivoRepresentacion = catalogoService.getCatalogoByCod(codLdv);
      solicitudService.saveSolicitudesPersonas(solicitud, personaDto, ldvMotivoRepresentacion);

    }
  }

  private PersonaDto saveOrDeleteRepresentante(SolicitudDto solicitud, boolean actualizar, PersonaDto personaDto) {
    // Si se está editando, el representante tiene id pero no tiene sus campos
    // obligatorios, se elimina
    if (actualizar && !isCamposObligatoriosPersona(personaDto) && personaDto.getIdPer() != null) {
      personaService.desactivarPersona(personaDto);
      return personaDto;
    } else {
      return saveRepresentanteSolicitud(solicitud, personaDto);
    }
  }

  private boolean isCamposObligatoriosPersona(PersonaDto personaDto) {
    return personaDto.getNombre() != null && !personaDto.getNombre().isEmpty() && personaDto.getApellido1() != null
        && !personaDto.getApellido1().isEmpty();
  }

  private PersonaDto saveRepresentanteSolicitud(SolicitudDto solicitud, PersonaDto personaDto) {
    if (personaDto != null) {
      // Se valida si se ha realizado la busqueda de rastreo utilizando un interesado
      // de otro expediente y se guardan los datos anteriores
      if (personaDto.getIdPer() != null) {
        PersonaDto interesadoAnterior = personaService.getPersonaByIdPer(personaDto.getIdPer());
        if (interesadoAnterior != null) {
          if (interesadoAnterior.getProgenitor1() != null) {
            personaDto.setProgenitor1(interesadoAnterior.getProgenitor1());
          }
          if (interesadoAnterior.getProgenitor2() != null) {
            personaDto.setProgenitor1(interesadoAnterior.getProgenitor2());
          }
          if (interesadoAnterior.getLugarNacimiento() != null) {
            personaDto.setLugarNacimiento(interesadoAnterior.getLugarNacimiento());
          }
          if (interesadoAnterior.getEstadoCivil() != null
              && interesadoAnterior.getEstadoCivil().getIdLdvMae() != null) {
            personaDto.setEstadoCivil(interesadoAnterior.getEstadoCivil());
          }
          if (interesadoAnterior.getSexo() != null && interesadoAnterior.getSexo().getIdLdvMae() != null) {
            personaDto.setSexo(interesadoAnterior.getSexo());
          }
          personaDto.setFechaNacimiento(interesadoAnterior.getFechaNacimiento());
          personaDto.setNacionalidad(interesadoAnterior.getNacionalidad());
          personaDto.setPaisNacimiento(interesadoAnterior.getPaisNacimiento());

        }
      }
      return personaService.savePersona(personaDto);
    }
    return personaDto;
  }

  private void limpiarCamposCombos(SolicitudDto solicitudDto) {
    setNullPersonaDatosIfEmpty(solicitudDto.getInteresado());
    setNullPersonaDatosIfEmpty(solicitudDto.getRepresentante1());
    setNullPersonaDatosIfEmpty(solicitudDto.getRepresentante2());
    setNullPersonaDatosIfEmpty(solicitudDto.getRepresentanteMandato());
    setNullDatosNotificacionIfEmpty(solicitudDto);
  }

  private void setNullPersonaDatosIfEmpty(PersonaDto personaDto) {
    if (personaDto != null) {
      LocalidadesDto localidadDto = (LocalidadesDto) UtilFiltro.setNullOnEmpty(personaDto.getLocalNac());
      personaDto.setLocalNac(localidadDto);
      PaisesDto paisDto = (PaisesDto) UtilFiltro.setNullOnEmpty(personaDto.getPaisNacimiento());
      personaDto.setPaisNacimiento(paisDto);
      PaisesDto paisNacionalidadDto = (PaisesDto) UtilFiltro.setNullOnEmpty(personaDto.getNacionalidad());
      personaDto.setNacionalidad(paisNacionalidadDto);
      PaisesDto paisSegundaNacionalidadDto = (PaisesDto) UtilFiltro.setNullOnEmpty(personaDto.getSegundaNacionalidad());
      personaDto.setSegundaNacionalidad(paisSegundaNacionalidadDto);
      ProvinciasDto provinciasDto = (ProvinciasDto) UtilFiltro.setNullOnEmpty(personaDto.getProvNac());
      personaDto.setProvNac(provinciasDto);
      LdvMaestraDto ldvEstadoCivilDto = (LdvMaestraDto) UtilFiltro.setNullOnEmpty(personaDto.getEstadoCivil());
      personaDto.setEstadoCivil(ldvEstadoCivilDto);
      LdvMaestraDto ldvSexoDto = (LdvMaestraDto) UtilFiltro.setNullOnEmpty(personaDto.getSexo());
      personaDto.setSexo(ldvSexoDto);

      if (personaDto.getPersonasContactosElectronicosDtos() != null
          && !personaDto.getPersonasContactosElectronicosDtos().isEmpty()) {
        PaisesDto paisPrefijoDto = (PaisesDto) UtilFiltro.setNullOnEmpty(personaDto
            .getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto().getPrefijoTelefono());
        personaDto.getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto()
            .setPrefijoTelefono(paisPrefijoDto);
      }

      if (!personaDto.getPersonasDomiciliosDto().isEmpty()
          && personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto() != null) {
        setNullDatosDomicilioIfEmpty(personaDto);
      }
      if (!personaDto.getPersonaFamDtos().isEmpty()) {
        for (PersonaFamDto personaFamDto : personaDto.getPersonaFamDtos()) {
          PaisesDto paisHijoDto = (PaisesDto) UtilFiltro.setNullOnEmpty(personaFamDto.getPaisNacimiento());
          personaFamDto.setPaisNacimiento(paisHijoDto);
        }
      }
    }
  }

  private void setNullDatosDomicilioIfEmpty(PersonaDto personaDto) {
    PaisesDto paisDomicilioDto = (PaisesDto) UtilFiltro
        .setNullOnEmpty(personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getPaisDto());
    personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().setPaisDto(paisDomicilioDto);
    ProvinciasDto provinciasDto = (ProvinciasDto) UtilFiltro
        .setNullOnEmpty(personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getProvinciaDto());
    personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().setProvinciaDto(provinciasDto);
    LocalidadesDto localidadDto = (LocalidadesDto) UtilFiltro
        .setNullOnEmpty(personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getLocalidadDto());
    personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().setLocalidadDto(localidadDto);
    TiposViaDto tipoViaDto = (TiposViaDto) UtilFiltro
        .setNullOnEmpty(personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getTipoVia());
    personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().setTipoVia(tipoViaDto);
  }

  private void setNullDatosNotificacionIfEmpty(SolicitudDto solDto) {
    if (solDto.getPersonaContactoElectronicoDtoNotificacion() != null) {
      PaisesDto paisDomicilioDto = (PaisesDto) UtilFiltro
          .setNullOnEmpty(solDto.getPersonaContactoElectronicoDtoNotificacion().getPrefijoTelefono());
      solDto.getPersonaContactoElectronicoDtoNotificacion().setPrefijoTelefono(paisDomicilioDto);
    }
  }

  /**
   * public SolicitudFormularioDto getFormularioSolicitudById(BigInteger
   * idSolicitud) throws SinacException { SolicitudDto solicitud =
   * solicitudService.getSolicitudPorId(idSolicitud); return
   * solicitudService.getFormularioSolicitudPorId(solicitud); }
   * 
   * @throws SinacException
   * 
   */

  @Override
  public Page<ResultadoBusquedaSolicitudesDto> getSolicitudesPaginated(BusquedaSolicitudesDto busquedaDto,
      Pageable pageable, String rol) throws SinacException {
    int pageSize = pageable.getPageSize();
    int currentPage = pageable.getPageNumber();
    Map<Integer, List<ResultadoBusquedaSolicitudesDto>> mapa = solicitudService.getSolicitudesPaginated(busquedaDto,
        pageable, rol);
    return new PageImpl<>(mapa.values().stream().toList().get(0), PageRequest.of(currentPage, pageSize),
        mapa.keySet().stream().toList().get(0));
  }

  @Override
  public List<SolicitudDocumentoDto> getDocumentosPromDocTipo(Short idPro, BigInteger idSol) throws SinacException {

    return solicitudService.getDocumentosPromDocTipo(idPro, idSol);
  }

  @Transactional(readOnly = false)
  @Override
  public void borrarSolicitud(BigInteger idSol) throws SinacException {
    solicitudService.borrarSolicitud(idSol);
  }

  @Override
  public Long getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(String codPro, String codFase,
      String codTra, String codOpe, String codAcc) throws SinacException {

    return procedimientosService.getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(codPro, codFase, codTra,
        codOpe, codAcc);
  }

  @Override
  public PaisesDto getPaisPorCodigo(String codPais) {
    return paisesService.getPaisPorCodigo(codPais);
  }

  @Override
  public LinkedList<DocumentoToSaveDto> transformMultipartToDocumentoToSave(SolicitudDto documentosEntrada) {
    return documentoService.transformMultipartToDocumentoToSave(documentosEntrada);
  }

  public List<SolicitudDocumentoDto> getDocumentosSolicitud(BigInteger idSol, String codTipo) throws SinacException {
    return solicitudService.getDocumentosSolicitud(idSol, codTipo);
  }

  private boolean ambosPresentes(Object a, Object b) {
    return a != null && b != null;
  }

  private void comprobarPersonasDistintas(SolicitudDto solicitudDto) {
    // ejemplo: Interesado es obligatorio, el resto opcional
    if (solicitudDto == null || solicitudDto.getInteresado() == null) {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_14);
    }

    if (ambosPresentes(solicitudDto.getInteresado(), solicitudDto.getRepresentante1())
        && Utilidades.isMismaPersona(solicitudDto.getInteresado(), solicitudDto.getRepresentante1())) {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_7);
    }

    if (ambosPresentes(solicitudDto.getInteresado(), solicitudDto.getRepresentante2())
        && Utilidades.isMismaPersona(solicitudDto.getInteresado(), solicitudDto.getRepresentante2())) {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_8);
    }

    if (ambosPresentes(solicitudDto.getInteresado(), solicitudDto.getRepresentanteMandato())
        && Utilidades.isMismaPersona(solicitudDto.getInteresado(), solicitudDto.getRepresentanteMandato())) {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_9);
    }

    if (ambosPresentes(solicitudDto.getRepresentante1(), solicitudDto.getRepresentante2())
        && Utilidades.isMismaPersona(solicitudDto.getRepresentante1(), solicitudDto.getRepresentante2())) {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_10);
    }

    if (ambosPresentes(solicitudDto.getRepresentante1(), solicitudDto.getRepresentanteMandato())
        && Utilidades.isMismaPersona(solicitudDto.getRepresentante1(), solicitudDto.getRepresentanteMandato())) {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_11);
    }

    if (ambosPresentes(solicitudDto.getRepresentante2(), solicitudDto.getRepresentanteMandato())
        && Utilidades.isMismaPersona(solicitudDto.getRepresentante2(), solicitudDto.getRepresentanteMandato())) {
      throw new SinacException(SinacExceptionMessageType.MESSAGE_12);
    }
  }

  @Override
  public SolicitudDto transferDatosExpedienteSolicitud(ExpedienteDto exp, SolicitudDto sol) {
    return solicitudService.transferDatosExpedienteSolicitud(exp, sol);
  }

  @Override
  public List<SolicitudDocumentoDto> getDocumentosSolicitudObligatorios(BigInteger idSol, String codPro)
      throws SinacException {

    return solicitudService.getDocumentosSolicitudObligatorios(idSol, codPro);
  }

  @Override
  public List<DocumentoTipoDto> getDocumentosSolicitudObligatorios(String codPro) throws SinacException {

    return documentoService.getDocumentosSolicitudObligatorios(codPro);
  }

  @Override
  public Page<PersonaDto> getPersonasRastreo(String identificador, String nombre, String apellido1, String apellido2,
      Date fechaNacimiento, Pageable pageable) throws SinacException {
    return personaService.getPersonasRastreo(identificador, nombre, apellido1, apellido2, fechaNacimiento, pageable,
        null, null);
  }

  @Override
  public PersonaDto getPersonaByIdPer(BigInteger idPer) throws SinacException {
    return personaService.getPersonaByIdPer(idPer);
  }

  @Override
  public List<ExpedienteDto> getExpedientesByIdPerInteresadoCodCortoPro(BigInteger idPer, String codCortoPro)
      throws SinacException {
    return expedientesService.getExpedientesByIdPerInteresadoCodCortoPro(idPer, codCortoPro);
  }

  @Override
  public List<ExpedienteDto> getExpedientesByIdPerInteresadoCodCortoProDistinto(BigInteger idPer, String codCortoPro)
      throws SinacException {
    return expedientesService.getExpedientesByIdPerInteresadoCodCortoProDistinto(idPer, codCortoPro);
  }

  @Override
  public EstadoSolicitudDto getEstadoSolicitud() {
    return solicitudService.getEstadoSolicitud();
  }

  @Override
  public void clearEstadoSolicitud() {
    solicitudService.clearEstadoSolicitud();
  }

  @Override
  public List<SolicitudDto> getListaSolicitudesSede() {
    return solicitudService.getListaSolicitudesSede();
  }

  @Override
  public String calcularCodigoExpediente(SolicitudDto solicitudDto) {
    LOG.info("SolicitudesFacadeImpl.calcularCodigoExpediente - Init");
    Calendar calendarFechaEfectos = Calendar.getInstance();
    calendarFechaEfectos.setTime(solicitudDto.getFechaEfectos());
    int añoFechaEfectos = calendarFechaEfectos.get(Calendar.YEAR);
    int numero = expedientesService.getSecuenciaExpediente(solicitudDto.getProcedimientoDto().getIdPro(),
        (short) añoFechaEfectos);
    String codigoExpediente = solicitudDto.getProcedimientoDto().getCodCorto() + String.format("%06d", numero) + "/"
        + añoFechaEfectos;

    LOG.info("SolicitudesFacadeImpl.calcularCodigoExpediente - End");
    return codigoExpediente;
  }

}
