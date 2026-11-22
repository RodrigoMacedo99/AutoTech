package autotech.entidades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Veiculo {
    private String placa;
    private String marca;
    private String modelo;
    private int anoFabricacao;
    private double preco;
    private final Motor motor;
    private final Cliente cliente;
    private final List<Manutencao> historicoManutencoes = new ArrayList<>();

    protected Veiculo(String placa, String marca, String modelo, int ano, double preco,
                      String combustivel, int cv, Cliente cliente) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = ano;
        this.preco = preco;
        this.motor = new Motor(combustivel, cv);
        this.cliente = cliente;
    }

    public void adicionarManutencao(Manutencao m) {
        historicoManutencoes.add(m);
    }

    /** Retorna a manutenção em aberto (sem data de saída), ou null se não houver. */
    public Manutencao getManutencaoAtiva() {
        for (int i = historicoManutencoes.size() - 1; i >= 0; i--) {
            Manutencao m = historicoManutencoes.get(i);
            if (m.getDataSaida() == null) {
                return m;
            }
        }
        return null;
    }

    public abstract String getTipoVeiculo();

    public abstract String acelerar();

    public abstract String frear();

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getAnoFabricacao() {
        return anoFabricacao;
    }

    public double getPreco() {
        return preco;
    }

    public Motor getMotor() {
        return motor;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<Manutencao> getHistoricoManutencoes() {
        return Collections.unmodifiableList(historicoManutencoes);
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setAnoFabricacao(int anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}
