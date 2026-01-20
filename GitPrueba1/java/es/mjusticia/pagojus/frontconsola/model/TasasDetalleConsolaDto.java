package es.mjusticia.pagojus.frontconsola.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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

public class TasasDetalleConsolaDto extends TasasBusquedaConsolaDto {

  private String codTasa;

  private String expediente;

  @JsonProperty("Resultado")
  private String resultado;

  @JsonProperty("NIF Pagador")
  private String documentoPagador;

  @JsonProperty("NIF Representante")
  private String documentoRepresentante;

  @JsonProperty("Nombre Representante")
  private String nombreCompletoRepresentante;

  @JsonProperty("Cuenta Persona Juridica")
  private Boolean cuentaPJ;

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

  public String getResultado() {
    return resultado;
  }

  public void setResultado(String resultado) {
    this.resultado = resultado;
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

  public String getNombreCompletoRepresentante() {
    return nombreCompletoRepresentante;
  }

  public void setNombreCompletoRepresentante(String nombreCompletoRepresentante) {
    this.nombreCompletoRepresentante = nombreCompletoRepresentante;
  }

  public Boolean getCuentaPJ() {
    return cuentaPJ;
  }

  public void setCuentaPJ(Boolean cuentaPJ) {
    this.cuentaPJ = cuentaPJ;
  }

}
