/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.cadastro.controlador;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modulo.cadastro.modelo.dao.CertificacaoDAO;
import modulo.cadastro.modelo.negocio.Certificacao;
import modulo.cadastro.visao.CertificacaoBusca;
import modulo.cadastro.visao.CertificacaoFormulario;
import modulo.sistema.controlador.Controlador;
import modulo.sistema.modelo.negocio.SOptionPane;
import modulo.sistema.visao.Busca;
import modulo.sistema.visao.Formulario;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author augusto
 */
public class CertificacaoControlador extends Controlador {

    public CertificacaoControlador(Busca busca, Formulario formulario) {
        super(busca, formulario);
    }
    
    @Override
    public void atualizarGrid(int selecionar, List<Object> registros) {
        CertificacaoBusca busca = (CertificacaoBusca) super.getBusca();
        
        try {  
            if ( registros.isEmpty() ) {
                registros = CertificacaoDAO.getInstance().findAll(new Certificacao());
            }
            
            DefaultTableModel modelo = (DefaultTableModel) busca.getTabela().getModel();
            modelo.setNumRows(0);
            for ( int i = 0; i < registros.size(); i ++ ) {                
                Certificacao certificacao = (Certificacao) registros.get(i);
                modelo.addRow(new Object[] {
                    certificacao.getId(), 
                    certificacao.getNome()!=null?certificacao.getNome():"",
                });
                
                // Verifica item a selecionar
                if ( certificacao.getId() == selecionar ) {
                    busca.getTabela().addRowSelectionInterval(i, i);
                }
            }
            
            if ( selecionar == -1 ) {
                busca.getBotaoEditar().setEnabled(false);
                busca.getBotaoExcluir().setEnabled(false);
            }
            
        } catch (Exception err) {
            SOptionPane.showMessageDialog(busca, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoNovoActionPerformed(ActionEvent evt) {
        CertificacaoBusca busca = (CertificacaoBusca) super.getBusca();
        busca.form = new CertificacaoFormulario(busca, true);
        busca.form.setLocationRelativeTo(null);
        busca.form.setVisible(true);
    }

    @Override
    public void botaoEditarActionPerformed(ActionEvent evt) {
        CertificacaoBusca busca = (CertificacaoBusca) super.getBusca();
        try {
            int selected = busca.getTabela().getSelectedRow();
            Object registro = busca.getTabela().getValueAt(selected, 0);
            int certificacao_id = Integer.parseInt(registro.toString());

            Object certificacao = CertificacaoDAO.getInstance().getById(new Certificacao(), certificacao_id);

            busca.form = new CertificacaoFormulario(busca, true);
            busca.form.popularCampos((Certificacao) certificacao);
            busca.form.setLocationRelativeTo(null);
            busca.form.setVisible(true);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(busca, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoExcluirActionPerformed(ActionEvent evt) {
        CertificacaoBusca busca = (CertificacaoBusca) super.getBusca();
        try {
            int selected = busca.getTabela().getSelectedRow();
            Object registro = busca.getTabela().getValueAt(selected, 0);
            int grupodecertificacaos_id = Integer.parseInt(registro.toString());

            int escolha = JOptionPane.showConfirmDialog(null, "Você têm certeza que deseja excluir este registro?", "Atenção!", JOptionPane.YES_NO_OPTION);

            if ( escolha == JOptionPane.YES_OPTION ) 
            {
                Certificacao certificacao = new Certificacao();
                certificacao.setId(grupodecertificacaos_id);
                CertificacaoDAO.getInstance().remove(certificacao);

                this.atualizarGrid(-1, new ArrayList());
                JOptionPane.showMessageDialog(busca, "Registro excluído com sucesso!", "Sucesso!", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception err) {
            SOptionPane.showMessageDialog(busca, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoBuscarActionPerformed(ActionEvent evt) {
        CertificacaoBusca busca = (CertificacaoBusca) super.getBusca();
        try {
            String campoBusca = busca.getCampoBusca().getText();

            Disjunction or = Restrictions.disjunction();
            or.add(Restrictions.ilike("nome", campoBusca, MatchMode.ANYWHERE));
            //or.add(Restrictions.ilike("ativo", busca, MatchMode.ANYWHERE));

            try {
                or.add(Restrictions.eq("id", Integer.parseInt(campoBusca)));
            } catch (Exception err) {
            }

            List<Object> grupos = CertificacaoDAO.getInstance().findByCriteria(new Certificacao(), Restrictions.conjunction(), or);
            this.atualizarGrid(-1, grupos);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(busca, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void popularCampos(Object object) {
        CertificacaoFormulario formulario = (CertificacaoFormulario) super.getFormulario();
        try {
            Certificacao certificacao = (Certificacao) object;
            formulario.getId().setText(Integer.toString(certificacao.getId()));
            formulario.getNome().setText(certificacao.getNome());
        } catch (Exception err) {
            SOptionPane.showMessageDialog(formulario, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public boolean validarCampos() {
        CertificacaoFormulario formulario = (CertificacaoFormulario) super.getFormulario();
        try {
            if ( !(formulario.getNome().getText().length() > 0) ) {
                throw new Exception("O campo 'Nome' é requerido!");
            }
            return true;
        } catch (Exception err) {
            SOptionPane.showMessageDialog(formulario, err, "Erro!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public void botaoSalvarActionPerformed(ActionEvent evt) {
        CertificacaoFormulario formulario = (CertificacaoFormulario) super.getFormulario();
        try {
            if ( this.validarCampos() )
            {
                Certificacao certificacao = new Certificacao();
                if ( formulario.getId().getText().length() > 0 )
                {
                    certificacao.setId(Integer.parseInt(formulario.getId().getText()));
                }
                certificacao.setNome(formulario.getNome().getText());

                if ( formulario.getId().getText().length() > 0 ) {
                    CertificacaoDAO.getInstance().merge(certificacao);
                } else {
                    CertificacaoDAO.getInstance().persist(certificacao);
                }

                this.atualizarGrid(certificacao.getId(), new ArrayList());
                JOptionPane.showMessageDialog(formulario, "Registro efetuado com sucesso!", "Sucesso!", JOptionPane.INFORMATION_MESSAGE);
                
                formulario.setVisible(false);
            }
        } catch (Exception err) {
            SOptionPane.showMessageDialog(formulario, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoCancelarActionPerformed(ActionEvent evt) {
        CertificacaoFormulario formulario = (CertificacaoFormulario) super.getFormulario();
        formulario.setVisible(false);
    }
}
