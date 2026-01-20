package es.mjusticia.pagojus.frontconsola.eis.requestParams;

import es.mjusticia.pagojus.frontconsola.enums.CodigosError;
import es.mjusticia.pagojus.frontconsola.enums.Estados;
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
import es.mjusticia.pagojus.frontconsola.enums.EstadosPermitidos;
import es.mjusticia.pagojus.frontconsola.enums.RolesConsola;
import es.mjusticia.pagojus.frontconsola.eis.requestParams.ServicesResponse;
import es.mjusticia.pagojus.frontconsola.utils.Validador;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PeticionConsolaDetallePagoTasaEditar extends PeticionErrorHandler {

  public static final String UPDATE_ESTADO = "estado";
  public static final String UPDATE_EXPEDIENTE = "expediente";
  public static final String UPDATE_NRC = "nrc";
  public static final String UPDATE_COD_APP = "codAplicacion";
  private static final String ERROR_NRC_Y_NO_PAGADO = "No es posible modificar el NRC si el estado final no es PAGADO";

  @NotBlank
  private String codAplicacion;
  @NotNull
  private Long idPago;

  @Size(max = MAX_STRING_SIZE_50)
  private String nrc;

  @Size(max = MAX_STRING_SIZE_50)
  private String expediente;

  @Size(max = MAX_STRING_SIZE_50)
  private String estado;

  public String getCodAplicacion() {
    return codAplicacion.toUpperCase();
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

  public String getNrc() {
    return nrc;
  }

  public void setNrc(String nrc) {
    this.nrc = nrc;
  }

  public String getExpediente() {
    return expediente;
  }

  public void setExpediente(String expediente) {
    this.expediente = expediente;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  @Override
  protected String validarFormato() {
    if (!Validador.isNullOrEmpty(this.estado)) {
      String regex = this.getRegExpForEstadosPermitidos(EstadosPermitidos.CONSOLA_EDITAR_ESTADO_TASA);
      if (!estado.matches(regex)) {
        return UPDATE_ESTADO;
      }
      if (!this.estado.equalsIgnoreCase(Estados.PAGADO.name()) && !Validador.isNullOrEmpty(this.nrc)) {
        return ERROR_NRC_Y_NO_PAGADO;
      }
    }
    return "";
  }

  public ServicesResponse validarAcceso() {

    if (this.codAplicacion.equalsIgnoreCase(RolesConsola.ROL_VENTANILLA.getRol())
        && (!estado.equalsIgnoreCase(Estados.UTILIZADO.name()) || !Validador.isNullOrEmpty(nrc)
            || !Validador.isNullOrEmpty(expediente))) {
      return new ServicesResponse(CodigosError.CODE_409_403);
    }

    return null;
  }

  public Map<String, Object> getUpdates() {
    Map<String, Object> updates = new HashMap<>();
    if (!Validador.isNullOrEmpty(this.estado)) {
      updates.put(UPDATE_ESTADO, this.estado);
    }

    if (!Validador.isNullOrEmpty(this.nrc)) {
      updates.put(UPDATE_NRC, this.nrc);
    }
    if (!Validador.isNullOrEmpty(this.expediente)) {
      updates.put(UPDATE_EXPEDIENTE, this.expediente);
    }

    updates.put(UPDATE_COD_APP, this.codAplicacion);

    return updates;
  }

  public String getRegExpForEstadosPermitidos(EstadosPermitidos estadosPermitidos) {
    return "(?i)^(" + Arrays.stream(estadosPermitidos.getPermitidos().split(" ")).collect(Collectors.joining("|"))
        + ")$";
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("codAplicacion = '")
        .append(codAplicacion != null && !codAplicacion.isEmpty() ? codAplicacion : NO_INFORMADO).append("', ");
    sb.append("idPago = ").append(idPago != null ? idPago : NO_INFORMADO).append(", ");
    sb.append("nrc = '").append(nrc != null && !nrc.isEmpty() ? nrc : NO_INFORMADO).append("', ");
    sb.append("expediente = '").append(expediente != null && !expediente.isEmpty() ? expediente : NO_INFORMADO)
        .append("', ");
    sb.append("estado = '").append(estado != null && !estado.isEmpty() ? estado : NO_INFORMADO).append("'");

    return sb.toString();
  }

}
