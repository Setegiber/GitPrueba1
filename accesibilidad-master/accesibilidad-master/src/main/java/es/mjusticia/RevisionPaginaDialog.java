
package es.mjusticia;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Diálogo modal para revisar una página: observaciones + estado de cada infracción (por índice). */
public class RevisionPaginaDialog extends JDialog {
    private final ConfiguracionDatos datos;
    private final File dirProyecto;
    private final File pngFile;
    private final String baseName;

    private final JLabel lblTitulo = new JLabel("", SwingConstants.LEFT);
    private final JTextArea taObservaciones = new JTextArea(6, 50);
    private final JTable tabla = new JTable();
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnVolver = new JButton("Volver");
    private final InfraccionesTableModel tableModel = new InfraccionesTableModel();

    private final JLabel lblMensaje = new JLabel(" ");

    public RevisionPaginaDialog(Window owner, ConfiguracionDatos datos, File dirProyecto, File pngFile) {
        super(owner, "Revisión", ModalityType.APPLICATION_MODAL);
        this.datos = datos;
        this.dirProyecto = dirProyecto;
        this.pngFile = pngFile;
        this.baseName = quitarExtension(pngFile.getName());

        setLayout(new BorderLayout(12, 12));
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(owner);

        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        lblTitulo.setText("Revisión: " + baseName);
        add(lblTitulo, BorderLayout.NORTH);

        add(crearCentro(), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);

        cargarDatos();
    }

    private JPanel crearCentro() {
        JPanel centro = new JPanel(new BorderLayout(8, 8));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        if (pngFile.exists()) {
            ImageIcon icon = new ImageIcon(pngFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(240, 160, Image.SCALE_SMOOTH);
            JLabel lblImagen = new JLabel(new ImageIcon(img));
            lblImagen.setBorder(BorderFactory.createTitledBorder("Vista previa"));
            top.add(lblImagen, BorderLayout.WEST);
        }

        JPanel obsPanel = new JPanel(new BorderLayout(4, 4));
        obsPanel.setBorder(BorderFactory.createTitledBorder("Observaciones"));
        taObservaciones.setLineWrap(true);
        taObservaciones.setWrapStyleWord(true);
        obsPanel.add(new JScrollPane(taObservaciones), BorderLayout.CENTER);
        top.add(obsPanel, BorderLayout.CENTER);

        centro.add(top, BorderLayout.NORTH);

        tabla.setModel(tableModel);
        tabla.setRowHeight(22);
        tabla.setFillsViewportHeight(true);

        JComboBox<String> combo = new JComboBox<>(new String[]{
                ObservacionesRevisionIO.REV_AUTOMATICA,
                ObservacionesRevisionIO.REV_MANUAL,
                ObservacionesRevisionIO.REV_FALSO_POSITIVO
        });
        tabla.getColumnModel().getColumn(tableModel.getRevisionColIndex())
                .setCellEditor(new DefaultCellEditor(combo));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Infracciones"));
        centro.add(scroll, BorderLayout.CENTER);
        return centro;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout(8, 8));

        lblMensaje.setText(" ");
        lblMensaje.setForeground(Color.DARK_GRAY);
        lblMensaje.setHorizontalAlignment(SwingConstants.LEFT);
        pie.add(lblMensaje, BorderLayout.WEST);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnGuardar);
        botones.add(btnVolver);
        pie.add(botones, BorderLayout.EAST);

        btnGuardar.addActionListener(e -> guardar());
        btnVolver.addActionListener(e -> dispose());
        return pie;
    }

    private void cargarDatos() {
        // Construir lista con una sola página para reutilizar el extractor
        List<PaginaSeleccionada> seleccion = new ArrayList<>();
        seleccion.add(new PaginaSeleccionada(baseName, pngFile));

        // Extraer infracciones (sin preocuparse por revisión; la aplicamos por índice aquí)
        List<InformePagina> paginas = InformeDatosExtractor.extraer(seleccion, dirProyecto);
        InformePagina pagina = paginas.isEmpty() ? null : paginas.get(0);

        // Observaciones desde base.txt
        File txtObs = new File(dirProyecto, baseName + ".txt");
        String observaciones = ObservacionesRevisionIO.leerObservaciones(txtObs);
        taObservaciones.setText(observaciones);

        // Revisiones por índice (si existen)
        File txtRev = ObservacionesRevisionIO.archivoRevisiones(dirProyecto, baseName);
        Map<Integer, String> mapRev = ObservacionesRevisionIO.leerRevisionesPorIndice(txtRev);

        // Rellenar filas
        List<FilaInf> filas = new ArrayList<>();
        if (pagina != null) {
            int idx = 0;
            for (InformeInfracciones v : pagina.getInfracciones()) {
                idx++; // índices 1..N
                String estado = mapRev.getOrDefault(idx, ObservacionesRevisionIO.REV_AUTOMATICA);
                filas.add(new FilaInf(idx, v, estado));
            }
        }
        tableModel.setFilas(filas);
    }

    private void guardar() {
        if (tabla.isEditing()) {
            try { tabla.getCellEditor().stopCellEditing(); } catch (Exception ignored) {}
        }

        // Recoger observaciones y revisiones actuales por índice
        String obs = taObservaciones.getText();
        Map<Integer, String> revisiones = new LinkedHashMap<>();
        for (FilaInf f : tableModel.getFilas()) {
            revisiones.put(f.indice, f.revision);
        }

        if (!dirProyecto.exists()) { dirProyecto.mkdirs(); }

        // Ficheros destino
        File txtObs = new File(dirProyecto, baseName + ".txt");
        File txtRev = ObservacionesRevisionIO.archivoRevisiones(dirProyecto, baseName);

        // Escribir
        ObservacionesRevisionIO.escribirObservaciones(txtObs, obs);
        ObservacionesRevisionIO.escribirRevisionesPorIndice(txtRev, revisiones);

        mostrarMensaje("Revisión guardada correctamente.", false);
    }

    private void mostrarMensaje(String msg, boolean error) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(error ? Color.RED : new Color(0, 128, 0));
        Timer t = new Timer(3500, e -> limpiarMensaje());
        t.setRepeats(false);
        t.start();
    }

    private void limpiarMensaje() {
        lblMensaje.setText(" ");
        lblMensaje.setForeground(Color.DARK_GRAY);
    }

    /* ================== Modelo de tabla ================== */

    private static class FilaInf {
        final int indice;             // 1..N
        final InformeInfracciones inf;
        String revision;              // editable
        FilaInf(int indice, InformeInfracciones inf, String revision) {
            this.indice = indice; this.inf = inf; this.revision = revision;
        }
    }

    private static class InfraccionesTableModel extends AbstractTableModel {
        private final String[] cols = {"Regla", "Impacto", "Selector", "WCAG", "Mensaje", "Revisión"};
        private final int COL_REV = cols.length - 1;
        private List<FilaInf> filas = new ArrayList<>();

        public void setFilas(List<FilaInf> f) { this.filas = new ArrayList<>(f); fireTableDataChanged(); }
        public List<FilaInf> getFilas() { return filas; }
        public int getRevisionColIndex() { return COL_REV; }

        @Override public int getRowCount() { return filas.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public Class<?> getColumnClass(int col) { return String.class; }
        @Override public boolean isCellEditable(int row, int col) { return col == COL_REV; }

        @Override
        public Object getValueAt(int row, int col) {
            FilaInf f = filas.get(row);
            switch (col) {
                case 0: return f.inf.getReglaId();
                case 1: return f.inf.getImpacto();
                case 2: return primera(f.inf.getSelectores());
                case 3: return traducirNivel(f.inf.getWcagNivel());
                case 4: return TextoTraductor.traducir(f.inf.getMensaje());
                case 5: return f.revision;
                default: return "";
            }
        }

        @Override
        public void setValueAt(Object val, int row, int col) {
            if (col == COL_REV) {
                filas.get(row).revision = val == null ? ObservacionesRevisionIO.REV_AUTOMATICA : val.toString();
                fireTableRowsUpdated(row, row);
            }
        }
    }

    /* ================== utilidades ================== */

    private static String primera(List<String> sel) {
        if (sel == null || sel.isEmpty()) return "-";
        String s = sel.get(0);
        return s == null || s.isBlank() ? "-" : s;
    }
    private static String traducirNivel(String wcagNivel) {
        if (wcagNivel == null) return "-";
        switch (wcagNivel.toLowerCase()) {
            case "wcag2a": return "WCAG 2.1 - Nivel A";
            case "wcag2aa": return "WCAG 2.1 - Nivel AA";
            case "wcag2aaa": return "WCAG 2.1 - Nivel AAA";
            default: return "-";
        }
    }
    private static String quitarExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0 ? name.substring(0, i) : name);
    }
}