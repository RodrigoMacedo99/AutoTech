package autotech;

public interface FormaPagamento {
    double calcularValorFinal(double valorBase);

    String gerarComprovante(double valorFinal);
}
