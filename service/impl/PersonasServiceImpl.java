package es.mjusticia.sinac.core.business.service.impl;

/*-
 * #%L
 * sinac-core
 * %%
 * Copyright (C) 2023 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.apache.cxf.common.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.PersonasService;
import es.mjusticia.sinac.core.model.dto.PersonaDomicilioDto;
import es.mjusticia.sinac.core.model.dto.PersonaDto;
import es.mjusticia.sinac.core.model.dto.PersonaFamDto;
import es.mjusticia.sinac.core.model.dto.PersonaFamMatrimonioDto;
import es.mjusticia.sinac.core.model.dto.PersonaIdentificaDto;
import es.mjusticia.sinac.core.model.dto.PersonaRcDto;
import es.mjusticia.sinac.core.model.dto.PersonasContactosElectronicosDto;
import es.mjusticia.sinac.core.model.dto.PersonasDomiciliosDto;
import es.mjusticia.sinac.core.model.entity.PersonaContactoElectronicoEntity;
import es.mjusticia.sinac.core.model.entity.PersonaDomicilioEntity;
import es.mjusticia.sinac.core.model.entity.PersonaEntity;
import es.mjusticia.sinac.core.model.entity.PersonaFamEntity;
import es.mjusticia.sinac.core.model.entity.PersonaFamMatrimonioEntity;
import es.mjusticia.sinac.core.model.entity.PersonaIdentificaEntity;
import es.mjusticia.sinac.core.model.entity.PersonaRcEntity;
import es.mjusticia.sinac.core.model.entity.PersonasContactosElectronicosEntity;
import es.mjusticia.sinac.core.model.entity.PersonasDomiciliosEntity;
import es.mjusticia.sinac.core.model.mapper.PersonaDomicilioMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaFamMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaFamMatrimonioMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaIdentificaMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaMapperAux;
import es.mjusticia.sinac.core.model.mapper.PersonaRcMapper;
import es.mjusticia.sinac.core.model.mapper.PersonaWithSpMapper;
import es.mjusticia.sinac.core.model.mapper.PersonasContactosElectronicosMapper;
import es.mjusticia.sinac.core.model.mapper.PersonasDomiciliosMapper;
import es.mjusticia.sinac.core.persistence.PaisesDao;
import es.mjusticia.sinac.core.persistence.PersonaContactoElectronicoDao;
import es.mjusticia.sinac.core.persistence.PersonaDao;
import es.mjusticia.sinac.core.persistence.PersonaDomicilioDao;
import es.mjusticia.sinac.core.persistence.PersonaFamDao;
import es.mjusticia.sinac.core.persistence.PersonaFamMatrimonioDao;
import es.mjusticia.sinac.core.persistence.PersonaIdentificaDao;
import es.mjusticia.sinac.core.persistence.PersonaRcDao;
import es.mjusticia.sinac.core.persistence.PersonasContactosElectronicosDao;
import es.mjusticia.sinac.core.persistence.PersonasDomiciliosDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

@Component
public class PersonasServiceImpl implements PersonasService {

  private static final Logger LOG = LoggerFactory.getLogger(PersonasServiceImpl.class);

  private static final String PERSONA_NO_ENCONTRADA = "Persona no encontrada";

  @Autowired
  private CatalogosService catalogoService;

  @Autowired
  private PersonaDao personaDao;

  @Autowired
  private PersonaMapper personaMapper;

  @Autowired
  private PersonaRcDao personaRcDao;

  @Autowired
  private PaisesDao paisesDao;

  @Autowired
  private PersonaRcMapper personaRcMapper;

  @Autowired
  private PersonasContactosElectronicosMapper personasConEleMapper;

  @Autowired
  private PersonaContactoElectronicoDao personaConEleDao;

  @Autowired
  private PersonasContactosElectronicosDao personasConEleDao;

  @Autowired
  private PersonasDomiciliosDao personasDomiciliosDao;

  @Autowired
  private PersonaDomicilioMapper personaDomicilioMapper;

  @Autowired
  private PersonasDomiciliosMapper personasDomiciliosMapper;

  @Autowired
  private PersonaWithSpMapper personasWithSpMapper;

  @Autowired
  private PersonaDomicilioDao personaDomicilioDao;

  @Autowired
  private PersonaIdentificaDao personaIdentificaDao;

  @Autowired
  private PersonaIdentificaMapper personaIdentificaMapper;

  @Autowired
  private PersonaFamDao personaFamDao;

  @Autowired
  private PersonaFamMatrimonioDao personaFamMatrimonioDao;

  @Autowired
  private PersonaFamMapper personaFamMapper;

  @Autowired
  private PersonaFamMatrimonioMapper personaFamMatrimonioMapper;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private PersonaMapperAux personaMapperAux;

  @Override
  public PersonaDomicilioDto getPersonaDomicilioById(BigInteger idPer) {
    PersonaDomicilioEntity personaDomicilioEntity = personaDomicilioDao.findById(idPer)
        .orElseThrow(() -> new EntityNotFoundException(PERSONA_NO_ENCONTRADA));

    return personaDomicilioMapper.toDto(personaDomicilioEntity);
  }

  @Override
  public PersonaDto savePersona(PersonaDto personaDto) throws SinacException {
    LOG.debug("PersonasServiceImpl.savePersona - Init");
    try {
      if (personaDto.getProvNac() != null && personaDto.getProvNac().getIdProvincia() == null) {
        personaDto.setProvNac(null);
      }
      if (personaDto.getLocalNac() != null && personaDto.getLocalNac().getIdMunicipio() == null) {
        personaDto.setLocalNac(null);
      }
      boolean actualizar = false;
      if (personaDto.getIdPer() != null) {
        actualizar = true;
      }
      if (personaDto.getNombre() != null && !personaDto.getNombre().isEmpty()) {
        PersonaEntity personaEntity = personaMapper.toEntity(personaDto);
        personaEntity.setFlgActivo(true);
        if (actualizar) {
          PersonaEntity personaEntityPrevio = personaDao.findByIdPersona(personaDto.getIdPer());
          personaEntity.setFechaCreacion(personaEntityPrevio.getFechaCreacion());
          personaEntity.setFechaIniVig(personaEntityPrevio.getFechaIniVig());
          personaEntity.setCreadoPor(personaEntityPrevio.getCreadoPor());
        }
        personaDao.save(personaEntity);

        /*
         * En esta parte se condiciona por el historico con el flag activo, por eso solo
         * se hace el save si no es actualizar
         */
        if (actualizar) {
          desactivarDatosPersonasAnteriores(personaDto);
        }

        saveDatosCompletosPersona(personaDto, personaEntity);

        return personaMapper.toDto(personaEntity);
      } else
        LOG.debug("PersonasServiceImpl.savePersona - End");
      return personaDto;
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_PERSONAS_1).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public PersonaDto saveSoloPersona(PersonaDto personaDto) throws SinacException {
    LOG.debug("PersonasServiceImpl.savePersona - Init");
    try {
      if (personaDto.getProvNac() != null && personaDto.getProvNac().getIdProvincia() == null) {
        personaDto.setProvNac(null);
      }
      if (personaDto.getLocalNac() != null && personaDto.getLocalNac().getIdMunicipio() == null) {
        personaDto.setLocalNac(null);
      }
      if (personaDto.getSegundaNacionalidad() != null && personaDto.getSegundaNacionalidad().getIdPais() == null) {
    	  personaDto.setSegundaNacionalidad(null);
      }

      if (personaDto.getEstadoCivil().getCodLdvMae() == null) {
        personaDto.setEstadoCivil(null);
      }

      if (personaDto.getSexo().getCodLdvMae() == null) {
        personaDto.setSexo(null);
      }

      boolean actualizar = false;
      if (personaDto.getIdPer() != null) {
        actualizar = true;
      }
      if ((personaDto.getNombre() != null && !personaDto.getNombre().isEmpty())
          && (personaDto.getApellido1() != null && !personaDto.getApellido1().isEmpty())) {
        PersonaEntity personaEntity = personaMapper.toEntity(personaDto);
        personaEntity.setFlgActivo(true);
        if (actualizar) {
          PersonaEntity personaEntityPrevio = personaDao.findByIdPersona(personaDto.getIdPer());
          personaEntity.setFechaCreacion(personaEntityPrevio.getFechaCreacion());
          personaEntity.setFechaIniVig(personaEntityPrevio.getFechaIniVig());
          personaEntity.setCreadoPor(personaEntityPrevio.getCreadoPor());
        }
        personaDao.save(personaEntity);

        return personaMapper.toDto(personaEntity);
      } else
        LOG.debug("PersonasServiceImpl.savePersona - End");
      return personaDto;
    } catch (Exception e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_PERSONAS_1).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public void desactivarPersona(PersonaDto personaDto) throws SinacException {
    desactivarDatosPersonasAnteriores(personaDto);
    PersonaEntity personaEntityPrevio = personaDao.findByIdPersona(personaDto.getIdPer());
    personaEntityPrevio.setFlgActivo(false);
    personaDao.save(personaEntityPrevio);
  }

  private void saveDatosCompletosPersona(PersonaDto personaDto, PersonaEntity personaEntity) {
    if (!personaDto.getPersonaRcDtos().isEmpty() && !personaDto.getPersonaRcDtos().get(0).getNomRc().isBlank()) {
      for (PersonaRcDto personaRcDtoAlta : personaDto.getPersonaRcDtos()) {
        PersonaRcEntity personaRcAltaEntity = personaRcMapper.toEntity(personaRcDtoAlta);
        personaRcAltaEntity.setPersonaEntity(personaEntity);
        personaRcDao.save(personaRcAltaEntity);
      }
    }

    for (PersonaIdentificaDto personaDocIdDto : personaDto.getPersonasIdentificaDtos()) {
      PersonaIdentificaEntity personaIdentificacionAltaEntity = personaIdentificaMapper.toEntity(personaDocIdDto);
      personaIdentificacionAltaEntity.setPersonaEntity(personaEntity);
      personaIdentificacionAltaEntity.setFlgActivo(true);
      personaIdentificacionAltaEntity.setIdPerIden(null);
      personaIdentificaDao.save(personaIdentificacionAltaEntity);
    }

    for (PersonasDomiciliosDto personasDomiciliosDto : personaDto.getPersonasDomiciliosDto()) {
      PersonasDomiciliosEntity personasDomiciliosEntity = personasDomiciliosMapper.toEntity(personasDomiciliosDto);
      PersonaDomicilioEntity personaDomicilioEntity = personasDomiciliosEntity.getPersonaDomicilioEntity();
      if (personasDomiciliosDto.getPersonaDomicilioDto() != null
          && personasDomiciliosDto.getPersonaDomicilioDto().getTipoDomicilio() != null
          && personasDomiciliosDto.getPersonaDomicilioDto().getTipoDomicilio().equals("1")) {
        personaDomicilioEntity.setPaises(paisesDao.getPaisPorCodigo("724"));
      }
      personaDomicilioEntity.setFlgActivo(true);
      personaDomicilioEntity.setIdPerDom(null);
      personaDomicilioEntity = personaDomicilioDao.save(personaDomicilioEntity);
      personasDomiciliosEntity.setPersonaEntity(personaEntity);
      personasDomiciliosEntity.setPersonaDomicilioEntity(personaDomicilioEntity);
      personasDomiciliosEntity.setFlgActivo(true);
      personasDomiciliosEntity.setIdPerPerDom(null);
      personasDomiciliosEntity = personasDomiciliosDao.save(personasDomiciliosEntity);
      personaEntity.getPersonasDomiciliosEntity().clear();
      personaEntity.getPersonasDomiciliosEntity().add(personasDomiciliosEntity);
    }

    for (PersonasContactosElectronicosDto personasPersonasContactosElectronicosDtos : personaDto
        .getPersonasContactosElectronicosDtos()) {
      PersonasContactosElectronicosEntity personasContactosElectronicosEntity = personasConEleMapper
          .toEntity(personasPersonasContactosElectronicosDtos);
      PersonaContactoElectronicoEntity personaContactoElectronicoEntity = personasContactosElectronicosEntity
          .getPersonaContactoElectronicoEntity();

      personaContactoElectronicoEntity.setFlgActivo(true);
      personaContactoElectronicoEntity.setIdPerConEle(null);
      personaContactoElectronicoEntity = personaConEleDao.save(personaContactoElectronicoEntity);
      personasContactosElectronicosEntity.setPersonaEntity(personaEntity);
      personasContactosElectronicosEntity.setPersonaContactoElectronicoEntity(personaContactoElectronicoEntity);
      personasContactosElectronicosEntity.setFlgActivo(true);
      personasContactosElectronicosEntity.setIdPerPerConEle(null);
      personasContactosElectronicosEntity = personasConEleDao.save(personasContactosElectronicosEntity);
      personaEntity.getPersonasContactosElectronicosEntity().clear();
      personaEntity.getPersonasContactosElectronicosEntity().add(personasContactosElectronicosEntity);
    }

    if (personaDto.getPersonaFamDtos() != null && !personaDto.getPersonaFamDtos().isEmpty()) {
      for (PersonaFamDto personaFamDto : personaDto.getPersonaFamDtos()) {
        personaFamDto.setLdvMaestraDto(catalogoService.getCatalogoByCod("CON-HIJ"));
        PersonaFamEntity personaFamEntity = personaFamMapper.toEntity(personaFamDto);
        personaFamEntity.setPersonaEntity(personaEntity);
        personaFamEntity.setFlgActivo(true);
        personaFamEntity.setIdPerFam(null);
        personaFamDao.save(personaFamEntity);
      }
    }

    if (personaDto.getPersonaFamMatrimonioDto() != null) {
      personaDto.getPersonaFamMatrimonioDto().getPersonaFamDto()
          .setLdvMaestraDto(catalogoService.getCatalogoByCod("CON-CON"));
      if (personaDto.getPersonaFamMatrimonioDto().getPersonaFamDto() != null
          && personaDto.getPersonaFamMatrimonioDto().getPersonaFamDto().getNombre() != null
          && !personaDto.getPersonaFamMatrimonioDto().getPersonaFamDto().getNombre().isEmpty()
          && tieneDatosMatrimonio(personaDto.getPersonaFamMatrimonioDto())) {
        PersonaFamEntity personaFamEntity = personaFamMapper
            .toEntity(personaDto.getPersonaFamMatrimonioDto().getPersonaFamDto());
        personaFamEntity.setPersonaEntity(personaEntity);
        personaFamEntity.setFlgActivo(true);
        personaFamEntity.setIdPerFam(null);
        personaFamEntity.setPersonaFamMatrimonioEntity(null);

        personaFamEntity = personaFamDao.save(personaFamEntity);

        PersonaFamMatrimonioEntity personaMatrimonioEntity = personaFamMatrimonioMapper
            .toEntity(personaDto.getPersonaFamMatrimonioDto());

        personaMatrimonioEntity.setPersonaFamEntity(personaFamEntity);
        personaMatrimonioEntity.setFlgActivo(true);
        personaMatrimonioEntity.setIdMatrimonio(null);

        personaMatrimonioEntity = personaFamMatrimonioDao.save(personaMatrimonioEntity);

      }
    }

  }

  private boolean tieneDatosMatrimonio(PersonaFamMatrimonioDto matrimonioDto) {
    return matrimonioDto != null && (matrimonioDto.getFechaMatrimonio() != null
        || matrimonioDto.getInscritoRrcc() != null && !matrimonioDto.getInscritoRrcc().isEmpty()
        || matrimonioDto.getPagina() != null && !matrimonioDto.getPagina().isEmpty()
        || matrimonioDto.getTomo() != null && !matrimonioDto.getTomo().isEmpty());
  }

  @Override
  public String getNumeroAcreditacionByIdPersonaAndFlagPrincipalToTrue(final BigInteger idPersona)
      throws SinacException {
    LOG.debug("PersonasServiceImpl.getNumeroAcreditacionByIdPersonaAndFlagPrincipalToTrue - Init");

    String numeroAcreditacion = null;

    try {
      numeroAcreditacion = personaIdentificaDao.getNumeroAcreditacionByIdPersonaAndFlagPrincipalToTrue(idPersona)
          .orElseThrow().getNumAcreditacion();
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PERSONAS_2)
          .logMessageParams(idPersona).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PERSONAS_3).logMessageParams(idPersona)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("PersonasServiceImpl.getNumeroAcreditacionByIdPersonaAndFlagPrincipalToTrue - End");

    return numeroAcreditacion;
  }

  private void desactivarDatosPersonasAnteriores(PersonaDto personaDto) {
    if (personaDto.getPersonaRcDtos() != null && !personaDto.getPersonaRcDtos().isEmpty()) {
      PersonaRcEntity personaRcEntity = personaRcDao.findByIdPersona(personaDto.getIdPer());
      if (personaRcEntity != null) {
        PersonaRcDto personaRcAnteriorDto = personaRcMapper.toDto(personaRcEntity);
        if (!personaDto.getPersonaRcDtos().get(0).compareExistentValues(personaRcAnteriorDto)) {
          personaRcEntity.setFlgActivo(false);
          personaRcDao.save(personaRcEntity);
        } else {
          personaDto.getPersonaRcDtos().clear();
        }
      }
    }
    List<PersonaIdentificaEntity> personaDocsIdentidadEntity = personaIdentificaDao
        .findByIdPersona(personaDto.getIdPer());

    if (personaDocsIdentidadEntity != null) {
      for (PersonaIdentificaEntity personaDocIdentidadEntity : personaDocsIdentidadEntity) {
        PersonaIdentificaDto personaDocIdentidadAnteriorDto = personaIdentificaMapper.toDto(personaDocIdentidadEntity);
        boolean existePersona = false;
        PersonaIdentificaDto eliminarPersona = null;
        for (PersonaIdentificaDto persona : personaDto.getPersonasIdentificaDtos()) {
          if (personaDocIdentidadEntity.getIdPerIden().equals(persona.getIdPerIden())) {
            existePersona = true;
            if (!persona.compareExistentValues(personaDocIdentidadAnteriorDto)) {
              personaDocIdentidadEntity.setFlgActivo(false);
              personaIdentificaDao.save(personaDocIdentidadEntity);
            } else {
              eliminarPersona = persona;
            }
          }
        }
        if (eliminarPersona != null) {
          personaDto.getPersonasIdentificaDtos().remove(eliminarPersona);
        }
        if (!existePersona) {
          personaDocIdentidadEntity.setFlgActivo(false);
          personaIdentificaDao.save(personaDocIdentidadEntity);
        }
      }
    }

    PersonasDomiciliosEntity personasDomiciliosEntity = personasDomiciliosDao.findByIdPersona(personaDto.getIdPer());
    PersonaDomicilioEntity personaDomicilioEntity = personaDomicilioDao.findByIdPersona(personaDto.getIdPer());
    if (personasDomiciliosEntity != null && personaDomicilioEntity != null) {
      PersonaDomicilioDto personaDomicilioDtoAnterior = personaDomicilioMapper.toDto(personaDomicilioEntity);
      if (personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getLugarResidencia() != null
          && personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().getLugarResidencia().isBlank()) {

        personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto().setLugarResidencia(null);
      }
      if (!personaDto.getPersonasDomiciliosDto().get(0).getPersonaDomicilioDto()
          .compareExistentValues(personaDomicilioDtoAnterior)) {
        personaDomicilioEntity.setFlgActivo(false);
        personaDomicilioDao.save(personaDomicilioEntity);
        personasDomiciliosEntity.setFlgActivo(false);
        personasDomiciliosDao.save(personasDomiciliosEntity);
      } else {
        personaDto.getPersonasDomiciliosDto().clear();
      }
    }

    PersonasContactosElectronicosEntity personasContactosElectronicosEntity = personasConEleDao
        .findByIdPersona(personaDto.getIdPer());
    if (personasContactosElectronicosEntity != null
        && personasContactosElectronicosEntity.getPersonaContactoElectronicoEntity() != null
        && personasContactosElectronicosEntity.getPersonaContactoElectronicoEntity().getIdPerConEle() != null) {
      PersonasContactosElectronicosDto personasContactosElectronicosAnteriorDto = personasConEleMapper
          .toDto(personasContactosElectronicosEntity);
      if (!personaDto.getPersonasContactosElectronicosDtos().get(0).getPersonaContactoElectronicoDto()
          .compareExistentValues(personasContactosElectronicosAnteriorDto.getPersonaContactoElectronicoDto())) {
        personasContactosElectronicosEntity.setFlgActivo(false);
        personasConEleDao.save(personasContactosElectronicosEntity);
      } else {
        personaDto.getPersonasContactosElectronicosDtos().clear();
      }
    }

    Set<PersonaFamEntity> listaPersonaFamEntity = personaFamDao.findByIdPersona(personaDto.getIdPer());
    if (listaPersonaFamEntity != null) {
      for (PersonaFamEntity personaFamEntity : listaPersonaFamEntity) {
        PersonaFamDto personaFamDtoAnterior = personaFamMapper.toDto(personaFamEntity);
        boolean existePersona = false;
        PersonaFamDto eliminarPersona = null;
        for (PersonaFamDto persona : personaDto.getPersonaFamDtos()) {
          if (personaFamEntity.getIdPerFam().equals(persona.getIdPerFam())) {
            existePersona = true;
            if (!persona.compareExistentValues(personaFamDtoAnterior)) {
              personaFamEntity.setFlgActivo(false);
              personaFamDao.save(personaFamEntity);
            } else {
              eliminarPersona = persona;
            }
          }
        }
        if (eliminarPersona != null) {
          personaDto.getPersonaFamDtos().remove(eliminarPersona);
        }
        if (!existePersona) {
          personaFamEntity.setFlgActivo(false);
          personaFamDao.save(personaFamEntity);
        }
      }
    }

    PersonaFamEntity conyugeEntity = personaFamDao.getConyugeByIdPersona(personaDto.getIdPer());
    if (conyugeEntity != null) {
      PersonaFamMatrimonioDto personaFamMatrimonioDtoAnterior = personaFamMatrimonioMapper
          .toDto(personaFamMatrimonioDao.getPersonaFamMatrimonioByIdPerFam(conyugeEntity.getIdPerFam()));
      if (!personaDto.getPersonaFamMatrimonioDto().compareExistentValues(personaFamMatrimonioDtoAnterior)) {
        conyugeEntity.getPersonaFamMatrimonioEntity().setFlgActivo(false);
        PersonaFamMatrimonioEntity matrimonioEntity = conyugeEntity.getPersonaFamMatrimonioEntity();
        personaFamMatrimonioDao.save(matrimonioEntity);
      } else {
        personaDto.setPersonaFamMatrimonioDto(null);
      }

    }

  }

  /**
   * Metodo para eliminar una persona.
   * 
   * @param persona persona a eliminar.
   */
  @Override
  public void deletePersona(PersonaEntity persona) {
    deletePersonaRcEntities(persona);
    deletePersonasIdentificaEntities(persona);
    deletePersonasDomiciliosEntity(persona);
    deletePersonasContactosElectronicosEntity(persona);
    deletePersonaFamEntities(persona);
  }

  /**
   * Metodo que elimina los datos del registro asignados a una persona.
   * 
   * @param persona La persona a eliminar.
   */
  private void deletePersonaRcEntities(PersonaEntity persona) {
    if (persona.getPersonaRcEntities() != null && !persona.getPersonaRcEntities().isEmpty()) {
      for (PersonaRcEntity registrocivil : persona.getPersonaRcEntities()) {
        personaRcDao.delete(registrocivil);
      }
    }
  }

  /**
   * Metodo que elimina los datos de identificación asignados a una persona.
   * 
   * @param persona La persona a eliminar.
   */
  private void deletePersonasIdentificaEntities(PersonaEntity persona) {
    if (persona.getPersonasIdentificaEntities() != null && !persona.getPersonasIdentificaEntities().isEmpty()) {
      for (PersonaIdentificaEntity identificacion : persona.getPersonasIdentificaEntities()) {
        personaIdentificaDao.delete(identificacion);
      }
    }
  }

  /**
   * Metodo que elimina los datos del domicilio asignados a una persona.
   * 
   * @param persona La persona a eliminar.
   */
  private void deletePersonasDomiciliosEntity(PersonaEntity persona) {
    if (persona.getPersonasDomiciliosEntity() != null && !persona.getPersonasDomiciliosEntity().isEmpty()) {
      for (PersonasDomiciliosEntity domicilios : persona.getPersonasDomiciliosEntity()) {
        personasDomiciliosDao.delete(domicilios);
        personaDomicilioDao.delete(domicilios.getPersonaDomicilioEntity());
      }
    }
  }

  /**
   * Metodo que elimina los datos del contacto electronico asignados a una
   * persona.
   * 
   * @param persona La persona a eliminar.
   */
  private void deletePersonasContactosElectronicosEntity(PersonaEntity persona) {
    if (persona.getPersonasContactosElectronicosEntity() != null
        && !persona.getPersonasContactosElectronicosEntity().isEmpty()) {
      for (PersonasContactosElectronicosEntity contactos : persona.getPersonasContactosElectronicosEntity()) {
        personasConEleDao.delete(contactos);
        personaConEleDao.delete(contactos.getPersonaContactoElectronicoEntity());
      }
    }
  }

  /**
   * Metodo que elimina los datos de los hijo asignados a una persona.
   * 
   * @param persona La persona a eliminar.
   */
  private void deletePersonaFamEntities(PersonaEntity persona) {
    if (persona.getPersonaFamEntities() != null && !persona.getPersonaFamEntities().isEmpty()) {
      for (PersonaFamEntity personaFamEntity : persona.getPersonaFamEntities()) {
        personaFamDao.delete(personaFamEntity);
      }
    }
  }

  @Override
  public PersonaFamDto getConyugeByIdPersona(BigInteger idPersona) {
    return personaFamMapper.toDto(personaFamDao.getConyugeByIdPersona(idPersona));
  }

  @Override
  public Page<PersonaDto> getPersonasRastreo(String identificador, String nombre, String primerApellido,
      String segundoApellido, Date fechaNacimiento, Pageable pageable, String tipoOrdenacion, String columnaOrdenar) {

    List<PersonaEntity> personasRastreoEntity;

    Map<Integer, List<PersonaDto>> mapa = new HashMap<>();
    Map<String, Object> parametrosNuevo = new LinkedHashMap<>();
    if (identificador != null) {
      parametrosNuevo.put("Nº Identificador", identificador);
    }
    if (nombre != null) {
      parametrosNuevo.put("Nombre", nombre);
    }

    if (fechaNacimiento != null) {
      parametrosNuevo.put("Fecha de Nacimiento", fechaNacimiento);
    }
    if (primerApellido != null) {
      parametrosNuevo.put("Primer Apellido", primerApellido);
    }
    if (segundoApellido != null) {
      parametrosNuevo.put("Segundo Apellido", segundoApellido);
    }

    while (!parametrosNuevo.isEmpty()) {
      personasRastreoEntity = personaDao.getPersonasRastreo((String) parametrosNuevo.get("Nº Identificador"),
          (String) parametrosNuevo.get("Nombre"), (Date) parametrosNuevo.get("Fecha de Nacimiento"),
          (String) parametrosNuevo.get("Primer Apellido"), (String) parametrosNuevo.get("Segundo Apellido"));
      if (!CollectionUtils.isEmpty(personasRastreoEntity)) {
        mapa = addPersonaRastreoDtoToList(personasRastreoEntity, pageable);
        break;
      }
      String lastKey = (String) parametrosNuevo.keySet().toArray()[parametrosNuevo.size() - 1];
      parametrosNuevo.remove(lastKey);
    }

    if (mapa.isEmpty()) {
      return null;
    } else {
      if (tipoOrdenacion != null && columnaOrdenar != null) {
        ordenacionMapaRastreo(mapa, tipoOrdenacion, columnaOrdenar);
      }
      if (!parametrosNuevo.isEmpty()) {

        if (parametrosNuevo.get("Fecha de Nacimiento") != null) {
          Date fecha = (Date) parametrosNuevo.get("Fecha de Nacimiento");

          SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
          String fechaTransformada = formato.format(fecha);
          parametrosNuevo.put("Fecha de Nacimiento", fechaTransformada);
        }
        filtrosEmpleados(parametrosNuevo, mapa);
      }
      return new PageImpl<>(mapa.values().stream().toList().get(0), pageable, mapa.keySet().stream().toList().get(0));
    }
  }

  private Map<Integer, List<PersonaDto>> addPersonaRastreoDtoToList(List<PersonaEntity> personasRastreoEntity,
      Pageable pageable) {
    Map<Integer, List<PersonaDto>> mapa = new HashMap<>();
    List<PersonaDto> personasRastreoDto = new ArrayList<>();
    for (PersonaEntity personaEntity : personasRastreoEntity) {
      personasRastreoDto.add(personaMapper.toDto(personaEntity));
    }
    mapa.put(personasRastreoDto.size(), personasRastreoDto);
    List<PersonaDto> resultado = new ArrayList<>();
    int pagina = pageable.getPageNumber();
    int total = ((pageable.getPageNumber() + 1) * (pageable.getPageSize()));
    resultado.addAll(personasRastreoDto.subList(((pagina + (pageable.getPageSize() - pagina)) * pagina),
        (total > personasRastreoDto.size() ? personasRastreoDto.size() : total)));
    mapa.put(personasRastreoDto.size(), resultado);
    return mapa;
  }

  private void filtrosEmpleados(Map<String, Object> parametrosNuevo, Map<Integer, List<PersonaDto>> mapa) {
    for (Map.Entry<String, Object> entry : parametrosNuevo.entrySet()) {
      mapa.values().stream().toList().get(0).get(0).getFiltrosEmpleadosRastreo().put(entry.getKey(),
          (String) entry.getValue());
    }
  }

  private void ordenacionMapaRastreo(Map<Integer, List<PersonaDto>> mapa, String tipoOrdenacion,
      String columnaOrdenar) {
    if (tipoOrdenacion != null && tipoOrdenacion.equals("ASC") && columnaOrdenar != null
        && columnaOrdenar.equals("numIdentificacion")) {
      mapa.values().stream().toList().get(0).sort((p1, p2) -> {
        p1.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        p2.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        String nombre1 = p1.getPersonasIdentificaDtos().isEmpty() ? ""
            : p1.getPersonasIdentificaDtos().get(0).getNumAcreditacion();
        String nombre2 = p2.getPersonasIdentificaDtos().isEmpty() ? ""
            : p2.getPersonasIdentificaDtos().get(0).getNumAcreditacion();
        return nombre1.compareTo(nombre2);
      });
    } else if (tipoOrdenacion != null && tipoOrdenacion.equals("DESC") && columnaOrdenar != null
        && columnaOrdenar.equals("numIdentificacion")) {
      mapa.values().stream().toList().get(0).sort((p1, p2) -> {
        p1.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        p2.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        String nombre1 = p1.getPersonasIdentificaDtos().isEmpty() ? ""
            : p1.getPersonasIdentificaDtos().get(0).getNumAcreditacion();
        String nombre2 = p2.getPersonasIdentificaDtos().isEmpty() ? ""
            : p2.getPersonasIdentificaDtos().get(0).getNumAcreditacion();
        return nombre2.compareTo(nombre1);
      });
    }

    if (tipoOrdenacion != null && tipoOrdenacion.equals("ASC") && columnaOrdenar != null
        && columnaOrdenar.equals("nombre")) {
      mapa.values().stream().toList().get(0).sort((p1, p2) -> {
        p1.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        p2.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        String nombre1 = p1.getNombre().isEmpty() ? "" : p1.getNombre();
        String nombre2 = p2.getNombre().isEmpty() ? "" : p2.getNombre();
        return nombre1.compareTo(nombre2);
      });
    } else if (tipoOrdenacion != null && tipoOrdenacion.equals("DESC") && columnaOrdenar != null
        && columnaOrdenar.equals("nombre")) {
      mapa.values().stream().toList().get(0).sort((p1, p2) -> {
        p1.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        p2.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        String nombre1 = p1.getNombre().isEmpty() ? "" : p1.getNombre();
        String nombre2 = p2.getNombre().isEmpty() ? "" : p2.getNombre();
        return nombre2.compareTo(nombre1);
      });
    }

    if (tipoOrdenacion != null && tipoOrdenacion.equals("ASC") && columnaOrdenar != null
        && columnaOrdenar.equals("apellido1")) {
      mapa.values().stream().toList().get(0).sort((p1, p2) -> {
        p1.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        p2.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        String nombre1 = p1.getApellido1().isEmpty() ? "" : p1.getApellido1();
        String nombre2 = p2.getApellido1().isEmpty() ? "" : p2.getApellido1();
        return nombre1.compareTo(nombre2);
      });
    } else if (tipoOrdenacion != null && tipoOrdenacion.equals("DESC") && columnaOrdenar != null
        && columnaOrdenar.equals("apellido1")) {
      mapa.values().stream().toList().get(0).sort((p1, p2) -> {
        p1.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        p2.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        String nombre1 = p1.getApellido1().isEmpty() ? "" : p1.getApellido1();
        String nombre2 = p2.getApellido1().isEmpty() ? "" : p2.getApellido1();
        return nombre2.compareTo(nombre1);
      });
    }

    if (mapa.values().stream().toList().get(0).get(0).getApellido2() != null && tipoOrdenacion != null
        && tipoOrdenacion.equals("ASC") && columnaOrdenar != null && columnaOrdenar.equals("apellido2")) {
      mapa.values().stream().toList().get(0).sort((p1, p2) -> {
        p1.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        p2.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        String nombre1 = p1.getApellido2();
        String nombre2 = p2.getApellido2();
        return nombre1.compareTo(nombre2);
      });
    } else if (mapa.values().stream().toList().get(0).get(0).getApellido2() != null && tipoOrdenacion != null
        && tipoOrdenacion.equals("DESC") && columnaOrdenar != null && columnaOrdenar.equals("apellido2")) {
      mapa.values().stream().toList().get(0).sort((p1, p2) -> {
        p1.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        p2.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        String nombre1 = p1.getApellido2().isEmpty() ? "" : p1.getApellido2();
        String nombre2 = p2.getApellido2().isEmpty() ? "" : p2.getApellido2();
        return nombre2.compareTo(nombre1);
      });
    }

    if (tipoOrdenacion != null && tipoOrdenacion.equals("ASC") && columnaOrdenar != null
        && columnaOrdenar.equals("fechaNacimiento")) {
      mapa.values().stream().toList().get(0).sort((p1, p2) -> {
        p1.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        p2.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        Date fecha1 = p1.getFechaNacimiento();
        Date fecha2 = p2.getFechaNacimiento();
        if (fecha1 == null && fecha2 == null)
          return 0;
        if (fecha1 == null)
          return 1;
        if (fecha2 == null)
          return -1;
        return fecha1.compareTo(fecha2);
      });
    } else if (tipoOrdenacion != null && tipoOrdenacion.equals("DESC") && columnaOrdenar != null
        && columnaOrdenar.equals("fechaNacimiento")) {
      mapa.values().stream().toList().get(0).sort((p1, p2) -> {
        p1.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        p2.setTipoOrdenacionBusquedaRastreo(tipoOrdenacion);
        Date fecha1 = p1.getFechaNacimiento();
        Date fecha2 = p2.getFechaNacimiento();
        if (fecha1 == null && fecha2 == null)
          return 0;
        if (fecha1 == null)
          return 1;
        if (fecha2 == null)
          return -1;
        return fecha2.compareTo(fecha1);
      });
    }
  }

  @Override
  public PersonaDto getPersonaByIdPer(BigInteger idPer) throws SinacException {
    PersonaDto personaDto = personasWithSpMapper.toDto(personaDao.getPersonaByIdPer(idPer));
    personaDto.setPersonaRcDtos(personaDto.getPersonaRcDtos().stream().filter(e -> e.getFlgActivo()).toList());
    personaDto.setPersonasContactosElectronicosDtos(personaDto.getPersonasContactosElectronicosDtos().stream()
        .filter(e -> e.getPersonaContactoElectronicoDto().isFlgActivo()).toList());
    List<PersonaRcDto> personasRc = new ArrayList<>();
    setPersonaRc(personaDto, personasRc);
    personaDto.setPersonaRcDtos(personasRc);
    List<PersonasContactosElectronicosDto> personasContacto = new ArrayList<>();
    setPersonasContacto(personaDto, personasContacto);
    personaDto.setPersonasContactosElectronicosDtos(personasContacto);
    List<PersonasDomiciliosDto> personasDomicilios = new ArrayList<>();
    setPersonasDomicilios(personaDto, personasDomicilios);
    personaDto.setPersonasDomiciliosDto(personasDomicilios);
    List<PersonaIdentificaDto> personasIdentificaDto = new ArrayList<>();
    for (PersonaIdentificaDto personaIdentificaDto : personaDto.getPersonasIdentificaDtos()) {
      if (personaIdentificaDto.isFlgActivo().booleanValue()) {
        personasIdentificaDto.add(personaIdentificaDto);
      }
    }
    personaDto.setPersonasIdentificaDtos(personasIdentificaDto);

    return personaDto;
  }

  private void setPersonasDomicilios(PersonaDto personaDto, List<PersonasDomiciliosDto> personasDomicilios) {
    for (PersonasDomiciliosDto personasDomiciliosDto : personaDto.getPersonasDomiciliosDto()) {
      if (personasDomiciliosDto.getPersonaDomicilioDto().isFlgActivo().booleanValue()) {
        personasDomicilios.add(personasDomiciliosDto);
        break;
      }
    }
  }

  private void setPersonasContacto(PersonaDto personaDto, List<PersonasContactosElectronicosDto> personasContacto) {
    for (PersonasContactosElectronicosDto personaContacto : personaDto.getPersonasContactosElectronicosDtos()) {
      if (personaContacto.getPersonaContactoElectronicoDto().isFlgActivo().booleanValue()) {
        personasContacto.add(personaContacto);
        break;
      }
    }
  }

  private void setPersonaRc(PersonaDto personaDto, List<PersonaRcDto> personasRc) {
    for (PersonaRcDto personaRc : personaDto.getPersonaRcDtos()) {
      if (personaRc.getFlgActivo().booleanValue()) {
        personasRc.add(personaRc);
        break;
      }
    }
  }

  @Override
  public List<BigInteger> getIdsInteresadosAltaFiliaciones() throws SinacException {
    return personaDao.getIdsInteresadosAltaFiliaciones();
  }

  @Override
  public List<BigInteger> getIdsInteresadosConsultaFiliaciones(String maxItemConsultaFiliaciones)
      throws SinacException {
    return personaDao.getIdsInteresadosConsultaFiliaciones(entityManager, maxItemConsultaFiliaciones);
  }

  @Override
  public List<PersonaDto> getPersonasRastreoVea(String numAcreditacion) {

    return personaMapperAux.toDtos(personaDao.getPersonasRastreoVea(numAcreditacion));
  }

  @Override
  public PersonaDto buscarPersonaPorIdentificacion(String numAcreditacion, String codTipoDocumento)
      throws SinacException {
    LOG.debug("Buscando persona por identificación - num={}, tipo={}", numAcreditacion, codTipoDocumento);

    try {
      Optional<PersonaEntity> personaEntity = personaDao.findByNumeroIdentificacionAndTipo(numAcreditacion,
          codTipoDocumento);

      if (personaEntity.isPresent()) {
        LOG.info("Persona encontrada - idPer={}, num={}", personaEntity.get().getIdPer(), numAcreditacion);
        return personaMapper.toDto(personaEntity.get());
      } else {
        LOG.debug("No se encontró persona con identificación - num={}, tipo={}", numAcreditacion, codTipoDocumento);
        return null;
      }
    } catch (Exception e) {
      LOG.error("Error buscando persona por identificación - num={}, tipo={}: {}", numAcreditacion, codTipoDocumento,
          e.getMessage(), e);
      throw new SinacException(e, SinacExceptionMessageType.SINAC_PERSONAS_1).type(SinacExceptionType.DATA);
    }
  }

}
