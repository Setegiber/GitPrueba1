
package es.mjusticia;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Utilidad de observaciones y revisiones. Formato de revisiones: por índice de fila (1..N). */
public class ObservacionesRevisionIO {

    public static final String REV_AUTOMATICA     = "Automática";
    public static final String REV_MANUAL         = "Manual";
    public static final String REV_FALSO_POSITIVO = "Falso Positivo";

    public static final String SUFIJO_REVISIONES = "_revisiones.txt";

    /** Devuelve el fichero de revisiones para un baseName en un directorio. */
    public static File archivoRevisiones(File dir, String baseName) {
        return new File(dir, baseName + SUFIJO_REVISIONES);
    }

    /* ====================== OBSERVACIONES (.txt) ====================== */

    public static String leerObservaciones(File txt) {
        if (txt == null || !txt.exists()) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(txt), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error leyendo observaciones: " + e.getMessage());
        }
        return sb.toString().trim();
    }

    public static void escribirObservaciones(File txt, String observaciones) {
        asegurarPadre(txt);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(txt), StandardCharsets.UTF_8))) {
            String obs = observaciones == null ? "" : observaciones.trim();
            bw.write(obs);
            bw.write("\n");
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir observaciones: " + e.getMessage(), e);
        }
    }

    /* ====================== REVISIONES POR ÍNDICE ====================== */

    /** Lee un mapa índice->estado desde *_revisiones.txt (formato: "1=Automática"). */
    public static Map<Integer, String> leerRevisionesPorIndice(File revTxt) {
        Map<Integer, String> map = new LinkedHashMap<>();
        if (revTxt == null || !revTxt.exists()) return map;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(revTxt), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String l = linea.trim();
                if (l.isEmpty()) continue;
                int idxEq = l.indexOf('=');
                if (idxEq <= 0) continue;
                String idxStr = l.substring(0, idxEq).trim();
                String estado = l.substring(idxEq + 1).trim();
                try {
                    int idx = Integer.parseInt(idxStr);
                    map.put(idx, estadoValido(estado));
                } catch (NumberFormatException ignored) {
                    // ignora líneas inválidas
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo revisiones: " + e.getMessage());
        }
        return map;
    }

    /** Escribe un mapa índice->estado en *_revisiones.txt (se reescribe completo). */
    public static void escribirRevisionesPorIndice(File revTxt, Map<Integer, String> revisiones) {
        asegurarPadre(revTxt);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(revTxt), StandardCharsets.UTF_8))) {
            if (revisiones == null || revisiones.isEmpty()) {
                bw.write(""); // crear/limpiar
                return;
            }
            for (Map.Entry<Integer, String> e : revisiones.entrySet()) {
                bw.write(e.getKey() + "=" + estadoValido(e.getValue()));
                bw.write("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir revisiones: " + e.getMessage(), e);
        }
    }

    /** Genera por defecto 1..N=Automática. */
    public static void generarPorDefecto(File revTxt, int filas) {
        LinkedHashMap<Integer, String> def = new LinkedHashMap<>();
        for (int i = 1; i <= filas; i++) {
            def.put(i, REV_AUTOMATICA);
        }
        escribirRevisionesPorIndice(revTxt, def);
    }

    private static String estadoValido(String s) {
        if (s == null) return REV_AUTOMATICA;
        String v = s.trim();
        if (v.equalsIgnoreCase(REV_AUTOMATICA))     return REV_AUTOMATICA;
        if (v.equalsIgnoreCase(REV_MANUAL))         return REV_MANUAL;
        if (v.equalsIgnoreCase(REV_FALSO_POSITIVO)) return REV_FALSO_POSITIVO;
        // Permite "Automático" del mensaje del usuario, lo mapeamos a "Automática" para la UI
        if (v.equalsIgnoreCase("Automático"))       return REV_AUTOMATICA;
        return REV_AUTOMATICA;
    }

    private static void asegurarPadre(File f) {
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
    }
}