package autotech;

public class PagamentoDinheiro implements FormaPagamento {
    private static final double DESCONTO = 0.10; // 10% de desconto em dinheiro

    @Override
    public double calcularValorFinal(double valorBase) {
        return valorBase * (1 - DESCONTO);
    }

    @Override
    public String gerarComprovante(double valorFinal) {
        return String.format("Pagamento em Dinheiro - Valor pago: R$ %.2f", valorFinal);
    }
}
