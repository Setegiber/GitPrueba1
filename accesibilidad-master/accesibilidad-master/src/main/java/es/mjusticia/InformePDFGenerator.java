
package es.mjusticia;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class InformePDFGenerator {

    public void generar(String nombreDocumento,
                        String proyecto,
                        String urlInicial,
                        String navegador,
                        String axeVersion,
                        List<InformePagina> paginas,
                        Map<String, Long> resumenImpactos,
                        Map<String, Long> agrupadoPorRegla,
                        File destinoPdf) {
        Document doc = new Document(PageSize.A4, 50, 50, 100, 60);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(destinoPdf));
            HeaderFooterEvento evento = new HeaderFooterEvento(
                    nombreDocumento,
                    HeaderFooterEvento.resolverImagenHeaderPath()
            );
            writer.setPageEvent(evento);
            doc.open();

            Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            Font fH2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.BLACK);
            Font fNormal = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font fNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font fOk = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(0, 128, 0));
            Font fWarn = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.RED);
            Font fLink = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLUE);

            // Portada
            Paragraph titulo = new Paragraph("Informe de Accesibilidad Web", fTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);
            doc.add(lineaRotuloValor("Proyecto:", proyecto, fNegrita, fNormal));
            doc.add(lineaRotuloValor("URL inicial:", urlInicial, fNegrita, fNormal));
            doc.add(lineaRotuloValor("Navegador:", navegador, fNegrita, fNormal));
            doc.add(lineaRotuloValor("Versión aXe:", (axeVersion == null || axeVersion.isBlank() ? "-" : axeVersion), fNegrita, fNormal));
            doc.add(Chunk.NEWLINE);

            // Resumen Ejecutivo
            doc.add(new Paragraph("Resumen Ejecutivo", fH2));
            long totalInf = paginas.stream().mapToLong(InformePagina::totalInfracciones).sum();
            doc.add(new Paragraph("Páginas analizadas: " + paginas.size(), fNormal));
            doc.add(Chunk.NEWLINE);

            // Listado de páginas
            PdfPTable tablaPaginas = new PdfPTable(1);
            tablaPaginas.setWidthPercentage(100);
            addHeaderCell(tablaPaginas, "Listado de páginas");
            for (InformePagina p : paginas) {
                String nombre = p.getNombreInformePagina().replaceFirst("\\.png$", "");
                Font colorFont = p.totalInfracciones() == 0
                        ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(0, 128, 0))
                        : FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.RED);
                PdfPCell cell = new PdfPCell(new Phrase(nombre, colorFont));
                cell.setPadding(5f);
                tablaPaginas.addCell(cell);
            }
            doc.add(tablaPaginas);
            doc.add(Chunk.NEWLINE);

            // Resumen global
            doc.add(new Paragraph("Total de infracciones: " + totalInf, totalInf == 0 ? fOk : fWarn));
            doc.add(new Paragraph(String.format(
                    "Críticas: %d, Graves: %d, Moderadas: %d, Menores: %d",
                    resumenImpactos.getOrDefault("critical", 0L),
                    resumenImpactos.getOrDefault("serious", 0L),
                    resumenImpactos.getOrDefault("moderate", 0L),
                    resumenImpactos.getOrDefault("minor", 0L)
            ), fNormal));
            doc.add(Chunk.NEWLINE);

            // Resultados globales por regla
            doc.add(new Paragraph("Resultados globales por regla", fH2));
            doc.add(Chunk.NEWLINE);

            PdfPTable tablaReglas = new PdfPTable(3);
            tablaReglas.setWidthPercentage(100);
            tablaReglas.setWidths(new float[]{40f, 20f, 40f});
            addHeaderCell(tablaReglas, "Regla");
            addHeaderCell(tablaReglas, "Ocurrencias");
            addHeaderCell(tablaReglas, "Nivel WCAG (si aplica)");
            tablaReglas.setHeaderRows(1);
            for (Map.Entry<String, Long> e : agrupadoPorRegla.entrySet()) {
                addCell(tablaReglas, e.getKey());
                addCell(tablaReglas, String.valueOf(e.getValue()));
                addCell(tablaReglas, nivelWcagDePrimera(paginas, e.getKey()));
            }
            doc.add(tablaReglas);
            doc.add(Chunk.NEWLINE);

            // Detalle por página
            Paragraph detalleHeader = new Paragraph("Detalle por página", fH2);
            detalleHeader.setSpacingBefore(10f);
            doc.add(detalleHeader);

            for (int idx = 0; idx < paginas.size(); idx++) {
                InformePagina p = paginas.get(idx);
                if (idx > 0) {
                    doc.newPage();
                }

                Paragraph pTitle = new Paragraph(p.getNombreInformePagina().replaceFirst("\\.png$", ""), fH2);
                pTitle.setSpacingBefore(8f);
                doc.add(pTitle);

                // Observaciones (si existen) -> SOLO el .txt (sin revisiones)
                if (p.getObservacionesFile() != null && p.getObservacionesFile().exists()) {
                    try (Scanner sc = new Scanner(p.getObservacionesFile())) {
                        StringBuilder obs = new StringBuilder();
                        while (sc.hasNextLine()) { obs.append(sc.nextLine()).append("\n"); }
                        Paragraph obsTitle = new Paragraph("Observaciones:", fH2);
                        obsTitle.setSpacingBefore(6f);
                        doc.add(obsTitle);
                        doc.add(new Paragraph(obs.toString(), fNormal));
                        doc.add(Chunk.NEWLINE);
                    } catch (Exception ignored) { }
                }

                // Imagen
                if (p.getImagenPng() != null && p.getImagenPng().exists()) {
                    Image img = Image.getInstance(p.getImagenPng().getAbsolutePath());
                    img.scaleToFit(300, 180);
                    img.setAlignment(Image.ALIGN_LEFT);
                    doc.add(img);
                }

                // Resumen infracciones de esa página
                Paragraph resumenPag = new Paragraph(String.format(
                        "Infracciones: %d (Críticas: %d, Graves: %d, Moderadas: %d, Menores: %d)",
                        p.totalInfracciones(),
                        p.contarPorImpacto("critical"),
                        p.contarPorImpacto("serious"),
                        p.contarPorImpacto("moderate"),
                        p.contarPorImpacto("minor")
                ), fNormal);
                resumenPag.setSpacingBefore(6f);
                doc.add(resumenPag);
                doc.add(Chunk.NEWLINE);

                // === Tabla de infracciones (7 columnas) ===
                PdfPTable t = new PdfPTable(7);
                t.setWidthPercentage(100);

                // Anchos: Selector (24f) con wrap; Revisión más ancha (16f) y sin wrap
                // ANTES: {16f, 14f, 24f, 12f, 22f, 12f, 10f}
                // AHORA:
                t.setWidths(new float[]{16f, 14f, 24f, 12f, 22f, 12f, 16f});

                // Encabezados: "Revisión" sin wrap para que el título quede en una sola línea
                addHeaderCell(t, "Regla");
                addHeaderCell(t, "Impacto");
                addHeaderCell(t, "Selector");          // CON wrap (por defecto)
                addHeaderCell(t, "WCAG");
                addHeaderCell(t, "Mensaje");
                addHeaderCell(t, "Más info");
                addHeaderCellNoWrap(t, "Revisión");     // SIN wrap
                t.setHeaderRows(1);

                for (InformeInfracciones v : p.getInfracciones()) {
                    // Selector: COMPLETO y con wrap si hace falta (sin puntos suspensivos)
                    String selectorPrimero = v.getSelectores().isEmpty()
                            ? "-"
                            : v.getSelectores().get(0);

                    addCell(t, v.getReglaId());
                    addCellImpacto(t, v.getImpacto(), v.getImpacto());
                    addCell(t, selectorPrimero);                    // wrap por defecto
                    addCell(t, traducirNivel(v.getWcagNivel()));
                    addCell(t, TextoTraductor.traducir(v.getMensaje()));

                    String url = sanitizeUrl(v.getHelpUrl());
                    PdfPCell cLink;
                    if (url == null || url.isBlank()) {
                        cLink = new PdfPCell(new Phrase("-", FontFactory.getFont(FontFactory.HELVETICA, 9)));
                    } else {
                        Chunk linkChunk = new Chunk("Abrir", FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLUE));
                        linkChunk.setAnchor(url);
                        cLink = new PdfPCell(new Phrase(linkChunk));
                    }
                    cLink.setPadding(5f);
                    t.addCell(cLink);

                    // Revisión: SIN wrap para asegurar "Automática" siempre en una línea
                    addCellNoWrap(t, v.getRevision());
                }

                doc.add(t);
            }

            // Recomendaciones
            doc.newPage();
            doc.add(new Paragraph("Recomendaciones generales", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.BLACK)));
            com.lowagie.text.List lista = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
            lista.add(new ListItem("Ajustar contrastes para cumplir WCAG 1.4.3 (nivel AA).", FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK)));
            lista.add(new ListItem("Asegurar texto alternativo en todas las imágenes (WCAG 1.1.1, nivel A).", FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK)));
            lista.add(new ListItem("Revisar estructura semántica (encabezados y landmarks).", FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK)));
            lista.add(new ListItem("Garantizar texto discernible en enlaces y botones (WCAG 2.4.4).", FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK)));
            doc.add(lista);

        } catch (Exception ex) {
            throw new RuntimeException("Error generando PDF: " + ex.getMessage(), ex);
        } finally {
            doc.close();
        }
    }

    private static Paragraph lineaRotuloValor(String rotulo, String valor, Font fRotulo, Font fValor) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(rotulo + " ", fRotulo));
        p.add(new Chunk(valor != null ? valor : "-", fValor));
        return p;
    }

    private static void addHeaderCell(PdfPTable t, String txt) {
        PdfPCell c = new PdfPCell(new Phrase(txt, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        c.setBackgroundColor(new Color(230, 230, 230));
        c.setPadding(5f);
        t.addCell(c);
    }

    // Header sin salto de línea
    private static void addHeaderCellNoWrap(PdfPTable t, String txt) {
        PdfPCell c = new PdfPCell(new Phrase(txt, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        c.setBackgroundColor(new Color(230, 230, 230));
        c.setPadding(5f);
        c.setNoWrap(true);
        t.addCell(c);
    }

    private static void addCell(PdfPTable t, String txt) {
        PdfPCell c = new PdfPCell(new Phrase(txt, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        c.setPadding(5f);
        // wrap por defecto
        t.addCell(c);
    }

    // Celda sin salto de línea
    private static void addCellNoWrap(PdfPTable t, String txt) {
        PdfPCell c = new PdfPCell(new Phrase(txt, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        c.setPadding(5f);
        c.setNoWrap(true);
        t.addCell(c);
    }

    private static void addCellImpacto(PdfPTable t, String impacto, String txt) {
        PdfPCell c = new PdfPCell(new Phrase(txt, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        c.setPadding(5f);
        c.setBackgroundColor(colorImpacto(impacto));
        t.addCell(c);
    }

    private static Color colorImpacto(String impacto) {
        if (impacto == null) return Color.WHITE;
        switch (impacto.toLowerCase()) {
            case "critical": return new Color(255, 220, 220);
            case "serious":  return new Color(255, 240, 220);
            case "moderate": return new Color(255, 255, 220);
            case "minor":    return new Color(230, 255, 230);
            default:         return Color.WHITE;
        }
    }

    private static String nivelWcagDePrimera(List<InformePagina> paginas, String reglaId) {
        for (InformePagina p : paginas) {
            for (InformeInfracciones v : p.getInfracciones()) {
                if (reglaId.equalsIgnoreCase(v.getReglaId())) {
                    return traducirNivel(v.getWcagNivel());
                }
            }
        }
        return "-";
    }

    private static String traducirNivel(String wcagNivel) {
        if (wcagNivel == null) return "-";
        switch (wcagNivel.toLowerCase()) {
            case "wcag2a":   return "WCAG 2.1 - Nivel A";
            case "wcag2aa":  return "WCAG 2.1 - Nivel AA";
            case "wcag2aaa": return "WCAG 2.1 - Nivel AAA";
            default:         return "-";
        }
    }

    private static String sanitizeUrl(String raw) {
        if (raw == null) return null;
        String u = raw.trim();
        if (u.isEmpty()) return u;
        if (u.startsWith("http://") || u.startsWith("https://")) return u;
        return "https://" + u;
    }

    // === PageEvent (igual que tu versión) ===
    static class HeaderFooterEvento extends PdfPageEventHelper {
        private final String nombreDocumento;
        private final File imagenHeaderFile;
        private Image headerImg;

        HeaderFooterEvento(String nombreDocumento, File imagenHeaderFile) {
            this.nombreDocumento = nombreDocumento;
            this.imagenHeaderFile = imagenHeaderFile;
            if (imagenHeaderFile != null && imagenHeaderFile.exists()) {
                try {
                    headerImg = Image.getInstance(imagenHeaderFile.getAbsolutePath());
                    headerImg.scaleToFit(520, 60);
                } catch (Exception ignored) { }
            }
        }

        static File resolverImagenHeaderPath() {
            File configFile = new File(System.getProperty("user.dir"), Aplicacion.ficheroConfiguracion);
            ConfiguracionDatos tmp = new ConfiguracionDatos();
            tmp.cargar(configFile);
            String path = tmp.getImagenHeader();
            if (path == null || path.isBlank()) {
                return null;
            }
            File f = new File(path);
            if (!f.isAbsolute()) {
                f = new File(System.getProperty("user.dir"), path);
            }
            return f;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Rectangle page = document.getPageSize();
            if (headerImg != null) {
                headerImg.setAbsolutePosition(document.left(), page.getTop() - 80);
                try {
                    writer.getDirectContent().addImage(headerImg);
                } catch (Exception ignored) { }
            }
            try {
                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                float y = page.getBottom() + 30;
                cb.beginText();
                cb.setFontAndSize(bf, 9);
                cb.showTextAligned(Element.ALIGN_CENTER, nombreDocumento, page.getWidth() / 2, y, 0);
                cb.endText();
                cb.beginText();
                cb.setFontAndSize(bf, 9);
                cb.showTextAligned(Element.ALIGN_RIGHT, "Página " + writer.getPageNumber(),
                        page.getRight() - document.rightMargin(), y, 0);
                               cb.endText();
            } catch (Exception ignored) { }
        }
    }
}