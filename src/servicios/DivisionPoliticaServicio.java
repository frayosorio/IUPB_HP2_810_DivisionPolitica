package servicios;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import com.fasterxml.jackson.databind.ObjectMapper;

import modelos.Pais;

public class DivisionPoliticaServicio {

    private static List<Pais> paises;

    public static void cargarDatos() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String nombreArchivo = System.getProperty("user.dir") + "/src/datos/DivisionPolitica.json";
            paises = objectMapper.readValue(new File(nombreArchivo),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Pais.class));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se pudieron cargar los datos" + e);
        }
    }

    public static void mostrar(DefaultMutableTreeNode nodoRaiz) {
        if (paises != null) {
            paises.forEach(pais -> {
                DefaultMutableTreeNode nodoPais = new DefaultMutableTreeNode(pais.getNombre());
                if (pais.getRegiones() != null) {
                    pais.getRegiones().forEach(region -> {
                        DefaultMutableTreeNode nodoRegion = new DefaultMutableTreeNode(region.getNombre());
                        if (region.getCiudades() != null) {
                            region.getCiudades().forEach(ciudad -> {
                                DefaultMutableTreeNode nodoCiudad = new DefaultMutableTreeNode(ciudad.getNombre());
                                nodoRegion.add(nodoCiudad);
                            });
                        }
                        nodoPais.add(nodoRegion);
                    });
                }
                nodoRaiz.add(nodoPais);
            });
        }
    }

    public static Pais obtenerPaisPorNombre(String nombre) {
        return paises.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public static void mostrarMapa(JXMapViewer visorMapa, String nombrePais) {
        var pais = obtenerPaisPorNombre(nombrePais);
        if (pais == null)
            return;

        GeoPosition posicion = new GeoPosition(pais.getLatitud(), pais.getLongitud());
        visorMapa.setZoom(pais.getZoom());

        visorMapa.setAddressLocation(posicion);

        // Marcador
        Set<Waypoint> waypoints = new HashSet<>();
        waypoints.add(new DefaultWaypoint(posicion));

        WaypointPainter<Waypoint> painter = new WaypointPainter<>();
        painter.setWaypoints(waypoints);

        visorMapa.setOverlayPainter(painter);

    }

}
