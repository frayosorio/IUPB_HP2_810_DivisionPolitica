package servicios;

import java.util.List;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

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
                nodoRaiz.add(nodoPais);
            });
        }
    }

}
