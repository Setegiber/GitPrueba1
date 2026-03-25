
package es.mjusticia;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class InformeDatosExtractor {

    public static List<InformePagina> extraer(List<PaginaSeleccionada> seleccion, File dirProyecto) {
        List<InformePagina> paginas = new ArrayList<>();
        for (PaginaSeleccionada sel : seleccion) {
            String base = sel.getBaseName();
            File json = new File(dirProyecto, base + ".json");
            File txtObs = new File(dirProyecto, base + ".txt"); // observaciones
            File txtRev = ObservacionesRevisionIO.archivoRevisiones(dirProyecto, base); // revisiones por índice

            InformePagina pagina = new InformePagina(sel.getNombreEditable(), sel.getArchivoPng(),
                    json.exists() ? json : null,
                    txtObs.exists() ? txtObs : null);

            // Cargar revisiones por índice (si existen)
            Map<Integer, String> revPorIndice = ObservacionesRevisionIO.leerRevisionesPorIndice(txtRev);

            // Parsear JSON de aXe y construir infracciones
            if (pagina.getJsonFile() != null) {
                try (FileInputStream fis = new FileInputStream(pagina.getJsonFile())) {
                    JSONObject root = new JSONObject(new JSONTokener(fis));
                    JSONObject results = root.has("violations") ? root :
                            (root.has("results") ? root.getJSONObject("results") : root);
                    if (results.has("violations")) {
                        JSONArray violations = results.getJSONArray("violations");
                        int fila = 0; // 1..N para mapear revisiones
                        for (int i = 0; i < violations.length(); i++) {
                            JSONObject viol = violations.getJSONObject(i);
                            String reglaId = viol.optString("id", "");
                            String descripcion = viol.optString("description", "");
                            String ayuda = viol.optString("help", "");
                            String helpUrl = viol.optString("helpUrl", "");
                            String wcagNivel = extraerWcagNivel(viol.optJSONArray("tags"));

                            JSONArray nodes = viol.optJSONArray("nodes");
                            if (nodes != null) {
                                for (int j = 0; j < nodes.length(); j++) {
                                    JSONObject node = nodes.getJSONObject(j);
                                    String impacto = node.optString("impact", viol.optString("impact", "minor"));
                                    String mensaje = node.optString("failureSummary", "");
                                    if (mensaje.isBlank()) {
                                        JSONArray anyArr = node.optJSONArray("any");
                                        if (anyArr != null && anyArr.length() > 0) {
                                            mensaje = anyArr.getJSONObject(0).optString("message", ayuda);
                                        } else {
                                            mensaje = ayuda;
                                        }
                                    }
                                    List<String> selectores = new ArrayList<>();
                                    JSONArray targets = node.optJSONArray("target");
                                    if (targets != null) {
                                        for (int t = 0; t < targets.length(); t++) {
                                            selectores.add(targets.getString(t));
                                        }
                                    }

                                    InformeInfracciones inf = new InformeInfracciones(
                                            reglaId, descripcion, ayuda, helpUrl,
                                            impacto, mensaje, selectores, wcagNivel
                                    );

                                    // Aplicar revisión por índice de fila
                                    fila++;
                                    String estado = revPorIndice.getOrDefault(fila, ObservacionesRevisionIO.REV_AUTOMATICA);
                                    inf.setRevision(estado);

                                    pagina.getInfracciones().add(inf);
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error leyendo JSON " + json.getAbsolutePath() + ": " + ex.getMessage());
                }
            }

            paginas.add(pagina);
        }
        return paginas;
    }

    private static String extraerWcagNivel(JSONArray tags) {
        if (tags == null) return "";
        Set<String> set = new HashSet<>();
        for (int i = 0; i < tags.length(); i++) {
            set.add(tags.getString(i).toLowerCase(Locale.ROOT));
        }
        if (set.contains("wcag2aa")) return "wcag2aa";
        if (set.contains("wcag2a"))  return "wcag2a";
        if (set.contains("wcag2aaa"))return "wcag2aaa";
        return "";
    }

    public static Map<String, Long> resumenImpactos(List<InformePagina> paginas) {
        Map<String, Long> resumen = new LinkedHashMap<>();
        resumen.put("critical", paginas.stream().mapToLong(p -> p.contarPorImpacto("critical")).sum());
        resumen.put("serious",  paginas.stream().mapToLong(p -> p.contarPorImpacto("serious")).sum());
        resumen.put("moderate", paginas.stream().mapToLong(p -> p.contarPorImpacto("moderate")).sum());
        resumen.put("minor",    paginas.stream().mapToLong(p -> p.contarPorImpacto("minor")).sum());
        return resumen;
    }

    public static Map<String, Long> agrupadoPorRegla(List<InformePagina> paginas) {
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (InformePagina p : paginas) {
            for (InformeInfracciones v : p.getInfracciones()) {
                mapa.put(v.getReglaId(), mapa.getOrDefault(v.getReglaId(), 0L) + 1);
            }
        }
        return mapa;
    }
}
