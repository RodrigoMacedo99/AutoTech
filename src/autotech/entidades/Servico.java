package autotech.entidades;

public class Servico {
    private final String tipoServico;
    private final String descricao;
    private final double preco;

    public Servico(String tipoServico, String descricao) {
        this.tipoServico = tipoServico;
        this.descricao = descricao;
        this.preco = definirPrecoPorTipo(tipoServico);
    }

    private double definirPrecoPorTipo(String tipo) {
        switch (tipo.trim().toLowerCase()) {
            case "troca de óleo":
                return 150.0;
            case "revisão":
                return 350.0;
            case "freio":
                return 280.0;
            case "alinhamento":
                return 120.0;
            case "troca de pneus":
                return 400.0;
            default:
                return 200.0;
        }
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }
}
