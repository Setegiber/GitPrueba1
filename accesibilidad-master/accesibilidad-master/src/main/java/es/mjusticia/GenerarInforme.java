
package es.mjusticia;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Pantalla "Generar informe" con miniaturas, selección y acceso a revisión por página. */
public class GenerarInforme extends JPanel {
    private final ConfiguracionDatos datos;
    private final Runnable onExitToMain;
    private final SeleccionListener onContinuar;

    private final JLabel lblTitulo = new JLabel("Generar informe", SwingConstants.LEFT);
    private final JLabel lblMensaje = new JLabel(" ");
    private final JPanel panelListado = new JPanel(new GridLayout(0, 3, 12, 12));
    private final JButton btnContinuar = new JButton("Continuar");
    private final JButton btnVolver = new JButton("Volver al menú principal");

    private final List<ItemSeleccion> itemsSeleccionados = new ArrayList<>();

    private File dirProyecto;

    public GenerarInforme(ConfiguracionDatos datos, Runnable onExitToMain, SeleccionListener onContinuar) {
        this.datos = Objects.requireNonNull(datos);
        this.onExitToMain = Objects.requireNonNull(onExitToMain);
        this.onContinuar = Objects.requireNonNull(onContinuar);

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        add(lblTitulo, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(panelListado);
        add(scroll, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(8, 8));
        lblMensaje.setForeground(Color.DARK_GRAY);
        pie.add(lblMensaje, BorderLayout.WEST);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnContinuar);
        botones.add(btnVolver);
        pie.add(botones, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);

        btnContinuar.addActionListener(e -> continuarConSeleccion());
        btnVolver.addActionListener(e -> onExitToMain.run());
    }

    public void prepararAntesDeMostrar() {
        panelListado.removeAll();
        itemsSeleccionados.clear();
        lblMensaje.setText(" ");

        dirProyecto = new File(System.getProperty("user.dir"), datos.getProyecto());
        if (!dirProyecto.exists() || !dirProyecto.isDirectory()) {
            mostrarMensaje("El directorio del proyecto no existe.", true);
            revalidate(); repaint();
            return;
        }

        File[] archivos = dirProyecto.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (archivos == null || archivos.length == 0) {
            mostrarMensaje("No hay capturas disponibles.", true);
        } else {
            mostrarMensaje("Selecciona las capturas, pulsa 'Revisión' si quieres revisar, y después 'Continuar'.", false);
            for (File archivo : archivos) {
                JPanel itemPanel = crearItemPanel(archivo);
                panelListado.add(itemPanel);
            }
        }
        revalidate();
        repaint();
    }

    private JPanel crearItemPanel(File archivo) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        ImageIcon icon = new ImageIcon(archivo.getAbsolutePath());
        Image img = icon.getImage().getScaledInstance(120, 90, Image.SCALE_SMOOTH);
        JLabel lblImagen = new JLabel(new ImageIcon(img));
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblImagen, BorderLayout.CENTER);

        // Pie con checkbox + botón "Revisión"
        String baseName = archivo.getName().replaceFirst("\\.png$", "");
        JCheckBox checkBox = new JCheckBox(baseName);
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnRevision = new JButton("Revisión");
        btnRevision.addActionListener(e -> abrirRevision(archivo));

        JPanel south = new JPanel(new GridLayout(2, 1, 4, 4));
        south.add(checkBox);
        south.add(btnRevision);
        panel.add(south, BorderLayout.SOUTH);

        itemsSeleccionados.add(new ItemSeleccion(archivo, checkBox));
        return panel;
    }

    private void abrirRevision(File pngFile) {
        Window w = SwingUtilities.getWindowAncestor(this);
        RevisionPaginaDialog dlg = new RevisionPaginaDialog(w, datos, dirProyecto, pngFile);
        dlg.setVisible(true);
        // Al cerrar el diálogo, no hay nada más que hacer aquí; se ha guardado en su TXT.
    }

    private void continuarConSeleccion() {
        List<File> seleccionados = new ArrayList<>();
        for (ItemSeleccion item : itemsSeleccionados) {
            if (item.checkBox.isSelected()) {
                seleccionados.add(item.archivo);
            }
        }
        if (seleccionados.isEmpty()) {
            mostrarMensaje("Debes seleccionar al menos una captura.", true);
            return;
        }
        onContinuar.onSeleccion(seleccionados);
    }

    private void mostrarMensaje(String msg, boolean error) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(error ? Color.RED : new Color(0, 128, 0));
    }

    private static class ItemSeleccion {
        File archivo;
        JCheckBox checkBox;
        ItemSeleccion(File archivo, JCheckBox checkBox) { this.archivo = archivo; this.checkBox = checkBox; }
    }

    @FunctionalInterface
       public interface SeleccionListener {
        void onSeleccion(List<File> seleccionados);
    }
}