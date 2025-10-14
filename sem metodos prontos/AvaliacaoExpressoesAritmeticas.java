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

        public boolean ehOperador() {
            return "+-*/^".contains(this.valor);
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

    // ------------------- Lista Simples -------------------
    public static class ListaNos {
        private No[] dados;
        private int tamanho;

        public ListaNos() {
            this.dados = new No[50];
            this.tamanho = 0;
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

        public No get(int i) {
            return dados[i];
        }

        public void set(int i, No n) {
            dados[i] = n;
        }

        public void remover(int i) {
            for (int j = i; j < tamanho - 1; j++)
                dados[j] = dados[j + 1];
            dados[tamanho - 1] = null;
            tamanho--;
        }

        public int tamanho() {
            return tamanho;
        }

        public boolean contem(String valor) {
            for (int i = 0; i < tamanho; i++)
                if (dados[i] != null && dados[i].getValor().equals(valor))
                    return true;
            return false;
        }

        // inserir em posição específica
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
    }

    // ------------------- Etapa 1: Fragmentação -------------------
    public static ListaNos fragmentar(String expressao) {
        ListaNos vetor = new ListaNos();
        String numero = "";

        for (int i = 0; i < expressao.length(); i++) {
            char c = expressao.charAt(i);
            if (c == ' ')
                continue;

            if (c >= '0' && c <= '9') {
                numero += c;
            } else {
                if (!numero.equals("")) {
                    vetor.adicionar(new No(numero));
                    numero = "";
                }
                vetor.adicionar(new No("" + c));
            }
        }
        if (!numero.equals(""))
            vetor.adicionar(new No(numero));

        return vetor;
    }

    // ------------------- Aglutinar operadores -------------------
    private static void aglutinar(ListaNos vetor, String operadores) {
        int i = 1;
        while (i < vetor.tamanho() - 1) {
            No atual = vetor.get(i);
            if (atual.ehOperador() && pertence(operadores, atual.getValor().charAt(0))) {
                No esquerda = vetor.get(i - 1);
                No direita = vetor.get(i + 1);
                atual.setEsq(esquerda);
                atual.setDir(direita);

                vetor.set(i - 1, atual);
                vetor.remover(i);
                vetor.remover(i);
                i--;
            }
            i++;
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
        // Lida com parênteses
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

            for (int k = 0; k <= (fim - inicio); k++)
                vetor.remover(inicio);
            vetor.adicionarPos(inicio, subArvore);
        }

        // Aglutina por precedência
        aglutinar(vetor, "^");
        aglutinar(vetor, "*/");
        aglutinar(vetor, "+-");

        if (vetor.tamanho() == 0) return null;
        return vetor.get(0);
    }

    // ------------------- Avaliação -------------------
    public static double avaliar(No no) {
        if (no == null) return 0;
        if (!no.ehOperador()) return paraDouble(no.getValor());

        double esq = avaliar(no.getEsq());
        double dir = avaliar(no.getDir());

        if (no.getValor().equals("+")) return esq + dir;
        if (no.getValor().equals("-")) return esq - dir;
        if (no.getValor().equals("*")) return esq * dir;
        if (no.getValor().equals("/")) return esq / dir;
        if (no.getValor().equals("^")) return pot(esq, dir);
        return 0;
    }

    private static double paraDouble(String s) {
        double num = 0;
        for (int i = 0; i < s.length(); i++) {
            num = num * 10 + (s.charAt(i) - '0');
        }
        return num;
    }

    private static double pot(double a, double b) {
        double r = 1;
        for (int i = 0; i < (int)b; i++)
            r *= a;
        return r;
    }

    // ------------------- Exibir árvore -------------------
    public static void exibirArvore(No no, int nivel) {
        if (no == null) return;
        exibirArvore(no.getDir(), nivel + 1);

        for (int i = 0; i < nivel; i++)
            System.out.print("    ");
        System.out.println(no.getValor());

        exibirArvore(no.getEsq(), nivel + 1);
    }
}
