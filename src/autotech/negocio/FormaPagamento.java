package autotech.negocio;

public interface FormaPagamento {
    String getNome();

    double calcularValorFinal(double valorBase);

    String gerarComprovante(double valorFinal);
}
