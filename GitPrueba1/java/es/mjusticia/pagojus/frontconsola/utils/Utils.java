package es.mjusticia.pagojus.frontconsola.utils;

import java.io.ByteArrayInputStream;
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
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

public class Utils {

  public static final String ORIGEN_METODOS_PAGO = "METODOS_PAGO";
  public static final String ORIGEN_TASAS = "TASAS";
  public static final String PAGO_REALIZADO_DESC = "Pago realizado";
  public static final String EXPEDIENTE = "EXPEDIENTE";
  public static final String LOCALIZADOR = "LOCALIZADOR";
  public static final String NRC = "NRC";
  public static final String INICIO = "Inicio. ";
  public static final String FIN = "Fin. ";
  private static final Logger logger = LoggerFactory.getLogger(Utils.class);
  public static final int RESULTADO_CONTROL_LONGITUD = 250;

  public static final int MAX_SIZE_MSG_ERROR = 2000;
  public static final int MAX_SIZE_COD_ERROR = 50;
  private static final int DECIMALES = 2;

  public static final int MAX_RAZON_SOCIAL_SIZE_EPAGO = 200;
  public static final int MAX_NOMBRE_SIZE_EPAGO = 100;
  private static final List<DateTimeFormatter> FORMATS = Arrays.asList(DateTimeFormatter.ofPattern("yyyy/MM/dd"),
      DateTimeFormatter.ofPattern("yyyy-MM-dd"), DateTimeFormatter.ofPattern("dd/MM/yyyy"),
      DateTimeFormatter.ofPattern("dd-MM-yyyy"));

  private Utils() {
    // Pedido por sonar
  }

  /**
   * Convierte un archivo en una cadena en formato base64.
   *
   * @param filePath la ruta relativa del archivo
   * @return la cadena en formato base64 o null si ocurre un error
   */
  public static String fileToBase64Converter(String filePath, String servicio) {
    try {
      InputStream inputStream = Utils.class.getClassLoader().getResourceAsStream(filePath);
      if (inputStream != null) {
        byte[] fileBytes = inputStream.readAllBytes();
        return Base64.getEncoder().encodeToString(fileBytes);
      } else {
        String error = servicio.concat("No se ha encontrado el archivo en la ruta especificada: {}");
        logger.error(error, filePath);
        return null;
      }
    } catch (IOException e) {
      String error = servicio.concat("No se ha podido codificar el archivo: {}");
      logger.error(error, filePath, e);
      return null;
    }
  }

  public static double importeParseDouble(BigDecimal importeBD) {
    // Asegurarse de que el BigDecimal tenga exactamente dos decimales
    BigDecimal scaledValue = importeBD.setScale(DECIMALES, RoundingMode.HALF_UP);
    return scaledValue.doubleValue();
  }

  public static String truncarCadena(String cadena, Integer nuevaLongitud) {
    if (cadena == null) {
      return "";
    }
    if (cadena.length() <= nuevaLongitud) {
      return cadena;
    }
    return cadena.substring(0, nuevaLongitud);
  }

  // Método que convierte un BigDecimal a String con dos decimales
  public static String bigDecimalToString(BigDecimal importeBD) {
    if (importeBD == null) {
      return null;
    }
    // Asegurarse de que el BigDecimal tenga exactamente dos decimales
    BigDecimal scaledValue = importeBD.setScale(DECIMALES, RoundingMode.HALF_UP);
    // Usar DecimalFormat para asegurar el formato de dos decimales
    DecimalFormat df = new DecimalFormat("#0.00");
    return df.format(scaledValue.doubleValue());
  }

  public static String normalizarTexto(String input) {
    if (input == null) {
      return "";
    }
    // Paso 1: Normalizar el texto a la forma de descomposición canónica
    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

    // Paso 2: Eliminar los diacríticos usando una expresión regular
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    String sinDiacriticos = pattern.matcher(normalized).replaceAll("");

    // Paso 3: Reemplazar la "ñ" por "n"
    String resultado = sinDiacriticos.replace("ñ", "n").replace("Ñ", "N");

    return resultado.replace("_", " ").replace("¿", " ").replace("¡", " ").replace("´", " ").replace("`", " ")
        .replace("¨", " ").replace("·", " ").replace("¬", " ").replace("º", " ").replace("ª", " ")
        .replace("\u2013", "\u002D") // Guion medio a guion normal
        .replace("\u201C", "\"") // Comillas dobles de apertura
        .replace("\u201D", "\"") // Comillas dobles de cierre
        .replace("\u2018", "'") // Comillas simples de apertura
        .replace("\u2019", "'") // Comillas simples de cierre
        .replace("\u2014", "-") // Guion largo a guion normal
        .replace("\u00A0", " "); // Espacio no rompible

  }

  public static XMLGregorianCalendar convertirFechaCaducidad(String fechaCaducidadTarjeta)
      throws DatatypeConfigurationException {

    String diaActualString = String.format("%02d", LocalDateTime.now().getDayOfMonth());
    String fechaCaducidadString = diaActualString.concat("/").concat(fechaCaducidadTarjeta);

    LocalDateTime fechaHora = LocalDateTime.parse(fechaCaducidadString + " 00:00:00",
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

    LocalDateTime fechaManana = fechaHora.plusDays(1);

    ZonedDateTime zonedDateTime = fechaManana.atZone(ZoneId.systemDefault());

    return DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(zonedDateTime));

  }

  public static String timeToString(LocalDateTime time) {
    if (time == null) {
      return null;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    return time.format(formatter);
  }

  public static String fechaYhoraToString(LocalDateTime time) {
    if (time == null) {
      return null;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    return time.format(formatter);
  }

  public static LocalDate getLocalDate(String fechaStr) {

    if (fechaStr == null || fechaStr.isBlank()) {
      return null;
    }
    for (DateTimeFormatter format : FORMATS) {
      try {
        // Intenta analizar el string con el formato actual
        return LocalDate.parse(fechaStr, format);

      } catch (DateTimeParseException e) {
        // Ignora la excepción y continúa con el siguiente formato
      }
    }
    // Retorna null si no se pudo realizar la conversión
    return null;
  }

  public static InputStreamSource base64ToInputStreamSource(String base64String) throws IllegalArgumentException {

    byte[] decodedBytes = Base64.getDecoder().decode(base64String);
    return () -> new ByteArrayInputStream(decodedBytes);
  }

  public static <T> List<T> getDistinct(List<T> lista) {
    // Hacemos una copia defensiva de la lista original para evitar modificarla
    if (lista != null && !lista.isEmpty()) {
      List<T> copia = new ArrayList<>(lista);
      return copia.stream().distinct().toList();
    }
    return lista;
  }

  public static Path genTempFolderPath(String folderPath, String localizador) {
    UUID uuid = UUID.randomUUID();
    StringBuilder folderName = new StringBuilder();
    folderName.append("temp-").append(localizador).append(uuid);

    // En caso de que folderPath tenga la barra invertida significa que es un path
    // de windows
    // Y hay que formar la ruta de forma diferente
    Path tempFolderPath = null;
    if (folderPath.contains("\\")) {
      if (!folderPath.endsWith("\\")) {
        folderPath += "\\";
      }

      tempFolderPath = Path.of(folderPath + folderName.toString());
    } else {
      tempFolderPath = Path.of(folderPath, folderName.toString());
    }

    logger.info("tempFolderPath: {}", tempFolderPath);
    return tempFolderPath;

  }

  public static String totalTrim(String cadena) {
    String[] palabras = cadena.trim().split("\\s+");
    return String.join(" ", palabras);
  }

  public static boolean verificarVigenciaFechaTasa(LocalDateTime desde, LocalDateTime hasta) {
    LocalDateTime ahora = LocalDateTime.now();
    return (desde.isBefore(ahora) && (hasta == null || hasta.isAfter(ahora)));

  }

  public static String stringBuilder(String... cadenas) {
    StringBuilder builder = new StringBuilder();
    if (cadenas != null) {
      for (String cab : cadenas) {
        if (cab != null) {
          builder.append(cab.trim()).append(" ");
        }
      }
      return builder.toString().trim();
    } else {
      return "";
    }
  }

  public static String toString(List<Long> listaDeLongs) {

    if (listaDeLongs.isEmpty()) {
      return "No Informado";
    } else {

      return listaDeLongs.stream().map(longValue -> longValue != null ? String.valueOf(longValue) : "null")
          .collect(Collectors.joining(", "));

    }
  }

}
