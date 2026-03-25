
package es.mjusticia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InformePagina {
    private final String nombreInformePagina;
    private final File imagenPng;
    private final File jsonFile;
    private final File observacionesFile; // NUEVO

    private final List<InformeInfracciones> infracciones = new ArrayList<>();

    public InformePagina(String nombreInformePagina, File imagenPng, File jsonFile, File observacionesFile) {
        this.nombreInformePagina = nombreInformePagina;
        this.imagenPng = imagenPng;
        this.jsonFile = jsonFile;
        this.observacionesFile = observacionesFile;
    }

    public String getNombreInformePagina() { return nombreInformePagina; }
    public File getImagenPng() { return imagenPng; }
    public File getJsonFile() { return jsonFile; }
    public File getObservacionesFile() { return observacionesFile; }

    public List<InformeInfracciones> getInfracciones() { return infracciones; }

    public int totalInfracciones() { return infracciones.size(); }

    public long contarPorImpacto(String impacto) {
        return infracciones.stream().filter(v -> impacto.equalsIgnoreCase(v.getImpacto())).count();
    }
}