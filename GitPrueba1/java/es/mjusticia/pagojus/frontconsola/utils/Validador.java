package es.mjusticia.pagojus.frontconsola.utils;

/*-
 * #%L
 * pagojus-restws
 * %%
 * Copyright (C) 2023 - 2025 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validador {
  public static final String BAD_REQUEST_CODE = "400";
  public static final String CONFLICT_CODE = "409";
  public static final String GENERIC_ERROR_CODE = "500";
  public static final String ERROR_CAMPO_OBLIGATORIO = "Los campos obligatorios XXX no están informados.";
  public static final String ERROR_LONGITUD = "Los campos XXX no cumplen con la longitud permitida.";
  public static final String ERROR_FORMATO = "No se cumple el formato del campo XXX.";
  public static final String ERROR_SERVICIO_APUNTES_PAGO = "Se ha producido un error en el servicio de apuntes de pago.";
  public static final String ERROR_NO_EXISTE_LOCALIZADOR = "El localizador adjuntado a la petición no existe en nuestro sistema.";
  public static final String ERROR_NO_EXISTE_PAGADOR_PAGO = "El localizador adjuntado a la petición no tiene relación con el documento de identidad.";
  public static final String ERROR_GENERICOS = "XXX";

  public static final long LOCALIZADOR_MIN_VALUE = 7132441698L;
  public static final String REGEXP_FORMATO_CODTASA = "^[a-zA-Z]{1,10}_\\d{3}_\\d{3}$";

  public static final String REGEXP_EMAIL = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
  public static final String REGEXP_LOCALIZADOR = "^[1-9]\\d{0,19}$";
  public static final String REGEXP_NOMBRE = "^[a-zA-ZñáéíóúüÁÉÍÓÚÜ]+$";
  public static final String REGEXP_IBAN = "^[A-Z]{2}\\d{2}[A-Z0-9]{1,30}$";

  public static final int MAX_SIZE_STRING_20 = 20;
  public static final int MAX_SIZE_STRING_50 = 50;
  public static final int MAX_SIZE_STRING_250 = 250;

  // Patrón para validar el formato básico del IBAN
  private static final Pattern IBAN_PATTERN = Pattern.compile(REGEXP_IBAN);
  private static final long IBAN_MODULO_97 = 97;
  private static final int IBAN_CHAR_HEAD = 4;

  private Validador() {
  }

  public static boolean isNullOrEmpty(Object obj) {
    if (obj == null) {
      return true;
    }

    if (obj instanceof CharSequence charSecuence) {
      return charSecuence.toString().trim().isEmpty();
    }

    return false;
  }

  public static boolean validarUrl(String urlString) {
    boolean esCorrecto = true;
    // final int HTTP_DEFAULT_PORT = 80;
    // final int HTTPS_DEFAULT_PORT = 443;
    try {
      URL url = new URL(urlString);

      // Verificar el esquema
      if (!url.getProtocol().equals("http") && !url.getProtocol().equals("https")) {
        esCorrecto = false;
      }

      // Verificar el dominio
      if (esCorrecto) {
        String domain = url.getHost();
        if (domain == null || domain.isEmpty()) {
          esCorrecto = false;
        }
      }

      // Verificar el puerto

      // int port = url.getPort();
      // if (port == -1) {
      // // Si el puerto no está explícitamente especificado,
      // // obtener el puerto predeterminado
      // port = url.getDefaultPort();
      // }
      // if ((url.getProtocol().equals("http") && port != HTTP_DEFAULT_PORT)
      // || (url.getProtocol().equals("https") && port != HTTPS_DEFAULT_PORT)) {
      // return false;
      // }

      // Verificar la ruta

      String path = url.getPath();
      if (path == null || path.isEmpty()) {
        return false;

      }
    } catch (MalformedURLException e) {
      esCorrecto = false;
    }
    return esCorrecto;
  }

  public static boolean validarNombre(String nombre) {
    nombre = nombre.trim();
    String[] partes = nombre.split("\\s");
    Pattern pattern = Pattern.compile(REGEXP_NOMBRE);

    for (String parte : partes) {
      String parteTrimmed = parte.trim();
      Matcher matcher = pattern.matcher(parteTrimmed);
      if (!matcher.matches()) {
        return false;
      }
    }

    return true;
  }

  public static boolean validarContraRegExp(String validar, String regExp) {
    if (validar == null) {
      return false;
    }
    Pattern pattern = Pattern.compile(regExp);
    return pattern.matcher(validar).matches();
  }

  public static boolean validarIBAN(String iban) {
    // Eliminar espacios en blanco
    String cleanedIban = iban.replaceAll("\\s+", "");

    // Verificar el formato básico
    if (!IBAN_PATTERN.matcher(cleanedIban).matches()) {
      return false;
    }

    // Mover los primeros cuatro caracteres al final
    String rearrangedIban = cleanedIban.substring(IBAN_CHAR_HEAD) + cleanedIban.substring(0, IBAN_CHAR_HEAD);

    // Convertir letras a números
    StringBuilder numericIban = new StringBuilder();
    for (char ch : rearrangedIban.toCharArray()) {
      if (Character.isDigit(ch)) {
        numericIban.append(ch);
      } else {
        numericIban.append(Character.getNumericValue(ch));
      }
    }

    // Validar con módulo 97
    BigInteger ibanNumber = new BigInteger(numericIban.toString());
    return ibanNumber.mod(BigInteger.valueOf(IBAN_MODULO_97)).intValue() == 1;
  }

}
