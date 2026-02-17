package es.mjusticia.sinac.core.batch;

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

import java.math.BigInteger;

import org.springframework.web.multipart.MultipartFile;

public class SubirArchivoManualMjuDto {
  
  /**
   * Constructor
   */
  public SubirArchivoManualMjuDto() {
    super();
  }
  
  private BigInteger idExpediente;
  private MultipartFile penMju;
  private String mensajePenMju;
  private MultipartFile zipMju;
  private String mensajeZipMju;
  
  /**
   * @return the idExpediente
   */
  public BigInteger getIdExpediente() {
    return idExpediente;
  }
  /**
   * @param idExpediente the idExpediente to set
   */
  public void setIdExpediente(BigInteger idExpediente) {
    this.idExpediente = idExpediente;
  }
  /**
   * @return the penMju
   */
  public MultipartFile getPenMju() {
    return penMju;
  }
  /**
   * @param penMju the penMju to set
   */
  public void setPenMju(MultipartFile penMju) {
    this.penMju = penMju;
  }
  /**
   * @return the mensajePenMju
   */
  public String getMensajePenMju() {
    return mensajePenMju;
  }
  /**
   * @param mensajePenMju the mensajePenMju to set
   */
  public void setMensajePenMju(String mensajePenMju) {
    this.mensajePenMju = mensajePenMju;
  }
  /**
   * @return the zipMju
   */
  public MultipartFile getZipMju() {
    return zipMju;
  }
  /**
   * @param zipMju the zipMju to set
   */
  public void setZipMju(MultipartFile zipMju) {
    this.zipMju = zipMju;
  }
  /**
   * @return the mensajeZipMju
   */
  public String getMensajeZipMju() {
    return mensajeZipMju;
  }
  /**
   * @param mensajeZipMju the mensajeZipMju to set
   */
  public void setMensajeZipMju(String mensajeZipMju) {
    this.mensajeZipMju = mensajeZipMju;
  }
  
  @Override
  public String toString() {
    return "SubirArchivoManualMjuDto [idExpediente=" + idExpediente + ", penMju=" + penMju + ", mensajePenMju="
        + mensajePenMju + ", zipMju=" + zipMju + ", mensajeZipMju=" + mensajeZipMju + "]";
  }

}
