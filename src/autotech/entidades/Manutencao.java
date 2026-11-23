package autotech.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Ordem de serviço. Associação muitos-para-muitos com Servico (itens do catálogo). */
public class Manutencao {
    private static int proximoId = 1;

    private final int id;
    private final LocalDate dataEntrada;
    private LocalDate dataSaida;
    private final List<Servico> servicos = new ArrayList<>();
    private String formaPagamento;
    private double valorBase;
    private double valorPago;

    public Manutencao(LocalDate dataEntrada) {
        this.id = proximoId++;
        this.dataEntrada = dataEntrada;
    }

    public void adicionarServico(Servico s) {
        servicos.add(s);
    }

    public double calcularTotal() {
        double total = 0;
        for (Servico s : servicos) {
            total += s.getPrecoTabelado();
        }
        return total;
    }

    public int getId() {
        return id;
    }

    public List<Servico> getServicos() {
        return Collections.unmodifiableList(servicos);
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
    }

    /** Registra o pagamento na saída: valor base, forma usada e valor efetivamente pago. */
    public void registrarPagamento(String formaPagamento, double valorBase, double valorPago) {
        this.formaPagamento = formaPagamento;
        this.valorBase = valorBase;
        this.valorPago = valorPago;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public double getValorBase() {
        return valorBase;
    }

    /** Diferença entre o valor pago e o valor base: negativa para desconto, positiva para juros/acréscimo. */
    public double getAjuste() {
        return valorPago - valorBase;
    }

    public double getValorPago() {
        return valorPago;
    }
}
