package autotech.persistencia;

import autotech.entidades.Manutencao;
import autotech.entidades.Veiculo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

/** Grava o histórico de manutenções em arquivo de texto (uma linha por manutenção). */
public class ArquivoManutencoes {
    private static final String NOME_ARQUIVO = "manutencoes.txt";

    public void salvar(Veiculo v, Manutencao m) {
        try (PrintWriter out = new PrintWriter(new FileWriter(NOME_ARQUIVO, true))) {
            out.printf("%d;%s;%s;%d;%.2f%n", m.getId(), v.getPlaca(), m.getDataEntrada(),
                    m.getServicos().size(), m.calcularTotal());
        } catch (IOException e) {
            throw new UncheckedIOException("não foi possível salvar a manutenção: " + e.getMessage(), e);
        }
    }
}
