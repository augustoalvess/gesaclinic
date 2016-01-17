/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.cadastro.visao;

import javax.swing.table.DefaultTableModel;
import modulo.cadastro.controlador.CertificacaoControlador;
import modulo.sistema.visao.Busca;

/**
 *
 * @author augusto
 */
public class CertificacaoBusca extends Busca {

    public static CertificacaoFormulario form;
    
    /**
     * Creates new form CertificacaoBusca
     */
    public CertificacaoBusca() {
        super("certificacao", "Certificação");
        super.setControlador(new CertificacaoControlador(this, form));
        super.initComponents();
    }
    
    @Override
    public DefaultTableModel construirGrid() {
        String colunas[] = new String [] {"ID", "Nome"};
        DefaultTableModel defaultTableModel = new javax.swing.table.DefaultTableModel(new Object [][] {}, colunas) {
            Class[] types = new Class [] {java.lang.Integer.class, java.lang.String.class};
            boolean[] canEdit = new boolean [] {false, false};
        };
        
        return defaultTableModel;
    }
}
