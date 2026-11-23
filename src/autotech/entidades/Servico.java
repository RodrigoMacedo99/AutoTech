package autotech.entidades;

import java.time.LocalDate;

/**
 * Item do catálogo de serviços, com preço tabelado.
 * Alterações de preço não modificam o objeto: é criada uma nova entrada com nova dataVigencia,
 * preservando o histórico.
 */
public class Servico {
    private final String tipoServico;
    private final String descricao;
    private final double precoTabelado;
    private final LocalDate dataVigencia;

    public Servico(String tipoServico, String descricao, double precoTabelado, LocalDate dataVigencia) {
        if (precoTabelado < 0) {
            throw new IllegalArgumentException("preço não pode ser negativo");
        }
        this.tipoServico = tipoServico;
        this.descricao = descricao;
        this.precoTabelado = precoTabelado;
        this.dataVigencia = dataVigencia;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPrecoTabelado() {
        return precoTabelado;
    }

    public LocalDate getDataVigencia() {
        return dataVigencia;
    }
}
