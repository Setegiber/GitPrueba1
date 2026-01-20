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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import es.mjusticia.pagojus.frontconsola.business.exception.ServiceException;
import es.mjusticia.pagojus.frontconsola.enums.CodigosError;
import java.util.ArrayList;
import java.util.List;

public class TasasBusquedaConsolaDto {

  @JsonProperty("Id_detallepagotasa")
  private Long idPago;

  @JsonProperty("Localizador")
  private Long idLocalizador;

  @JsonProperty("Tasa")
  private String descTasa;

  @JsonProperty("Importe")
  private String importeTasa;

  @JsonProperty("NRC")
  private String nrc;

  @JsonProperty("Fecha pago")
  private String fhPago;

  @JsonProperty("Estado")
  private String estadoTasa;

  @JsonProperty("Pagador")
  private String nombreCompleto;

  @JsonProperty("Razon Social")
  private String razonSocial;

  public Long getIdPago() {
    return idPago;
  }

  public void setIdPago(Long idPago) {
    this.idPago = idPago;
  }

  public Long getIdLocalizador() {
    return idLocalizador;
  }

  public void setIdLocalizador(Long idLocalizador) {
    this.idLocalizador = idLocalizador;
  }

  public String getDescTasa() {
    return descTasa;
  }

  public void setDescTasa(String descTasa) {
    this.descTasa = descTasa;
  }

  public String getImporteTasa() {
    return importeTasa;
  }

  public void setImporteTasa(String importeTasa) {
    this.importeTasa = importeTasa;
  }

  public String getNrc() {
    return nrc;
  }

  public void setNrc(String nrc) {
    this.nrc = nrc;
  }

  public String getFhPago() {
    return fhPago;
  }

  public void setFhPago(String fhPago) {
    this.fhPago = fhPago;
  }

  public String getEstadoTasa() {
    return estadoTasa;
  }

  public void setEstadoTasa(String estadoTasa) {
    this.estadoTasa = estadoTasa;
  }

  public String getNombreCompleto() {
    return nombreCompleto;
  }

  public void setNombreCompleto(String nombreCompleto) {
    this.nombreCompleto = nombreCompleto;
  }

  public String getRazonSocial() {
    return razonSocial;
  }

  public void setRazonSocial(String razonSocial) {
    this.razonSocial = razonSocial;
  }

  // Método estático para obtener los nombres de las propiedades anotadas con
  // Jackson
  // Si existen propiedades sin notacion Jacson se provocara una
  // JsonMappingException
  //
  public static List<String> getPropertyNames() throws ServiceException {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
      mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());

      List<BeanPropertyDefinition> properties = mapper.getSerializationConfig()
          .introspect(mapper.constructType(TasasBusquedaConsolaDto.class)).findProperties();

      List<String> propertyNames = new ArrayList<>();
      for (BeanPropertyDefinition property : properties) {
        propertyNames.add(property.getName());
      }

      return propertyNames;
    } catch (Exception e) {
      throw new ServiceException(CodigosError.CODE_409_1, e.getMessage(), e);
    }
  }
}
