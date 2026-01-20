package es.mjusticia.pagojus.frontconsola.business.exception;

import es.mjusticia.pagojus.frontconsola.enums.CodigosError;

/*-
 * #%L
 * milanospizza-webservice
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

public class ServiceException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  private final CodigosError subCodigo;

  public ServiceException() {
    super();
    this.subCodigo = null;
  }

  public ServiceException(String message) {
    super(message);
    this.subCodigo = null;
  }

  public ServiceException(CodigosError codigo) {
    super(codigo.getMensajeRespuesta());
    this.subCodigo = codigo;
  }

  public ServiceException(CodigosError subCodigo, String message) {
    super(message);
    this.subCodigo = subCodigo;
  }

  public ServiceException(String message, java.lang.Throwable cause) {
    super(message, cause);
    this.subCodigo = null;
  }

  public ServiceException(CodigosError subCodigo, java.lang.Throwable cause) {
    super(cause);
    this.subCodigo = subCodigo;
  }

  public ServiceException(CodigosError subCodigo, String message, java.lang.Throwable cause) {
    super(message, cause);
    this.subCodigo = subCodigo;
  }

  public CodigosError getSubCodigo() {
    return subCodigo;
  }
}
