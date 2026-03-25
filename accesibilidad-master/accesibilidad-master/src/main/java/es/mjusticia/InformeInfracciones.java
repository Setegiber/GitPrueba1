
package es.mjusticia;

import java.util.List;

/** Una infracción de accesibilidad reportada por aXe. */
public class InformeInfracciones {
    private String reglaId;
    private String descripcion;
    private String ayuda;
    private String helpUrl;
    private String impacto;
    private String mensaje;
    private List<String> selectores;
    private String wcagNivel;

    /** Nueva columna: estado de revisión (Automática | Manual | Falso Positivo) */
    private String revision = ObservacionesRevisionIO.REV_AUTOMATICA;

    public InformeInfracciones(String reglaId, String descripcion, String ayuda, String helpUrl,
                               String impacto, String mensaje, List<String> selectores, String wcagNivel) {
        this.reglaId = reglaId;
        this.descripcion = descripcion;
        this.ayuda = ayuda;
        this.helpUrl = helpUrl;
        this.impacto = impacto;
        this.mensaje = mensaje;
        this.selectores = selectores;
        this.wcagNivel = wcagNivel;
    }

    public String getReglaId() { return reglaId; }
    public String getDescripcion() { return descripcion; }
    public String getAyuda() { return ayuda; }
    public String getHelpUrl() { return helpUrl; }
    public String getImpacto() { return impacto; }
    public String getMensaje() { return mensaje; }
    public List<String> getSelectores() { return selectores; }
    public String getWcagNivel() { return wcagNivel; }

    public String getRevision() { return revision; }
    public void setRevision(String revision) {
        if (revision == null || revision.isBlank()) return;
        this.revision = revision;
    }
}