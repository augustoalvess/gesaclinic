/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.sistema.visao;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import modulo.cadastro.modelo.negocio.Certificacao;
import modulo.sistema.controlador.Controlador;

/**
 *
 * @author augusto
 */
public class Formulario extends javax.swing.JDialog {

    private Controlador controlador;
    public javax.swing.JButton botaoCancelar;
    public javax.swing.JButton botaoSalvar;
    
    /**
     * Creates new form Formulario
     */
    public Formulario(String nome, String titulo, boolean modal) {
        this.setName(nome);
        this.setTitle(titulo);
        this.setModal(modal);
        this.setLocation(600, 530);
    }

    public Controlador getControlador() {
        return controlador;
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }
    
    public void popularCampos(Object object) {
        controlador.popularCampos(object);
    }
    
    public boolean validarCampos() {
        return controlador.validarCampos();
    }
    
    public void botaoSalvarActionPerformed(ActionEvent evt) {
        controlador.botaoSalvarActionPerformed(evt);
    }
    
    public void botaoCancelarActionPerformed(ActionEvent evt) {
        controlador.botaoCancelarActionPerformed(evt);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    public void initComponents() {
	botaoSalvar.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/salvar.png")));
        botaoCancelar.setIcon(new ImageIcon(this.getClass().getResource("/publico/imagens/cancelar.png"))); 
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
