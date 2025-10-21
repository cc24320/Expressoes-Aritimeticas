// AvaliacaoExpressoesAritmeticas.java
import java.io.*;

public class AvaliacaoExpressoesAritmeticas {

    // ------------------- Classe Nó -------------------
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

        public boolean ehOperador() { return "+-*/^".contains(this.valor); }

        @Override
        public String toString() {
            String ret = "No(" + this.valor + ")";
            if (esq != null) ret += " esq=" + esq.valor;
            if (dir != null) ret += " dir=" + dir.valor;
            return ret;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            No no = (No) obj;
            return (valor.equals(no.valor)) &&
                   ((esq == null && no.esq == null) || (esq != null && esq.equals(no.esq))) &&
                   ((dir == null && no.dir == null) || (dir != null && dir.equals(no.dir)));
        }

        @Override
        public int hashCode() {
            int ret = 666;
            ret = ret * 7 + (valor == null ? 0 : valor.hashCode());
            ret = ret * 7 + (esq == null ? 0 : esq.hashCode());
            ret = ret * 7 + (dir == null ? 0 : dir.hashCode());
            if (ret < 0) ret = -ret;
            return ret;
        }
    }

    // ------------------- Lista Simples -------------------
    public static class ListaNos {
        private No[] dados;
        private int tamanho;

        public ListaNos() {
            dados = new No[50];
            tamanho = 0;
        }

        public void adicionar(No n) {
            if (tamanho == dados.length) {
                No[] novo = new No[dados.length * 2];
                for (int i = 0; i < dados.length; i++)
                    novo[i] = dados[i];
                dados = novo;
            }
            dados[tamanho++] = n;
        }

        public No get(int i) { return dados[i]; }
        public void set(int i, No n) { dados[i] = n; }
        public int tamanho() { return tamanho; }

        public void remover(int i) {
            for (int j = i; j < tamanho - 1; j++)
                dados[j] = dados[j + 1];
            dados[--tamanho] = null;
        }

        public void adicionarPos(int pos, No n) {
            if (pos < 0) pos = 0;
            if (pos > tamanho) pos = tamanho;
            if (tamanho == dados.length) {
                No[] novo = new No[dados.length * 2];
                for (int i = 0; i < dados.length; i++)
                    novo[i] = dados[i];
                dados = novo;
            }
            for (int i = tamanho; i > pos; i--)
                dados[i] = dados[i - 1];
            dados[pos] = n;
            tamanho++;
        }

        public boolean contem(String valor) {
            for (int i = 0; i < tamanho; i++)
                if (dados[i] != null && valor.equals(dados[i].getValor()))
                    return true;
            return false;
        }
    }

    // ------------------- Fragmentar (com multiplicação implícita) -------------------
    public static ListaNos fragmentar(String expressao) {
        ListaNos vetor = new ListaNos();
        String numero = "";

        for (int i = 0; i < expressao.length(); i++) {
            char c = expressao.charAt(i);
            if (c == ' ') continue;

            if (Character.isDigit(c)) {
                numero += c;
            } else {
                if (!numero.equals("")) {
                    vetor.adicionar(new No(numero));
                    numero = "";
                }

                // Detecta multiplicação implícita: número ou ) seguido de (
                if (c == '(' && vetor.tamanho() > 0) {
                    No anterior = vetor.get(vetor.tamanho() - 1);
                    if (!anterior.ehOperador() && !anterior.getValor().equals("("))
                        vetor.adicionar(new No("*"));
                }

                vetor.adicionar(new No("" + c));
            }
        }
        if (!numero.equals("")) vetor.adicionar(new No(numero));

        // Trata casos como ")2" → ")*2"
        for (int i = 0; i < vetor.tamanho() - 1; i++) {
            No atual = vetor.get(i);
            No prox = vetor.get(i + 1);
            if (atual.getValor().equals(")") &&
                !prox.ehOperador() &&
                !prox.getValor().equals(")")) {
                vetor.adicionarPos(i + 1, new No("*"));
                i++;
            }
        }

        return vetor;
    }

    // ------------------- Aglutinação -------------------
    private static void aglutinar(ListaNos vetor, String operadores) {
    if (operadores.equals("^")) {
        // Potenciação: direita → esquerda (associatividade correta)
        for (int i = vetor.tamanho() - 2; i >= 1; i--) {
            No atual = vetor.get(i);
            if (atual == null || !atual.ehOperador()) continue;
            if (!pertence(operadores, atual.getValor().charAt(0))) continue;

            // segurança: evita índice inválido
            if (i - 1 < 0 || i + 1 >= vetor.tamanho()) continue;

            No esquerda = vetor.get(i - 1);
            No direita = vetor.get(i + 1);
            if (esquerda == null || direita == null) continue;

            atual.setEsq(esquerda);
            atual.setDir(direita);

            // substitui corretamente e remove nós usados
            vetor.set(i - 1, atual);
            vetor.remover(i); // remove direita
            vetor.remover(i); // remove operador duplicado
            i--; // volta um índice
        }
    } else {
        // Operadores com associatividade à esquerda
        int i = 1;
        while (i < vetor.tamanho() - 1) {
            No atual = vetor.get(i);
            if (atual == null || !atual.ehOperador()) { i++; continue; }
            if (!pertence(operadores, atual.getValor().charAt(0))) { i++; continue; }

            // segurança: evita índice inválido
            if (i - 1 < 0 || i + 1 >= vetor.tamanho()) { i++; continue; }

            No esquerda = vetor.get(i - 1);
            No direita = vetor.get(i + 1);
            if (esquerda == null || direita == null) { i++; continue; }

            atual.setEsq(esquerda);
            atual.setDir(direita);

            vetor.set(i - 1, atual);
            vetor.remover(i);
            vetor.remover(i);
            i--;
        }
    }
}


    private static boolean pertence(String s, char c) {
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == c)
                return true;
        return false;
    }

    // ------------------- Construção da árvore -------------------
    public static No construirArvore(ListaNos vetor) {
        while (vetor.contem("(")) {
            int inicio = -1, fim = -1;
            for (int i = 0; i < vetor.tamanho(); i++)
                if (vetor.get(i).getValor().equals("("))
                    inicio = i;
            for (int j = inicio + 1; j < vetor.tamanho(); j++)
                if (vetor.get(j).getValor().equals(")")) {
                    fim = j;
                    break;
                }

            ListaNos sub = new ListaNos();
            for (int k = inicio + 1; k < fim; k++)
                sub.adicionar(vetor.get(k));

            No subArvore = construirArvore(sub);
            for (int k = 0; k <= fim - inicio; k++)
                vetor.remover(inicio);
            vetor.adicionarPos(inicio, subArvore);
        }

        aglutinar(vetor, "^");
        aglutinar(vetor, "*/");
        aglutinar(vetor, "+-");

        if (vetor.tamanho() == 0) return null;
        return vetor.get(0);
    }

    // ------------------- Avaliar -------------------
    public static double avaliar(No no) {
        if (no == null) return 0;
        if (!no.ehOperador()) return paraDouble(no.getValor());

        double e = avaliar(no.getEsq());
        double d = avaliar(no.getDir());
        return switch (no.getValor()) {
            case "+" -> e + d;
            case "-" -> e - d;
            case "*" -> e * d;
            case "/" -> e / d;
            case "^" -> pot(e, d);
            default -> 0;
        };
    }

    private static double paraDouble(String s) {
        double num = 0;
        for (int i = 0; i < s.length(); i++)
            num = num * 10 + (s.charAt(i) - '0');
        return num;
    }

    private static double pot(double a, double b) {
        double r = 1;
        for (int i = 0; i < (int) b; i++)
            r *= a;
        return r;
    }

    // ------------------- Exibir árvore -------------------
    public static void exibirArvore(No no, int nivel) {
        if (no == null) return;
        exibirArvore(no.getDir(), nivel + 1);
        for (int i = 0; i < nivel; i++) System.out.print("    ");
        System.out.println(no.getValor());
        exibirArvore(no.getEsq(), nivel + 1);
    }
}
