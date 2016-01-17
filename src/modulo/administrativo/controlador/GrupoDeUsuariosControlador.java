/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo.administrativo.controlador;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modulo.administrativo.modelo.dao.GrupoDeUsuariosDAO;
import modulo.administrativo.modelo.dao.PermissaoDoGrupoDeUsuariosDAO;
import modulo.administrativo.modelo.negocio.GrupoDeUsuarios;
import modulo.administrativo.modelo.negocio.PermissaoDoGrupoDeUsuarios;
import modulo.administrativo.visao.GrupoDeUsuariosBusca;
import static modulo.administrativo.visao.GrupoDeUsuariosBusca.form;
import modulo.administrativo.visao.GrupoDeUsuariosFormulario;
import static modulo.administrativo.visao.GrupoDeUsuariosFormulario.parent;
import modulo.cadastro.visao.CertificacaoBusca;
import modulo.sistema.controlador.Controlador;
import modulo.sistema.modelo.negocio.SOptionPane;
import modulo.sistema.visao.SistemaVisao;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import static org.hibernate.criterion.Projections.id;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author augusto
 */
public class GrupoDeUsuariosControlador extends Controlador {

    public GrupoDeUsuariosControlador(Object busca, Object formulario) {
        super(busca, formulario);
    }

    @Override
    public void atualizarGrid(int selecionar, List<Object> registros) {
        GrupoDeUsuariosBusca busca = (GrupoDeUsuariosBusca) super.getBusca();
        try {
            if (registros.isEmpty()) {
                registros = GrupoDeUsuariosDAO.getInstance().findAll(new GrupoDeUsuarios());
            }

            DefaultTableModel modelo = (DefaultTableModel) busca.getTabela().getModel();
            modelo.setNumRows(0);

            for (int i = 0; i < registros.size(); i++) {
                GrupoDeUsuarios gruposDeUsuarios = (GrupoDeUsuarios) registros.get(i);
                modelo.addRow(new Object[]{gruposDeUsuarios.getId(), gruposDeUsuarios.getNome()});

                // Verifica item a selecionar
                if (gruposDeUsuarios.getId() == selecionar) {
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
        GrupoDeUsuariosBusca busca = (GrupoDeUsuariosBusca) super.getBusca();
        busca.form = new GrupoDeUsuariosFormulario(busca, true);
        this.atualizarGridPermissoes(busca.form);
        busca.form.setLocationRelativeTo(null);
        busca.form.setVisible(true);
    }

    @Override
    public void botaoEditarActionPerformed(ActionEvent evt) {
        GrupoDeUsuariosBusca busca = (GrupoDeUsuariosBusca) super.getBusca();
        try {
            int selected = busca.getTabela().getSelectedRow();
            Object registro = busca.getTabela().getValueAt(selected, 0);
            int grupodeusuarios_id = Integer.parseInt(registro.toString());

            Object grupoDeUsuarios = GrupoDeUsuariosDAO.getInstance().getById(new GrupoDeUsuarios(), grupodeusuarios_id);

            busca.form = new GrupoDeUsuariosFormulario(busca, true);
            busca.form.popularCampos((GrupoDeUsuarios) grupoDeUsuarios);
            busca.form.setLocationRelativeTo(null);
            busca.form.setVisible(true);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(busca, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoExcluirActionPerformed(ActionEvent evt) {
        GrupoDeUsuariosBusca busca = (GrupoDeUsuariosBusca) super.getBusca();
        try {
            int selected = busca.getTabela().getSelectedRow();
            Object registro = busca.getTabela().getValueAt(selected, 0);
            int grupodeusuarios_id = Integer.parseInt(registro.toString());

            int escolha = JOptionPane.showConfirmDialog(null, "Você têm certeza que deseja excluir este registro?", "Atenção!", JOptionPane.YES_NO_OPTION);

            if (escolha == JOptionPane.YES_OPTION) {
                GrupoDeUsuarios grupoDeUsuarios = new GrupoDeUsuarios();
                grupoDeUsuarios.setId(grupodeusuarios_id);
                GrupoDeUsuariosDAO.getInstance().remove(grupoDeUsuarios);

                this.atualizarGrid(-1, new ArrayList());
                JOptionPane.showMessageDialog(busca, "Registro excluído com sucesso!", "Sucesso!", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception err) {
            SOptionPane.showMessageDialog(busca, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoBuscarActionPerformed(ActionEvent evt) {
        GrupoDeUsuariosBusca busca = (GrupoDeUsuariosBusca) super.getBusca();
        try {
            String campoBusca = busca.getCampoBusca().getText();

            Disjunction or = Restrictions.disjunction();
            or.add(Restrictions.ilike("nome", campoBusca, MatchMode.ANYWHERE));

            try {
                or.add(Restrictions.eq("id", Integer.parseInt(campoBusca)));
            } catch (Exception err) {
            }

            List<Object> grupos = GrupoDeUsuariosDAO.getInstance().findByCriteria(new GrupoDeUsuarios(), Restrictions.conjunction(), or);
            this.atualizarGrid(-1, grupos);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(busca, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void popularCampos(Object entity) {
        GrupoDeUsuariosFormulario formulario = (GrupoDeUsuariosFormulario) super.getFormulario();
        try {
            GrupoDeUsuarios grupoDeUsuarios = (GrupoDeUsuarios) entity;
            formulario.getId().setText(Integer.toString(grupoDeUsuarios.getId()));
            formulario.getNome().setText(grupoDeUsuarios.getNome());
            this.atualizarGridPermissoes(formulario);
        } catch (Exception err) {
            SOptionPane.showMessageDialog(formulario, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public boolean validarCampos() {
        GrupoDeUsuariosFormulario formulario = (GrupoDeUsuariosFormulario) super.getFormulario();
        try {
            if (!(formulario.getNome().getText().length() > 0)) {
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
        GrupoDeUsuariosFormulario formulario = (GrupoDeUsuariosFormulario) super.getFormulario();
        try {
            if (this.validarCampos()) {
                GrupoDeUsuarios grupoDeUsuarios = new GrupoDeUsuarios();
                grupoDeUsuarios.setNome(formulario.getNome().getText());

                if (formulario.getId().getText().length() > 0) {
                    grupoDeUsuarios.setId(Integer.parseInt(formulario.getId().getText()));
                    GrupoDeUsuariosDAO.getInstance().merge(grupoDeUsuarios);
                } else {
                    GrupoDeUsuariosDAO.getInstance().persist(grupoDeUsuarios);
                }

                // Obter lista de permissões já registrada para o grupo
                Conjunction and = Restrictions.conjunction();
                and.add(Restrictions.eq("grupoDeUsuarios", grupoDeUsuarios));
                List findPermissoes = PermissaoDoGrupoDeUsuariosDAO.getInstance().findByCriteria(new PermissaoDoGrupoDeUsuarios(), and, Restrictions.disjunction());

                // Obter lista de permissões marcadas.
                DefaultTableModel modelo = (DefaultTableModel) formulario.getTabelaPermissoes().getModel();

                for (int c = 0; c < modelo.getRowCount(); c++) {
                    PermissaoDoGrupoDeUsuarios permissaoDoGrupoDeUsuarios = new PermissaoDoGrupoDeUsuarios();
                    permissaoDoGrupoDeUsuarios.setId((String) modelo.getValueAt(c, 0));
                    permissaoDoGrupoDeUsuarios.setGrupoDeUsuarios(grupoDeUsuarios);
                    permissaoDoGrupoDeUsuarios.setVisualizar((boolean) modelo.getValueAt(c, 3));
                    permissaoDoGrupoDeUsuarios.setInserir((boolean) modelo.getValueAt(c, 4));
                    permissaoDoGrupoDeUsuarios.setAtualizar((boolean) modelo.getValueAt(c, 5));
                    permissaoDoGrupoDeUsuarios.setExcluir((boolean) modelo.getValueAt(c, 6));
                    permissaoDoGrupoDeUsuarios.setAdmin((boolean) modelo.getValueAt(c, 7));

                    // Verifica se a permissão já está registrada para o grupo, se sim, atualiza, caso contrário, insere.
                    boolean merge = false;
                    for (Object object : findPermissoes) {
                        PermissaoDoGrupoDeUsuarios permissao = (PermissaoDoGrupoDeUsuarios) object;
                        if (permissao.getId().equals(permissaoDoGrupoDeUsuarios.getId())) {
                            merge = true;
                            break;
                        }
                    }

                    if (merge) {
                        PermissaoDoGrupoDeUsuariosDAO.getInstance().merge(permissaoDoGrupoDeUsuarios);
                    } else {
                        PermissaoDoGrupoDeUsuariosDAO.getInstance().persist(permissaoDoGrupoDeUsuarios);
                    }
                }

                this.atualizarGrid(grupoDeUsuarios.getId(), new ArrayList());
                JOptionPane.showMessageDialog(formulario, "Registro efetuado com sucesso!", "Sucesso!", JOptionPane.INFORMATION_MESSAGE);

                formulario.setVisible(false);
            }
        } catch (Exception err) {
            SOptionPane.showMessageDialog(formulario, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void botaoCancelarActionPerformed(ActionEvent evt) {
        GrupoDeUsuariosFormulario formulario = (GrupoDeUsuariosFormulario) super.getFormulario();
        formulario.setVisible(false);
    }

    public void atualizarGridPermissoes(GrupoDeUsuariosFormulario formulario) {
        try {
            List<Object> permissoes = new ArrayList();

            if (formulario.getId().getText().length() > 0) {
                GrupoDeUsuarios grupoDeusuarios = new GrupoDeUsuarios();
                grupoDeusuarios.setId(Integer.parseInt(formulario.getId().getText()));

                Conjunction conjunction = Restrictions.conjunction();
                conjunction.add(Restrictions.eq("grupoDeUsuarios", grupoDeusuarios));

                permissoes = PermissaoDoGrupoDeUsuariosDAO.getInstance().findByCriteria(new PermissaoDoGrupoDeUsuarios(), conjunction, Restrictions.disjunction());
            }

            DefaultTableModel modelo = (DefaultTableModel) formulario.getTabelaPermissoes().getModel();
            modelo.setNumRows(0);

            SistemaVisao sistemaVisao = new SistemaVisao();
            JMenuBar menuSistema = sistemaVisao.getMenu();
            Component[] modulos = menuSistema.getComponents();

            // Percorre os módulos.
            for (int m = 0; m < modulos.length; m++) {

                if (modulos[m] instanceof JMenu) {
                    JMenu modulo = (JMenu) modulos[m];
                    Component[] telas = modulo.getMenuComponents();

                    // Percorre as telas do módulo.
                    for (int t = 0; t < telas.length; t++) {
                        if (telas[t] instanceof JMenuItem) {
                            // Por padrão, todos itens desmarcados.
                            JMenuItem tela = (JMenuItem) telas[t];
                            boolean visualizar = false;
                            boolean inserir = false;
                            boolean atualizar = false;
                            boolean excluir = false;
                            boolean admin = false;

                            // Verifica se o grupo já possui registro na permissão, e popula conforme registros.
                            for (Object object : permissoes) {
                                PermissaoDoGrupoDeUsuarios permissaoDoGrupoDeUsuarios = (PermissaoDoGrupoDeUsuarios) object;

                                if (permissaoDoGrupoDeUsuarios.getId().equals(tela.getName())) {
                                    visualizar = permissaoDoGrupoDeUsuarios.isVisualizar();
                                    inserir = permissaoDoGrupoDeUsuarios.isInserir();
                                    atualizar = permissaoDoGrupoDeUsuarios.isAtualizar();
                                    excluir = permissaoDoGrupoDeUsuarios.isExcluir();
                                    admin = permissaoDoGrupoDeUsuarios.isAdmin();

                                    break;
                                }
                            }

                            // Popula a grid com os itens do menu, de seus respectivos módulos.
                            modelo.addRow(new Object[]{
                                tela.getName(), // ESTE É O ID DA TELA!!!
                                modulo.getText(),
                                tela.getText(),
                                visualizar,
                                inserir,
                                atualizar,
                                excluir,
                                admin
                            });
                        }
                    }
                }
            }
        } catch (Exception err) {
            SOptionPane.showMessageDialog(formulario, err, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }

}
