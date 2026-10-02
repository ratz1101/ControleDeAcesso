package controle.acesso.model;

public class FrequenciaUsuario {
    private int idUsuario;
    private String nome;
    private String cargo;
    private int diasComAcesso;
    private int totalEntradas;
    private int totalSaidas;
    private int totalNegados;

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public int getDiasComAcesso() { return diasComAcesso; }
    public void setDiasComAcesso(int diasComAcesso) { this.diasComAcesso = diasComAcesso; }
    public int getTotalEntradas() { return totalEntradas; }
    public void setTotalEntradas(int totalEntradas) { this.totalEntradas = totalEntradas; }
    public int getTotalSaidas() { return totalSaidas; }
    public void setTotalSaidas(int totalSaidas) { this.totalSaidas = totalSaidas; }
    public int getTotalNegados() { return totalNegados; }
    public void setTotalNegados(int totalNegados) { this.totalNegados = totalNegados; }
}
