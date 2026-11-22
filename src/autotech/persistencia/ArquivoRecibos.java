package autotech.persistencia;

import autotech.entidades.Manutencao;
import autotech.entidades.Servico;
import autotech.entidades.Veiculo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

/** Gera o recibo de saída em arquivo de texto. */
public class ArquivoRecibos {

    /** Grava o recibo e devolve o nome do arquivo gerado. */
    public String gerar(Veiculo v, Manutencao m, String comprovante) {
        String nomeArquivo = "recibo_" + v.getPlaca() + "_" + m.getId() + ".txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(nomeArquivo))) {
            out.println("AutoTech - Recibo de saída");
            out.println("Veículo: " + v.getPlaca() + " - " + v.getMarca() + " " + v.getModelo());
            out.println("Cliente: " + v.getCliente().getNome());
            out.println("Entrada: " + m.getDataEntrada() + " | Saída: " + m.getDataSaida());
            out.println("Serviços:");
            for (Servico s : m.getServicos()) {
                out.printf("  - %s (%s): R$ %.2f%n", s.getTipoServico(), s.getDescricao(), s.getPreco());
            }
            out.printf("Total dos serviços: R$ %.2f%n", m.calcularTotal());
            out.println(comprovante);
        } catch (IOException e) {
            throw new UncheckedIOException("não foi possível gerar o recibo: " + e.getMessage(), e);
        }
        return nomeArquivo;
    }
}
