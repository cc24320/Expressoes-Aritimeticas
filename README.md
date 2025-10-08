# Expressoes-Aritimeticas

### **Classe `AvaliacaoExpressoesAritmeticas`**

Responsável por avaliar expressões aritméticas usando uma **árvore binária**, dividindo a expressão, montando a árvore, avaliando e exibindo.

---

### **Classe interna `No`**

Representa um **nó da árvore** (número ou operador).

* **Atributos:** `esq` (filho esquerdo), `dir` (filho direito), `valor` (conteúdo).
* **Construtores:** permite criar nós com ou sem filhos.
* **Métodos importantes:**

  * `ehOperador()` → verifica se é `+ - * / ^`.
  * `toString()` → retorna a expressão do nó, incluindo filhos e parênteses.

---

### **`fragmentar(String expressao)`**

Transforma a string da expressão em **lista de nós**, separando números e operadores.

---

### **`aglutinar(List<No> vetor, String operadores)`**

Une operadores e operandos conforme a **precedência**, criando subárvores.

---

### **`construirArvore(List<No> vetor)`**

Constrói a **árvore completa**:

* Resolve parênteses recursivamente.
* Aplica precedência dos operadores (`^`, `* /`, `+ -`).
* Retorna o **nó raiz**.

---

### **`avaliar(No no)`**

Avalia a árvore **recursivamente**, retornando o valor final da expressão.

---

### **`exibirArvore(No no, int nivel)`**

Mostra a árvore no console, com indentação para visualizar hierarquia.

---

### **Resumo do fluxo**

1. Fragmenta a expressão em nós.
2. Constrói a árvore, resolvendo parênteses e precedência.
3. Avalia a árvore para obter o resultado.
4. Pode exibir a árvore pra ver a estrutura.
