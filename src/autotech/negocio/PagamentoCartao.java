package autotech.negocio;

public class PagamentoCartao implements FormaPagamento {
    private static final double ACRESCIMO = 0.03; // 3% de acréscimo no cartão

    @Override
    public double calcularValorFinal(double valorBase) {
        return valorBase * (1 + ACRESCIMO);
    }

    @Override
    public String gerarComprovante(double valorFinal) {
        return String.format("Pagamento via Cartão - Valor cobrado: R$ %.2f", valorFinal);
    }
}
