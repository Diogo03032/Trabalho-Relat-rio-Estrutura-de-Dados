// VectorStructure.java
public class Array {
    private int[] dados;
    private int tamanhoAtual;

    public Array(int capacidade) {
        // Inicializa o array com a capacidade máxima
        this.dados = new int[capacidade];
        this.tamanhoAtual = 0;
    }

    // --- Operações de Vetor ---
    public void inserir(int valor) {
        if (tamanhoAtual < dados.length) {
            dados[tamanhoAtual++] = valor;
        }
        // Se o array estiver cheio, o elemento é ignorado,
        // mantendo o escopo do trabalho (N é fixo).
    }

    /**
     * Retorna uma cópia manual do array de dados preenchido.
     * Necessário para testar a ordenação a partir de um estado inicial.
     */
    public int[] getDados() {
        int[] copia = new int[tamanhoAtual];
        for (int i = 0; i < tamanhoAtual; i++) {
            copia[i] = this.dados[i];
        }
        return copia;
    }

    // --- Busca Sequencial (O(N)) ---
    public boolean buscaSequencial(int valor) {
        for (int i = 0; i < tamanhoAtual; i++) {
            if (dados[i] == valor) {
                return true;
            }
        }
        return false;
    }

    // --- Busca Binária (O(log N)) ---
    // Aplicável apenas a vetores previamente ordenados.
    public boolean buscaBinaria(int valor) {
        int esq = 0;
        int dir = tamanhoAtual - 1;

        while (esq <= dir) {
            int meio = esq + (dir - esq) / 2;

            if (dados[meio] == valor) {
                return true;
            }
            if (dados[meio] < valor) {
                esq = meio + 1;
            } else {
                dir = meio - 1;
            }
        }
        return false;
    }

    // --- Algoritmo de Ordenação Simples: Bubble Sort (O(N^2)) ---
    public void bubbleSort() {
        int n = tamanhoAtual;
        // Laço externo para cada passagem
        for (int i = 0; i < n - 1; i++) {
            // Laço interno para comparações e trocas
            for (int j = 0; j < n - i - 1; j++) {
                if (dados[j] > dados[j + 1]) {
                    // Troca manual (swap)
                    int temp = dados[j];
                    dados[j] = dados[j + 1];
                    dados[j + 1] = temp;
                }
            }
        }
    }

    // --- Algoritmo de Ordenação Avançado: MergeSort (O(N log N)) ---
    public void mergeSort() {
        mergeSort(this.dados, tamanhoAtual);
    }

    private void mergeSort(int[] arr, int n) {
        if (n < 2) {
            return;
        }
        int mid = n / 2;
        int[] l = new int[mid];
        int[] r = new int[n - mid];

        // Cópia manual de dados para o array esquerdo (l)
        for (int i = 0; i < mid; i++) {
            l[i] = arr[i];
        }

        // Cópia manual de dados para o array direito (r)
        for (int i = mid; i < n; i++) {
            r[i - mid] = arr[i];
        }

        // Chamadas recursivas
        mergeSort(l, mid);
        mergeSort(r, n - mid);

        // Combinação (Merge)
        merge(arr, l, r, mid, n - mid);
    }

    private void merge(int[] arr, int[] l, int[] r, int left, int right) {
        int i = 0, j = 0, k = 0;

        // Combina l e r em arr
        while (i < left && j < right) {
            if (l[i] <= r[j]) {
                arr[k++] = l[i++];
            } else {
                arr[k++] = r[j++];
            }
        }
        // Copia os elementos restantes de l (se houver)
        while (i < left) {
            arr[k++] = l[i++];
        }
        // Copia os elementos restantes de r (se houver)
        while (j < right) {
            arr[k++] = r[j++];
        }
    }
}