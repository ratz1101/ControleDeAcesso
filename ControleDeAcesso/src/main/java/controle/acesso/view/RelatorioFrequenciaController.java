package controle.acesso.view;

import controle.acesso.dao.LogAcessoDAO;
import controle.acesso.model.FrequenciaUsuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatorioFrequenciaController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private DatePicker dtInicio;
    @FXML private DatePicker dtFim;
    @FXML private Label lblResumo;
    @FXML private TableView<FrequenciaUsuario> tblFrequencia;
    @FXML private TableColumn<FrequenciaUsuario, String> colNome;
    @FXML private TableColumn<FrequenciaUsuario, String> colCargo;
    @FXML private TableColumn<FrequenciaUsuario, Integer> colDias;
    @FXML private TableColumn<FrequenciaUsuario, Integer> colEntradas;
    @FXML private TableColumn<FrequenciaUsuario, Integer> colSaidas;
    @FXML private TableColumn<FrequenciaUsuario, Integer> colNegados;

    private final LogAcessoDAO logAcessoDAO = new LogAcessoDAO();

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colDias.setCellValueFactory(new PropertyValueFactory<>("diasComAcesso"));
        colEntradas.setCellValueFactory(new PropertyValueFactory<>("totalEntradas"));
        colSaidas.setCellValueFactory(new PropertyValueFactory<>("totalSaidas"));
        colNegados.setCellValueFactory(new PropertyValueFactory<>("totalNegados"));

        dtFim.setValue(LocalDate.now());
        dtInicio.setValue(LocalDate.now().withDayOfMonth(1));

        gerarRelatorio();
    }

    @FXML
    private void btnGerarRelatorioAction() {
        gerarRelatorio();
    }

    private void gerarRelatorio() {
        if (dtInicio.getValue() == null || dtFim.getValue() == null) {
            mostrar(Alert.AlertType.WARNING, "Informe o período inicial e final do relatório.");
            return;
        }
        if (dtInicio.getValue().isAfter(dtFim.getValue())) {
            mostrar(Alert.AlertType.WARNING, "A data inicial não pode ser depois da data final.");
            return;
        }

        LocalDateTime inicio = dtInicio.getValue().atStartOfDay();
        LocalDateTime fim = dtFim.getValue().atTime(LocalTime.MAX);

        try {
            List<FrequenciaUsuario> relatorio = logAcessoDAO.listarFrequencia(inicio, fim);
            tblFrequencia.setItems(FXCollections.observableArrayList(relatorio));
            lblResumo.setText(relatorio.size() + " usuário(s) no período de "
                    + dtInicio.getValue().format(FORMATO_DATA)
                    + " até "
                    + dtFim.getValue().format(FORMATO_DATA));
        } catch (Exception e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível gerar o relatório:\n" + e.getMessage());
        }
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Controle de Acesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
