package es.mjusticia.sinac.core.business.service.impl;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.threeten.extra.Days;

import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameter;
import de.jollyday.parameter.UrlManagerParameter;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.exception.SinacExceptionType;
import es.mjusticia.sinac.core.business.service.CatalogosService;
import es.mjusticia.sinac.core.business.service.PlazosService;
import es.mjusticia.sinac.core.model.dto.ExpedientesPlazosDto;
import es.mjusticia.sinac.core.model.dto.LdvMaestraDto;
import es.mjusticia.sinac.core.model.dto.PlazoDto;
import es.mjusticia.sinac.core.model.dto.PlazosProcedimientosFasesTramitesOperacionesAccionesDto;
import es.mjusticia.sinac.core.model.mapper.ExpedienteInformeMapper;
import es.mjusticia.sinac.core.model.mapper.ExpedientesPlazosMapper;
import es.mjusticia.sinac.core.model.mapper.LdvMaestraMapper;
import es.mjusticia.sinac.core.model.mapper.PlazoMapper;
import es.mjusticia.sinac.core.model.mapper.PlazosProcedimientosFasesTramitesOperacionesAccionesMapper;
import es.mjusticia.sinac.core.persistence.ExpedienteInformeDao;
import es.mjusticia.sinac.core.persistence.ExpedientesPlazosDao;
import es.mjusticia.sinac.core.persistence.PlazoDao;
import es.mjusticia.sinac.core.persistence.PlazosProcedimientosFasesTramitesOperacionesAccionesDao;
import es.mjusticia.sinac.core.utils.Constantes.Plazo;
import es.mjusticia.sinac.core.utils.Constantes.TiposInforme;
import jakarta.annotation.PostConstruct;

/**
 * Clase de Implementación de {@link PlazosService}.
 *
 * @author NTT Data.
 */
@Component
public class PlazosServiceImpl implements PlazosService {

  private static final Logger LOG = LoggerFactory.getLogger(PlazosServiceImpl.class);

  @Value("${sinac.configpath}")
  private String nfsEnvironmentPath;

  @Value("${nfs.ruta}")
  private String nfsPath;

  @Value("${nfs.ruta.holidays}")
  private String nfsHolidaysPath;

  private HolidayManager holidayManager;

  @Autowired
  private PlazoDao plazoDao;

  @Autowired
  private PlazosProcedimientosFasesTramitesOperacionesAccionesDao plazosProcedimientosFasesTramitesOperacionesAccionesDao;

  @Autowired
  private ExpedientesPlazosDao expedientesPlazosDao;

  @Autowired
  private ExpedienteInformeDao expedienteInformeDao;

  @Autowired
  private CatalogosService catalogosService;

  @Autowired
  private PlazoMapper plazoMapper;

  @Autowired
  private PlazosProcedimientosFasesTramitesOperacionesAccionesMapper plazosProcedimientosFasesTramitesOperacionesAccionesMapper;

  @Autowired
  private ExpedientesPlazosMapper expedientesPlazosMapper;

  @Autowired
  private LdvMaestraMapper ldvMaestraMapper;

  @Autowired
  private ExpedienteInformeMapper expedienteInformeMapper;

  @PostConstruct
  public void getHolidayManager() {
    try {
      URL url = new URL("file:" + nfsEnvironmentPath + nfsPath + nfsHolidaysPath);
      ManagerParameter managerParameter = new UrlManagerParameter(url, new Properties());
      holidayManager = HolidayManager.getInstance(managerParameter);
    } catch (MalformedURLException malformedURLException) {
      throw new SinacException(malformedURLException, SinacExceptionMessageType.SINAC_PLAZOS_1)
          .logMessageParams(malformedURLException.getMessage()).type(SinacExceptionType.BUSINESS);
    }
  }

  @Override
  public List<PlazosProcedimientosFasesTramitesOperacionesAccionesDto> getConfiguracionAccionPlazoByIdProFaseTraOpeAcc(
      long idProFaseTraOpeAcc) throws SinacException {
    LOG.debug("PlazosServiceImpl.getConfiguracionAccionPlazoByIdProFaseTraOpeAcc - Init");

    List<PlazosProcedimientosFasesTramitesOperacionesAccionesDto> plazosProcedimientosFasesTramitesOperacionesAccionesDtoList = null;

    try {
      plazosProcedimientosFasesTramitesOperacionesAccionesDtoList = plazosProcedimientosFasesTramitesOperacionesAccionesMapper
          .toDtoList(plazosProcedimientosFasesTramitesOperacionesAccionesDao
              .getConfiguracionAccionPlazoByIdProFaseTraOpeAcc(idProFaseTraOpeAcc));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_2)
          .logMessageParams(idProFaseTraOpeAcc).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_3).logMessageParams(idProFaseTraOpeAcc)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getConfiguracionAccionPlazoByIdProFaseTraOpeAcc - End");

    return plazosProcedimientosFasesTramitesOperacionesAccionesDtoList;
  }

  @Override
  public List<PlazosProcedimientosFasesTramitesOperacionesAccionesDto> getConfiguracionAccionPlazoByIdProFaseTraOpeAccAndEstado(
      long idProFaseTraOpeAcc, LdvMaestraDto estado) throws SinacException {
    LOG.debug("PlazosServiceImpl.getConfiguracionAccionPlazoByIdProFaseTraOpeAccAndEstado - Init");

    List<PlazosProcedimientosFasesTramitesOperacionesAccionesDto> plazosProcedimientosFasesTramitesOperacionesAccionesDtoList = null;

    try {
      plazosProcedimientosFasesTramitesOperacionesAccionesDtoList = plazosProcedimientosFasesTramitesOperacionesAccionesMapper
          .toDtoList(plazosProcedimientosFasesTramitesOperacionesAccionesDao
              .getConfiguracionAccionPlazoByIdProFaseTraOpeAccAndEstado(idProFaseTraOpeAcc,
                  ldvMaestraMapper.toEntity(estado).getIdLdvMae()));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_4)
          .logMessageParams(idProFaseTraOpeAcc, estado).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_5)
          .logMessageParams(idProFaseTraOpeAcc, estado).type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getConfiguracionAccionPlazoByIdProFaseTraOpeAccAndEstado - End");

    return plazosProcedimientosFasesTramitesOperacionesAccionesDtoList;
  }

  @Override
  public ExpedientesPlazosDto getPlazoVigenteByIdExpedienteAndIdPlazo(BigInteger idExpediente, short idPlazo)
      throws SinacException {
    LOG.debug("PlazosServiceImpl.getPlazoVigenteByIdExpedienteAndIdPlazo - Init");

    ExpedientesPlazosDto expedientesPlazosDto;

    try {
      expedientesPlazosDto = expedientesPlazosMapper
          .toDto(expedientesPlazosDao.getPlazoVigenteByIdExpedienteAndIdPlazo(idExpediente, idPlazo).orElse(null));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_6)
          .logMessageParams(idExpediente, idPlazo).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_7)
          .logMessageParams(idExpediente, idPlazo).type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getPlazoVigenteByIdExpedienteAndIdPlazo - End");

    return expedientesPlazosDto;
  }

  @Override
  public ExpedientesPlazosDto getPlazoVigenteByIdExpedienteAndIdPlazoAndEstado(BigInteger idExpediente, short idPlazo,
      String estado) throws SinacException {
    LOG.debug("PlazosServiceImpl.getPlazoVigenteByIdExpedienteAndIdPlazoAndEstado - Init");

    ExpedientesPlazosDto expedientesPlazosDto;

    try {
      expedientesPlazosDto = expedientesPlazosMapper.toDto(expedientesPlazosDao
          .getPlazoVigenteByIdExpedienteAndIdPlazoAndEstado(idExpediente, idPlazo, estado).orElse(null));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_8)
          .logMessageParams(idExpediente, idPlazo, estado).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_9)
          .logMessageParams(idExpediente, idPlazo, estado).type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getPlazoVigenteByIdExpedienteAndIdPlazoAndEstado - End");

    return expedientesPlazosDto;
  }

  @Override
  public ExpedientesPlazosDto getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimiento(BigInteger idExpediente,
      short idPlazo, BigInteger idRequerimiento) throws SinacException {
    LOG.debug("PlazosServiceImpl.getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimiento - Init");

    ExpedientesPlazosDto expedientesPlazosDto;

    try {
      if (idRequerimiento != null) {
        expedientesPlazosDto = expedientesPlazosMapper.toDto(expedientesPlazosDao
            .getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimiento(idExpediente, idPlazo, idRequerimiento)
            .orElse(null));
      } else {
        expedientesPlazosDto = getPlazoVigenteByIdExpedienteAndIdPlazo(idExpediente, idPlazo);
      }
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_10)
          .logMessageParams(idExpediente, idPlazo, idRequerimiento).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_11)
          .logMessageParams(idExpediente, idPlazo, idRequerimiento).type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimiento - End");

    return expedientesPlazosDto;
  }

  @Override
  public ExpedientesPlazosDto getPlazoVigenteByIdExpedienteAndIdPlazoAndCodTipoPlazoAndIdRequerimiento(
      BigInteger idExpediente, short idPlazo, String codTipoPlazo, BigInteger idRequerimiento) throws SinacException {
    LOG.debug("PlazosServiceImpl.getPlazoVigenteByIdExpedienteAndIdPlazoAndCodTipoPlazoAndIdRequerimiento - Init");

    ExpedientesPlazosDto expedientesPlazosDto = null;

    try {
      if (idRequerimiento != null && "TPLA-SUB".equals(codTipoPlazo)) {
        expedientesPlazosDto = getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimiento(idExpediente, idPlazo,
            idRequerimiento);
      } else {
        expedientesPlazosDto = getPlazoVigenteByIdExpedienteAndIdPlazo(idExpediente, idPlazo);
      }
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_12)
          .logMessageParams(idExpediente, idPlazo, idRequerimiento).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_13)
          .logMessageParams(idExpediente, idPlazo, idRequerimiento).type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getPlazoVigenteByIdExpedienteAndIdPlazoAndCodTipoPlazoAndIdRequerimiento - End");

    return expedientesPlazosDto;
  }

  @Override
  public ExpedientesPlazosDto getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimientoAndEstado(
      BigInteger idExpediente, short idPlazo, BigInteger idRequerimiento, String estado) throws SinacException {
    LOG.debug("PlazosServiceImpl.getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimientoAndEstado - Init");

    ExpedientesPlazosDto expedientesPlazosDto;

    try {
      if (idRequerimiento != null) {
        expedientesPlazosDto = expedientesPlazosMapper
            .toDto(expedientesPlazosDao.getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimientoAndEstado(idExpediente,
                idPlazo, idRequerimiento, estado).orElse(null));
      } else {
        expedientesPlazosDto = getPlazoVigenteByIdExpedienteAndIdPlazoAndEstado(idExpediente, idPlazo, estado);
      }
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_14)
          .logMessageParams(idExpediente, idPlazo, idRequerimiento, estado).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_15)
          .logMessageParams(idExpediente, idPlazo, idRequerimiento, estado).type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getPlazoVigenteByIdExpedienteAndIdPlazoAndIdRequerimientoAndEstado - End");

    return expedientesPlazosDto;
  }

  @Override
  public List<ExpedientesPlazosDto> getPlazosExpedienteByIdExpediente(BigInteger idExpediente) throws SinacException {
    LOG.debug("PlazosServiceImpl.getPlazosExpedienteByIdExpediente - Init");

    List<ExpedientesPlazosDto> expedientesPlazosDtoList = null;

    try {
      expedientesPlazosDtoList = expedientesPlazosMapper
          .toDtoList(expedientesPlazosDao.getPlazosExpedienteByIdExpediente(idExpediente));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_16)
          .logMessageParams(idExpediente).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_17).logMessageParams(idExpediente)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getPlazosExpedienteByIdExpediente - End");

    return expedientesPlazosDtoList;
  }

  @Override
  public boolean existsPlazosExpedienteEnCursoForPlazoResolucion(Short idProcedimiento, BigInteger idExpediente)
      throws SinacException {
    LOG.debug("PlazosServiceImpl.existsPlazosExpedienteEnCursoForPlazoResolucion - Init");

    List<ExpedientesPlazosDto> expedientesPlazosDtoList = null;

    try {
      expedientesPlazosDtoList = expedientesPlazosMapper.toDtoList(
          expedientesPlazosDao.existsPlazosExpedienteEnCursoForPlazoResolucion(idProcedimiento, idExpediente));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_18)
          .logMessageParams(idExpediente).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_19).logMessageParams(idExpediente)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.existsPlazosExpedienteEnCursoForPlazoResolucion - End");

    return !CollectionUtils.isEmpty(expedientesPlazosDtoList);
  }

  @Override
  public List<ExpedientesPlazosDto> getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazo(BigInteger idExpediente,
      short idPlazo) throws SinacException {
    LOG.debug("PlazosServiceImpl.getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazo - Init");

    List<ExpedientesPlazosDto> expedientesPlazosDtoList = null;

    try {
      expedientesPlazosDtoList = expedientesPlazosMapper
          .toDtoList(expedientesPlazosDao.getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazo(idExpediente, idPlazo));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_20)
          .logMessageParams(idExpediente, idPlazo).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_21)
          .logMessageParams(idExpediente, idPlazo).type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazo - End");

    return expedientesPlazosDtoList;
  }

  @Override
  public List<ExpedientesPlazosDto> getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazoAndIdRequerimiento(
      BigInteger idExpediente, short idPlazo, BigInteger idRequerimiento) throws SinacException {
    LOG.debug("PlazosServiceImpl.getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazoAndIdRequerimiento - Init");

    List<ExpedientesPlazosDto> expedientesPlazosDtoList = null;

    try {
      if (idRequerimiento != null) {
        expedientesPlazosDtoList = expedientesPlazosMapper.toDtoList(
            expedientesPlazosDao.getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazoAndIdRequerimiento(idExpediente,
                idPlazo, idRequerimiento));
      } else {
        expedientesPlazosDtoList = getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazo(idExpediente, idPlazo);
      }
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_22)
          .logMessageParams(idExpediente, idPlazo, idRequerimiento).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_23)
          .logMessageParams(idExpediente, idPlazo, idRequerimiento).type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getHistoricoPlazoExpedienteByIdExpedienteAndIdPlazoAndIdRequerimiento - End");

    return expedientesPlazosDtoList;
  }

  @Override
  public void updatePlazoExpedienteNoVigente(BigInteger idExpMPla) throws SinacException {
    LOG.debug("PlazosServiceImpl.updatePlazoExpedienteNoVigente - Init");

    Date now = new Date();

    try {
      expedientesPlazosDao.updatePlazoExpedienteNoVigente(idExpMPla, now);
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_24)
          .logMessageParams(idExpMPla).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_25).logMessageParams(idExpMPla)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.updatePlazoExpedienteNoVigente - End");
  }

  @Override
  public ExpedientesPlazosDto crearPlazoExpediente(ExpedientesPlazosDto expedientesPlazosDto) throws SinacException {
    LOG.debug("PlazosServiceImpl.crearPlazoExpediente - Init");

    try {
      expedientesPlazosDto = expedientesPlazosMapper
          .toDto(expedientesPlazosDao.save(expedientesPlazosMapper.toEntity(expedientesPlazosDto)));
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_26)
          .logMessageParams(expedientesPlazosDto.toString()).type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.crearPlazoExpediente - End");

    return expedientesPlazosDto;
  }

  @Override
  public Date getNextDate(Date fecha) {
    Calendar calendar = Calendar.getInstance();
    calendar.setLenient(true);
    calendar.setTime(fecha);
    calendar.add(Calendar.DATE, 1);

    return calendar.getTime();
  }

  @Override
  public Date getNextDate(Calendar fecha) {
    fecha.add(Calendar.DATE, 1);

    return fecha.getTime();
  }

  @Override
  public Date getDateByDaysToBeAdded(Date fecha, int days, String... args) {
    Date nextDate = fecha;

    while (days > 0) {
      nextDate = getNextDate(nextDate);

      if (isBusinessDay(nextDate, args)) {
        days--;
      }
    }

    return nextDate;
  }

  @Override
  public Date getDateByMonthsToBeAdded(Date fecha, int months, String... args) {
    Calendar calendar = Calendar.getInstance();
    calendar.setLenient(true);
    calendar.setTime(fecha);

    if (months > 0) {
      calendar.add(Calendar.MONTH, months);

      Date nextDate;
      while (!isBusinessDay(calendar.getTime(), args)) {
        nextDate = getNextDate(calendar);

        calendar.setTime(nextDate);
      }
    }

    return calendar.getTime();
  }

  @Override
  public Date getDateByYearsToBeAdded(Date fecha, int years, String... args) {
    Calendar calendar = Calendar.getInstance();
    calendar.setLenient(true);
    calendar.setTime(fecha);

    if (years > 0) {
      calendar.add(Calendar.YEAR, years);

      Date nextDate;
      while (!isBusinessDay(calendar.getTime(), args)) {
        nextDate = getNextDate(calendar);

        calendar.setTime(nextDate);
      }
    }

    return calendar.getTime();
  }

  @Override
  public boolean isWeekend(Date fecha) {
    Calendar calendar = Calendar.getInstance();
    calendar.setLenient(true);
    calendar.setTime(fecha);

    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

    return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
  }

  @Override
  public boolean isWeekend(Calendar fecha) throws SinacException {
    int dayOfWeek = fecha.get(Calendar.DAY_OF_WEEK);

    return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
  }

  @Override
  public boolean isHoliday(Date fecha, String... args) throws SinacException {
    Calendar calendar = Calendar.getInstance();
    calendar.setLenient(true);
    calendar.setTime(fecha);

    return holidayManager.isHoliday(calendar, args);
  }

  @Override
  public boolean isBusinessDay(Date fecha, String... args) throws SinacException {
    return !isWeekend(fecha) && !isHoliday(fecha, args);
  }

  @Override
  public Date getNextBusinessDay(Date fecha, String... args) {
    fecha = getNextDate(fecha);

    while (!isBusinessDay(fecha, args)) {
      fecha = getNextDate(fecha);
    }

    return fecha;
  }

  @Override
  public int getElapsedDays(Date fechaInicial, Date fechaFinal) {
    return Days.between(fechaInicial.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
        fechaFinal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).getAmount();
  }

  @Override
  public Date getDateByTimeToBeAdded(Date fecha, String codTipoPlazoInTime, short cantidad, String... args) {
    return switch (codTipoPlazoInTime) {
    case "PLA-DIA" -> getDateByDaysToBeAdded(fecha, cantidad, args);
    case "PLA-MES" -> getDateByMonthsToBeAdded(fecha, cantidad, args);
    case "PLA-ANO" -> getDateByYearsToBeAdded(fecha, cantidad, args);
    default -> null;
    };
  }

  @Override
  public String getCodTipoInformeByCodPlazoRespuestaInforme(String codPlazoRespuestaInforme) {
    return switch (codPlazoRespuestaInforme) {
    case "TPLA-IDGP" -> TiposInforme.TIPO_INFORME_DGP;
    case "TPLA-IMJU" -> TiposInforme.TIPO_INFORME_MJU;
    default -> null;
    };
  }

  @Override
  public String getCodTipoInformeByCodPlazoCaducidadInforme(String codPlazoCaducidadInforme) {
    return switch (codPlazoCaducidadInforme) {
    case Plazo.Tipo.CADUCIDAD_INFORME_DGP -> "TINF-DGP";
    case Plazo.Tipo.CADUCIDAD_INFORME_MJU -> "TINF-MJU";
    default -> null;
    };
  }

  @Override
  public String getCodPlazoCaducidadInformeByCodTipoInforme(String codTipoInforme) {
    return switch (codTipoInforme) {
    case "TINF-DGP" -> Plazo.Tipo.CADUCIDAD_INFORME_DGP;
    case "TINF-MJU" -> Plazo.Tipo.CADUCIDAD_INFORME_MJU;
    default -> null;
    };
  }

  @Override
  public PlazoDto getPlazoByIdProcedimientoAndCodTipoPlazo(short idProcedimiento, String codTipoPlazo)
      throws SinacException {
    LOG.debug("PlazosServiceImpl.getPlazoByIdProcedimientoAndCodTipoPlazo - Init");

    PlazoDto plazoDto = null;

    if (!StringUtils.isEmpty(codTipoPlazo)) {
      LdvMaestraDto tipoPlazoInforme = catalogosService.getCatalogoByCod(codTipoPlazo);

      if (tipoPlazoInforme != null) {
        try {
          plazoDto = plazoMapper.toDto(plazoDao
              .getPlazoByIdProcedimientoAndIdTipoPlazo(idProcedimiento, tipoPlazoInforme.getIdLdvMae()).orElse(null));
        } catch (NoSuchElementException noSuchElementException) {
          throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_27)
              .logMessageParams(idProcedimiento, codTipoPlazo).type(SinacExceptionType.DATA);
        } catch (Exception exception) {
          throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_28)
              .logMessageParams(idProcedimiento, codTipoPlazo).type(SinacExceptionType.DATA);
        }
      }
    }

    LOG.debug("PlazosServiceImpl.getPlazoByIdProcedimientoAndCodTipoPlazo - End");

    return plazoDto;
  }

  @Override
  public boolean isPlazoCaducidadInforme(String codTipoPlazo) {
    return switch (codTipoPlazo) {
    case Plazo.Tipo.CADUCIDAD_INFORME_DGP, Plazo.Tipo.CADUCIDAD_INFORME_MJU -> true;
    default -> false;
    };
  }

  @Override
  public boolean existsInformeRecibidoForPlazoCaducidadInforme(BigInteger idExpediente, String codTipoPlazo)
      throws SinacException {
    LOG.debug("PlazosServiceImpl.existsInformeRecibidoForPlazoCaducidadInforme - Init");

    boolean existsInformeSolicitado = false;

    String codTipoInforme = getCodTipoInformeByCodPlazoCaducidadInforme(codTipoPlazo);

    if (!StringUtils.isEmpty(codTipoInforme)) {
      try {
        existsInformeSolicitado = expedienteInformeMapper.toDto(expedienteInformeDao
            .getExpedienteInformeRecibidoByIdExpedienteAndCodTipoInforme(idExpediente, codTipoInforme)
            .orElse(null)) != null;
      } catch (NoSuchElementException noSuchElementException) {
        throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_29)
            .logMessageParams(idExpediente, codTipoInforme).type(SinacExceptionType.DATA);
      } catch (Exception exception) {
        throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_30)
            .logMessageParams(idExpediente, codTipoInforme).type(SinacExceptionType.DATA);
      }
    }

    LOG.debug("PlazosServiceImpl.existsInformeRecibidoForPlazoCaducidadInforme - End");

    return existsInformeSolicitado;
  }

  @Override
  public List<ExpedientesPlazosDto> getPlazosVigentesVencidosByEstado(String estado) throws SinacException {
    LOG.debug("PlazosServiceImpl.getPlazosVigentesVencidosByEstado - Init");

    List<ExpedientesPlazosDto> expedientesPlazosDtoList = null;

    try {
      expedientesPlazosDtoList = expedientesPlazosMapper.toDtoList(expedientesPlazosDao
          .getPlazosVigentesVencidosByEstado(estado).filter(list -> !CollectionUtils.isEmpty(list)).orElse(List.of()));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_31)
          .logMessageParams(estado).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_32).logMessageParams(estado)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getPlazosVigentesVencidosByEstado - End");

    return expedientesPlazosDtoList;
  }

  @Override
  public ExpedientesPlazosDto getPlazoResolucionVigenteByIdExpediente(BigInteger idExpediente) throws SinacException {
    LOG.debug("PlazosServiceImpl.getPlazoResolucionVigenteByIdExpediente - Init");

    ExpedientesPlazosDto expedientesPlazosDto;

    try {
      expedientesPlazosDto = expedientesPlazosMapper
          .toDto(expedientesPlazosDao.getPlazoResolucionVigenteByIdExpediente(idExpediente).orElse(null));
    } catch (NoSuchElementException noSuchElementException) {
      throw new SinacException(noSuchElementException, SinacExceptionMessageType.SINAC_PLAZOS_33)
          .logMessageParams(idExpediente).type(SinacExceptionType.DATA);
    } catch (Exception exception) {
      throw new SinacException(exception, SinacExceptionMessageType.SINAC_PLAZOS_34).logMessageParams(idExpediente)
          .type(SinacExceptionType.DATA);
    }

    LOG.debug("PlazosServiceImpl.getPlazoResolucionVigenteByIdExpediente - End");

    return expedientesPlazosDto;
  }

  @Override
  public Date getPreviousDate(Date fecha) {
    Calendar calendar = Calendar.getInstance();
    calendar.setLenient(true);
    calendar.setTime(fecha);
    calendar.add(Calendar.DATE, -1);

    return calendar.getTime();
  }

  @Override
  public Date getDateByDaysToTakeOff(Date fecha, int days, String... args) {
    Date previousDate = fecha;

    while (days > 0) {
      previousDate = getPreviousDate(previousDate);

      if (isBusinessDay(previousDate, args)) {
        days--;
      }
    }

    return previousDate;
  }

}
