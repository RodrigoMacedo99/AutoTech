package autotech.negocio;

public class PagamentoPix implements FormaPagamento {
    private static final double DESCONTO = 0.05; // 5% de desconto no Pix

    @Override
    public String getNome() {
        return "PIX";
    }

    @Override
    public double calcularValorFinal(double valorBase) {
        return valorBase * (1 - DESCONTO);
    }

    @Override
    public String gerarComprovante(double valorFinal) {
        return String.format("Pagamento via PIX - Valor pago: R$ %.2f", valorFinal);
    }
}
