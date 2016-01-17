/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.administrativo.visao;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modulo.administrativo.controlador.GrupoDeUsuariosControlador;
import modulo.administrativo.modelo.dao.GrupoDeUsuariosDAO;
import modulo.administrativo.modelo.negocio.GrupoDeUsuarios;
import modulo.sistema.modelo.negocio.SOptionPane;
import modulo.sistema.visao.Busca;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author augusto
 */
public class GrupoDeUsuariosBusca extends Busca {

    public static GrupoDeUsuariosFormulario form;
    
    /**
     * Creates new form ModeloBusca
     */
    public GrupoDeUsuariosBusca() {
        super("grupodeusuarios", "Grupo de usuários");
        super.setControlador(new GrupoDeUsuariosControlador(this, form));
        super.initComponents();
    }
    
    @Override
    public DefaultTableModel construirGrid() {
        DefaultTableModel defaultTableModel = new javax.swing.table.DefaultTableModel(
            new Object [][] {},
                
            // Colunas
            new String [] {
                "ID", 
                "Nome"
            }
        ) {
            // Tipos
            Class[] types = new Class [] {
                java.lang.Integer.class, 
                java.lang.String.class,
            };
            
            // Podem ser editados
            boolean[] canEdit = new boolean [] {
                false, 
                false,
            };
        };
        
        return defaultTableModel;
    }
}
