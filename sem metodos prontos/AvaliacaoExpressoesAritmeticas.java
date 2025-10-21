public class AvaliacaoExpressoesAritmeticas {

    // ======= CLASSE NÓ =======
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

    // ======= CLASSE LISTA DE NÓS =======
    public static class ListaNos {
        private String[] itens = new String[100];
        private int tamanho = 0;

        public void add(String s) {
            if (tamanho == itens.length) {
                String[] novo = new String[itens.length * 2];
                for (int i = 0; i < itens.length; i++)
                    novo[i] = itens[i];
                itens = novo;
            }
            itens[tamanho++] = s;
        }

        public String get(int i) {
            return itens[i];
        }

        public int size() {
            return tamanho;
        }
    }

    // ======= CLASSE PILHA GENÉRICA =======
    public static class PilhaNo {
        private No[] dados = new No[100];
        private int topo = 0;

        public void push(No n) {
            dados[topo++] = n;
        }

        public No pop() {
            if (topo == 0) return null;
            return dados[--topo];
        }

        public No peek() {
            if (topo == 0) return null;
            return dados[topo - 1];
        }

        public boolean isEmpty() {
            return topo == 0;
        }
    }

    public static class PilhaString {
        private String[] dados = new String[100];
        private int topo = 0;

        public void push(String s) {
            dados[topo++] = s;
        }

        public String pop() {
            if (topo == 0) return null;
            return dados[--topo];
        }

        public String peek() {
            if (topo == 0) return null;
            return dados[topo - 1];
        }

        public boolean isEmpty() {
            return topo == 0;
        }
    }

    // ======= FRAGMENTAR EXPRESSÃO =======
    public static ListaNos fragmentar(String expr) {
        ListaNos lista = new ListaNos();
        String num = "";

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == ' ') continue;

            if (Character.isDigit(c) || c == '.') {
                num += c;
            } else {
                if (!num.equals("")) {
                    lista.add(num);
                    num = "";
                }
                if ("+-*/^()".indexOf(c) != -1) {
                    lista.add("" + c);
                }
            }
        }

        if (!num.equals("")) lista.add(num);

        return lista;
    }

    // ======= CONSTRUIR ÁRVORE =======
    public static No construirArvore(ListaNos lista) {
        PilhaNo operandos = new PilhaNo();
        PilhaString operadores = new PilhaString();

        for (int i = 0; i < lista.size(); i++) {
            String token = lista.get(i);

            if (ehNumero(token)) {
                operandos.push(new No(token));
            } else if (token.equals("(")) {
                operadores.push(token);
            } else if (token.equals(")")) {
                while (!operadores.isEmpty() && !operadores.peek().equals("(")) {
                    String op = operadores.pop();
                    No dir = operandos.pop();
                    No esq = operandos.pop();
                    No novo = new No(op);
                    novo.esq = esq;
                    novo.dir = dir;
                    operandos.push(novo);
                }
                operadores.pop(); // remove '('
            } else if (ehOperador(token)) {
                while (!operadores.isEmpty() &&
                       precedencia(operadores.peek()) >= precedencia(token)) {
                    String op = operadores.pop();
                    No dir = operandos.pop();
                    No esq = operandos.pop();
                    No novo = new No(op);
                    novo.esq = esq;
                    novo.dir = dir;
                    operandos.push(novo);
                }
                operadores.push(token);
            }
        }

        while (!operadores.isEmpty()) {
            String op = operadores.pop();
            No dir = operandos.pop();
            No esq = operandos.pop();
            No novo = new No(op);
            novo.esq = esq;
            novo.dir = dir;
            operandos.push(novo);
        }

        return operandos.pop();
    }

    // ======= FUNÇÕES AUXILIARES =======
    private static boolean ehOperador(String s) {
        return "+-*/^".indexOf(s) != -1;
    }

    private static boolean ehNumero(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)) && s.charAt(i) != '.')
                return false;
        }
        return true;
    }

    private static int precedencia(String op) {
        switch (op) {
            case "^": return 3;
            case "*":
            case "/": return 2;
            case "+":
            case "-": return 1;
        }
        return 0;
    }

    // ======= AVALIAR ÁRVORE =======
    public static double avaliar(No n) {
        if (n == null)
            return 0;
        if (!ehOperador(n.valor))
            return Double.parseDouble(n.valor);

        double esq = avaliar(n.esq);
        double dir = avaliar(n.dir);

        switch (n.valor) {
            case "+": return esq + dir;
            case "-": return esq - dir;
            case "*": return esq * dir;
            case "/": return esq / dir;
            case "^": return pot(esq, dir);
        }
        return 0;
    }

    private static double pot(double a, double b) {
        double res = 1;
        for (int i = 0; i < (int) b; i++)
            res *= a;
        return res;
    }

    // ======= EXIBIR ÁRVORE =======
    public static void exibirArvore(No n, int nivel) {
        if (n == null) return;
        exibirArvore(n.dir, nivel + 1);
        for (int i = 0; i < nivel; i++) System.out.print("    ");
        System.out.println(n.valor);
        exibirArvore(n.esq, nivel + 1);
    }
}