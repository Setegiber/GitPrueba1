package es.mjusticia.pagojus.frontconsola.eis.requestParams;

import es.mjusticia.pagojus.frontconsola.enums.Estados;
import es.mjusticia.pagojus.frontconsola.utils.Utils;
import es.mjusticia.pagojus.frontconsola.utils.Validador;
import es.mjusticia.pagojus.frontconsola.utils.ValidarCodigoTasa;
import es.mjusticia.pagojus.frontconsola.utils.ValidarLocalDate;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.validation.FieldError;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PeticionDetalleTasasPagador extends PeticionErrorHandler implements PeticionesConsola {

  public static final String ID_PAGO_ORDEN = "idPago";
  public static final String LOCALIZADOR_ORDEN = "pago.idLocalizador";
  public static final String TASA_ORDEN = "tasa.descTasa";
  public static final String IMPORTE_TASA_ORDEN = "importe";
  public static final String NRC_ORDEN = "nrc";
  public static final String FH_PAGO_ORDEN = "fhPago";
  public static final String ESTADO_TASA_ORDEN = "estado";
  public static final String PAGADOR_ORDEN = "pago.pagador.nombrePagador";
  public static final String RAZON_SOCIAL_ORDEN = "pago.pagador.razonSocialPagador";

  @NotBlank
  @ValidarCodigoTasa(regex = REGEXP_FORMATO_CODTASA)
  private String tasa;

  private String nrc;
  private String expediente;
  private Long localizador;
  private String estadoTasa;

  @ValidarLocalDate
  private String fechaPagoDesde;

  @ValidarLocalDate
  private String fechaPagoHasta;

  private String nifPagador;
  @Size(max = MAX_NOMBRE_SIZE)
  private String nombrePagador;
  @Size(max = MAX_NOMBRE_SIZE)
  private String apellidoUnoPagador;
  @Size(max = MAX_NOMBRE_SIZE)
  private String apellidoDosPagador;

  @Size(max = MAX_RAZON_SOCIAL_SIZE)
  private String razonSocialPagador;

  private String nifRepresentante;

  @Size(max = MAX_NOMBRE_SIZE)
  private String nombreRepresentante;
  @Size(max = MAX_NOMBRE_SIZE)
  private String apellidoUnoRepresentante;
  @Size(max = MAX_NOMBRE_SIZE)
  private String apellidoDosRepresentante;

  @NotNull
  private String codAplicacion;

  @Min(value = MIN_VALUE_PAGINACION)
  private Integer numeroPagina;

  @NotNull
  @Min(value = MIN_VALUE_PAGINACION)
  @Max(value = MAX_VALUE_PAGINACION)
  private Integer tamanioPagina;

  private String columnaOrden;
  private String orden;

  public String getTasa() {
    return tasa;
  }

  public void setTasa(String tasa) {
    this.tasa = tasa;
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

  public Long getLocalizador() {
    return localizador;
  }

  public void setLocalizador(Long localizador) {
    this.localizador = localizador;
  }

  public String getEstadoTasa() {
    return estadoTasa;
  }

  public void setEstadoTasa(String estadoTasa) {
    this.estadoTasa = estadoTasa;
  }

  public String getFechaPagoDesde() {
    return fechaPagoDesde;
  }

  public LocalDate fechaPagoDesdeToLocalDate() {

    if (fechaPagoDesde != null) {
      return Utils.getLocalDate(fechaPagoDesde);
    }
    return null;

  }

  public void setFechaPagoDesde(String fechaPagoDesde) {
    if (Validador.isNullOrEmpty(fechaPagoDesde)) {
      this.fechaPagoDesde = null;
    } else {
      this.fechaPagoDesde = fechaPagoDesde;
    }
  }

  public String getFechaPagoHasta() {
    return fechaPagoHasta;
  }

  public LocalDate fechaPagoHastaToLocalDate() {
    if (fechaPagoHasta != null) {
      return Utils.getLocalDate(fechaPagoHasta);
    }
    return null;
  }

  public void setFechaPagoHasta(String fechaPagoHasta) {
    if (Validador.isNullOrEmpty(fechaPagoHasta)) {
      this.fechaPagoHasta = null;
    } else {
      this.fechaPagoHasta = fechaPagoHasta;
    }
  }

  public String getNifPagador() {
    return nifPagador;
  }

  public void setNifPagador(String nifPagador) {
    this.nifPagador = nifPagador;
  }

  public String getNombrePagador() {
    return nombrePagador;
  }

  public void setNombrePagador(String nombrePagador) {
    this.nombrePagador = nombrePagador;
  }

  public String getNifRepresentante() {
    return nifRepresentante;
  }

  public void setNifRepresentante(String nifRepresentante) {
    this.nifRepresentante = nifRepresentante;
  }

  public String getNombreRepresentante() {
    return nombreRepresentante;
  }

  public void setNombreRepresentante(String nombreRepresentante) {
    this.nombreRepresentante = nombreRepresentante;
  }

  public String getApellidoUnoPagador() {
    return apellidoUnoPagador;
  }

  public void setApellidoUnoPagador(String apellidoUnoPagador) {
    this.apellidoUnoPagador = apellidoUnoPagador;
  }

  public String getApellidoDosPagador() {
    return apellidoDosPagador;
  }

  public void setApellidoDosPagador(String apellidoDosPagador) {
    this.apellidoDosPagador = apellidoDosPagador;
  }

  public String getApellidoUnoRepresentante() {
    return apellidoUnoRepresentante;
  }

  public void setApellidoUnoRepresentante(String apellidoUnoRepresentante) {
    this.apellidoUnoRepresentante = apellidoUnoRepresentante;
  }

  public String getApellidoDosRepresentante() {
    return apellidoDosRepresentante;
  }

  public void setApellidoDosRepresentante(String apellidoDosRepresentante) {
    this.apellidoDosRepresentante = apellidoDosRepresentante;
  }

  public String getCodAplicacion() {
    return codAplicacion.toUpperCase();
  }

  public void setCodAplicacion(String codAplicacion) {
    this.codAplicacion = codAplicacion;
  }

  public String getRazonSocialPagador() {
    return razonSocialPagador;
  }

  public void setRazonSocialPagador(String razonSocialPagador) {
    this.razonSocialPagador = razonSocialPagador;
  }

  public Integer getNumeroPagina() {
    return numeroPagina;
  }

  public void setNumeroPagina(Integer numeroPagina) {
    this.numeroPagina = numeroPagina;
  }

  public Integer getTamanioPagina() {
    return tamanioPagina;
  }

  public void setTamanioPagina(Integer tamanioPagina) {
    this.tamanioPagina = tamanioPagina;
  }

  public String getColumnaOrden() {
    return columnaOrden;
  }

  public void setColumnaOrden(String columnaOrden) {
    this.columnaOrden = columnaOrden;
  }

  public String getOrden() {
    return orden;
  }

  public void setOrden(String orden) {
    this.orden = orden;
  }

  @Override
  protected String validarFormato() {

    List<String> errores = this.getResult().getFieldErrors().stream()
        .filter(fieldError -> fieldError.getCode().equals("Min") || fieldError.getCode().equals("Max")
            || fieldError.getCode().equals("ValidarCodigoTasa") || fieldError.getCode().equals("ValidarLocalDate"))
        .map(FieldError::getField).collect(Collectors.toList());

    boolean result = Validador.isNullOrEmpty(this.nifPagador) || validarDocumentacion(this.nifPagador);
    if (!result) {
      errores.add("nifPagador");
    }
    result = Validador.isNullOrEmpty(this.nifRepresentante) || validarDocumentacion(this.nifRepresentante);
    if (!result) {
      errores.add("nifRepresentante");
    }
    return String.join(", ", errores);
  }

  @Override
  public Map<String, Object> getPaginadoYOrden() {

    Map<String, Object> pagOrden = new HashMap<>();

    if (Validador.isNullOrEmpty(this.orden) || this.orden.equalsIgnoreCase(PeticionesConsola.ORDER_ASC)) {
      pagOrden.put(PeticionesConsola.ORDEN_TYPE, PeticionesConsola.ORDER_DEFAULT);
    } else {
      pagOrden.put(PeticionesConsola.ORDEN_TYPE, PeticionesConsola.ORDER_DESC);
    }
    if (Validador.isNullOrEmpty(this.columnaOrden)) {
      pagOrden.put(PeticionesConsola.COL_ORDEN, ID_PAGO_ORDEN);
    } else {
      pagOrden.put(PeticionesConsola.COL_ORDEN, this.columnaOrden);
    }
    if (this.numeroPagina == null) {
      pagOrden.put(PeticionesConsola.N_PAGINA, PeticionesConsola.N_PAGINA_DEFAULT);
    } else {
      pagOrden.put(PeticionesConsola.N_PAGINA, this.numeroPagina);
    }

    pagOrden.put(PeticionesConsola.TAMANIO_PAGINA, this.tamanioPagina);

    return pagOrden;
  }

  public boolean needsPagador() {
    return (!Validador.isNullOrEmpty(this.nifPagador) || !Validador.isNullOrEmpty(this.nifRepresentante)
        || !Validador.isNullOrEmpty(this.nombrePagador) || !Validador.isNullOrEmpty(this.nombreRepresentante)
        || !Validador.isNullOrEmpty(this.razonSocialPagador) || !Validador.isNullOrEmpty(this.apellidoUnoPagador)
        || !Validador.isNullOrEmpty(this.apellidoDosPagador) || !Validador.isNullOrEmpty(this.apellidoUnoRepresentante)
        || !Validador.isNullOrEmpty(this.apellidoDosRepresentante));

  }

  public boolean needsPagos() {
    return this.localizador != null;
  }

  public boolean needsDetalleTasas() {

    return (!Validador.isNullOrEmpty(this.nrc)) || (!Validador.isNullOrEmpty(this.expediente))
        || (!Validador.isNullOrEmpty(this.estadoTasa)) || (!Validador.isNullOrEmpty(this.fechaPagoDesde))
        || (!Validador.isNullOrEmpty(this.fechaPagoHasta));
  }

  public boolean needsLetfJoin() {

    if (!Validador.isNullOrEmpty(this.estadoTasa) && this.estadoTasa.equalsIgnoreCase(Estados.INICIADO.name())) {
      return true;
    }

    if (!Validador.isNullOrEmpty(this.columnaOrden)) {
      return this.columnaOrden.equals(PAGADOR_ORDEN) || this.columnaOrden.equals(RAZON_SOCIAL_ORDEN);

    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("PeticionDetalleTasasPagador { ").append("tasa = ")
        .append(Validador.isNullOrEmpty(tasa) ? NO_INFORMADO : tasa).append(", ").append("nrc = ")
        .append(Validador.isNullOrEmpty(nrc) ? NO_INFORMADO : nrc).append(", ").append("expediente = ")
        .append(Validador.isNullOrEmpty(expediente) ? NO_INFORMADO : expediente).append(", ").append("localizador = ")
        .append(localizador == null ? NO_INFORMADO : localizador).append(", ").append("estado = ")
        .append(Validador.isNullOrEmpty(estadoTasa) ? NO_INFORMADO : estadoTasa).append(", ")
        .append("fechaPagoDesde = ").append(Validador.isNullOrEmpty(fechaPagoDesde) ? NO_INFORMADO : fechaPagoDesde)
        .append(", ").append("fechaPagoHasta = ")
        .append(Validador.isNullOrEmpty(fechaPagoHasta) ? NO_INFORMADO : fechaPagoHasta).append(", ")
        .append("nifPagador = ").append(Validador.isNullOrEmpty(nifPagador) ? NO_INFORMADO : nifPagador).append(", ")
        .append("nombrePagador = ").append(Validador.isNullOrEmpty(nombrePagador) ? NO_INFORMADO : nombrePagador)
        .append(", ").append("nifRepresentante = ")
        .append(Validador.isNullOrEmpty(nifRepresentante) ? NO_INFORMADO : nifRepresentante).append(", ")
        .append("nombreRepresentante = ")
        .append(Validador.isNullOrEmpty(nombreRepresentante) ? NO_INFORMADO : nombreRepresentante).append(", ")
        .append("codAplicacion = ")
        .append(Validador.isNullOrEmpty(codAplicacion) ? NO_INFORMADO : codAplicacion.toUpperCase()).append(", ")
        .append("numeroPagina = ").append(numeroPagina == null ? NO_INFORMADO : numeroPagina).append(", ")
        .append("tamanioPagina = ").append(tamanioPagina == null ? NO_INFORMADO : tamanioPagina).append(", ")
        .append("columnaOrden = ").append(Validador.isNullOrEmpty(columnaOrden) ? NO_INFORMADO : columnaOrden)
        .append(", ").append("orden = ").append(Validador.isNullOrEmpty(orden) ? NO_INFORMADO : orden).append(" }");
    return sb.toString();
  }
}
