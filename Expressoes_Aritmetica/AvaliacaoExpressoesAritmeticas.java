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
            String ret = "No com valor \"" + this.valor + "\"";

            if (this.esq != null)
                ret += ", esquerda = " + this.esq.valor;
            if (this.dir != null)
                ret += ", direita = " + this.dir.valor;

            return ret;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;

            if (obj == null)
                return false;

            if (this.getClass() != obj.getClass())
                return false;

            No no = (No) obj;

            if (this.valor == null) {
                if (no.valor != null)
                    return false;
            } else if (!this.valor.equals(no.valor))
                return false;

            if (this.esq == null) {
                if (no.esq != null)
                    return false;
            } else if (!this.esq.equals(no.esq))
                return false;

            if (this.dir == null) {
                if (no.dir != null)
                    return false;
            } else if (!this.dir.equals(no.dir))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int ret = 666; // qualquer positivo

            ret = ret * 7 + (this.valor == null ? 0 : this.valor.hashCode());
            ret = ret * 7 + (this.esq == null ? 0 : this.esq.hashCode());
            ret = ret * 7 + (this.dir == null ? 0 : this.dir.hashCode());

            if (ret < 0)
                ret = -ret;

            return ret;
        }
    }

    // --- Etapa 1: Fragmentação ---
    public static List<No> fragmentar(String expressao) {
        expressao = expressao.replaceAll("\\s+", "");
        List<No> vetor = new ArrayList<>();
        StringBuilder numero = new StringBuilder();

        for (char c : expressao.toCharArray()) {
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

    // --- Etapa 3 e 4: Aglutinação por precedência ---
    private static void aglutinar(List<No> vetor, String operadores) {
        for (int i = 1; i < vetor.size() - 1; i++) {
            No atual = vetor.get(i);
            if (atual.ehOperador() && operadores.contains(atual.valor)) {
                No esquerda = vetor.get(i - 1);
                No direita = vetor.get(i + 1);
                atual.esq = esquerda;
                atual.dir = direita;
                // Substitui três elementos por um
                vetor.set(i - 1, atual);
                vetor.remove(i); // remove operador
                vetor.remove(i); // remove direita
                i--; // ajusta índice
            }
        }
    }

    // --- Etapa 2 e 5: Construção da árvore com subexpressões e eliminação de parênteses ---
    public static No construirArvore(List<No> vetor) {
        while (vetor.stream().anyMatch(n -> "(".equals(n.valor))) {
            int inicio = -1;
            for (int i = 0; i < vetor.size(); i++)
                if ("(".equals(vetor.get(i).valor))
                    inicio = i;

            if (inicio == -1) break;

            int fim = -1;
            for (int j = inicio + 1; j < vetor.size(); j++)
                if (")".equals(vetor.get(j).valor)) {
                    fim = j;
                    break;
                }

            if (fim == -1) break;

            List<No> sub = new ArrayList<>(vetor.subList(inicio + 1, fim));
            No subArvore = construirArvore(sub);

            // Substitui (subArvore) no vetor
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

    // --- Etapa 6: Avaliação ---
    public static double avaliar(No no) {
        if (no == null) return 0;
        if (!no.ehOperador()) return Double.parseDouble(no.valor);

        double esq = avaliar(no.esq);
        double dir = avaliar(no.dir);

        return switch (no.valor) {
            case "+" -> esq + dir;
            case "-" -> esq - dir;
            case "*" -> esq * dir;
            case "/" -> esq / dir;
            case "^" -> Math.pow(esq, dir);
            default -> 0;
        };
    }

    // --- Exibir árvore (opcional) ---
    public static void exibirArvore(No no, int nivel) {
        if (no == null) return;
        exibirArvore(no.dir, nivel + 1);
        System.out.println("    ".repeat(nivel) + no.valor);
        exibirArvore(no.esq, nivel + 1);
    }

}
