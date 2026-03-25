
package es.mjusticia;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/** Pantalla para ordenar/renombrar páginas y generar el PDF. */
public class PrepararInforme extends JPanel {
    private final Runnable onVolver;
    private final String proyecto;
    private final ConfiguracionDatos datos;
    private final PaginasTableModel tableModel = new PaginasTableModel();
    private final JTable tabla = new JTable(tableModel);
    private final JTextField tfNombreInforme = new JTextField(30);
    private final JLabel lblMensaje = new JLabel(" ");
    private final JButton btnSubir = new JButton("Subir");
    private final JButton btnBajar = new JButton("Bajar");
    private final JButton btnGenerar = new JButton("Generar PDF");
    private final JButton btnVolver = new JButton("Volver");
    private final String nombrePorDefecto;

    public PrepararInforme(List<File> paginasSeleccionadas, String proyecto, ConfiguracionDatos datos, Runnable onVolver) {
        this.proyecto = proyecto;
        this.datos = datos;
        this.onVolver = onVolver;
        this.nombrePorDefecto = "Informe_accesibilidad_" + proyecto + ".pdf";

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titulo = new JLabel("Preparar Informe", SwingConstants.LEFT);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        add(titulo, BorderLayout.NORTH);

        add(crearCentro(), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);

        cargarPaginas(paginasSeleccionadas);
    }

    private JPanel crearCentro() {
        JPanel centro = new JPanel(new BorderLayout(8, 8));
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(22);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tabla.setSurrendersFocusOnKeystroke(true);
        JScrollPane scroll = new JScrollPane(tabla);
        centro.add(scroll, BorderLayout.CENTER);

        JPanel controles = new JPanel(new GridLayout(2, 1, 4, 4));
        controles.add(btnSubir);
        controles.add(btnBajar);
        centro.add(controles, BorderLayout.EAST);

        JPanel nombreInformePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nombreInformePanel.add(new JLabel("Nombre del informe:"));
        tfNombreInforme.setText(nombrePorDefecto);
        nombreInformePanel.add(tfNombreInforme);
        centro.add(nombreInformePanel, BorderLayout.SOUTH);

        btnSubir.addActionListener(e -> moverFila(-1));
        btnBajar.addActionListener(e -> moverFila(1));
        return centro;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout(8, 8));
        lblMensaje.setForeground(Color.DARK_GRAY);
        pie.add(lblMensaje, BorderLayout.WEST);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnGenerar);
        botones.add(btnVolver);
        pie.add(botones, BorderLayout.EAST);
        btnGenerar.addActionListener(e -> generarInformePdf());
        btnVolver.addActionListener(e -> onVolver.run());
        return pie;
    }

    private void cargarPaginas(List<File> paginasSeleccionadas) {
        List<PaginaSeleccionada> lista = new ArrayList<>();
        for (File f : paginasSeleccionadas) {
            String base = quitarExtension(f.getName());
            lista.add(new PaginaSeleccionada(base, f));
        }
        tableModel.setDatos(lista);
    }

    private void moverFila(int direccion) {
        int index = tabla.getSelectedRow();
        if (index == -1) return;
        int nuevo = index + direccion;
        if (nuevo < 0 || nuevo >= tableModel.getRowCount()) return;
        tableModel.mover(index, nuevo);
        tabla.getSelectionModel().setSelectionInterval(nuevo, nuevo);
    }

    private void generarInformePdf() {
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }
        String nombreInforme = tfNombreInforme.getText().trim();
        if (nombreInforme.isEmpty()) {
            mostrarMensaje("El nombre del informe es obligatorio.", true);
            return;
        }
        if (!nombreInforme.toLowerCase().endsWith(".pdf")) {
            nombreInforme = nombreInforme + ".pdf";
        }

        List<PaginaSeleccionada> seleccion = tableModel.getDatos();
        File dirProyecto = new File(System.getProperty("user.dir"), proyecto);
        if (!dirProyecto.exists()) {
            if (!dirProyecto.mkdirs()) {
                mostrarMensaje("No se pudo crear el directorio del proyecto.", true);
                return;
            }
        }

        try {
            // Cargar diccionario (si se indicó)
            TextoTraductor.cargarDiccionario(datos.getTraducciones());

            // 1) Extraer datos de JSON → objetos
            List<InformePagina> paginas = InformeDatosExtractor.extraer(seleccion, dirProyecto);

            // 2) Resúmenes globales
            var resumenImpactos = InformeDatosExtractor.resumenImpactos(paginas);
            var agrupadoPorRegla = InformeDatosExtractor.agrupadoPorRegla(paginas);

            // 3) Generar PDF (pasamos la nueva axeVersion)
            File destinoPdf = new File(dirProyecto, nombreInforme);
            new InformePDFGenerator().generar(
                    nombreInforme,
                    proyecto,
                    datos.getPaginaInicio(),
                    datos.getNavegador(),
                    datos.getAxeVersion(), // NUEVO parámetro
                    paginas,
                    resumenImpactos,
                    agrupadoPorRegla,
                    destinoPdf
            );
            mostrarMensaje("Informe generado: " + destinoPdf.getAbsolutePath(), false);
        } catch (Exception ex) {
            mostrarMensaje("Error generando el informe: " + ex.getMessage(), true);
        }
    }

    private void mostrarMensaje(String msg, boolean error) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(error ? Color.RED : new Color(0, 128, 0));
    }

    // ==== utilidades de nombre/archivo ====
    private static String quitarExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0 ? name.substring(0, i) : name);
    }

    /** Sanea el nombre base para el sistema de ficheros: quita caracteres problemáticos y espacios extremos. */
    private static String sanearBase(String s) {
        if (s == null) return "";
        String v = s.trim();
        v = v.replaceAll("\\s+", "_");
        v = v.replaceAll("[\\\\/:*?\"<>|]", "");
        return v;
    }

    // ==== TableModel con edición inline que RENOMBRA archivos ====
    private class PaginasTableModel extends AbstractTableModel {
        private final String[] columnas = {"Página"};
        private List<PaginaSeleccionada> datos = new ArrayList<>();

        public void setDatos(List<PaginaSeleccionada> d) {
            this.datos = new ArrayList<>(d);
            fireTableDataChanged();
        }
        public List<PaginaSeleccionada> getDatos() {
            return new ArrayList<>(datos);
        }
        public PaginaSeleccionada getAt(int row) {
            return datos.get(row);
        }
        public void mover(int from, int to) {
            PaginaSeleccionada p = datos.remove(from);
            datos.add(to, p);
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return datos.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int column) { return columnas[column]; }
        @Override public Class<?> getColumnClass(int columnIndex) { return String.class; }
        @Override public boolean isCellEditable(int rowIndex, int columnIndex) { return true; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            PaginaSeleccionada p = datos.get(rowIndex);
            String visible = (p.getNombreEditable() == null || p.getNombreEditable().isBlank())
                    ? p.getBaseName()
                    : p.getNombreEditable();
            return visible;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            PaginaSeleccionada p = datos.get(rowIndex);
            String nuevoBase = aValue == null ? "" : aValue.toString();
            nuevoBase = sanearBase(nuevoBase);
            String baseActual = p.getBaseName();
            if (nuevoBase.isBlank() || nuevoBase.equals(baseActual)) {
                p.setNombreEditable(nuevoBase.isBlank() ? baseActual : nuevoBase);
                fireTableRowsUpdated(rowIndex, rowIndex);
                return;
            }

            File antiguoPng = p.getArchivoPng();
            File dir = antiguoPng.getParentFile();
            File nuevoPng = new File(dir, nuevoBase + ".png");
            File antiguoJson = new File(dir, baseActual + ".json");
            File antiguoTxt = new File(dir, baseActual + ".txt");
            File antiguoRev = ObservacionesRevisionIO.archivoRevisiones(dir, baseActual);
            File nuevoJson = new File(dir, nuevoBase + ".json");
            File nuevoTxt = new File(dir, nuevoBase + ".txt");
            File nuevoRev = ObservacionesRevisionIO.archivoRevisiones(dir, nuevoBase);

            if (nuevoPng.exists() || nuevoJson.exists() || nuevoTxt.exists() || nuevoRev.exists()) {
                mostrarMensaje("Ya existen ficheros con ese nombre. Elige otro.", true);
                fireTableRowsUpdated(rowIndex, rowIndex);
                return;
            }

            List<RenameOp> realizados = new ArrayList<>();
            try {
                if (antiguoJson.exists()) {
                    mover(antiguoJson.toPath(), nuevoJson.toPath());
                    realizados.add(new RenameOp(nuevoJson.toPath(), antiguoJson.toPath()));
                }
                if (antiguoTxt.exists()) {
                    mover(antiguoTxt.toPath(), nuevoTxt.toPath());
                    realizados.add(new RenameOp(nuevoTxt.toPath(), antiguoTxt.toPath()));
                }
                if (antiguoRev.exists()) {
                    mover(antiguoRev.toPath(), nuevoRev.toPath());
                    realizados.add(new RenameOp(nuevoRev.toPath(), antiguoRev.toPath()));
                }
                mover(antiguoPng.toPath(), nuevoPng.toPath());
                realizados.add(new RenameOp(nuevoPng.toPath(), antiguoPng.toPath()));

                p.setNombreEditable(nuevoBase);
                p.setArchivoPng(nuevoPng);
                mostrarMensaje("Renombrado a " + nuevoBase + " (PNG/JSON/TXT/REVISIÓN)", false);
                fireTableRowsUpdated(rowIndex, rowIndex);
            } catch (Exception ex) {
                for (int i = realizados.size() - 1; i >= 0; i--) {
                    RenameOp op = realizados.get(i);
                    try { mover(op.from, op.to); } catch (Exception ignored) {}
                }
                mostrarMensaje("No se pudo renombrar: " + ex.getMessage(), true);
                fireTableRowsUpdated(rowIndex, rowIndex);
            }
        }

        private void mover(Path src, Path dst) throws IOException {
            Files.move(src, dst, StandardCopyOption.ATOMIC_MOVE);
        }

        private class RenameOp {
            final Path from; // estado tras mover (nuevo)
            final Path to;   // ruta original para deshacer            final Path to;   // ruta original para deshacer
            RenameOp(Path from, Path to) { this.from = from; this.to = to; }
        }
    }
}