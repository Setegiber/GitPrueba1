package es.mjusticia.sinac.core.business.service.impl;

import java.text.MessageFormat;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.bridge.MessageUtil;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.sshtools.common.logger.Log;

import es.mjusticia.sinac.core.batch.SinacJob;
import es.mjusticia.sinac.core.batch.SinacJobAuditoria;
import es.mjusticia.sinac.core.batch.SinacJobDto;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.service.JobsService;
import es.mjusticia.sinac.core.eis.connector.EmailConnector;
import es.mjusticia.sinac.core.model.dto.EnviarEmailDto;
import es.mjusticia.sinac.core.model.dto.TriggerErroresDto;
import es.mjusticia.sinac.core.model.entity.SiqrJobDetailEntity;
import es.mjusticia.sinac.core.model.entity.SiqrTriggersEntity;
import es.mjusticia.sinac.core.model.entity.TriggerErroresEntity;
import es.mjusticia.sinac.core.model.mapper.TriggerErroresMapper;
import es.mjusticia.sinac.core.persistence.SiqrErrorDao;
import es.mjusticia.sinac.core.persistence.SiqrJobDetailDao;
import es.mjusticia.sinac.core.persistence.SiqrTriggersDao;

/**
 * Implementación del servicio para la gestion de tareas en segundo plano de
 * SINAC
 * 
 * @author NTTData
 */
@Service
public class JobsServiceImpl implements JobsService {

  private static final Logger LOG = LoggerFactory.getLogger(JobsServiceImpl.class);

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private SiqrErrorDao siqrErrorDao;

  @Autowired
  private TriggerErroresMapper siqrErrorMapper;

  @Autowired
  private SiqrTriggersDao siqrTriggersDao;

  @Autowired
  private SiqrJobDetailDao siqrJobDetailDao;

  @Autowired
  private EmailConnector emailConnector;
  
  @Autowired
  private SchedulerFactoryBean schedulerFactory;

  @Value("${sinac.jobs.correo-reporte:unknow@externos.mjusticia.es}")
  private String reporteDestinatario;

  @Value("${email.from.generico}")
  private String emailFrom;

  @Value(value = "${env.environmentId:}")
  private String entorno;

  @Override
  public List<SinacJobDto> getSinacJobs() throws SinacException {
    List<SinacJobDto> ret = new ArrayList<>();
    try {
      Iterable<SiqrJobDetailEntity> jobs = siqrJobDetailDao.findAll();

      for (SiqrJobDetailEntity job : jobs) {
        SinacJobDto sinacJob = new SinacJobDto();
        sinacJob.setDescripcion(job.getDescription());
        sinacJob.setNombre(job.getJobClassName());
        sinacJob.setId(job.getId().getJobName());
        sinacJob.setEstado(getTriggerState(job.getId().getJobName()));
        ret.add(sinacJob);
      }
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.MESSAGE_55);
    }
    return ret;
  }

  @Override
  public void ejecutarJob(SinacJobDto sinacJob, Map<String, Object> model) throws SinacException {
    try {
//      JobDataMap jdm = new JobDataMap(model);
//      Scheduler scheduler = schedulerFactoryBean.getScheduler();
//      scheduler.triggerJob(jobKey, jdm);

      SinacJob jobEjecutar = (SinacJob) applicationContext.getBean(Class.forName(sinacJob.getNombre()));
      Map<String, Object> contextData = new HashMap<>();
      // Nacho, pongo la descripción del job como nombre ya que en el nombre viene el
      // nombre de la clase. Hasta que se vuelva a ejecutar desde quartz
      contextData.put("jobName", sinacJob.getDescripcion());
      contextData.put("descripcion", sinacJob.getDescripcion());
      contextData.put("tiporespdgp", model.get("tiporespdgp"));
      contextData.put("codExp", model.get("codExp"));
      // TODO Habría que revisar si esta manera de pasar el contextData es correcta,
      // Ya que al reiniciar el contextData, se pierden los parametros pasados por la
      // pantalla /procesos
      contextData.put("idEnvio", model.get("idEnvio"));
      contextData.put("estadoBoe", model.get("estadoBoe"));
      jobEjecutar.execute(contextData);
      if (contextData.get("errorJob") != null) {
        model.put("errorJob", contextData.get("errorJob").toString());
      }
    } catch (Exception ex) {
      throw new SinacException(ex, SinacExceptionMessageType.MESSAGE_56);
    }
  }

  @Override
  public void guardaJobError(TriggerErroresDto triggerErrorDto) throws SinacException {
    SiqrTriggersEntity trigger = siqrTriggersDao.getTriggerByDescJobClassNameJobName(triggerErrorDto.getDescripcion(),
        triggerErrorDto.getJobClassName());
    TriggerErroresEntity error = siqrErrorMapper.toEntity(triggerErrorDto);
    error.setSiqrTrigger(trigger);
    siqrErrorDao.save(error);
  }

  @Override
  public void guardaJobErrorDgpRecibir(TriggerErroresDto triggerErrorDto) throws SinacException {
    List<SiqrTriggersEntity> trigger = siqrTriggersDao.getTriggerByDescJobClassNameJobName2(
        triggerErrorDto.getDescripcion(), triggerErrorDto.getJobClassName(), triggerErrorDto.getJobName());
    TriggerErroresEntity error = siqrErrorMapper.toEntity(triggerErrorDto);
    for (SiqrTriggersEntity siqrTriggersEntity : trigger) {
      if (siqrTriggersEntity.getId().getTriggerName().equals("sinacJobDgpRecibirTrigger")) {
        error.setSiqrTrigger(siqrTriggersEntity);
        siqrErrorDao.save(error);
      }
    }
  }

  @Override
  public void envioReporte(Map<String, Object> contextData, SinacJobAuditoria sinacJobAuditoria) throws SinacException {
    try {
      EnviarEmailDto enviarEmailDto = new EnviarEmailDto();
      enviarEmailDto.setCorreoFrom(emailFrom);
      enviarEmailDto.setPlantilla("/plantillas/reporte-job");
      Map<String, Object> reporteContext = new HashMap<>();
      reporteContext.put("job", contextData.get("descripcion"));
      reporteContext.put("itemsTotal", sinacJobAuditoria.getItemsTotal());
      reporteContext.put("itemsProcesados", sinacJobAuditoria.getItemsProcesados());
      reporteContext.put("itemsError", sinacJobAuditoria.getItemsError());
      enviarEmailDto.setVariablesPlantilla(reporteContext);
      enviarEmailDto.setAsunto("[SINAC-JOB " + entorno.toUpperCase() + "] Reporte tarea " + contextData.get("jobName"));
      enviarEmailDto.setCorreosDestinatarios(Arrays.asList(reporteDestinatario.split(";")));
      emailConnector.sendEmail(enviarEmailDto);
    } catch (Exception ex) {
      LOG.error(
          MessageFormat.format("Error enviando mail de reporte para {0}. Total: {1} - Procesados: {2} - Error: {3}",
              contextData.get("descripcion"), sinacJobAuditoria.getItemsTotal(), sinacJobAuditoria.getItemsProcesados(),
              sinacJobAuditoria.getItemsError()),
          ex);
    }
  }
  
  @Override
  public void pauseTrigger(String jobName) throws SinacException {
    try {
      JobKey jk = new JobKey(jobName);
      List<? extends Trigger> triggers = schedulerFactory.getScheduler().getTriggersOfJob(jk);
      if(triggers != null && !triggers.isEmpty()) {
        TriggerKey tk = schedulerFactory.getScheduler().getTriggersOfJob(jk).get(0).getKey();
        TriggerState ts = schedulerFactory.getScheduler().getTriggerState(tk);
        if (TriggerState.NORMAL.equals(ts)) {
          schedulerFactory.getScheduler().pauseTrigger(tk);
        }
      }
    } catch (SchedulerException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_JOBS_TGPAUSAR).logMessageParams(jobName);
    }
  }

  @Override
  public void resumeTrigger(String jobName) throws SinacException {
    try {
      JobKey jk = new JobKey(jobName);
      List<? extends Trigger> triggers = schedulerFactory.getScheduler().getTriggersOfJob(jk);
      if(triggers != null && !triggers.isEmpty()) {
        TriggerKey tk = schedulerFactory.getScheduler().getTriggersOfJob(jk).get(0).getKey();
        TriggerState ts = schedulerFactory.getScheduler().getTriggerState(tk);
        if (TriggerState.PAUSED.equals(ts)) {
          schedulerFactory.getScheduler().resumeTrigger(tk);
        }
      }
    } catch (SchedulerException e) {
      throw new SinacException(e, SinacExceptionMessageType.SINAC_JOBS_TGREANUDAR).logMessageParams(jobName);
    }
  }

  @Override
  public String getTriggerState(String jobName) throws SinacException {
    try {
      JobKey jk = new JobKey(jobName);
      List<? extends Trigger> triggers = schedulerFactory.getScheduler().getTriggersOfJob(jk);
      if(triggers != null && !triggers.isEmpty()) {
        TriggerKey tk = schedulerFactory.getScheduler().getTriggersOfJob(jk).get(0).getKey();
        TriggerState ts = schedulerFactory.getScheduler().getTriggerState(tk);
        return ts.name();
      }
      return null;
    } catch (NullPointerException | SchedulerException e) {
      Log.error(MessageFormat.format("Error recuperando estado del trigger para el job {0}", jobName), e);
      return null;
    }
  }

}
