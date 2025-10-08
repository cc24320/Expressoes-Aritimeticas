// AvaliacaoExpressoesAritmeticas.java
import java.util.*;

public class AvaliacaoExpressoesAritmeticas {

    // Classe Nó
    public static class No {
        private No esq;
        private String valor;
        private No dir;

        public No(No e, String v, No d) {
            this.esq = e;
            this.valor = v;
            this.dir = d;
        }

        public No(String v) {
            this(null, v, null);
        }

        public No getEsq() { return esq; }
        public String getValor() { return valor; }
        public No getDir() { return dir; }

        public void setEsq(No e) { this.esq = e; }
        public void setDir(No d) { this.dir = d; }

        public boolean ehOperador() {
            return "+-*/^".contains(valor);
        }

        @Override
        public String toString() {
            if (esq == null && dir == null) return valor;
            return "(" + (esq != null ? esq.toString() : "") + valor + (dir != null ? dir.toString() : "") + ")";
        }
    }

    // Etapa 1: Fragmentação
    public static List<No> fragmentar(String expressao) {
        if (expressao == null) expressao = "";
        expressao = expressao.replaceAll("\\s+", "");
        List<No> vetor = new ArrayList<>();
        StringBuilder numero = new StringBuilder();

        for (int i = 0; i < expressao.length(); i++) {
            char c = expressao.charAt(i);
            if (Character.isDigit(c)) {
                numero.append(c);
            } else {
                if (numero.length() > 0) {
                    vetor.add(new No(numero.toString()));
                    numero.setLength(0);
                }
                vetor.add(new No(String.valueOf(c)));
            }
        }
        if (numero.length() > 0)
            vetor.add(new No(numero.toString()));

        return vetor;
    }

    // Etapa 3 e 4: Aglutinação por precedência 
    private static void aglutinar(List<No> vetor, String operadores) {
        for (int i = 1; i < vetor.size() - 1; i++) {
            No atual = vetor.get(i);
            if (atual.ehOperador() && operadores.contains(atual.getValor())) {
                No esquerda = vetor.get(i - 1);
                No direita = vetor.get(i + 1);
                atual.setEsq(esquerda);
                atual.setDir(direita);
                vetor.set(i - 1, atual);
                vetor.remove(i); // remove operador
                vetor.remove(i); // remove direita
                i--;
            }
        }
    }

    // Etapa 2 e 5: Construção da árvore com subexpressões e eliminação de parênteses 
    public static No construirArvore(List<No> vetor) {
        while (vetor.stream().anyMatch(n -> "(".equals(n.getValor()))) {
            int inicio = -1;
            for (int i = 0; i < vetor.size(); i++)
                if ("(".equals(vetor.get(i).getValor()))
                    inicio = i;

            if (inicio == -1) break;

            int fim = -1;
            for (int j = inicio + 1; j < vetor.size(); j++)
                if (")".equals(vetor.get(j).getValor())) {
                    fim = j;
                    break;
                }

            if (fim == -1) break;

            List<No> sub = new ArrayList<>(vetor.subList(inicio + 1, fim));
            No subArvore = construirArvore(sub);

            // Substitui (subArvore) no vetor original
            for (int k = 0; k <= (fim - inicio); k++)
                vetor.remove(inicio);
            vetor.add(inicio, subArvore);
        }

        // Aglutinar operadores por precedência
        aglutinar(vetor, "^");
        aglutinar(vetor, "*/");
        aglutinar(vetor, "+-");

        return vetor.isEmpty() ? null : vetor.get(0);
    }

    // Etapa 6: Avaliação recursiva 
    public static double avaliar(No no) {
        if (no == null) return 0.0;
        if (!no.ehOperador()) return Double.parseDouble(no.getValor());
        double esq = avaliar(no.getEsq());
        double dir = avaliar(no.getDir());
        return switch (no.getValor()) {
            case "+" -> esq + dir;
            case "-" -> esq - dir;
            case "*" -> esq * dir;
            case "/" -> esq / dir;
            case "^" -> Math.pow(esq, dir);
            default -> 0.0;
        };
    }

    // --- Exibir árvore ---
    public static void exibirArvore(No no, int nivel) {
        if (no == null) return;
        exibirArvore(no.getDir(), nivel + 1);
        System.out.println("    ".repeat(nivel) + no.getValor());
        exibirArvore(no.getEsq(), nivel + 1);
    }
}
