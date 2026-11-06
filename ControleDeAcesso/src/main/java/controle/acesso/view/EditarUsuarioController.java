package controle.acesso.view;

import controle.acesso.dao.UsuarioDAO;
import controle.acesso.model.StatusEnum;
import controle.acesso.model.Usuario;
import controle.acesso.util.Validacao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarUsuarioController {

    @FXML private Label lblIdUsuario;
    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private ComboBox<String> cmbCargo;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<StatusEnum> cmbStatus;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuario;
    private boolean salvo = false;

    @FXML
    public void initialize() {
        cmbStatus.getItems().setAll(StatusEnum.values());
        cmbCargo.getItems().setAll("Professor", "Funcionário");
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        lblIdUsuario.setText("#" + usuario.getIdUsuario());
        txtNome.setText(usuario.getNome());
        txtCpf.setText(usuario.getCpf());
        cmbCargo.setValue(usuario.getCargo());
        txtEmail.setText(usuario.getEmail());
        cmbStatus.setValue(usuario.getStatus());
    }

    public boolean isSalvo() {
        return salvo;
    }

    @FXML
    private void btnSalvarAction() {
        if (txtNome.getText().isBlank()
                || txtCpf.getText().isBlank()
                || cmbCargo.getValue() == null
                || txtEmail.getText().isBlank()
                || cmbStatus.getValue() == null) {
            mostrar(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios.");
            return;
        }

        boolean cpfAlterado = !Validacao.formatarCpf(txtCpf.getText()).equals(usuario.getCpf());
        if (cpfAlterado && !Validacao.cpfValido(txtCpf.getText())) {
            mostrar(Alert.AlertType.WARNING, "Informe um CPF válido.");
            return;
        }

        if (!Validacao.emailValido(txtEmail.getText())) {
            mostrar(Alert.AlertType.WARNING, "Informe um e-mail válido.");
            return;
        }

        usuario.setNome(txtNome.getText().trim());
        usuario.setCpf(Validacao.formatarCpf(txtCpf.getText()));
        usuario.setCargo(cmbCargo.getValue());
        usuario.setEmail(txtEmail.getText().trim());
        usuario.setStatus(cmbStatus.getValue());

        boolean alterarSenha = !txtSenha.getText().isBlank();
        if (alterarSenha && txtSenha.getText().length() < 4) {
            mostrar(Alert.AlertType.WARNING, "A senha deve ter pelo menos 4 caracteres.");
            return;
        }
        if (alterarSenha) {
            usuario.setSenha(txtSenha.getText());
        }

        try {
            usuarioDAO.atualizarUsuario(usuario, alterarSenha);
            salvo = true;
            fechar();
        } catch (Exception e) {
            mostrar(Alert.AlertType.ERROR, "Erro ao salvar as alterações:\n" + Validacao.mensagemErro(e));
        }
    }

    @FXML
    private void btnCancelarAction() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Controle de Acesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
