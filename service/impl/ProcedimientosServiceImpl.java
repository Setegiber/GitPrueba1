
package es.mjusticia.sinac.core.business.service.impl;

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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.service.ProcedimientosService;
import es.mjusticia.sinac.core.model.dto.EstadoDto;
import es.mjusticia.sinac.core.model.dto.ExpedienteDocumentoDto;
import es.mjusticia.sinac.core.model.dto.FaseDto;
import es.mjusticia.sinac.core.model.dto.FormularioOposicionDatosDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientoDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesAccionesDto;
import es.mjusticia.sinac.core.model.dto.ProcedimientosFasesTramitesOperacionesDto;
import es.mjusticia.sinac.core.model.dto.TramiteDto;
import es.mjusticia.sinac.core.model.entity.EstadoEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientoEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientosFasesEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientosFasesTramitesOperacionesAccionesEntity;
import es.mjusticia.sinac.core.model.entity.ProcedimientosFasesTramitesOperacionesEntity;
import es.mjusticia.sinac.core.model.entity.TramiteEntity;
import es.mjusticia.sinac.core.model.mapper.EstadoMapper;
import es.mjusticia.sinac.core.model.mapper.FaseMapper;
import es.mjusticia.sinac.core.model.mapper.FormularioOposicionDatosMapper;
import es.mjusticia.sinac.core.model.mapper.ProcedimientoMapper;
import es.mjusticia.sinac.core.model.mapper.ProcedimientoMaquinaEstadosMapper;
import es.mjusticia.sinac.core.model.mapper.ProcedimientosFasesMapper;
import es.mjusticia.sinac.core.model.mapper.ProcedimientosFasesTramitesOperacionesAccionesMapper;
import es.mjusticia.sinac.core.model.mapper.ProcedimientosFasesTramitesOperacionesMapper;
import es.mjusticia.sinac.core.model.mapper.TramiteMapper;
import es.mjusticia.sinac.core.persistence.EstadoDao;
import es.mjusticia.sinac.core.persistence.FormularioOposicionDatosDao;
import es.mjusticia.sinac.core.persistence.ParametrizacionDao;
import es.mjusticia.sinac.core.persistence.ProcedimientoDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosFasesDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosFasesTramitesOperacionesAccionesDao;
import es.mjusticia.sinac.core.persistence.ProcedimientosFasesTramitesOperacionesDao;
import es.mjusticia.sinac.core.persistence.TramiteDao;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

/**
 * Implementacion de los servicios para la recuperacion de procedimientos o
 * relacionados con estos
 *
 * @author Nttdata
 */
@Component
public class ProcedimientosServiceImpl implements ProcedimientosService {

  private static final Logger LOG = LoggerFactory.getLogger(ProcedimientosServiceImpl.class);

  @Autowired
  private ParametrizacionDao parametrizacionDao;
  @Autowired
  private ProcedimientoDao procedimientoDao;
  @Autowired
  private ProcedimientosFasesTramitesOperacionesDao procedimientosFasesTramitesOperacionesDao;
  @Autowired
  private ProcedimientosFasesTramitesOperacionesAccionesDao procedimientosFasesTramitesOperacionesAccionesDao;
  @Autowired
  private ProcedimientosFasesDao procedimientosFasesDao;
  @Autowired
  private ProcedimientoMapper procedimientoMapper;
  @Autowired
  private ProcedimientosFasesMapper procedimientosFasesMapper;
  @Autowired
  private FaseMapper faseMapper;
  @Autowired
  private TramiteDao tramiteDao;
  @Autowired
  private TramiteMapper tramiteMapper;
  @Autowired
  private ProcedimientoMaquinaEstadosMapper procedimientoMaquinaEstadosMapper;
  @Autowired
  private ProcedimientosFasesTramitesOperacionesMapper procedimientosFasesTramitesOperacionesMapper;
  @Autowired
  private ProcedimientosFasesTramitesOperacionesAccionesMapper procedimientosFasesTramitesOperacionesAccionesMapper;
  @Autowired
  private EstadoDao estadoDao;
  @Autowired
  private EstadoMapper estadoMapper;
  @Autowired
  private FormularioOposicionDatosMapper formularioOposicionDatosMapper;
  @Autowired
  private FormularioOposicionDatosDao formularioOposicionDatosDao;

  @Override
  public List<ProcedimientoDto> getProcedimientos() {
    Iterable<ProcedimientoEntity> procedimientos = procedimientoDao.findAll();

    List<ProcedimientoEntity> listaProcedimientos = new ArrayList<>();
    procedimientos.forEach(listaProcedimientos::add);

    List<ProcedimientoDto> listaProcedimientosDto = listaProcedimientos.stream().map(p -> procedimientoMapper.toDto(p))
        .toList();
    if (LOG.isDebugEnabled()) {
      LOG.debug("get procedimientos: {}", String.format("%s", listaProcedimientosDto));
    }

    return listaProcedimientosDto;
  }

  @Override
  public List<EstadoDto> getEstados() {
    Iterable<EstadoEntity> estados = estadoDao.findAll();

    List<EstadoEntity> listaEstados = new ArrayList<>();
    estados.forEach(listaEstados::add);

    List<EstadoDto> listaEstadosDto = listaEstados.stream().map(e -> estadoMapper.toDto(e)).toList();

    if (LOG.isDebugEnabled()) {
      LOG.debug("get estados: {}", String.format("%s", listaEstadosDto));
    }
    return listaEstadosDto;
  }

  @Override
  public List<EstadoDto> getEstadoByProcedimientoId(Short idProcedimiento) {
    LOG.debug("get estados por procedimientos con id: {}", idProcedimiento);

    List<EstadoEntity> listaEstados = procedimientoDao.recuperarEstadosPorProcedimientoId(idProcedimiento);

    List<EstadoDto> listaEstadosDto = listaEstados.stream().map(e -> estadoMapper.toDto(e)).toList();

    if (LOG.isDebugEnabled()) {
      LOG.debug("get estados por procedimientos lista de estados: {}", String.format("%s", listaEstadosDto));
    }

    return listaEstadosDto;
  }

  @Override
  public ProcedimientoDto getProcedimientoDtoById(short id) {
    ProcedimientoEntity ldvMaestraEntity = procedimientoDao.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Procedimiento no encontrado"));

    return procedimientoMapper.toDto(ldvMaestraEntity);
  }

  @Override
  public String getCodSiaByIdProcedimiento(final short idProcedimiento) throws SinacException {
    LOG.debug("ProcedimientosServiceImpl.getCodSiaByIdProcedimiento - Init");

    String codSia = null;

    try {
      codSia = procedimientoDao.findById(idProcedimiento).orElseThrow().getCodSia();
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_1)
          .logMessageParams(idProcedimiento).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_2)
          .logMessageParams(idProcedimiento).type(SinacExceptionType.DATA);
    }

    LOG.debug("ProcedimientosServiceImpl.getCodSiaByIdProcedimiento - End");

    return codSia;
  }

  @Override
  public ProcedimientosFasesTramitesOperacionesAccionesDto getProcedimientosFasesTramitesOperacionesAccionesByIdProFasesTraOpeCodAccion(
      Long id, String codAccion) throws SinacException {
    ProcedimientosFasesTramitesOperacionesAccionesEntity procedimientosFasesTramitesOperacionesAccionesEntity = null;
    try {
      procedimientosFasesTramitesOperacionesAccionesEntity = procedimientosFasesTramitesOperacionesAccionesDao
          .getProcedimientosFasesTramitesOperacionesAccionesByIdProFasesTraOpeCodAccion(id, codAccion);
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_3).logMessageParams(id, codAccion).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_4).logMessageParams(id, codAccion);
    }
    return procedimientosFasesTramitesOperacionesAccionesMapper
        .toDto(procedimientosFasesTramitesOperacionesAccionesEntity);
  }

  @Override
  public ProcedimientosFasesTramitesOperacionesDto getProcedimientosFasesTramitesOperacionesById(Long id)
      throws SinacException {
    ProcedimientosFasesTramitesOperacionesEntity procedimientosFasesTramitesOperacionesEntity = null;
    try {
      procedimientosFasesTramitesOperacionesEntity = procedimientosFasesTramitesOperacionesDao
          .getProcedimientosFasesTramitesOperacionesById(id);
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_5)
          .logMessageParams(id).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_6).logMessageParams(id);
    }
    return procedimientosFasesTramitesOperacionesMapper.toDto(procedimientosFasesTramitesOperacionesEntity);
  }

  @Override
  public ProcedimientoDto getProcedimientoCompleto(short idPro) throws SinacException {
    ProcedimientoEntity procedimientoEntity = procedimientoDao.recuperarProcedimientoCompleto(idPro);
    procedimientoEntity.getProcedimientosFasesEntities().removeIf(p -> !p.isFlgActivo());
    for (ProcedimientosFasesEntity proFase : procedimientoEntity.getProcedimientosFasesEntities()) {
      proFase.getProFasesTras().removeIf(pf -> !pf.isFlgActivo());
    }
    return procedimientoMaquinaEstadosMapper.toDto(procedimientoEntity);
  }

  @Override
  public List<ExpedienteDocumentoDto> getDocumentosRequeridos(BigInteger idExp) throws SinacException {
//    List<ExpedienteDocumentoEntity> listaDocumentosRequeridosEntity = expedienteRequerimientoDao
//        .recuperarDocumentosRequeridosByIdExp(idExp);

    return null;
  }

  @Override
  public ProcedimientosFasesTramitesOperacionesDto getProcedimientosFasesTramitesOperacionesByIdProFaseTraOpe(
      final long idProFaseTraOpe) throws SinacException {
    LOG.debug("ProcedimientosServiceImpl.getProcedimientosFasesTramitesOperacionesByIdProFaseTraOpe - Init");

    ProcedimientosFasesTramitesOperacionesDto procedimientosFasesTramitesOperacionesDto = null;

    try {
      procedimientosFasesTramitesOperacionesDto = procedimientosFasesTramitesOperacionesMapper
          .toDto(procedimientosFasesTramitesOperacionesDao.findById(idProFaseTraOpe).orElseThrow());
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_7)
          .logMessageParams(idProFaseTraOpe).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_8)
          .logMessageParams(idProFaseTraOpe).type(SinacExceptionType.DATA);
    }

    LOG.debug("ProcedimientosServiceImpl.getProcedimientosFasesTramitesOperacionesByIdProFaseTraOpe - End");

    return procedimientosFasesTramitesOperacionesDto;
  }

  @Override
  public ProcedimientosFasesTramitesOperacionesAccionesDto getProcedimientosFasesTramitesOperacionesAccionesByIdProFaseTraOpeAndIdAccion(
      final long idProFaseTraOpe, final String codAccion) throws SinacException {
    LOG.debug(
        "ProcedimientosServiceImpl.getProcedimientosFasesTramitesOperacionesAccionesByIdProFaseTraOpeAndIdAccion - Init");

    ProcedimientosFasesTramitesOperacionesAccionesDto procedimientosFasesTramitesOperacionesAccionesDto = null;

    try {
      procedimientosFasesTramitesOperacionesAccionesDto = procedimientosFasesTramitesOperacionesAccionesMapper
          .toDto(procedimientosFasesTramitesOperacionesAccionesDao
              .getProcedimientosFasesTramitesOperacionesAccionesByIdProFaseTraOpeAndIdAccion(idProFaseTraOpe, codAccion)
              .orElseThrow());
    } catch (final NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_9)
          .logMessageParams(idProFaseTraOpe, codAccion).type(SinacExceptionType.DATA);
    } catch (final Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_10)
          .logMessageParams(idProFaseTraOpe, codAccion).type(SinacExceptionType.DATA);
    }

    LOG.debug(
        "ProcedimientosServiceImpl.getProcedimientosFasesTramitesOperacionesAccionesByIdProFaseTraOpeAndIdAccion - End");

    return procedimientosFasesTramitesOperacionesAccionesDto;
  }

  @Override
  public Long getProcedimientosFasesTramitesOperacionesAccionesDtoByCodigos(String codPro, String codFase,
      String codTra, String codOpe, String codAcc) throws SinacException {

    return procedimientosFasesTramitesOperacionesAccionesDao
        .getIdProcedimientosFasesTramitesOperacionesAccionesByCod(codPro, codFase, codTra, codOpe, codAcc);
  }

  @Override
  public Long getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(String codPro, String codTra,
      String codOpe, String codAcc) throws SinacException {
    return procedimientosFasesTramitesOperacionesAccionesDao
        .getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(codPro, codTra, codOpe, codAcc);
  }

  @Override
  public Long getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(long idProFasTraOpe, String codAcc)
      throws SinacException {
    return procedimientosFasesTramitesOperacionesAccionesDao
        .getIdProcedimientosFasesTramitesOperacionesAccionesByCodProTraOpeAcc(idProFasTraOpe, codAcc);
  }

  @Override
  @Transactional
  public List<ProcedimientosFasesDto> getListaProcedimientoFases() {
    List<ProcedimientosFasesEntity> lista = Lists.newArrayList(procedimientosFasesDao.findAll());
    List<ProcedimientosFasesDto> resultado = new ArrayList<>();
    lista.forEach(entity -> {
      ProcedimientosFasesDto procedimientosFasesDto = procedimientosFasesMapper.toDto(entity);
      procedimientosFasesDto.setProcedimientoDto(procedimientoMapper.toDto(entity.getProcedimientoEntity()));
      FaseDto faseDto = faseMapper.toDto(entity.getFaseEntity());
      procedimientosFasesDto.setFaseDto(faseDto);
      resultado.add(procedimientosFasesDto);
    });
    return resultado;
  }

  @Override
  public List<TramiteDto> getListaTramiteByIdProIdFase(Short procId, Short faseId) {
    List<TramiteEntity> lista = tramiteDao.getListaTramiteByIdProIdFase(procId, faseId);
    List<TramiteDto> resultado = new ArrayList<>();
    lista.forEach(tramite -> resultado.add(tramiteMapper.toDto(tramite)));
    return resultado;
  }

  @Override
  public List<EstadoDto> getEstadosByidProcedimientoidTramiteIdFase(Short procId, Short faseId, Short tramiteId) {
    List<EstadoDto> resultado = new ArrayList<>();
    List<EstadoEntity> listaEstadoEntities = estadoDao.getEstadosByidProcedimientoidTramiteIdFase(procId, tramiteId,
        faseId);
    listaEstadoEntities.forEach(estado -> resultado.add(estadoMapper.toDto(estado)));
    return resultado;
  }

  @Override
  public ProcedimientoDto getProcedimientoByCodPro(String codPro) {

    return procedimientoMapper.toDto(procedimientoDao.recuperarProcedimientoByCodPro(codPro));
  }

  @Override
  public Long getIdProFaseTraOpeAccByCodProcedimientoAndCodTramiteAndCodOperacionAndCodAccion(String codProcedimiento,
      String codTramite, String codOperacion, String codAccion) throws SinacException {
    LOG.debug(
        "ProcedimientosServiceImpl.getIdProFaseTraOpeAccByCodProcedimientoAndCodTramiteAndCodOperacionAndCodAccion - Init");

    Long idProFaseTraOpeAcc;

    try {
      idProFaseTraOpeAcc = procedimientosFasesTramitesOperacionesAccionesDao
          .getIdProFaseTraOpeAccByCodProcedimientoAndCodTramiteAndCodOperacionAndCodAccion(codProcedimiento, codTramite,
              codOperacion, codAccion)
          .orElseThrow();
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_11)
          .logMessageParams(codProcedimiento, codTramite, codOperacion, codAccion).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_12)
          .logMessageParams(codProcedimiento, codTramite, codOperacion, codAccion).type(SinacExceptionType.DATA);
    }

    LOG.debug(
        "ProcedimientosServiceImpl.getIdProFaseTraOpeAccByCodProcedimientoAndCodTramiteAndCodOperacionAndCodAccion - End");

    return idProFaseTraOpeAcc;
  }

  @Override
  public ProcedimientoDto getProcedimientoByIdExp(BigInteger idExp) throws SinacException {
    try {
      return procedimientoMapper.toDto(procedimientoDao.getProcedimientoByIdExp(idExp));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PROCEDIMIENTOS_13).logMessageParams(idExp).type(SinacExceptionType.DATA);
    }
  }

  @Override
  public Boolean validarProcedimientoConsulta(String codCorto) throws SinacException {
    return parametrizacionDao.existsByCodCortoAndValParamConsulta(codCorto);
  }

  @Override
  public FormularioOposicionDatosDto getFormularioOposicionDatosByCodCampo(String codCampo) throws SinacException {
      return formularioOposicionDatosMapper.toDto(formularioOposicionDatosDao.findByCodCampo(codCampo));
  }

//  @Override
//  public Map<String, List<ProcedimientoDto>> getProcedimientosValidadosGestAvi() throws SinacException {
//    try {
//      // Obtiene la lista de ProcedimientoEntities
//      List<ProcedimientoEntity> listaProcedimientoEntities = procedimientoDao.getProcedimientosValidosGestAvisos();
//      // Convertir cada ProcedimientoEntity a ProcedimientoDto y agrupar por
//      // ldvMaestra.codLdvMae
//      Map<String, List<ProcedimientoDto>> procedimientosAgrupados = listaProcedimientoEntities.stream()
//          // Primero convertimos cada entity en un DTO
//          .map(procedimientoMapper::toDto) // Uso correcto de procedimientoMapper
//          // Luego agrupamos por el código ldvMaestra.codLdvMae
//          .collect(Collectors.groupingBy(procedimientoDto -> procedimientoDto.getLdvMaestraDto().getCodLdvMae()));
//      // Convertimos a un LinkedHashMap para ordenar por idLdvMaestra
//      Map<String, List<ProcedimientoDto>> procedimientosOrdenados = procedimientosAgrupados.entrySet().stream()
//          // Ordenar las entradas por idLdvMaestra
//          .sorted(Comparator.comparing(entry -> ((Map.Entry<String, List<ProcedimientoDto>>) entry) // Manejo adecuado
//                                                                                                    // de
//                                                                                                    // Map.Entry
//              .getValue().get(0).getLdvMaestraDto().getIdLdvMae()))
//          // Convertir de nuevo a un Map (LinkedHashMap para mantener el orden)
//          .collect(Collectors.toMap(Map.Entry::getKey, // La clave es el codLdvMae
//              Map.Entry::getValue, // El valor es la lista de ProcedimientoDto
//              (oldValue, newValue) -> oldValue, // En caso de colisión, mantener el valor antiguo (no aplicaría en este
//                                                // caso)
//              LinkedHashMap::new // Usar LinkedHashMap para mantener el orden
//          ));
//      return procedimientosOrdenados;
//    } catch (Exception e) {
//      // Lanzar una excepción personalizada en caso de error
//      throw new SinacException(e, SinacExceptionMessageType.MESSAGE_62)
//          .type(SinacExceptionType.DATA);
//    }
//  }

}
