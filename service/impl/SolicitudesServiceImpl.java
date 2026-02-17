package es.mjusticia.sinac.core.business.service.impl;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.DocumentosService;
import es.mjusticia.sinac.core.business.service.EstadoSolicitudService;
import es.mjusticia.sinac.core.business.service.PersonasService;
import es.mjusticia.sinac.core.business.service.SolicitudesService;
import es.mjusticia.sinac.core.model.dto.BusquedaSolicitudesDto;
import es.mjusticia.sinac.core.model.dto.EstadoSolicitudDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDto;
import es.mjusticia.sinac.core.model.dto.ExpedientesPersonasDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.PersonaFamDto;
import es.mjusticia.sinac.core.model.dto.PersonasDomiciliosDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosDocumentosTipoDto;
import es.mjusticia.sinac.core.model.dto.RegistroDto;
import es.mjusticia.sinac.core.model.dto.ResultadoBusquedaSolicitudesDto;
import es.mjusticia.sinac.core.model.dto.SolOpoForDatosDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDocumentoDto;
import es.mjusticia.sinac.core.model.dto.SolicitudDto;
import es.mjusticia.sinac.core.model.dto.SolicitudFormularioValDto;
import es.mjusticia.sinac.core.model.dto.SolicitudesPersonasDto;
import es.mjusticia.sinac.core.model.entity.FormularioCamposValidaEntity;
import es.mjusticia.sinac.core.model.entity.PersonaEntity;
import es.mjusticia.sinac.core.model.entity.PersonaIdentificaEntity;
import es.mjusticia.sinac.core.model.entity.RegistroEntity;
import es.mjusticia.sinac.core.model.entity.SolOpoForDatosEntity;
import es.mjusticia.sinac.core.model.entity.SolicitudDocumentoEntity;
import es.mjusticia.sinac.core.model.entity.SolicitudEntity;
import es.mjusticia.sinac.core.model.entity.SolicitudFormularioValEntity;
import es.mjusticia.sinac.core.model.entity.SolicitudesPersonasEntity;
import es.mjusticia.sinac.core.model.mapper.LdvMaestraMapper;
import es.mjusticia.sinac.core.model.mapper.PaisesMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaContactoElectronicoMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaDomicilioMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaMapper;
import es.mjusticia.sinac.core.model.mapper.ProcedimientosDocumentosTipoMapper;
import es.mjusticia.sinac.core.model.mapper.SolOpoForDatosMapper;
import es.mjusticia.sinac.core.model.mapper.SolicitudDocumentoMapper;
import es.mjusticia.sinac.core.model.mapper.SolicitudFormularioValMapper;
import es.mjusticia.sinac.core.model.mapper.SolicitudMapper;
import es.mjusticia.sinac.core.model.mapper.SolicitudesPersonasMapper;
import es.mjusticia.sinac.core.persistence.PaisesDao;
import es.mjusticia.sinac.core.persistence.PersonaContactoElectronicoDao;
import es.mjusticia.sinac.core.persistence.PersonaDomicilioDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosDocumentosTipoDao;
import es.mjusticia.sinac.core.persistence.RegistroDao;
import es.mjusticia.sinac.core.persistence.SolOpoForDatosDao;
import es.mjusticia.sinac.core.persistence.SolicitudDao;
import es.mjusticia.sinac.core.persistence.SolicitudDocumentoDao;
import es.mjusticia.sinac.core.persistence.SolicitudFormularioValDao;
import es.mjusticia.sinac.core.persistence.SolicitudesPersonasDao;
import es.mjusticia.sinac.core.utils.Constantes;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class SolicitudesServiceImpl implements SolicitudesService {

  private static final Logger LOG = LoggerFactory.getLogger(SolicitudesServiceImpl.class);
  private static final String ESPANIA_COD_PAIS = "724";

  @Autowired
  private DocumentosService documentoService;

  @Autowired
  private PersonasService personasService;

  @Autowired
  private LdvMaestraMapper ldvMaestraMapper;

  @Autowired
  private SolicitudMapper solicitudMapper;

  @Autowired
  private PersonaMapper personaMapper;

  @Autowired
  private SolicitudDao solicitudDao;

  @Autowired
  private CatalogosService catalogoService;

  @Autowired
  private SolicitudFormularioValMapper solicitudFormularioValMapper;

  @Autowired
  private SolicitudFormularioValDao solicitudFormularioValDao;

  @Autowired
  private SolicitudDocumentoMapper solicitudDocumentoMapper;

  @Autowired
  private SolicitudDocumentoDao solicitudDocumentoDao;

  @Autowired
  private SolicitudesPersonasMapper solicitudesPersonasMapper;

  @Autowired
  private SolicitudesPersonasDao solicitudesPersonasDao;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private ProcedimientosDocumentosTipoDao procedimientosDocumentosTipoDao;

  @Autowired
  private ProcedimientosDocumentosTipoMapper procedimientosDocumentosTipoMapper;

  @Autowired
  private RegistroDao registroDao;

  @Autowired
  private PersonaDomicilioMapper personaDomicilioMapper;

  @Autowired
  private PersonaDomicilioDao personaDomicilioDao;

  @Autowired
  private PersonaContactoElectronicoDao personaContactoElectronicoDao;

  @Autowired
  private PersonaContactoElectronicoMapper personaContactoElectronicoMapper;

  @Autowired
  private PaisesDao paisesDao;

  @Autowired
  private PaisesMapper paisesMapper;

  @Autowired
  private EstadoSolicitudService estadoSolicitudService;

  @Autowired
  private SolOpoForDatosDao solOpoForDatosDao;

  @Autowired
  private SolOpoForDatosMapper solOpoForMapper;

  @Override
  public SolicitudDto saveSolicitud(SolicitudDto solicitudDto) throws SinacException {
    LOG.debug("Init - SolicitudesServiceImpl.saveSolicitud de la solicitud {}", solicitudDto.getIdSol());

    try {
      SolicitudEntity solicitudEntity = solicitudMapper.toEntity(solicitudDto);
      if (solicitudDto.getIdSol() != null) {
        SolicitudEntity solicitudPreviaEntity = solicitudDao.recuperarDetalleSolicitudPorId(solicitudEntity.getIdSol());
        solicitudEntity.setCreadoPor(solicitudPreviaEntity.getCreadoPor());
        solicitudEntity.setFechaIniVig(solicitudPreviaEntity.getFechaIniVig());
        solicitudEntity.setFlgActivo(true);
      }

      solicitudEntity = solicitudDao.save(solicitudEntity);

      solicitudDto = solicitudMapper.toDto(solicitudEntity);
      // Estado de la solicitud en borrador por defecto.
      LOG.info("Se ha guardado correctamente la solicitud {}", solicitudDto.getIdSol());
      LOG.debug("End - SolicitudesServiceImpl.saveSolicitud de la solicitud {}", solicitudDto.getIdSol());
      return solicitudDto;
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_SOLICITUDES_1)
          .logMessageParams(solicitudDto.getIdSol()).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void saveSolicitudFormularioVal(SolicitudFormularioValDto solicitudFormularioValDto) throws SinacException {
    try {
      SolicitudFormularioValEntity solicitudFormularioValEntity = solicitudFormularioValMapper
          .toEntity(solicitudFormularioValDto);
      solicitudFormularioValEntity.setFlgActivo(true);
      solicitudFormularioValDao.save(solicitudFormularioValEntity);
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_SOLICITUDES_2)
          .logMessageParams(solicitudFormularioValDto.getSolicitudDto().getIdSol()).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void desactivarSolicitudFormularioValAnterioresByIdSol(BigInteger idSol) throws SinacException {
    LOG.debug("SolicitudesServiceImpl.desactivarSolicitudFormularioValAnterioresByIdSol - Init");
    try {
      for (SolicitudFormularioValEntity solicitudFormularioValEntity : solicitudFormularioValDao
          .getSolFormPorIdSol(idSol)) {
        solicitudFormularioValEntity.setFlgActivo(false);
        solicitudFormularioValDao.save(solicitudFormularioValEntity);
      }
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_SOLICITUDES_3).logMessageParams(idSol)
          .type(SinacExceptionType.DATA);
    }
    LOG.debug("SolicitudesServiceImpl.desactivarSolicitudFormularioValAnterioresByIdSol - End");

  }

  @Override
  public List<SolicitudesPersonasDto> getPersonasSolicitudBySolicitudId(BigInteger idSolicitud) throws SinacException {
    LOG.debug("SolicitudesServiceImpl.getPersonasSolicitudBySolicitudId - Init");
    try {
      List<SolicitudesPersonasEntity> listaSolPerEntity = solicitudesPersonasDao
          .recuperarSolicitudPersonasPorId(idSolicitud);
      List<SolicitudesPersonasDto> listaSolPerDto = new ArrayList<>();
      for (SolicitudesPersonasEntity solicitudPerEntity : listaSolPerEntity) {
        listaSolPerDto.add(solicitudesPersonasMapper.toDto(solicitudPerEntity));
      }
      LOG.debug("SolicitudesServiceImpl.getPersonasSolicitudBySolicitudId - Init");
      return listaSolPerDto;
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_SOLICITUDES_4).logMessageParams(idSolicitud)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void saveSolicitudDocumento(SolicitudDocumentoDto solicitudDocumentoDto) {
    SolicitudDocumentoEntity solicitudDocumentoEntity = solicitudDocumentoMapper.toEntity(solicitudDocumentoDto);
    solicitudDocumentoEntity.setFlgActivo(true);
    solicitudDocumentoDao.save(solicitudDocumentoEntity);
  }

  @Override
  public void deleteSolicitudDocumento(SolicitudDocumentoDto solicitudDocumentoDto) {
    solicitudDocumentoDao.delete(solicitudDocumentoMapper.toEntity(solicitudDocumentoDto));

  }

  @Override
  public List<SolicitudDocumentoDto> getSolicitudDocumentosBySolicitudId(BigInteger idSolicitud) {
    List<SolicitudDocumentoEntity> solicitudDocumentoEntityLista = solicitudDocumentoDao
        .findSolicitudDocumentosByIdSolicitud(idSolicitud);
    List<SolicitudDocumentoDto> solicitudDocumentoDtoLista = new ArrayList<>();
    for (SolicitudDocumentoEntity entity : solicitudDocumentoEntityLista) {
      solicitudDocumentoDtoLista.add(solicitudDocumentoMapper.toDto(entity));
    }

    return solicitudDocumentoDtoLista;

  }

  @Override
  public SolicitudDto getSolicitudPorId(BigInteger idSol) throws SinacException {
    LOG.debug("SolicitudesServiceImpl.getSolicitudPorId - Init");
    try {
      SolicitudEntity solicitudEntity = getSolicitudFormada(idSol);
      SolicitudDto solicitudDto = solicitudMapper.toDto(solicitudEntity);

      List<PersonasDomiciliosDto> personasDomiciliosDtos = new ArrayList<>();
      PersonasDomiciliosDto personasDomiciliosDto = new PersonasDomiciliosDto();
      personasDomiciliosDto.setPersonaDomicilioDto(solicitudDto.getPersonaDomicilioDtoNotificacion());
      personasDomiciliosDtos.add(personasDomiciliosDto);
      setTipoDomicilio(personasDomiciliosDtos);
      solicitudDto.setPersonaDomicilioDtoNotificacion(personasDomiciliosDtos.get(0).getPersonaDomicilioDto());

      for (SolicitudesPersonasDto solp : solicitudDto.getSolicitudesPersonasDtos()) {
        if (Boolean.TRUE.equals(solp.getFlgNotificar())) {
          solicitudDto.setFlgPersonaConsiente(solp.getFlgConsiente());
          solicitudDto.setIdPersonaNotificar(solp.getPersonaDto().getIdPer());
        }
        if (solp.getLdvMaestraDto().getCodLdvMae().equals(Constantes.Personas.TIPO_INTERESADO)) {
          setInteresadoSolicitud(solicitudDto, solp);
        } else if (isRepresentante1or2(solp)) {
          solicitudDto.setMotivoRepresentacion(solp.getLdvMaestraDto());
          if (solicitudDto.getRepresentante1() == null) {
            setRepresentante1Solicitud(solicitudDto, solp);
          } else {
            setRepresentante2Solicitud(solicitudDto, solp);
          }
        } else if (solp.getLdvMaestraDto().getCodLdvMae().equals(Constantes.Personas.TIPO_REPRESENTANTE_MANDATO)) {
          setRepresentanteMandatoSolicitud(solicitudDto, solp);
        }
      }
      if (solicitudDto.getInteresado().getPersonaFamDtos() != null
          && !solicitudDto.getInteresado().getPersonaFamDtos().isEmpty()) {
        for (PersonaFamDto personaFam : solicitudDto.getInteresado().getPersonaFamDtos()) {
          if (personaFam.getLdvMaestraDto().getCodLdvMae().equals("CON-CON")) {
            if (personaFam.getPersonaFamMatrimonioDto() != null) {
              solicitudDto.getInteresado().setPersonaFamMatrimonioDto(personaFam.getPersonaFamMatrimonioDto());
              solicitudDto.getInteresado().getPersonaFamMatrimonioDto().setPersonaFamDto(personaFam);
              solicitudDto.getInteresado().getPersonaFamDtos().remove(personaFam);
              break;
            }
          }
        }
      }

      List<SolicitudDocumentoDto> listaSolicitudesDocumentos = getDocsSolBySolicitudId(solicitudDto.getIdSol());
      if (listaSolicitudesDocumentos != null && !listaSolicitudesDocumentos.isEmpty()) {
        BigInteger idSolicitud = listaSolicitudesDocumentos.get(0).getIdSolDoc();
        RegistroDto registroDto = documentoService.getRegistroByIdSolicitudDocumento(idSolicitud);
        solicitudDto.getRegistroDtos().clear();
        solicitudDto.getRegistroDtos().add(registroDto);
      }
      if (solicitudDto.getRegistroDtos().isEmpty()
          || (!solicitudDto.getRegistroDtos().isEmpty() && solicitudDto.getRegistroDtos().get(0) == null)) {
        if (!solicitudDto.getRegistroDtos().isEmpty())
          solicitudDto.getRegistroDtos().remove(0);
        RegistroDto registroFechaEfectos = new RegistroDto();
        registroFechaEfectos.setFechaReg(solicitudDto.getFechaEfectos());
        solicitudDto.getRegistroDtos().add(registroFechaEfectos);
      }
      LOG.debug("SolicitudesServiceImpl.getSolicitudPorId - End");
      return solicitudDto;
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_SOLICITUDES_5).logMessageParams(idSol)
          .type(SinacExceptionType.DATA);
    }
  }

  private void setRepresentanteMandatoSolicitud(SolicitudDto solicitudDto, SolicitudesPersonasDto solp) {
    solicitudDto.setRepresentanteMandato(solp.getPersonaDto());
    solicitudDto.getRepresentanteMandato().setFlgNotificar(solp.getFlgNotificar());
    List<PersonasDomiciliosDto> personasDomiciliosDto = solicitudDto.getRepresentanteMandato()
        .getPersonasDomiciliosDto();
    if (personasDomiciliosDto != null && !personasDomiciliosDto.isEmpty()) {
      setTipoDomicilio(personasDomiciliosDto);
    }
  }

  private void setRepresentante2Solicitud(SolicitudDto solicitudDto, SolicitudesPersonasDto solp) {
    solicitudDto.setRepresentante2(solp.getPersonaDto());
    solicitudDto.getRepresentante2().setFlgNotificar(solp.getFlgNotificar());
    List<PersonasDomiciliosDto> personasDomiciliosDto = solicitudDto.getRepresentante2().getPersonasDomiciliosDto();
    if (personasDomiciliosDto != null && !personasDomiciliosDto.isEmpty()) {
      setTipoDomicilio(personasDomiciliosDto);
    }
  }

  private void setRepresentante1Solicitud(SolicitudDto solicitudDto, SolicitudesPersonasDto solp) {
    solicitudDto.setRepresentante1(solp.getPersonaDto());
    solicitudDto.getRepresentante1().setFlgNotificar(solp.getFlgNotificar());
    List<PersonasDomiciliosDto> personasDomiciliosDto = solicitudDto.getRepresentante1().getPersonasDomiciliosDto();
    if (personasDomiciliosDto != null && !personasDomiciliosDto.isEmpty()) {
      setTipoDomicilio(personasDomiciliosDto);
    }
  }

  private void setInteresadoSolicitud(SolicitudDto solicitudDto, SolicitudesPersonasDto solp) {
    solicitudDto.setInteresado(solp.getPersonaDto());
    List<PersonasDomiciliosDto> personasDomiciliosDto = solicitudDto.getInteresado().getPersonasDomiciliosDto();
    if (personasDomiciliosDto != null && !personasDomiciliosDto.isEmpty()) {
      setTipoDomicilio(personasDomiciliosDto);
    }
    solicitudDto.getInteresado().setFlgNotificar(solp.getFlgNotificar());
  }

  private boolean isRepresentante1or2(SolicitudesPersonasDto solp) {
    return solp.getLdvMaestraDto().getCodLdvMae().equals("PER-RL14")
        || solp.getLdvMaestraDto().getCodLdvMae().equals("PER-RL18")
        || solp.getLdvMaestraDto().getCodLdvMae().equals("PER-RLJ")
        || solp.getLdvMaestraDto().getCodLdvMae().equals("PER-RLJL");
  }

  private void setTipoDomicilio(List<PersonasDomiciliosDto> personasDomiciliosDto) {
    for (PersonasDomiciliosDto personaDomicilio : personasDomiciliosDto) {
      if (personaDomicilio.getPersonaDomicilioDto() != null) {
        if (personaDomicilio.getPersonaDomicilioDto().getPaisDto() == null) {
          personaDomicilio.getPersonaDomicilioDto().setTipoDomicilio("0");
        } else if (personaDomicilio.getPersonaDomicilioDto().getPaisDto().getCodPais().equals(ESPANIA_COD_PAIS)) {
          personaDomicilio.getPersonaDomicilioDto().setTipoDomicilio("1");
        } else {
          personaDomicilio.getPersonaDomicilioDto().setTipoDomicilio("2");
        }
      }
    }
  }

  private SolicitudEntity getSolicitudFormada(BigInteger idSol) {
    SolicitudEntity solicitudEntity = solicitudDao.recuperarDetalleSolicitudPorId(idSol);
    Set<SolicitudDocumentoEntity> solicitudDocumentoEntity = solicitudDocumentoDao.getSolicitudDocumentosByIdSol(idSol);
    Set<SolicitudesPersonasEntity> solicitudPersonaEntity = solicitudesPersonasDao.getSolPersonasPorIdSol(idSol);

    Set<SolicitudFormularioValEntity> solicitudFormularioEntity = solicitudFormularioValDao.getSolFormPorIdSol(idSol);

    for (SolicitudesPersonasEntity solPer : solicitudPersonaEntity) {
      solPer.getPersonaEntity().getPersonasDomiciliosEntity().removeIf(p -> !p.isFlgActivo());
      solPer.getPersonaEntity().getPersonasContactosElectronicosEntity().removeIf(p -> !p.isFlgActivo());
      solPer.getPersonaEntity().getPersonasIdentificaEntities().removeIf(p -> !p.isFlgActivo());
      solPer.getPersonaEntity().getPersonaRcEntities().removeIf(p -> !p.isFlgActivo());
      solPer.getPersonaEntity().getPersonaFamEntities().removeIf(p -> !p.isFlgActivo());
      if (solPer.getSolicitudEntity().getPersonaDomicilioEntityNotificacion() != null
          && solPer.getSolicitudEntity().getPersonaDomicilioEntityNotificacion().getPaises().getNomPais() == "ESPAÑA") {
        SolicitudEntity personaDomicilioEntityNotificacion = solicitudDao
            .getSolicitudPersonaDomicilioEntityNotificacion(idSol);
        solPer.getSolicitudEntity().setPersonaDomicilioEntityNotificacion(
            personaDomicilioEntityNotificacion.getPersonaDomicilioEntityNotificacion());
      }
    }

    for (FormularioCamposValidaEntity formCampo : solicitudEntity.getProcedimientoEntity()
        .getFormularioCamposValidaEntities()) {
      formCampo.getSolicitudFormularioValEntities().removeIf(val -> !val.isFlgActivo());
      formCampo.getSolicitudFormularioValEntities()
          .removeIf(val -> (val.getSolicitudEntity().getIdSol() != solicitudEntity.getIdSol()));
    }

    solicitudEntity.setSolicitudDocumentoEntities(solicitudDocumentoEntity);
    solicitudEntity.setSolicitudesPersonasEntities(solicitudPersonaEntity);
    solicitudEntity.setSolicitudFormularioValEntities(solicitudFormularioEntity);
    return solicitudEntity;
  }

  @Override
  public List<SolicitudDocumentoDto> getDocsSolBySolicitudId(BigInteger idSolicitud) throws SinacException {
    LOG.info("SolicitudesServiceImpl.getDocsSolBySolicitudId - Init");

    List<SolicitudDocumentoEntity> listaSolDoc = solicitudDocumentoDao.recuperarDocsSolicitudesPorId(idSolicitud);
    List<SolicitudDocumentoDto> listaSolDocDto = new ArrayList<>();
    for (SolicitudDocumentoEntity solDoc : listaSolDoc) {
      listaSolDocDto.add(solicitudDocumentoMapper.toDto(solDoc));
    }
    LOG.info("SolicitudesServiceImpl.getDocsSolBySolicitudId - End");
    return listaSolDocDto;
  }

  /**
   * Método que retorna una lista de solicitudes.
   * 
   * @param busquedaSolDto objeto que contiene los parámetros de búsqueda.
   * @return lista de solicitudes.
   * @throws SinacException
   */

  @Override
  public Map<Integer, List<ResultadoBusquedaSolicitudesDto>> getSolicitudesPaginated(
      BusquedaSolicitudesDto busquedaSolDto, Pageable pageable, String rol) throws SinacException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Get solicitudes: {}", String.format("%s", busquedaSolDto));
    }

    Map<Integer, List<SolicitudEntity>> mapa = solicitudDao.filtroSolicitudes(busquedaSolDto, entityManager, pageable,
        rol);
    List<SolicitudEntity> listaSolicitudes = mapa.values().stream().toList().get(0);
    List<ResultadoBusquedaSolicitudesDto> listaBusquedaSolicitudesDtos = new ArrayList<>();
    if (!listaSolicitudes.isEmpty()) {
      listaBusquedaSolicitudesDtos.addAll(solicitudToResultadoBusqueda(listaSolicitudes));
    }
    LOG.debug("Lista solicitud: {}", listaBusquedaSolicitudesDtos);
    Map<Integer, List<ResultadoBusquedaSolicitudesDto>> resultadoMapa = new HashMap<>();
    resultadoMapa.put(mapa.keySet().stream().toList().get(0), listaBusquedaSolicitudesDtos);
    return resultadoMapa;
  }

  @Override
  public void saveSolicitudesPersonas(SolicitudDto solicitud, PersonaDto persona, LdvMaestraDto tipo)
      throws SinacException {
    LOG.debug("SolicitudesServiceImpl.savePersona - Init");
    SolicitudesPersonasEntity solicitudesPersonasActualizar = solicitudesPersonasDao
        .recuperarSolicitudPersonasId(solicitud.getIdSol(), persona.getIdPer());
    if (solicitudesPersonasActualizar == null) {
      solicitudesPersonasActualizar = new SolicitudesPersonasEntity();
    }
    solicitudesPersonasActualizar.setFlgConsiente(persona.isFlgConsiente());
    solicitudesPersonasActualizar.setFlgNotificar(persona.getFlgNotificar());
    solicitudesPersonasActualizar.setPersonaEntity(personaMapper.toEntity(persona));
    solicitudesPersonasActualizar.setSolicitudEntity(solicitudMapper.toEntity(solicitud));
    solicitudesPersonasActualizar.setLdvMaestraEntity(ldvMaestraMapper.toEntity(tipo));
    try {
      solicitudesPersonasDao.save(solicitudesPersonasActualizar);
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_SOLICITUDES_6)
          .logMessageParams(solicitudesPersonasActualizar.getSolicitudEntity().getIdSol())
          .type(SinacExceptionType.DATA);
    }
    LOG.debug("SolicitudesServiceImpl.savePersona - End");
  }

  @Override
  public void deleteSolicitudPersona(SolicitudDto solicitud, PersonaDto persona) throws SinacException {
    LOG.debug("SolicitudesServiceImpl.deleteSolicitudPersona - Init");
    SolicitudesPersonasEntity solicitudPersona = solicitudesPersonasDao
        .recuperarSolicitudPersonasId(solicitud.getIdSol(), persona.getIdPer());
    try {
      solicitudesPersonasDao.delete(solicitudPersona);
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.SINAC_SOLICITUDES_7)
          .logMessageParams(solicitudPersona.getSolicitudEntity().getIdSol()).type(SinacExceptionType.DATA);
    }
    LOG.debug("SolicitudesServiceImpl.deleteSolicitudPersona - End");
  }

  @Override
  public List<SolicitudDocumentoDto> getDocumentosPromDocTipo(Short idPro, BigInteger idSol) {

    List<ProcedimientosDocumentosTipoDto> listaObligatorios = procedimientosDocumentosTipoMapper
        .toDto(procedimientosDocumentosTipoDao.getListaProcedimientoDocTipoObligatorios(idPro));

    List<SolicitudDocumentoDto> listaSolDocs = new ArrayList<>();
    if (idSol != null) {
      listaSolDocs
          .addAll(solicitudDocumentoMapper.toDtoSet(solicitudDocumentoDao.getSolicitudDocumentosByIdSol(idSol)));
    }

    List<SolicitudDocumentoDto> listaDocs = new ArrayList<>();
    // añadimos documentos obligatorios, los que no existan se los añadimos con el
    // tipo de doc solamente
    for (ProcedimientosDocumentosTipoDto procedimientosDocumentosTipoDto : listaObligatorios) {
      List<ProcedimientosDocumentosTipoDto> listaProDocTipo = new ArrayList<>();
      listaProDocTipo.add(procedimientosDocumentosTipoDto);
      boolean yaExiste = false;
      for (SolicitudDocumentoDto solicitudDocumentoDtoSol : listaSolDocs) {
        if (solicitudDocumentoDtoSol.getDocumentoTipoDto().getIdDocTipo() == procedimientosDocumentosTipoDto
            .getDocumentosTipo().getIdDocTipo()) {
          solicitudDocumentoDtoSol.getDocumentoTipoDto().setProcedimientosDocumentosTipoDtos(listaProDocTipo);
          listaDocs.add(solicitudDocumentoDtoSol);
          yaExiste = true;
        }
      }

      if (!yaExiste) {
        SolicitudDocumentoDto solicitudDocumentoDto = new SolicitudDocumentoDto();
        solicitudDocumentoDto.setDocumentoTipoDto(procedimientosDocumentosTipoDto.getDocumentosTipo());
        solicitudDocumentoDto.getDocumentoTipoDto().setProcedimientosDocumentosTipoDtos(listaProDocTipo);
        listaDocs.add(solicitudDocumentoDto);
      }
    }

    for (SolicitudDocumentoDto solicitudDocumentoDtoSol : listaSolDocs) {
      boolean aniadir = true;
      for (ProcedimientosDocumentosTipoDto procedimientosDocumentosTipoDto : listaObligatorios) {
        if (solicitudDocumentoDtoSol.getDocumentoTipoDto().getIdDocTipo() == procedimientosDocumentosTipoDto
            .getDocumentosTipo().getIdDocTipo()) {
          aniadir = false;
        }
      }
      if (aniadir) {
        listaDocs.add(solicitudDocumentoDtoSol);
      }
    }
    return listaDocs;
  }

  private List<ResultadoBusquedaSolicitudesDto> solicitudToResultadoBusqueda(List<SolicitudEntity> listaSolicitudes) {
    List<ResultadoBusquedaSolicitudesDto> listaResultadoBusquedaSolicitudes = new ArrayList<>();
    ResultadoBusquedaSolicitudesDto resultadoBusquedaSolicitudesDto;
    for (SolicitudEntity solicitudEntity : listaSolicitudes) {
      resultadoBusquedaSolicitudesDto = new ResultadoBusquedaSolicitudesDto();
      resultadoBusquedaSolicitudesDto.setIdSol(solicitudEntity.getIdSol().toString());
      PersonaEntity interesado = solicitudEntity.getSolicitudesPersonasEntities().stream().filter(p -> p.isFlgActivo())
          .filter(p -> p.getLdvMaestraEntity().getCodLdvMae().equals(Constantes.Personas.TIPO_INTERESADO))
          .map(SolicitudesPersonasEntity::getPersonaEntity).toList().get(0);
      resultadoBusquedaSolicitudesDto.setNombreInteresado(interesado.getNombre());
      resultadoBusquedaSolicitudesDto.setApellido1Interesado(interesado.getApellido1());
      resultadoBusquedaSolicitudesDto.setApellido2Interesado(interesado.getApellido2());
      PersonaIdentificaEntity identificacionInteresado = interesado.getPersonasIdentificaEntities().stream()
          .filter(PersonaIdentificaEntity::isFlgActivo).toList().get(0);
      resultadoBusquedaSolicitudesDto.setNumAcreditacionInteresado(identificacionInteresado.getNumAcreditacion());
      resultadoBusquedaSolicitudesDto.setEstado(solicitudEntity.getLdvMaestraEntityByIdEstSolLdv().getNomLdvMae());
      resultadoBusquedaSolicitudesDto.setFechaCreacion(solicitudEntity.getFechaCreacion());
      listaResultadoBusquedaSolicitudes.add(resultadoBusquedaSolicitudesDto);
    }
    return listaResultadoBusquedaSolicitudes;
  }

  @Override
  public void borrarSolicitud(BigInteger idSol) throws SinacException {
    SolicitudEntity solicitudEntity = solicitudDao.recuperarDetalleSolicitudPorId(idSol);

    Set<SolicitudDocumentoEntity> solicitudDocumentoEntity = solicitudDocumentoDao.getSolicitudDocumentosByIdSol(idSol);
    for (SolicitudDocumentoEntity solicitudDocumento : solicitudDocumentoEntity) {
      RegistroEntity registro = registroDao.getRegistroByIdSolicitudDocumento(solicitudDocumento.getIdSolDoc());
      if (registro != null) {
        registroDao.delete(registro);
      }
      solicitudDocumentoDao.delete(solicitudDocumento);
    }
    List<PersonaEntity> personasBorrar = new ArrayList<>();
    Set<SolicitudesPersonasEntity> solicitudPersonaEntity = solicitudesPersonasDao.getSolPersonasPorIdSol(idSol);
    for (SolicitudesPersonasEntity solPer : solicitudPersonaEntity) {
      personasBorrar.add(solPer.getPersonaEntity());
      solicitudesPersonasDao.delete(solPer);
    }
    for (PersonaEntity persona : personasBorrar) {
      personasService.deletePersona(persona);
    }

    Set<SolicitudFormularioValEntity> solicitudFormularioEntity = solicitudFormularioValDao
        .getAllSolFormPorIdSol(idSol);
    for (SolicitudFormularioValEntity solicitudFormularioSEntity : solicitudFormularioEntity) {
      solicitudFormularioValDao.delete(solicitudFormularioSEntity);
    }

    solicitudDao.delete(solicitudEntity);
  }

  @Override
  public RegistroEntity getRegistroSolicitud(final BigInteger idSol) throws SinacException {
    LOG.debug("SolicitudesServiceImpl.getFechaEfectosRegistro - Init");
    try {
      LOG.debug("SolicitudesServiceImpl.getFechaEfectosRegistro - End");
      return registroDao.getRegistroSolicitud(idSol);
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_SOLICITUDES_8)
          .logMessageParams(idSol).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_SOLICITUDES_9).logMessageParams(idSol)
          .type(SinacExceptionType.DATA);
    }
  }

  @Override
  public List<SolicitudDocumentoDto> getDocumentosSolicitud(BigInteger idSol, String codTipo) throws SinacException {
    List<SolicitudDocumentoDto> listaDocumentosSolicitud = null;
    if (StringUtils.isNotBlank(codTipo)) {
      listaDocumentosSolicitud = solicitudDocumentoMapper
          .toDtoSet(solicitudDocumentoDao.getSolicitudDocumentosByIdSolCodTipo(idSol, codTipo)).stream().toList();
    } else {
      listaDocumentosSolicitud = solicitudDocumentoMapper
          .toDtoSet(solicitudDocumentoDao.getSolicitudDocumentosByIdSol(idSol)).stream().toList();
    }

    return listaDocumentosSolicitud;
  }

  @Override
  public SolicitudDto saveSolicitudesCorDom(SolicitudDto solicitud) {
    if (solicitud.getTipoPersonaNotificar() != null && !solicitud.getTipoPersonaNotificar().isBlank()) {
      if (solicitud.getPersonaDomicilioDtoNotificacion() != null
          && "1".equals(solicitud.getPersonaDomicilioDtoNotificacion().getTipoDomicilio())) {
        solicitud.getPersonaDomicilioDtoNotificacion()
            .setPaisDto(paisesMapper.toDto(paisesDao.getPaisPorCodigo("724")));
      }

      if (solicitud.getPersonaContactoElectronicoDtoNotificacion() != null) {
        solicitud.setPersonaContactoElectronicoDtoNotificacion(
            personaContactoElectronicoMapper.toDto(personaContactoElectronicoDao.save(
                personaContactoElectronicoMapper.toEntity(solicitud.getPersonaContactoElectronicoDtoNotificacion()))));
      }

      // domicilio
      if (solicitud.getPersonaDomicilioDtoNotificacion() != null) {
        solicitud.setPersonaDomicilioDtoNotificacion(personaDomicilioMapper.toDto(
            personaDomicilioDao.save(personaDomicilioMapper.toEntity(solicitud.getPersonaDomicilioDtoNotificacion()))));
      }

    } else {
      solicitud.setPersonaContactoElectronicoDtoNotificacion(null);
      solicitud.setPersonaDomicilioDtoNotificacion(null);
    }
    return solicitud;
  }

  @Override
  public SolicitudDto transferDatosExpedienteSolicitud(ExpedienteDto exp, SolicitudDto sol) {
    for (ExpedientesPersonasDto persona : exp.getExpedientesPersonasDtos()) {
      if (persona.getFlgNotificar()) {
        sol.setTipoPersonaNotificar(persona.getLdvMaestraDto().getCodLdvMae());
        sol.setFlgPersonaConsiente(persona.getFlgConsiente());
      }
    }
    sol.setInteresado(exp.getInteresado());
    sol.setRepresentante1(exp.getRepresentante1());
    sol.setRepresentante2(exp.getRepresentante2());
    sol.setRepresentanteMandato(exp.getRepresentanteMandato());
    sol.setPersonaContactoElectronicoDtoNotificacion(exp.getPersonaContactoElectronicoDtoNotificacion());
    sol.setPersonaDomicilioDtoNotificacion(exp.getPersonaDomicilioDtoNotificacion());
    sol.setFlgObedienciaLeyes(exp.getFlgObedienciaLeyes());
    sol.setFlgOposicion(exp.getFlgOposicion());
    sol.setFlgRenunNacAnte(exp.getFlgRenunNacAnte());
    sol.setLdvMaestraDtoByIdMotivoSolLdv(
        catalogoService.getCatalogoByCod(sol.getLdvMaestraDtoByIdMotivoSolLdv().getCodLdvMae()));
    sol.setLdvMaestraDtoByIdOriSolLdv(exp.getOrigenSolicitud());
    sol.setMotivoOposicion(exp.getMotivoOposicion());
    sol.setMotivoRepresentacion(exp.getMotivoRepresentacion());
    sol.setMotivoSol(exp.getMotivoSolicitud());
    sol.setMotivoSolicitudOtros(exp.getMotivoSolicitudOtros());
    return sol;
  }

  @Override
  public List<SolicitudDocumentoDto> getDocumentosSolicitudObligatorios(BigInteger idSol, String codPro) {
    List<SolicitudDocumentoDto> listaDocumentosSolicitud = null;
    if (StringUtils.isNotBlank(codPro)) {
      listaDocumentosSolicitud = solicitudDocumentoMapper
          .toDtoSet(solicitudDocumentoDao.getSolicitudDocumentosByIdSolObligatorio(idSol, codPro)).stream().toList();
    }
    return listaDocumentosSolicitud;
  }

  @Override
  public EstadoSolicitudDto getEstadoSolicitud() {
    if (RequestContextHolder.getRequestAttributes() != null) {
      return new EstadoSolicitudDto(estadoSolicitudService.getEstado());
    }
    return null;
  }

  @Override
  public void setEstadoSolicitud(String estado) {
    if (RequestContextHolder.getRequestAttributes() != null) {
      estadoSolicitudService.setEstado(estado);
    }
  }

  @Override
  public void clearEstadoSolicitud() {
    if (RequestContextHolder.getRequestAttributes() != null) {
      estadoSolicitudService.setEstado(null);
    }
  }

  @Override
  public void saveSolOpoForDatos(SolOpoForDatosDto solOpoForDato, BigInteger idSol) throws SinacException {
    try {
      SolOpoForDatosEntity solOpoForEntity = solOpoForMapper.toEntity(solOpoForDato);
      solOpoForEntity.setSolicitud(new SolicitudEntity());
      solOpoForEntity.getSolicitud().setIdSol(idSol);
      solOpoForDatosDao.save(solOpoForEntity);
    } catch (Exception e) {
      throw new SinacException(SinacExceptionMessageType.CUSTOM_MESSAGE).logMessageParams(
          "Se ha producido un error al intentar guardar los datos de oposición: {" + idSol + "} - " + e.getMessage());
    }

  }

  @Override
  public List<SolicitudDto> getListaSolicitudesSede() {
    return solicitudMapper.toDtoList(solicitudDao.getListaIdSolicitudesSede());
  }

}
