package autotech.persistencia;

import autotech.entidades.Manutencao;
import autotech.entidades.Servico;
import autotech.entidades.Veiculo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gera a nota de serviço da saída em arquivo de texto.
 * Não é nota fiscal eletrônica: ela não é transmitida à prefeitura ou à SEFAZ.
 */
public class ArquivoRecibos {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String LINHA_DUPLA = "=".repeat(60);
    private static final String LINHA_SIMPLES = "-".repeat(60);

    /**
     * Grava a nota e devolve o nome do arquivo gerado.
     * @param comprovante texto de comprovante da forma de pagamento, impresso na nota
     */
    public String gerar(Veiculo v, Manutencao m, String comprovante) {
        String nomeArquivo = String.format("nota_%06d_%s.txt", m.getId(), v.getPlaca());
        try (PrintWriter out = new PrintWriter(new FileWriter(nomeArquivo))) {
            out.println(LINHA_DUPLA);
            out.printf("NOTA DE SERVIÇO Nº %06d%n", m.getId());
            out.println("(documento sem valor fiscal)");
            out.println(LINHA_DUPLA);

            out.println("EMITENTE");
            out.println("  Oficina AutoTech");
            out.println("  CNPJ: não informado");
            out.println();

            out.println("TOMADOR DO SERVIÇO");
            out.println("  Nome:     " + v.getCliente().getNome());
            out.println("  CPF:      " + v.getCliente().getCpf());
            out.println("  Telefone: " + v.getCliente().getTelefone());
            out.println();

            out.println("VEÍCULO");
            out.println("  Placa:  " + v.getPlaca());
            out.println("  Marca:  " + v.getMarca() + " " + v.getModelo() + " (" + v.getAnoFabricacao() + ")");
            out.println("  Tipo:   " + v.getTipoVeiculo());
            out.println();

            out.println("DATAS");
            out.println("  Entrada: " + m.getDataEntrada().format(DATA));
            out.println("  Saída:   " + m.getDataSaida().format(DATA));
            out.println("  Emissão: " + LocalDateTime.now().format(DATA_HORA));
            out.println();

            out.println("SERVIÇOS");
            out.printf("  %-40s %14s%n", "Descrição", "Valor (R$)");
            for (Servico s : m.getServicos()) {
                String descricao = s.getTipoServico() + " - " + s.getDescricao();
                out.printf("  %-40s %14.2f%n", descricao, s.getPrecoTabelado());
            }
            out.println(LINHA_SIMPLES);
            out.printf("  %-40s %14.2f%n", "Subtotal", m.getValorBase());
            if (m.getAjuste() < 0) {
                out.printf("  %-40s %14.2f%n", "Desconto (" + m.getFormaPagamento() + ")", m.getAjuste());
            } else if (m.getAjuste() > 0) {
                out.printf("  %-40s %14.2f%n", "Juros/acréscimo (" + m.getFormaPagamento() + ")", m.getAjuste());
            }
            out.printf("  %-40s %14.2f%n", "TOTAL PAGO", m.getValorPago());
            out.println();

            out.println("PAGAMENTO");
            out.println("  " + comprovante);
            out.println(LINHA_DUPLA);
        } catch (IOException e) {
            throw new UncheckedIOException("não foi possível gerar a nota: " + e.getMessage(), e);
        }
        return nomeArquivo;
    }
}
