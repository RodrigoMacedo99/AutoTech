package autotech.entidades;

public class Moto extends Veiculo {
    private int cilindradas;

    public Moto(String placa, String marca, String modelo, int ano, double preco,
                String combustivel, int cv, Cliente cliente, int cilindradas) {
        super(placa, marca, modelo, ano, preco, combustivel, cv, cliente);
        this.cilindradas = cilindradas;
    }

    public int getCilindradas() {
        return cilindradas;
    }

    public void setCilindradas(int cilindradas) {
        this.cilindradas = cilindradas;
    }

    @Override
    public String getTipoVeiculo() {
        return "Moto";
    }

    @Override
    public String acelerar() {
        return "Moto acelerando";
    }

    @Override
    public String frear() {
        return "Moto freando";
    }
}
