package es.mjusticia.sinac.core.batch;

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

import java.util.Date;

public class SinacJobAuditoria {

  private Date fechaInicioEjecucion;
  private Date fechaFinEjecucion;
  private int itemsTotal;
  private int itemsProcesados = 0;
  private int itemsError = 0;

  public Date getFechaInicioEjecucion() {
    return fechaInicioEjecucion;
  }

  public void setFechaInicioEjecucion(Date fechaInicioEjecucion) {
    this.fechaInicioEjecucion = fechaInicioEjecucion;
  }

  public Date getFechaFinEjecucion() {
    return fechaFinEjecucion;
  }

  public void setFechaFinEjecucion(Date fechaFinEjecucion) {
    this.fechaFinEjecucion = fechaFinEjecucion;
  }

  public int getItemsTotal() {
    return itemsTotal;
  }

  public void setItemsTotal(int itemsTotal) {
    this.itemsTotal = itemsTotal;
  }

  public int getItemsProcesados() {
    return itemsProcesados;
  }

  public void setItemsProcesados(int itemsProcesados) {
    this.itemsProcesados = itemsProcesados;
  }

  public int getItemsError() {
    return itemsError;
  }

  public void setItemsError(int itemsError) {
    this.itemsError = itemsError;
  }
  
  public void addProcesado() {
    this.itemsProcesados++;
  }
  
  public void addError() {
    this.itemsError++;
  }

}
