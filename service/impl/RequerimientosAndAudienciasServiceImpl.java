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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.RequerimientosAndAudienciasService;
import es.mjusticia.sinac.core.model.dto.DocumentoTipoDto;
import es.mjusticia.sinac.core.model.dto.DocumentoToRequerirDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteRequerimientoDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.RequerimientosDocumentosDto;
import es.mjusticia.sinac.core.model.dto.TipoOficioDto;
import es.mjusticia.sinac.core.model.entity.ExpedienteRequerimientoEntity;
import es.mjusticia.sinac.core.model.mapper.DocumentoTipoMapper;
import es.mjusticia.sinac.core.model.mapper.DocumentoToRequerirMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedienteRequerimientoMapper;
import es.mjusticia.sinac.core.model.mapper.LdvMaestraMapper;
import es.mjusticia.sinac.core.model.mapper.RequerimientosDocumentosMapper;
import es.mjusticia.sinac.core.model.mapper.TipoOficioMapper;
import es.mjusticia.sinac.core.persistence.DocumentoTipoDao;
import es.mjusticia.sinac.core.persistence.ExpedienteRequerimientoDao;
import es.mjusticia.sinac.core.persistence.RequerimientosDocumentosDao;
import es.mjusticia.sinac.core.utils.Constantes.GenerarRequerimientoOrAudiencia;

/**
 * Clase de Implementación de {@link RequerimientosAndAudienciasService}.
 *
 * @author NTT Data.
 */
@Component
public class RequerimientosAndAudienciasServiceImpl implements RequerimientosAndAudienciasService {

  private static final Logger LOG = LoggerFactory.getLogger(RequerimientosAndAudienciasServiceImpl.class);

  @Autowired
  private ExpedienteRequerimientoDao expedienteRequerimientoDao;

  @Autowired
  private RequerimientosDocumentosDao requerimientosDocumentosDao;

  @Autowired
  private DocumentoTipoDao documentoTipoDao;

  @Autowired
  private CatalogosService catalogosService;

  @Autowired
  private DocumentoToRequerirMapper documentoToRequerirMapper;

  @Autowired
  private TipoOficioMapper tipoOficioMapper;

  @Autowired
  private ExpedienteRequerimientoMapper expedienteRequerimientoMapper;

  @Autowired
  private RequerimientosDocumentosMapper requerimientosDocumentosMapper;

  @Autowired
  private DocumentoTipoMapper documentoTipoMapper;

  @Autowired
  private LdvMaestraMapper ldvMaestraMapper;

  @Override
  public List<TipoOficioDto> getTiposOficiosAndDocumentosToRequerirByIdProcedimiento(short idProcedimiento)
      throws SinacException {
    LOG.debug("RequerimientosAndAudienciasServiceImpl.getTiposOficiosAndDocumentosToRequerirByIdProcedimiento - Init");

    List<TipoOficioDto> tipoOficioDtoList;

    try {
      tipoOficioDtoList = tipoOficioMapper.toDtoList(
          expedienteRequerimientoDao.getTiposOficiosAndDocumentosToRequerirByIdProcedimiento(idProcedimiento));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_1)
          .logMessageParams(idProcedimiento).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_2)
          .logMessageParams(idProcedimiento).type(SinacExceptionType.DATA);
    }

    LOG.debug("RequerimientosAndAudienciasServiceImpl.getTiposOficiosAndDocumentosToRequerirByIdProcedimiento - End");

    return tipoOficioDtoList;
  }

  @Override
  public List<DocumentoToRequerirDto> getDocumentosToRequerirByIdProcedimiento(short idProcedimiento)
      throws SinacException {
    LOG.debug("RequerimientosAndAudienciasServiceImpl.getDocumentosToRequerirByIdProcedimiento - Init");

    List<DocumentoToRequerirDto> documentoToRequerirDtoList = null;

    try {
      documentoToRequerirDtoList = documentoToRequerirMapper
          .toDtoList(expedienteRequerimientoDao.getDocumentosToRequerirByIdProcedimiento(idProcedimiento));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_3)
          .logMessageParams(idProcedimiento).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_4)
          .logMessageParams(idProcedimiento).type(SinacExceptionType.DATA);
    }

    LOG.debug("RequerimientosAndAudienciasServiceImpl.getDocumentosToRequerirByIdProcedimiento - End");

    return documentoToRequerirDtoList;
  }

  @Override
  public List<ExpedienteRequerimientoDto> getRequerimientosByIdExpediente(BigInteger idExpediente)
      throws SinacException {
    LOG.debug("RequerimientosAndAudienciasServiceImpl.getRequerimientosByIdExpediente - Init");

    List<ExpedienteRequerimientoDto> expedienteRequerimientoDtoList = null;

    try {
      expedienteRequerimientoDtoList = expedienteRequerimientoMapper
          .toDtoList(expedienteRequerimientoDao.getRequerimientosByIdExpediente(idExpediente));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_5)
          .logMessageParams(idExpediente).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_6)
          .logMessageParams(idExpediente).type(SinacExceptionType.DATA);
    }

    LOG.debug("RequerimientosAndAudienciasServiceImpl.getRequerimientosByIdExpediente - End");

    return expedienteRequerimientoDtoList;
  }

  @Override
  public List<DocumentoTipoDto> validateAndGetDocumentosRequeridos(final Map<String, Object> values)
      throws SinacException {
    LOG.debug("RequerimientosAndAudienciasServiceImpl.validateDocumentosRequeridos - Init");

    List<DocumentoTipoDto> documentoTipoDtoList = null;

    if (values.containsKey(GenerarRequerimientoOrAudiencia.DOCUMENTOS_A_REQUERIR)
        && StringUtils.isNotEmpty(values.get(GenerarRequerimientoOrAudiencia.DOCUMENTOS_A_REQUERIR).toString())) {
      final List<Short> idsDocumentosToRequerir = Arrays
          .stream(values.get(GenerarRequerimientoOrAudiencia.DOCUMENTOS_A_REQUERIR).toString().split(","))
          .map(Short::parseShort).toList();

      try {
        documentoTipoDtoList = documentoTipoMapper
            .toDto(documentoTipoDao.getTiposDocumentosByIdsTiposDocumentos(idsDocumentosToRequerir));
      } catch (final NoSuchElementException noSuchElementException) {
        throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_7)
            .logMessageParams(idsDocumentosToRequerir).type(SinacExceptionType.DATA);
      } catch (final Exception exception) {
        throw new SinacException(exception, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_8)
            .logMessageParams(idsDocumentosToRequerir).type(SinacExceptionType.DATA);
      }
    }

    LOG.debug("RequerimientosAndAudienciasServiceImpl.validateDocumentosRequeridos - End");

    return documentoTipoDtoList;
  }

  @Override
  public ExpedienteRequerimientoDto generateRequerimientoWithDocumentosRequeridos(
      final ExpedienteDocumentoDto expedienteDocumentoDto, final List<DocumentoTipoDto> documentoTipoDtoList,
      final boolean isAudiencia) throws SinacException {
    LOG.debug("RequerimientosAndAudienciasServiceImpl.generateRequerimientoWithDocumentosRequeridos - Init");

    ExpedienteRequerimientoDto expedienteRequerimientoDto = new ExpedienteRequerimientoDto();
    expedienteRequerimientoDto.setExpedienteDocumentoDto(expedienteDocumentoDto);
    expedienteRequerimientoDto.setLdvMaestraDto(catalogosService.getCatalogoByCod("REQ-BOR"));
    expedienteRequerimientoDto.setAudiencia(isAudiencia);
    expedienteRequerimientoDto.setFlgActivo(true);

    try {
      expedienteRequerimientoDto = expedienteRequerimientoMapper
          .toDto(expedienteRequerimientoDao.save(expedienteRequerimientoMapper.toEntity(expedienteRequerimientoDto)));

      if (!CollectionUtils.isEmpty(documentoTipoDtoList)) {
        for (final DocumentoTipoDto documentoTipoDto : documentoTipoDtoList) {
          final RequerimientosDocumentosDto requerimientosDocumentosDto = new RequerimientosDocumentosDto();
          requerimientosDocumentosDto.setIdExpReq(expedienteRequerimientoDto);
          requerimientosDocumentosDto.setDocumentosTipo(documentoTipoDto);
          requerimientosDocumentosDto.setFlgActivo(true);

          requerimientosDocumentosDao.save(requerimientosDocumentosMapper.toEntity(requerimientosDocumentosDto));
        }
      }
    } catch (final SinacException sinacException) {
      throw new SinacException(sinacException,
          SinacExceptionMessageType.MESSAGE_63)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("RequerimientosAndAudienciasServiceImpl.generateRequerimientoWithDocumentosRequeridos - End");

    return expedienteRequerimientoDto;
  }

  @Override
  public ExpedienteRequerimientoDto getRequerimientoByIdDocumento(final BigInteger idDocumento) throws SinacException {
    LOG.debug("RequerimientosAndAudienciasServiceImpl.getRequerimientoByIdDocumento - Init");

    ExpedienteRequerimientoDto expedienteRequerimientoDto = null;

    try {
      Optional<ExpedienteRequerimientoEntity> expedienteRequerimientoEntity = expedienteRequerimientoDao
          .getRequerimientoByIdDocumento(idDocumento);
      if (expedienteRequerimientoEntity.isPresent()) {
        expedienteRequerimientoDto = expedienteRequerimientoMapper.toDto(expedienteRequerimientoEntity.get());
      }
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_10)
          .logMessageParams(idDocumento).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_11)
          .logMessageParams(idDocumento).type(SinacExceptionType.DATA);
    }

    LOG.debug("RequerimientosAndAudienciasServiceImpl.getRequerimientoByIdDocumento - End");

    return expedienteRequerimientoDto;
  }

  @Override
  public void updateEstadoRequerimiento(final BigInteger idRequerimiento, final LdvMaestraDto ldvMaestraDto)
      throws SinacException {
    LOG.debug("RequerimientosAndAudienciasServiceImpl.updateEstadoRequerimiento - Init");

    try {
      expedienteRequerimientoDao.updateEstadoRequerimiento(idRequerimiento, ldvMaestraMapper.toEntity(ldvMaestraDto));
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_12)
          .logMessageParams(idRequerimiento).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_13)
          .logMessageParams(idRequerimiento).type(SinacExceptionType.DATA);
    }

    LOG.debug("RequerimientosAndAudienciasServiceImpl.updateEstadoRequerimiento - End");
  }

  @Override
  public void updateEstadoAndFechaFinalizacionRequerimiento(BigInteger idRequerimiento, LdvMaestraDto ldvMaestraDto,
      Date fechaFinalizacion) throws SinacException {
    LOG.debug("RequerimientosAndAudienciasServiceImpl.updateEstadoAndFechaFinalizacionRequerimiento - Init");

    try {
      expedienteRequerimientoDao.updateEstadoAndFechaFinalizacionRequerimiento(idRequerimiento,
          ldvMaestraMapper.toEntity(ldvMaestraDto), fechaFinalizacion);
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_14)
          .logMessageParams(idRequerimiento).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_15)
          .logMessageParams(idRequerimiento).type(SinacExceptionType.DATA);
    }

    LOG.debug("RequerimientosAndAudienciasServiceImpl.updateEstadoAndFechaFinalizacionRequerimiento - End");
  }

  @Override
  public List<ExpedienteRequerimientoDto> getRequerimientosByIdExpedienteAndEstado(BigInteger idExpediente,
      List<String> estado) throws SinacException {
    LOG.debug("RequerimientosAndAudienciasServiceImpl.getRequerimientosByIdExpedienteAndEstado - Init");

    List<ExpedienteRequerimientoDto> expedienteRequerimientoDtoList = null;

    try {
      expedienteRequerimientoDtoList = expedienteRequerimientoMapper
          .toDtoList(expedienteRequerimientoDao.getRequerimientosByIdExpedienteAndEstado(idExpediente, estado));
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_16)
          .logMessageParams(idExpediente, estado).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_17)
          .logMessageParams(idExpediente, estado).type(SinacExceptionType.DATA);
    }

    LOG.debug("RequerimientosAndAudienciasServiceImpl.getRequerimientosByIdExpedienteAndEstado - End");

    return expedienteRequerimientoDtoList;
  }

  @Override
  public short getIdPlantillaByIdExpedienteAndIdExpReq(BigInteger idExpediente, BigInteger idExpReq)
      throws SinacException {
    LOG.debug("RequerimientosAndAudienciasServiceImpl.getIdPlantillaByIdExpedienteAndIdExpReq - Init");

    short idPlantilla;

    try {
      idPlantilla = ((Number) expedienteRequerimientoDao.getIdPlantillaByIdExpedienteAndIdExpReq(idExpediente, idExpReq)
          .get(0)[0]).shortValue();
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_18)
          .logMessageParams(idExpediente, idExpReq).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_19)
          .logMessageParams(idExpediente, idExpReq).type(SinacExceptionType.DATA);
    }

    LOG.debug("RequerimientosAndAudienciasServiceImpl.getIdPlantillaByIdExpedienteAndIdExpReq - End");

    return idPlantilla;
  }

  @Override
  public ExpedienteRequerimientoDto getRequerimientoByIdRequerimiento(BigInteger idRequerimiento)
      throws SinacException {
    LOG.debug("RequerimientosAndAudienciasServiceImpl.getRequerimientoByIdRequerimiento - Init");

    ExpedienteRequerimientoDto expedienteRequerimientoDto = null;

    try {
      Optional<ExpedienteRequerimientoEntity> expedienteRequerimientoEntity = expedienteRequerimientoDao
          .getRequerimientoByIdRequerimiento(idRequerimiento);
      if (expedienteRequerimientoEntity.isPresent()) {
        expedienteRequerimientoDto = expedienteRequerimientoMapper.toDto(expedienteRequerimientoEntity.get());
      }
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_20)
          .logMessageParams(idRequerimiento).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_REQ_AUDIENCIAS_21)
          .logMessageParams(idRequerimiento).type(SinacExceptionType.DATA);
    }

    LOG.debug("RequerimientosAndAudienciasServiceImpl.getRequerimientoByIdRequerimiento - End");

    return expedienteRequerimientoDto;
  }

}
