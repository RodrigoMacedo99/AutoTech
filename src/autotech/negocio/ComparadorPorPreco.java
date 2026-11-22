package autotech.negocio;

import autotech.entidades.Veiculo;

import java.util.Comparator;

public class ComparadorPorPreco implements Comparator<Veiculo> {
    @Override
    public int compare(Veiculo a, Veiculo b) {
        return Double.compare(a.getPreco(), b.getPreco());
    }
}
