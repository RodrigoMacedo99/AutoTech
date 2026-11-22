package autotech;

import java.util.Comparator;

public class ComparadorPorPreco implements Comparator<Veiculo> {
    @Override
    public int compare(Veiculo a, Veiculo b) {
        return Double.compare(a.getPreco(), b.getPreco());
    }
}
