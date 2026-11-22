package autotech.visao;

import autotech.entidades.Carro;
import autotech.entidades.Moto;
import autotech.entidades.Veiculo;

/** Camada de visão: formata veículos para exibição no console. */
public final class FormatadorVeiculo {
    public static final String CABECALHO_TABELA = String.format("%-6s %-9s %-14s %-12s %-6s %12s %-22s %s",
            "Tipo", "Placa", "Marca", "Modelo", "Ano", "Preço (R$)", "Cliente", "Detalhe");

    private FormatadorVeiculo() {
    }

    public static String linhaTabela(Veiculo v) {
        String detalhe;
        if (v instanceof Carro carro) {
            detalhe = carro.getNumeroPortas() + " portas";
        } else if (v instanceof Moto moto) {
            detalhe = moto.getCilindradas() + " cc";
        } else {
            detalhe = "";
        }
        return String.format("%-6s %-9s %-14s %-12s %-6d %12.2f %-22s %s",
                v.getTipoVeiculo(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getAnoFabricacao(),
                v.getPreco(), v.getCliente().getNome(), detalhe);
    }

    public static void imprimirDetalhado(Veiculo v) {
        System.out.println("=== " + v.getTipoVeiculo() + " ===");
        System.out.println("Placa: " + v.getPlaca() + " | " + v.getMarca() + " " + v.getModelo()
                + " (" + v.getAnoFabricacao() + ")");
        System.out.printf("Preço: R$ %.2f%n", v.getPreco());
        System.out.println("Motor: " + v.getMotor().getTipoCombustivel() + " - "
                + v.getMotor().getPotenciaCV() + " CV");
        System.out.println("Cliente: " + v.getCliente().getNome() + " (CPF " + v.getCliente().getCpf() + ")");
        System.out.println("Manutenções registradas: " + v.getHistoricoManutencoes().size());
    }

    public static void imprimirTabela(Iterable<Veiculo> veiculos) {
        System.out.println(CABECALHO_TABELA);
        boolean vazio = true;
        for (Veiculo v : veiculos) {
            System.out.println(linhaTabela(v));
            vazio = false;
        }
        if (vazio) {
            System.out.println("(nenhum veículo encontrado)");
        }
    }
}
