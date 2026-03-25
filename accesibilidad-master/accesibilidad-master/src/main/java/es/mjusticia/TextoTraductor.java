package es.mjusticia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextoTraductor {
    private static final Map<String, String> diccionario = new LinkedHashMap<>();

    public static void cargarDiccionario(String rutaFichero) {
        diccionario.clear();
        if (rutaFichero == null || rutaFichero.isBlank()) {
            System.err.println("No se especificó fichero de traducciones.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(rutaFichero))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue; // Ignorar líneas en blanco
                }

                int idx = linea.indexOf('=');
                if (idx == -1) {
                    System.err.println("Línea inválida (sin '='): " + linea);
                    continue;
                }

                String clave = linea.substring(0, idx).trim();
                String valor = linea.substring(idx + 1).trim();

                if (!clave.isEmpty() && !valor.isEmpty()) {
                    diccionario.put(clave, valor);
                    System.out.println("[Diccionario] " + clave + " => " + valor);
                }
            }
            System.out.println("Diccionario cargado desde: " + rutaFichero + " (" + diccionario.size() + " entradas)");
        } catch (IOException e) {
            System.err.println("Error cargando traducciones: " + e.getMessage());
        }
    }

    public static String traducir(String textoOriginal) {
        if (textoOriginal == null || textoOriginal.isBlank() || diccionario.isEmpty()) {
            return textoOriginal;
        }

        String resultado = textoOriginal;
        for (Map.Entry<String, String> entry : diccionario.entrySet()) {
            String clave = entry.getKey();
            String traduccion = entry.getValue();

            if (resultado.contains(clave)) {
                System.out.println("[Reemplazo] '" + clave + "' -> '" + traduccion + "'");
                resultado = resultado.replace(clave, traduccion); // Reemplazo exacto
            }
        }
        return resultado;
    }
}


