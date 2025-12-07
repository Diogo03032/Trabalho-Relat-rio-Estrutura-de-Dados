public class ArvoreAVL {
    No raiz;

    private int altura(No n) {
        return (n == null) ? 0 : n.altura;
    }

    private int fatorBalanceamento(No n) {
        return (n == null) ? 0 : altura(n.esquerda) - altura(n.direita);
    }

    private void atualizarAltura(No n) {
        if (n != null) {
            n.altura = Math.max(altura(n.esquerda), altura(n.direita)) + 1;
        }
    }

    private No rotacaoDireita(No y) {
        No x = y.esquerda;
        No T2 = x.direita;

        x.direita = y;
        y.esquerda = T2;

        atualizarAltura(y);
        atualizarAltura(x);

        return x;
    }

    private No rotacaoEsquerda(No x) {
        No y = x.direita;
        No T2 = y.esquerda;

        y.esquerda = x;
        x.direita = T2;

        atualizarAltura(x);
        atualizarAltura(y);

        return y;
    }

    public void inserir(int valor) {
        raiz = inserirRecursivo(raiz, valor);
    }

    private No inserirRecursivo(No no, int valor) {
        if (no == null) {
            return new No(valor);
        }
        if (valor < no.valor) {
            no.esquerda = inserirRecursivo(no.esquerda, valor);
        } else if (valor > no.valor) {
            no.direita = inserirRecursivo(no.direita, valor);
        } else {
            return no;
        }

        atualizarAltura(no);

        int balance = fatorBalanceamento(no);

        if (balance > 1 && valor < no.esquerda.valor) {
            return rotacaoDireita(no);
        }

        if (balance < -1 && valor > no.direita.valor) {
            return rotacaoEsquerda(no);
        }

        if (balance > 1 && valor > no.esquerda.valor) {
            no.esquerda = rotacaoEsquerda(no.esquerda);
            return rotacaoDireita(no);
        }

        if (balance < -1 && valor < no.direita.valor) {
            no.direita = rotacaoDireita(no.direita);
            return rotacaoEsquerda(no);
        }

        return no;
    }

    public boolean buscar(int valor) {
        return buscarRecursivo(raiz, valor);
    }

    private boolean buscarRecursivo(No atual, int valor) {
        if (atual == null) {
            return false;
        }
        if (valor == atual.valor) {
            return true;
        } else if (valor < atual.valor) {
            return buscarRecursivo(atual.esquerda, valor);
        } else {
            return buscarRecursivo(atual.direita, valor);
        }
    }
}