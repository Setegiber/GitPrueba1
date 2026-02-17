package es.mjusticia.sinac.core.batch;

import java.util.ArrayList;
import java.util.Date;
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
import java.util.concurrent.CountDownLatch;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import es.mjusticia.milano.security.auditing.AuditingData;
import es.mjusticia.milano.security.auditing.AuditingField;
import es.mjusticia.sinac.core.business.exception.SinacException;
import es.mjusticia.sinac.core.business.exception.SinacExceptionMessageType;
import es.mjusticia.sinac.core.business.facade.JobFacade;
import es.mjusticia.sinac.core.business.service.UsuariosService;
import es.mjusticia.sinac.core.model.dto.UsuarioDto;
import es.mjusticia.sinac.core.security.impl.NoSessionBean;
import es.mjusticia.sinac.core.utils.UtilError;

/**
 * Clase generica para crear Jobs en Sinac para procesar colecciones de tipo T
 * 
 * @param <T>
 */
@DisallowConcurrentExecution
public abstract class SinacJob<T> implements Job {

  @Autowired
  protected JobFacade jobFacade;

  public static final CountDownLatch latch = new CountDownLatch(1);
  protected SinacJobAuditoria sinacJobAuditoria;

  @Value(value = "${env.environmentId:pro}")
  protected String entorno;

  private String descripcion;

  private String jobName;

  public void execute(Map<String, Object> contextData) throws JobExecutionException {
    sinacJobAuditoria = new SinacJobAuditoria();
    sinacJobAuditoria.setFechaInicioEjecucion(new Date());
    NoSessionBean noSessionBean = new NoSessionBean();
    try {
      jobName = (String) contextData.get("jobName");
      descripcion = (String) contextData.get("descripcion");
      contextData.put("flgProceso", true);
      UsuarioDto usuarioDto = getUsuariosService().getUsuarioByUsuarioJusticia(getUsuarioJusticia());
      noSessionBean.setUsuario(usuarioDto);

      List<T> items = new ArrayList<>();
      try {
        items = recuperarItems(contextData);
      } catch (Exception e) {
        guardaErrorJob(e, descripcion, jobName);
        throw new SinacException(e, SinacExceptionMessageType.SINAC_JOB_RECUPERAR_ITEMS);
      }
      sinacJobAuditoria.setItemsTotal(items.size());
      for (T item : items) {
        try {
          procesarItem(item, contextData);
          sinacJobAuditoria.addProcesado();
        } catch (Exception e) {
          guardaErrorJob(e, descripcion, jobName);

        }

        if (contextData.get("stopProcesarItem") != null) {
          break;
        }
      }
      postEjecucion(contextData, items, noSessionBean);
      sinacJobAuditoria.setFechaFinEjecucion(new Date());
    } finally {
      noSessionBean.unload();
      jobFacade.envioReporte(contextData, sinacJobAuditoria);
    }
  }

  public void postEjecucion(/* JobExecutionContext context */Map<String, Object> contextData, List<T> items,
      NoSessionBean noSessionBean) throws JobExecutionException {
  }

  /**
   * Recupera el servicio de usuarios de Sinac
   * 
   * @return
   */
  protected abstract UsuariosService getUsuariosService();

  /**
   * Recupera la lista de intems
   * 
   * @param contextData
   * @return
   */
  public abstract List<T> recuperarItems(Map<String, Object> contextData);

  /**
   * Procesa un item
   * 
   * @param item
   * @param contextData
   * @throws Exception
   */
  public abstract void procesarItem(T item, Map<String, Object> contextData);

  /**
   * Recupera el usuario de Sinac con el que trabaja el Job
   * 
   * @return
   */
  protected abstract String getUsuarioJusticia();

  /**
   * Guarda los errores
   * 
   * @param e
   * @param jobDescripcion
   * @param jobName
   */
  public void guardaErrorJob(Exception e, String jobDescripcion, String jobName) {

    List<String> listaNombresClaseGuardarError = new ArrayList<>();
    listaNombresClaseGuardarError.add(this.getClass().getName());
    UtilError.guardaErrorJob(e, listaNombresClaseGuardarError, jobFacade, jobDescripcion, this.getClass().getName(),
        jobName);

  };

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    sinacJobAuditoria = new SinacJobAuditoria();
    sinacJobAuditoria.setFechaInicioEjecucion(new Date());
    // Estas tres etiquetas van por quartz automático, ejecución manual no hacen
    // falta
    AuditingData.put(AuditingField.REQUEST_ID.label(), "pruebauno");
    AuditingData.put(AuditingField.SESSION_ID.label(), "pruebados");
    AuditingData.put(AuditingField.CLIENT_IP.label(), "pruebatres");
    NoSessionBean noSessionBean = new NoSessionBean();
    try {
      UsuarioDto usuarioDto = getUsuariosService().getUsuarioByUsuarioJusticia(getUsuarioJusticia());
      noSessionBean.setUsuario(usuarioDto);
      Map<String, Object> contextData = context.getMergedJobDataMap().getWrappedMap();
      jobName = (String) contextData.get("jobName");
      descripcion = (String) contextData.get("descripcion");
      contextData.put("flgProceso", true);
      List<T> items = recuperarItems(contextData);
      sinacJobAuditoria.setItemsTotal(items.size());
      for (T item : items) {
        try {
          procesarItem(item, contextData);
          sinacJobAuditoria.addProcesado();
        } catch (Exception e) {
          guardaErrorJob(e, descripcion, jobName);
        }

        if (contextData.get("stopProcesarItem") != null) {
          break;
        }
      }
      postEjecucion(contextData, items, noSessionBean);
      sinacJobAuditoria.setFechaFinEjecucion(new Date());
    } finally {
      noSessionBean.unload();
      jobFacade.envioReporte(context.getMergedJobDataMap(), sinacJobAuditoria);
    }
  }

  protected void addError() {
    if (sinacJobAuditoria != null) {
      sinacJobAuditoria.addError();
    }
  }

  protected void addProcesado() {
    if (sinacJobAuditoria != null) {
      sinacJobAuditoria.addProcesado();
    }
  }
}
