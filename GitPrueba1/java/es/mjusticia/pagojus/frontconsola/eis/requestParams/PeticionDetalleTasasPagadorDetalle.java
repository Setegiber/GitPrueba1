package es.mjusticia.pagojus.frontconsola.eis.requestParams;

import es.mjusticia.pagojus.frontconsola.utils.Validador;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PeticionDetalleTasasPagadorDetalle extends PeticionErrorHandler {

  @NotNull
  @Size(max = MAX_STRING_SIZE_50)
  private String codAplicacion;

  @NotNull
  private Long idPago;

  @Override
  protected String validarFormato() {
    return "";
  }

  public String getCodAplicacion() {
    return codAplicacion;
  }

  public void setCodAplicacion(String codAplicacion) {
    this.codAplicacion = codAplicacion;
  }

  public Long getIdPago() {
    return idPago;
  }

  public void setIdPago(Long idPago) {
    this.idPago = idPago;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("PeticionDetalleTasasPagadorDetalle { ").append("codAplicacion = ")
        .append(Validador.isNullOrEmpty(codAplicacion) ? NO_INFORMADO : codAplicacion).append("idPago = ")
        .append(idPago == null ? NO_INFORMADO : idPago).append(" }");
    return sb.toString();
  }
}