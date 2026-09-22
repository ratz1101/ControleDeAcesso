package controle.acesso.dao;

import controle.acesso.factory.Conexao;
import controle.acesso.model.FrequenciaUsuario;
import controle.acesso.model.LogAcesso;
import controle.acesso.model.ResultadoAcessoEnum;
import controle.acesso.model.TipoAcessoEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogAcessoDAO {

    // AÇÃO JDBC 3: REGISTRAR UM ACESSO (INSERT + PreparedStatement)
    public void registrarAcesso(LogAcesso log) throws SQLException {
        String sql = "INSERT INTO log_acesso (id_usuario, tipo_acesso, resultado, motivo) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (log.getIdUsuario() == null) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, log.getIdUsuario());
            }
            stmt.setString(2, log.getTipoAcesso().name());
            stmt.setString(3, log.getResultado().name());
            stmt.setString(4, log.getMotivo());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) log.setIdLog(rs.getInt(1));
            }
        }
    }

    // AÇÃO JDBC 4: LISTAR HISTÓRICO DE ACESSOS (SELECT + filtros opcionais)
    public List<LogAcesso> listarHistorico(Integer idUsuario, LocalDateTime inicio, LocalDateTime fim) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT l.id_log, l.id_usuario, u.nome AS nome_usuario, l.data_hora, "
              + "l.tipo_acesso, l.resultado, l.motivo "
              + "FROM log_acesso l "
              + "LEFT JOIN usuario u ON u.id_usuario = l.id_usuario "
              + "WHERE 1 = 1 ");

        if (idUsuario != null) sql.append("AND l.id_usuario = ? ");
        if (inicio != null) sql.append("AND l.data_hora >= ? ");
        if (fim != null) sql.append("AND l.data_hora <= ? ");
        sql.append("ORDER BY l.data_hora DESC");

        List<LogAcesso> historico = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int indice = 1;
            if (idUsuario != null) stmt.setInt(indice++, idUsuario);
            if (inicio != null) stmt.setTimestamp(indice++, Timestamp.valueOf(inicio));
            if (fim != null) stmt.setTimestamp(indice++, Timestamp.valueOf(fim));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LogAcesso log = new LogAcesso();
                    log.setIdLog(rs.getInt("id_log"));
                    int idUsuarioLog = rs.getInt("id_usuario");
                    log.setIdUsuario(rs.wasNull() ? null : idUsuarioLog);
                    log.setNomeUsuario(rs.getString("nome_usuario"));
                    if (rs.getTimestamp("data_hora") != null) {
                        log.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
                    }
                    log.setTipoAcesso(TipoAcessoEnum.valueOf(rs.getString("tipo_acesso")));
                    log.setResultado(ResultadoAcessoEnum.valueOf(rs.getString("resultado")));
                    log.setMotivo(rs.getString("motivo"));
                    historico.add(log);
                }
            }
        }
        return historico;
    }

    // AÇÃO JDBC 5: RELATÓRIO DE FREQUÊNCIA (SELECT agregado por usuário, em um período)
    public List<FrequenciaUsuario> listarFrequencia(LocalDateTime inicio, LocalDateTime fim) throws SQLException {
        String sql =
                "SELECT u.id_usuario, u.nome, u.cargo, "
              + "COUNT(DISTINCT CASE WHEN l.id_log IS NOT NULL THEN DATE(l.data_hora) END) AS dias_com_acesso, "
              + "SUM(CASE WHEN l.tipo_acesso = 'ENTRADA' AND l.resultado = 'AUTORIZADO' THEN 1 ELSE 0 END) AS total_entradas, "
              + "SUM(CASE WHEN l.tipo_acesso = 'SAIDA' AND l.resultado = 'AUTORIZADO' THEN 1 ELSE 0 END) AS total_saidas, "
              + "SUM(CASE WHEN l.resultado = 'NEGADO' THEN 1 ELSE 0 END) AS total_negados "
              + "FROM usuario u "
              + "LEFT JOIN log_acesso l ON l.id_usuario = u.id_usuario AND l.data_hora BETWEEN ? AND ? "
              + "GROUP BY u.id_usuario, u.nome, u.cargo "
              + "ORDER BY u.nome";

        List<FrequenciaUsuario> relatorio = new ArrayList<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fim));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FrequenciaUsuario freq = new FrequenciaUsuario();
                    freq.setIdUsuario(rs.getInt("id_usuario"));
                    freq.setNome(rs.getString("nome"));
                    freq.setCargo(rs.getString("cargo"));
                    freq.setDiasComAcesso(rs.getInt("dias_com_acesso"));
                    freq.setTotalEntradas(rs.getInt("total_entradas"));
                    freq.setTotalSaidas(rs.getInt("total_saidas"));
                    freq.setTotalNegados(rs.getInt("total_negados"));
                    relatorio.add(freq);
                }
            }
        }
        return relatorio;
    }
}
