package linketinder.controller

import linketinder.model.Competencia
import linketinder.model.Vaga
import linketinder.service.EmpresaService

class VagaController {

    private final EmpresaService empresaService

    VagaController(EmpresaService empresaService) {
        this.empresaService = empresaService
    }

    List<Vaga> listar(int idEmpresa) {
        return empresaService.listarVagasDaEmpresa(idEmpresa)
    }

    void criar(int idEmpresa, Map dados, List<Competencia> competencias) {
        Vaga vaga = new Vaga.Builder()
                .nome(dados.nome)
                .descricao(dados.descricao)
                .horario(dados.horario)
                .localizacao(dados.localizacao)
                .remuneracao(dados.remuneracao)
                .competencias(competencias)
                .idEmpresa(idEmpresa)
                .build()
        empresaService.criarVaga(idEmpresa, vaga)
    }

    void editar(int idEmpresa, int indice, Map dados, List<Competencia> competencias) {
        List<Vaga> vagas = empresaService.listarVagasDaEmpresa(idEmpresa)
        Vaga vagaAtual = vagas[indice]
        Vaga atualizada = new Vaga.Builder()
                .nome(dados.nome ?: vagaAtual.nome)
                .descricao(dados.descricao ?: vagaAtual.descricao)
                .horario(dados.horario ?: vagaAtual.horario)
                .localizacao(dados.localizacao ?: vagaAtual.localizacao)
                .remuneracao(dados.remuneracao ?: vagaAtual.remuneracao)
                .competencias(competencias)
                .idEmpresa(idEmpresa)
                .build()
        empresaService.editarVaga(idEmpresa, indice, atualizada)
    }

    void excluir(int idEmpresa, int indice) {
        empresaService.excluirVaga(idEmpresa, indice)
    }
}