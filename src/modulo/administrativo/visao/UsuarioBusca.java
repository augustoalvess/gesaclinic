/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.administrativo.visao;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modulo.administrativo.controlador.UsuarioControlador;
import modulo.administrativo.modelo.dao.UsuarioDAO;
import modulo.administrativo.modelo.negocio.UserAccount;
import modulo.sistema.modelo.negocio.SOptionPane;
import modulo.sistema.visao.Busca;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author augusto
 */
public class UsuarioBusca extends Busca {

    public static UsuarioFormulario form;
    
    /**
     * Creates new form UsuarioBusca
     */
    public UsuarioBusca() {
        super("usuario", "Usuários");
        super.setControlador(new UsuarioControlador(this, form));
        super.initComponents();
        
        this.getBotaoNovo().setEnabled(false);
        this.getBotaoExcluir().setEnabled(false);
        super.forcarDesabilitarBotaoExcluir = true;
    }
    
    @Override
    public DefaultTableModel construirGrid() {
        
        DefaultTableModel defaultTableModel = new javax.swing.table.DefaultTableModel(
            new Object [][] {},
                
            // Colunas
            new String [] {
                "ID", 
                "Nome", 
                "Login", 
                "Ativo"
            }
        ) {
            // Tipos
            Class[] types = new Class [] {
                java.lang.Integer.class, 
                java.lang.String.class,
                java.lang.String.class,
                java.lang.String.class
            };
            
            // Podem ser editados
            boolean[] canEdit = new boolean [] {
                false, 
                false,
                false,
                false
            };
        };
        
        return defaultTableModel;
    }
}
