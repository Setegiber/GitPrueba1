package es.mjusticia.pagojus.frontconsola.eis.requestParams;

import es.mjusticia.milano.model.Nif;
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
import es.mjusticia.pagojus.frontconsola.enums.CodigosError;
import es.mjusticia.pagojus.frontconsola.eis.requestParams.ServicesResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

//TODO Valorar el que la calse no necesita ser abstracta. Ver validaciones de InformarPagador
public abstract class PeticionErrorHandler {

  protected static final int MAX_STRING_SIZE_50 = 50;
  public static final String REGEXP_NOMBRE = "^[a-zA-ZñáéíóúüÁÉÍÓÚÜ]+(\\s+[a-zA-ZñáéíóúüÁÉÍÓÚÜ]+)*$";
  public static final long LOCALIZADOR_MIN_VALUE = 7132441698L;
  public static final long ID_CONSUMO_MAX_VALUE = 9_999_999_999L;
  public static final int MAX_STRING_SIZE_URL = 2000;
  public static final String REGEXP_FORMATO_CODTASA = "^[a-zA-Z]{1,10}_\\d{3}_\\d{3}$";
  public static final String REGEXP_EMAIL = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
  public static final String REGEXP_IBAN = "^[A-Z]{2}\\d{2}[A-Z0-9]{1,30}$";
  public static final String REGEXP_RAZON_SOCIAL = "^[\\p{L}\\p{N} \\-&,;:@()./'áéíóúüñàèìòùäëïöâêîôûüÜàèìòùäëïöâêîôûÁáÉéÍíÓóÚúÑñÀàÈèÌìÒòÙùÄäËëÏïÖöÂâÊêÎîÔôÛûÇçA-Za-z0-9]+$";

  public static final long ID_MIN_VALUE = 1L;
  public static final long ID_MAX_VALUE = 9_999_999_999L;

  public static final String NO_INFORMADO = "No Informado";
  public static final int TIPO_PERSONA_FISICA = 0;
  public static final int TIPO_PERSONA_JURIDICA = 1;
  private static final Integer DOC_NIF = 1;
  private static final Integer DOC_DNI = 2;
  private static final Integer DOC_NIE = 3;
  protected static final int MAX_CODIGO_BANCO_SIZE = 4;

  protected static final int IDIOMA_SIZE = 2;
  protected static final int MAX_NOMBRE_SIZE = 100;
  protected static final int MAX_RAZON_SOCIAL_SIZE = 200;

  private BindingResult result = null;

  protected String validarObligatorios() {
    List<String> errores = result.getFieldErrors().stream()
        .filter(fieldError -> fieldError.getCode().equals("NotBlank") || fieldError.getCode().equals("NotNull"))
        .map(FieldError::getField).collect(Collectors.toList());

    return String.join(", ", errores);
  }

  protected String validarLongitudes() {
    List<String> errores = result.getFieldErrors().stream().filter(fieldError -> fieldError.getCode().equals("Size"))
        .map(FieldError::getField).collect(Collectors.toList());

    return String.join(", ", errores);
  }

  public ServicesResponse validarCampos() {
    String campos = validarObligatorios();
    ServicesResponse respuesta = null;
    if (!campos.isEmpty()) {
      respuesta = this.validacionResult(CodigosError.CODE_400_1, campos);
    }
    if (respuesta == null) {
      campos = validarLongitudes();
      if (!campos.isEmpty()) {
        respuesta = this.validacionResult(CodigosError.CODE_400_2, campos);
      }
    }

    if (respuesta == null) {
      campos = this.validarFormato();
      if (!campos.isEmpty()) {
        respuesta = this.validacionResult(CodigosError.CODE_400_3, campos);
      }
    }

    return respuesta;
  }

  protected ServicesResponse validacionResult(CodigosError respuesta, String camposError) {
    ServicesResponse response = new ServicesResponse();

    response.setCodigoRespuesta(respuesta);
    response.setRespuesta(respuesta.getMensajeRespuesta(camposError));

    return response;
  }

  public BindingResult getResult() {
    return result;
  }

  public void setResult(BindingResult result) {
    this.result = result;
  }

  protected boolean validarDocumentacion(String doc) {

    return Nif.isValid(doc);
  }

  protected boolean validarDocumentacion(String doc, int tipoPersona, Integer tipoDoc) {
    if (Nif.isValid(doc)) {
      Nif nif = Nif.valueOf(doc);
      boolean esValido = true;
      if (tipoPersona == 0) {
        esValido = this.validarTipoDocFisica(nif, tipoDoc);
      } else {
        esValido = nif.isPersonaJuridica();
      }
      return esValido;
    }
    return false;
  }

  private boolean validarTipoDocFisica(Nif doc, int tipoDoc) {
    boolean esValido = true;
    if (tipoDoc == DOC_DNI || tipoDoc == DOC_NIF) {
      esValido = doc.isDni();
    }
    if (tipoDoc == DOC_NIE) {
      esValido = doc.isNie();
    }
    return esValido;
  }

  protected String validarMinMax() {

    List<String> errores = result.getFieldErrors().stream()
        .filter(fieldError -> fieldError.getCode().equals("Min") || fieldError.getCode().equals("Max"))
        .map(FieldError::getField).collect(Collectors.toList());

    return String.join(", ", errores);
  }

  protected void appendAttribute(StringBuilder sb, String nombreCampo, Object valorCampo) {
    sb.append(nombreCampo).append("= '")
        .append(valorCampo == null ? NO_INFORMADO
            : (valorCampo instanceof String string && string.isEmpty() ? NO_INFORMADO : valorCampo))
        .append("'").append(", ");
  }

  protected abstract String validarFormato();

}
