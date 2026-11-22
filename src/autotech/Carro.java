package autotech;

public class Carro extends Veiculo {
    private int numeroPortas;

    public Carro(String placa, String marca, String modelo, int ano, double preco,
                 String combustivel, int cv, Cliente cliente, int numeroPortas) {
        super(placa, marca, modelo, ano, preco, combustivel, cv, cliente);
        this.numeroPortas = numeroPortas;
    }

    public int getNumeroPortas() {
        return numeroPortas;
    }

    public void setNumeroPortas(int numeroPortas) {
        this.numeroPortas = numeroPortas;
    }

    @Override
    public String getTipoVeiculo() {
        return "Carro";
    }

    @Override
    public String acelerar() {
        return "Carro acelerando";
    }

    @Override
    public String frear() {
        return "Carro freando";
    }

    @Override
    public String getLinhaTabela() {
        return String.format("%-6s %-9s %-14s %-12s %-6d %12.2f %-22s %d portas",
                getTipoVeiculo(), placa, marca, modelo, anoFabricacao, preco,
                getCliente().getNome(), numeroPortas);
    }
}
