/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.sistema.controlador;

import java.util.List;
import javax.swing.table.DefaultTableModel;
import modulo.sistema.visao.Busca;
import modulo.sistema.visao.Formulario;

/**
 *
 * @author augusto
 */
public abstract class Controlador {
    private Object busca;
    private Object formulario;
    
    public Controlador(Object busca, Object formulario) {
        this.busca = busca;
        this.formulario = formulario;
    }

    public Object getBusca() {
        return busca;
    }

    public void setBusca(Object busca) {
        this.busca = busca;
    }

    public Object getFormulario() {
        return formulario;
    }

    public void setFormulario(Object formulario) {
        this.formulario = formulario;
    }
    
    // Métodos para busca
    public abstract void atualizarGrid(int selecionar, List<Object> registros);
    public abstract void botaoNovoActionPerformed(java.awt.event.ActionEvent evt);
    public abstract void botaoEditarActionPerformed(java.awt.event.ActionEvent evt);
    public abstract void botaoExcluirActionPerformed(java.awt.event.ActionEvent evt);
    public abstract void botaoBuscarActionPerformed(java.awt.event.ActionEvent evt);
    
    // Métodos para formulário.
    public abstract void popularCampos(Object entity);
    public abstract boolean validarCampos();
    public abstract void botaoSalvarActionPerformed(java.awt.event.ActionEvent evt);
    public abstract void botaoCancelarActionPerformed(java.awt.event.ActionEvent evt);
}
