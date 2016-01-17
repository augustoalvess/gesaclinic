/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.administrativo.controlador;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modulo.administrativo.modelo.dao.GrupoDeUsuariosDAO;
import modulo.administrativo.modelo.dao.GrupoDoUsuarioDAO;
import modulo.administrativo.modelo.dao.UsuarioDAO;
import modulo.administrativo.modelo.negocio.GrupoDeUsuarios;
import modulo.administrativo.modelo.negocio.GrupoDoUsuario;
import modulo.administrativo.modelo.negocio.UserAccount;
import modulo.administrativo.visao.UsuarioBusca;
import static modulo.administrativo.visao.UsuarioBusca.form;
import modulo.administrativo.visao.UsuarioFormulario;
import static modulo.administrativo.visao.UsuarioFormulario.parent;
import modulo.sistema.controlador.Controlador;
import modulo.sistema.modelo.negocio.SOptionPane;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import static sun.security.jgss.GSSUtil.login;

/**
 *
 * @author augusto
 */
public class UsuarioControlador extends Controlador {

    public UserAccount useraccount;
    List<Object> gruposDoUsuario;
    
    public UsuarioControlador(Object busca, Object formulario) {
        super(busca, formulario);
    }

    @Override
    public void atualizarGrid(int selecionar, List<Object> registros) {
        UsuarioBusca busca = (UsuarioBusca) super.getBusca();
        try {
            if (registros.isEmpty()) {
                registros = UsuarioDAO.getInstance().findAll(new UserAccount());
            }

            DefaultTableModel modelo = (DefaultTableModel) busca.getTabela().getModel();
            modelo.setNumRows(0);

            for (int i = 0; i < registros.size(); i++) {
                UserAccount usuario = (UserAccount) registros.get(i);
                modelo.addRow(new Object[]{
                    usuario.getId(),
                    usuario.getName(),
                    usuario.getLogin(),
                    usuario.isActive() ? "SIM" : "NÃO"
                });

                // Verifica item a selecionar
                if (usuario.getId() == selecionar) {
                    busca.getTabela().addRowSelectionInterval(i, i);
                }
            }

            if (selecionar == -1) {
                busca.getBotaoEditar().setEnabled(false);
                busca.getBotaoExcluir().setEnabled(false);
            }

        } catch (Exception err) {
            SOptionPane.showMessageDialog(busca, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoNovoActionPerformed(ActionEvent evt) {
        UsuarioBusca busca = (UsuarioBusca) super.getBusca();
        form = new UsuarioFormulario(busca, true);
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

    @Override
    public void botaoEditarActionPerformed(ActionEvent evt) {
        UsuarioBusca busca = (UsuarioBusca) super.getBusca();
        try {
            int selected = busca.getTabela().getSelectedRow();
            Object registro = busca.getTabela().getValueAt(selected, 0);
            int usuario_id = Integer.parseInt(registro.toString());

            Object usuario = UsuarioDAO.getInstance().getById(new UserAccount(), usuario_id);

            form = new UsuarioFormulario(busca, true);
            form.popularCampos((UserAccount) usuario);
            form.setLocationRelativeTo(null);
            form.setVisible(true);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(busca, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoExcluirActionPerformed(ActionEvent evt) {
        UsuarioBusca busca = (UsuarioBusca) super.getBusca();
        try {
            int selected = busca.getTabela().getSelectedRow();
            Object registro = busca.getTabela().getValueAt(selected, 0);
            int grupodeusuarios_id = Integer.parseInt(registro.toString());

            int escolha = JOptionPane.showConfirmDialog(null, "Você têm certeza que deseja excluir este registro?", "Atenção!", JOptionPane.YES_NO_OPTION);

            if (escolha == JOptionPane.YES_OPTION) {
                UserAccount usuario = new UserAccount();
                usuario.setId(grupodeusuarios_id);
                UsuarioDAO.getInstance().remove(usuario);

                this.atualizarGrid(-1, new ArrayList());
                JOptionPane.showMessageDialog(busca, "Registro excluído com sucesso!", "Sucesso!", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception err) {
            SOptionPane.showMessageDialog(busca, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoBuscarActionPerformed(ActionEvent evt) {
        UsuarioBusca busca = (UsuarioBusca) super.getBusca();
        try {
            String campoBusca = busca.getCampoBusca().getText();

            Disjunction or = Restrictions.disjunction();
            or.add(Restrictions.ilike("login", campoBusca, MatchMode.ANYWHERE));
            or.add(Restrictions.ilike("name", campoBusca, MatchMode.ANYWHERE));

            try {
                or.add(Restrictions.eq("id", Integer.parseInt(campoBusca)));
            } catch (Exception err) {
            }

            List<Object> usuarios = UsuarioDAO.getInstance().findByCriteria(new UserAccount(), Restrictions.conjunction(), or);
            this.atualizarGrid(-1, usuarios);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(busca, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void popularCampos(Object entity) {
        UsuarioFormulario formulario = (UsuarioFormulario) super.getFormulario();
        UserAccount usuario = (UserAccount) entity;
        try {
            this.useraccount = usuario;
            formulario.getId().setText(Integer.toString(usuario.getId()));
            formulario.getNome().setText(usuario.getName());
            formulario.getLogin().setText(usuario.getLogin());
            formulario.getAtivo().setSelected(usuario.isActive());

            if (!Integer.toString(usuario.getId()).isEmpty()) {
                formulario.getNome().setEnabled(false);
                formulario.getLogin().setEnabled(false);
            }

            // Obter grupos de usuários, do usuário.
            Conjunction and = Restrictions.conjunction();
            and.add(Restrictions.eq("usuario", usuario));
            gruposDoUsuario = GrupoDoUsuarioDAO.getInstance().findByCriteria(new GrupoDoUsuario(), and, Restrictions.disjunction());

            // Popula combobox de grupos de usuários, somente com os grupos não pertencentes ao usuário editado.
            formulario.getGrupoDeUsuarios().addItem("");
            List<Object> grupos = GrupoDeUsuariosDAO.getInstance().findAll(new GrupoDeUsuarios());

            for (int i = 0; i < grupos.size(); i++) {
                GrupoDeUsuarios grupo = (GrupoDeUsuarios) grupos.get(i);
                boolean possuiGrupo = false;

                for (int g = 0; g < gruposDoUsuario.size(); g++) {
                    // Somente adicionar, se não existir no list de gruposDoUsuario
                    GrupoDoUsuario grupoDoUsuario = (GrupoDoUsuario) gruposDoUsuario.get(g);

                    if (grupoDoUsuario.getGrupoDeUsuarios().getId() == grupo.getId()) {
                        possuiGrupo = true;
                        break;
                    }
                }

                if (!possuiGrupo) {
                    formulario.getGrupoDeUsuarios().addItem(grupo);
                }
            }

            // Popula tabela de grupos de usuários, com os grupos pertencentes ao usuário editado.
            DefaultTableModel modelo = (DefaultTableModel) formulario.getTabelaDeGrupos().getModel();
            for (int g = 0; g < gruposDoUsuario.size(); g++) {
                GrupoDoUsuario grupoDoUsuario = (GrupoDoUsuario) gruposDoUsuario.get(g);
                modelo.addRow(new Object[]{grupoDoUsuario.getGrupoDeUsuarios().getId(), grupoDoUsuario.getGrupoDeUsuarios().getNome()});
            }
        } catch (Exception err) {
            SOptionPane.showMessageDialog(formulario, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public boolean validarCampos() {
        UsuarioFormulario formulario = (UsuarioFormulario) super.getFormulario();
        try {
            if (!formulario.getSenha().getText().isEmpty() && formulario.getSenha().getText().length() < 8) {
                throw new Exception("O campo 'Senha' deve ter no mínimo 8 caracteres");
            }

            return true;
        } catch (Exception err) {
            SOptionPane.showMessageDialog(formulario, err, "Erro!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public void botaoSalvarActionPerformed(ActionEvent evt) {
        UsuarioFormulario formulario = (UsuarioFormulario) super.getFormulario();
        try {
            if (this.validarCampos()) {
                useraccount.setName(formulario.getNome().getText());
                useraccount.setLogin(formulario.getLogin().getText());
                useraccount.setActive(formulario.getAtivo().isSelected());
                if (!formulario.getSenha().getText().isEmpty()) {
                    useraccount.setPassword(formulario.getSenha().getText());
                }
                UsuarioDAO.getInstance().merge(useraccount);

                // Deletar todos os grupos do usuário, e registrá-los novamente.
                for (int g = 0; g < gruposDoUsuario.size(); g++) {
                    // Somente adicionar, se não existir no list de gruposDoUsuario
                    GrupoDoUsuario grupoDoUsuario = (GrupoDoUsuario) gruposDoUsuario.get(g);
                    GrupoDoUsuarioDAO.getInstance().remove(grupoDoUsuario);
                }

                DefaultTableModel modelo = (DefaultTableModel) formulario.getTabelaDeGrupos().getModel();
                for (int i = 0; i < modelo.getRowCount(); i++) {
                    GrupoDeUsuarios grupo = new GrupoDeUsuarios();
                    grupo.setId((int) modelo.getValueAt(i, 0));

                    GrupoDoUsuario grupoDoUsuario = new GrupoDoUsuario();
                    grupoDoUsuario.setUsuario(useraccount);
                    grupoDoUsuario.setGrupoDeUsuarios(grupo);

                    GrupoDoUsuarioDAO.getInstance().merge(grupoDoUsuario);
                }

                parent.atualizarGrid(useraccount.getId(), new ArrayList());
                JOptionPane.showMessageDialog(formulario, "Registro efetuado com sucesso!", "Sucesso!", JOptionPane.INFORMATION_MESSAGE);

                formulario.setVisible(false);
            }
        } catch (Exception err) {
            SOptionPane.showMessageDialog(formulario, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoCancelarActionPerformed(ActionEvent evt) {
        UsuarioFormulario formulario = (UsuarioFormulario) super.getFormulario();
        formulario.setVisible(false);
    }

    public void grupoDeUsuariosActionPerformed(java.awt.event.ActionEvent evt) {
        UsuarioFormulario formulario = (UsuarioFormulario) super.getFormulario();
        if (formulario.getGrupoDeUsuarios().getSelectedItem().toString().isEmpty()) {
            formulario.getBotaoAdicionarGrupo().setEnabled(false);
        } else {
            formulario.getBotaoAdicionarGrupo().setEnabled(true);
        }
    }

    public void botaoAdicionarGrupoActionPerformed(java.awt.event.ActionEvent evt) {
        UsuarioFormulario formulario = (UsuarioFormulario) super.getFormulario();
        try {
            GrupoDeUsuarios grupo = (GrupoDeUsuarios) formulario.getGrupoDeUsuarios().getSelectedItem();
            DefaultTableModel modelo = (DefaultTableModel) formulario.getTabelaDeGrupos().getModel();
            modelo.addRow(new Object[]{grupo.getId(), grupo.getNome()});
            formulario.getGrupoDeUsuarios().removeItem(grupo);
            formulario.getGrupoDeUsuarios().setSelectedItem("");
        } catch (Exception err) {
            SOptionPane.showMessageDialog(formulario, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void botaoRemoverGrupoActionPerformed(java.awt.event.ActionEvent evt) {
        UsuarioFormulario formulario = (UsuarioFormulario) super.getFormulario();
        try {
            int selected = formulario.getTabelaDeGrupos().getSelectedRow();
            Object registro = formulario.getTabelaDeGrupos().getValueAt(selected, 0);
            int grupo_id = Integer.parseInt(registro.toString());

            Object grupo = GrupoDeUsuariosDAO.getInstance().getById(new GrupoDeUsuarios(), grupo_id);
            DefaultTableModel modelo = (DefaultTableModel) formulario.getTabelaDeGrupos().getModel();
            modelo.removeRow(selected);
            formulario.getGrupoDeUsuarios().addItem(grupo);
            formulario.getBotaoRemoverGrupo().setEnabled(false);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(formulario, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

}
