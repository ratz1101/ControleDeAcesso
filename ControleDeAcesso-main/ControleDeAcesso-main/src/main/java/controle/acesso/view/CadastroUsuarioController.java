package controle.acesso.view;

import controle.acesso.dao.UsuarioDAO;
import controle.acesso.model.StatusEnum;
import controle.acesso.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.SQLException;

public class CadastroUsuarioController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private ComboBox<String> cmbCargo; 
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<StatusEnum> cmbStatus; 
    @FXML private Button btnSalvar; // Conectado com o fx:id que colocamos na linha 41 do FXML

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuarioEdicao = null; 

    @FXML
    public void initialize() {
        cmbStatus.setItems(FXCollections.observableArrayList(StatusEnum.values()));
        cmbCargo.setItems(FXCollections.observableArrayList("Professor", "Administrativo", "Coordenador"));
    }

    // Método que a tela de listagem vai chamar para injetar os dados do usuário ao editar
    public void preencherCampos(Usuario usuario) {
        this.usuarioEdicao = usuario;
        
        txtNome.setText(usuario.getNome());
        txtCpf.setText(usuario.getCpf());
        cmbCargo.setValue(usuario.getCargo());
        txtEmail.setText(usuario.getEmail());
        cmbStatus.setValue(usuario.getStatus());
        
        txtSenha.setPromptText("Deixe em branco para manter a senha atual");
        btnSalvar.setText("Salvar Alterações");
    }

    @FXML
    private void btnCadastrarAction() {
        if (txtNome.getText().isEmpty() || txtCpf.getText().isEmpty() || cmbStatus.getValue() == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha todos os campos obrigatórios.");
            return;
        }

        try {
            if (usuarioEdicao == null) {
                // MODO CADASTRAR
                if (txtSenha.getText().isEmpty()) {
                    exibirAlerta(Alert.AlertType.WARNING, "Senha Obrigatória", "Defina uma senha para o novo usuário.");
                    return;
                }
                
                Usuario novoUsuario = new Usuario();
                atualizarObjetoComCampos(novoUsuario);
                novoUsuario.setSenha(txtSenha.getText()); 
                
                usuarioDAO.cadastrarUsuario(novoUsuario);
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário cadastrado com sucesso!");
                btnLimparAction();
                
            } else {
                // MODO ATUALIZAR
                atualizarObjetoComCampos(usuarioEdicao);
                usuarioDAO.atualizarUsuario(usuarioEdicao);
                
                if (!txtSenha.getText().trim().isEmpty()) {
                    usuarioDAO.atualizarSenha(usuarioEdicao.getIdUsuario(), txtSenha.getText());
                }
                
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados do usuário atualizados com sucesso!");
            }
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", e.getMessage());
        }
    }

    private void atualizarObjetoComCampos(Usuario usuario) {
        usuario.setNome(txtNome.getText());
        usuario.setCpf(txtCpf.getText());
        usuario.setCargo(cmbCargo.getValue());
        usuario.setEmail(txtEmail.getText());
        usuario.setStatus(cmbStatus.getValue());
    }

    @FXML
    private void btnLimparAction() {
        txtNome.clear();
        txtCpf.clear();
        cmbCargo.setValue(null);
        txtEmail.clear();
        txtSenha.clear();
        cmbStatus.setValue(null);
        txtSenha.setPromptText("Senha de acesso");
        if (btnSalvar != null) {
            btnSalvar.setText("Cadastrar");
        }
        usuarioEdicao = null;
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}
