
package es.mjusticia;

import java.io.File;

/** Modelo para una página (captura PNG) seleccionada para el informe. */
public class PaginaSeleccionada {

    private String nombreEditable; // nombre que aparecerá en el informe (editable)
    private File archivoPng;       // ruta del PNG

    public PaginaSeleccionada(String nombreEditable, File archivoPng) {
        this.nombreEditable = nombreEditable;
        this.archivoPng = archivoPng;
    }

    public String getNombreEditable() { return nombreEditable; }
    public void setNombreEditable(String nombreEditable) { this.nombreEditable = nombreEditable; }

    public File getArchivoPng() { return archivoPng; }
    public void setArchivoPng(File archivoPng) { this.archivoPng = archivoPng; }

    /** Base name sin extensión (para localizar JSON/TXT homólogos). */
    public String getBaseName() {
        String name = archivoPng.getName();
        int i = name.lastIndexOf('.');
        return (i > 0 ? name.substring(0, i) : name);
    }

    @Override
    public String toString() {
        return nombreEditable == null || nombreEditable.isBlank() ? getBaseName() : nombreEditable;
    }
}
