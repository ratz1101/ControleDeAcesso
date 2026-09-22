package controle.acesso.model;

import java.time.LocalDateTime;

public class LogAcesso {
    private int idLog;
    private Integer idUsuario;
    private String nomeUsuario;
    private LocalDateTime dataHora;
    private TipoAcessoEnum tipoAcesso;
    private ResultadoAcessoEnum resultado;
    private String motivo;

    public LogAcesso() {
    }

    public LogAcesso(Integer idUsuario, TipoAcessoEnum tipoAcesso, ResultadoAcessoEnum resultado, String motivo) {
        this.idUsuario = idUsuario;
        this.tipoAcesso = tipoAcesso;
        this.resultado = resultado;
        this.motivo = motivo;
    }

    public int getIdLog() { return idLog; }
    public void setIdLog(int idLog) { this.idLog = idLog; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public TipoAcessoEnum getTipoAcesso() { return tipoAcesso; }
    public void setTipoAcesso(TipoAcessoEnum tipoAcesso) { this.tipoAcesso = tipoAcesso; }
    public ResultadoAcessoEnum getResultado() { return resultado; }
    public void setResultado(ResultadoAcessoEnum resultado) { this.resultado = resultado; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
