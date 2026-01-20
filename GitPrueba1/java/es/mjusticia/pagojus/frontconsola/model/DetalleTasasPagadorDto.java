package es.mjusticia.pagojus.frontconsola.model;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import es.mjusticia.pagojus.frontconsola.enums.Estados;
import es.mjusticia.pagojus.frontconsola.utils.Utils;
import es.mjusticia.pagojus.frontconsola.utils.Validador;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DetalleTasasPagadorDto {

  @JsonView(Views.ListView.class)
  @JsonProperty("Id_detallepagotasa")
  private Long idPago;

  @JsonView(Views.ListView.class)
  @JsonProperty("Localizador")
  private Long idLocalizador;

  @JsonView(Views.ListView.class)
  @JsonProperty("Tasa")
  private String descTasa;

  private String codTasa;

  @JsonView(Views.ListView.class)
  @JsonProperty("Importe")
  private BigDecimal importeTasa;

  @JsonView(Views.ListView.class)
  @JsonProperty("NRC")
  private String nrc;

  @JsonView(Views.ListView.class)
  private LocalDateTime fhPago;

  @JsonView(Views.ListView.class)
  private String estadoTasa;

  private String expediente;

  @JsonProperty("NifPagador")
  private String documentoPagador;

  @JsonProperty("NifRepresentante")
  private String documentoRepresentante;

  @JsonIgnore
  private String nombrePagador;

  @JsonIgnore
  private String apellidoUnoPagador;

  @JsonIgnore
  private String apellidoDosPagador;

  @JsonIgnore
  String nombreRepresentante;

  @JsonIgnore
  String apellidoUnoRepresentante;

  @JsonIgnore
  String apellidoDosRepresentante;

  @JsonIgnore
  private String razonSocial;

  @JsonIgnore
  private Integer tipoPersonaPagador;

  public DetalleTasasPagadorDto() {
    // Todos los campos a null
  }

  public static class Views {
    private Views() {
      // NO instanciar
    }

    public static class ListView {
      private ListView() {
        // NO instanciar
      }
    }

  }
  // Constructor para permitir la creación desde una consulta
  // public DetalleTasasPagadorDto(Long idPago, Long idLocalizador, String
  // descTasa, BigDecimal importeTasa, String nrc,
  // LocalDateTime fhPago, String estadoTasa, String nombrePagador, String
  // apellidoUnoPagador,
  // String apellidoDosPagador, String razonSocial, Integer tipoPersonaPagador) {
  // this.idPago = idPago;
  // this.idLocalizador = idLocalizador;
  // this.descTasa = descTasa;
  // this.importeTasa = importeTasa;
  // this.nrc = nrc;
  // this.fhPago = fhPago;
  // this.estadoTasa = estadoTasa;
  // this.nombrePagador = nombrePagador;
  // this.apellidoUnoPagador = apellidoUnoPagador;
  // this.apellidoDosPagador = apellidoDosPagador;
  // this.razonSocial = razonSocial;
  // this.tipoPersonaPagador = tipoPersonaPagador;
  // }

  @JsonView(Views.ListView.class)
  @JsonProperty("Pagador")
  public String getNombreCompletoPagador() {
    if (Validador.isNullOrEmpty(tipoPersonaPagador)) {
      return null;
    }

    if (tipoPersonaPagador == 0) {
      String apellidoDos = "";
      if (!Validador.isNullOrEmpty(apellidoDosPagador)) {
        apellidoDos = apellidoDosPagador;
      }
      return Utils.stringBuilder(nombrePagador, apellidoUnoPagador, apellidoDos);
    } else {
      return razonSocial;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JsonProperty("Representante")
  public String getNombreCompletoRepresentante() {
    if (Validador.isNullOrEmpty(tipoPersonaPagador) || Validador.isNullOrEmpty(nombreRepresentante)) {
      return null;
    }

    if (tipoPersonaPagador == 1) {
      String apellidoDos = "";
      if (!Validador.isNullOrEmpty(apellidoDosRepresentante)) {
        apellidoDos = apellidoDosRepresentante;
      }
      return Utils.stringBuilder(nombreRepresentante, apellidoUnoRepresentante, apellidoDos);
    } else {
      return null;
    }
  }

  @JsonView(Views.ListView.class)
  @JsonProperty("Fecha pago")
  public String getFechaPago() {
    if (fhPago == null) {
      return null;
    } else {
      return Utils.timeToString(fhPago);
    }
  }

  @JsonView(Views.ListView.class)
  @JsonProperty("Importe")
  public String getImporte() {
    return importeTasa != null ? Utils.bigDecimalToString(importeTasa) : null;
  }

  // Getters y setters
  @JsonView(Views.ListView.class)
  public Long getIdPago() {
    return idPago;
  }

  public void setIdPago(Long idPago) {
    this.idPago = idPago;
  }

  @JsonView(Views.ListView.class)
  public Long getIdLocalizador() {
    return idLocalizador;
  }

  public void setIdLocalizador(Long idLocalizador) {
    this.idLocalizador = idLocalizador;
  }

  @JsonView(Views.ListView.class)
  public String getDescTasa() {
    return descTasa;
  }

  public void setDescTasa(String descTasa) {
    this.descTasa = descTasa;
  }

  @JsonIgnore
  public BigDecimal getImporteTasa() {
    return importeTasa;
  }

  public void setImporteTasa(BigDecimal importeTasa) {
    this.importeTasa = importeTasa;
  }

  @JsonView(Views.ListView.class)
  public String getNrc() {
    return nrc;
  }

  public void setNrc(String nrc) {
    this.nrc = nrc;
  }

  @JsonIgnore
  public LocalDateTime getFhPago() {
    return fhPago;
  }

  public void setFhPago(LocalDateTime fhPago) {
    this.fhPago = fhPago;
  }

  @JsonView(Views.ListView.class)
  @JsonProperty("Estado")
  public String getEstado() {
    return this.estadoTasa;
  }

  @JsonIgnore
  public Estados getEstadoTasa() {
    return Estados.getEstado(this.estadoTasa);
  }

  public void setEstadoTasa(String estadoTasa) {
    Estados estado = Estados.getEstado(estadoTasa);
    if (estado.equals(Estados.NO_ESTADO)) {
      this.estadoTasa = null;
    } else {
      this.estadoTasa = estadoTasa;
    }
  }

  public String getNombrePagador() {
    return nombrePagador;
  }

  public void setNombrePagador(String nombrePagador) {
    this.nombrePagador = nombrePagador;
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

  public String getRazonSocial() {
    return razonSocial;
  }

  public void setRazonSocial(String razonSocial) {
    this.razonSocial = razonSocial;
  }

  public Integer getTipoPersonaPagador() {
    return tipoPersonaPagador;
  }

  public void setTipoPersonaPagador(Integer tipoPersonaPagador) {
    this.tipoPersonaPagador = tipoPersonaPagador;
  }

  public String getCodTasa() {
    return codTasa;
  }

  public void setCodTasa(String codTasa) {
    this.codTasa = codTasa;
  }

  public String getExpediente() {
    return expediente;
  }

  public void setExpediente(String expediente) {
    this.expediente = expediente;
  }

  public String getDocumentoPagador() {
    return documentoPagador;
  }

  public void setDocumentoPagador(String documentoPagador) {
    this.documentoPagador = documentoPagador;
  }

  public String getDocumentoRepresentante() {
    return documentoRepresentante;
  }

  public void setDocumentoRepresentante(String documentoRepresentante) {
    this.documentoRepresentante = documentoRepresentante;
  }

  public String getNombreRepresentante() {
    return nombreRepresentante;
  }

  public void setNombreRepresentante(String nombreRepresentante) {
    this.nombreRepresentante = nombreRepresentante;
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

  public String toJson() {
    StringBuilder jsonBuilder = new StringBuilder();
    jsonBuilder.append("{");

    // Agregar campos que deseas incluir en el JSON
    jsonBuilder.append("\"Id_detallepagotasa\":").append(idPago).append(",");
    jsonBuilder.append("\"Localizador\":").append(idLocalizador).append(",");
    jsonBuilder.append("\"Tasa\":\"").append(descTasa).append("\",");
    jsonBuilder.append("\"CodTasa\":\"").append(codTasa).append("\",");
    jsonBuilder.append("\"Importe\":").append(importeTasa).append(",");
    jsonBuilder.append("\"NRC\":\"").append(nrc).append("\",");
    jsonBuilder.append("\"Estado\":\"").append(estadoTasa).append("\",");
    jsonBuilder.append("\"FechaPago\":").append(fhPago != null ? "\"" + fhPago + "\"" : null).append(",");

    // Otras propiedades...

    // Eliminar la última coma si es necesario
    if (jsonBuilder.length() > 1) {
      jsonBuilder.setLength(jsonBuilder.length() - 1); // Remueve la última coma
    }

    jsonBuilder.append("}");
    return jsonBuilder.toString();
  }
}
