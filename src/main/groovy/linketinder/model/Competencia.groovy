package linketinder.model

class Competencia {

    int id
    String nome

    Competencia(String nome) {
        this.nome = nome
    }

    @Override
    String toString() {
        return nome
    }
}