import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;

import servicios.DivisionPoliticaServicio;
import servicios.ReproductorAudio;

public class FrmDivisionPolitica extends JFrame {

    private JTree arbol;
    DefaultMutableTreeNode nodoRaiz;
    JXMapViewer visorMapa;

    public FrmDivisionPolitica() {
        setSize(600, 400);
        setTitle("División Política");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JToolBar tbDivisionPolitica = new JToolBar();

        JButton btnAgregarCuenta = new JButton();
        btnAgregarCuenta.setIcon(new ImageIcon(getClass().getResource("/iconos/Himno.png")));
        btnAgregarCuenta.setToolTipText("Reproducir Himno");
        btnAgregarCuenta.addActionListener(evt -> {
            reproducirHimno();
        });
        tbDivisionPolitica.add(btnAgregarCuenta);

        // Crear el nodo raíz del árbol
        nodoRaiz = new DefaultMutableTreeNode("Paises");

        // Crear el modelo del árbol
        arbol = new JTree(new DefaultTreeModel(nodoRaiz));
        JScrollPane spArbol = new JScrollPane(arbol);

        // Agregar listener para detectar selección de nodos
        arbol.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                mostrarMapa();
            }
        });

        // Crear el visor de mapa
        visorMapa = new JXMapViewer();
        JScrollPane spMapa = new JScrollPane(visorMapa);

        // configurar el servicio de mapa
        TileFactoryInfo info = new TileFactoryInfo(
                1, 15, 17,
                256,
                true, true,
                "https://tile.openstreetmap.org",
                "x", "y", "z") {

            @Override
            public String getTileUrl(int x, int y, int zoom) {
                int z = 17 - zoom;
                return this.baseURL + "/" + z + "/" + x + "/" + y + ".png";
            }
        };
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        visorMapa.setTileFactory(tileFactory);

        // Controles del Mapa
        MouseInputListener mia = new PanMouseInputListener(visorMapa);
        visorMapa.addMouseListener(mia);
        visorMapa.addMouseMotionListener(mia);
        visorMapa.addMouseWheelListener(new ZoomMouseWheelListenerCursor(visorMapa));

        // Divisor entre el árbol y el mapa
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spArbol, spMapa);
        splitPane.setDividerLocation(250); // Tamaño inicial del árbol

        getContentPane().add(tbDivisionPolitica, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        cargarDatos();
    }

    private void cargarDatos() {
        DivisionPoliticaServicio.cargarDatos();
        DivisionPoliticaServicio.mostrar(nodoRaiz);
    }

    private String obtenerPais(DefaultMutableTreeNode nodo) {
        while (nodo != null) {
            if (nodo.getParent() == nodoRaiz) { // Si el padre inmediato es la raíz "Paises"
                return nodo.toString(); // Es un país
            }
            nodo = (DefaultMutableTreeNode) nodo.getParent();
        }
        return "";
    }

    private void mostrarMapa() {
        var nodoSeleccionado = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();
        if (nodoSeleccionado != null) {
            String pais = obtenerPais(nodoSeleccionado);
            if (!pais.isEmpty()) {
                DivisionPoliticaServicio.mostrarMapa(visorMapa, pais);
            }
        }

    }

    private boolean reproduciendo = false;

    private void reproducirHimno() {
        if (!reproduciendo) {
            var nodoSeleccionado = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();
            if (nodoSeleccionado != null) {
                String pais = obtenerPais(nodoSeleccionado);
                if (!pais.isEmpty()) {
                    DivisionPoliticaServicio.reproducirHimno(pais);
                }
            }
        } else {
            ReproductorAudio.detener();
        }
        reproduciendo = !reproduciendo;
    }

}
