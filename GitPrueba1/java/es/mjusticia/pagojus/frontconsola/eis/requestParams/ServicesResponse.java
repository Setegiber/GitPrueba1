package es.mjusticia.pagojus.frontconsola.eis.requestParams;

import es.mjusticia.pagojus.frontconsola.enums.CodigosError;
import es.mjusticia.pagojus.frontconsola.utils.Utils;

/*-
 * #%L
 * pagojus-restws
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

public class ServicesResponse {

  public static final CodigosError SUCCESS_CODE = CodigosError.CODE_200;
  public static final String CONFLICT_CODE = "409";
  public static final String GENERIC_ERROR_CODE = "500";
  public static final String APP_NAME = "PAGOJUS";

  private CodigosError codigoRespuesta;
  private Object respuesta;

  public ServicesResponse() {
    this.codigoRespuesta = null;
    this.respuesta = null;
  }

  public ServicesResponse(CodigosError codigo) {
    this.codigoRespuesta = codigo;
    this.respuesta = codigo.getMensajeRespuesta();
  }

  public ServicesResponse(CodigosError codigo, Object respuesta) {
    this.codigoRespuesta = codigo;
    this.respuesta = respuesta;
  }

  public ServicesResponse(CodigosError codigo, String... strings) {
    this.codigoRespuesta = codigo;
    this.respuesta = Utils.stringBuilder(strings);
  }

  public ServicesResponse errorGenerico(String errorMsg) {
    this.codigoRespuesta = CodigosError.CODE_500;
    this.respuesta = errorMsg;

    return this;

  }

  public ServicesResponse errorServicio(CodigosError codigo) {
    this.codigoRespuesta = codigo;
    this.respuesta = codigo.getMensajeRespuesta();

    return this;
  }

  public ServicesResponse ok(Object responseData) {
    this.codigoRespuesta = SUCCESS_CODE;
    this.respuesta = responseData;

    return this;
  }

  public CodigosError getCodigoRespuesta() {
    return codigoRespuesta;
  }

  public void setCodigoRespuesta(CodigosError codigoRespuesta) {
    this.codigoRespuesta = codigoRespuesta;
  }

  public Object getRespuesta() {
    return respuesta;
  }

  public void setRespuesta(Object respuesta) {
    this.respuesta = respuesta;
  }

}
