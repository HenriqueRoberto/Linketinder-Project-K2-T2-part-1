package linketinder.controller

import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.model.Vaga
import linketinder.service.CandidatoService
import linketinder.service.CompetenciaService
import linketinder.service.EmpresaService
import linketinder.service.MatchService

class CandidatoController {

    private final CandidatoService   candidatoService
    private final CompetenciaService competenciaService
    private final EmpresaService     empresaService
    private final MatchService       matchService

    CandidatoController(CandidatoService candidatoService,
                        CompetenciaService competenciaService,
                        EmpresaService empresaService,
                        MatchService matchService) {
        this.candidatoService   = candidatoService
        this.competenciaService = competenciaService
        this.empresaService     = empresaService
        this.matchService       = matchService
    }

    Candidato cadastrar(Map dados) {
        Candidato novo = new Candidato.Builder()
                .nome(dados.nome as String)
                .email(dados.email as String)
                .cpf(dados.cpf as String)
                .idade(dados.idade as int)
                .estado(dados.estado as String)
                .cep(dados.cep as String)
                .descricao(dados.descricao as String)
                .competencias([])
                .senha(dados.senha as String)
                .build()
        candidatoService.cadastrar(novo)
        return novo
    }

    void editarDados(Candidato candidato, Map dados) {
        candidatoService.aplicarEdicao(candidato, dados)
    }

    void adicionarCompetencia(Candidato candidato, String nome) {
        competenciaService.adicionarAoCandidato(candidato, nome)
    }

    void editarCompetencia(Candidato candidato, int indice, String novoNome) {
        competenciaService.editarDoCandidato(candidato, indice, novoNome)
    }

    String excluirCompetencia(Candidato candidato, int indice) {
        return competenciaService.removerDoCandidato(candidato, indice)
    }

    List<Vaga> listarTodasVagas() {
        return empresaService.listarTodasVagas()
    }

    Empresa buscarEmpresaPorId(int id) {
        return empresaService.buscarPorId(id)
    }

    void registrarLikeCandidato(int idCandidato, int idVaga) {
        matchService.registrarLikeCandidato(idCandidato, idVaga)
    }

    boolean houveMatch(int idCandidato, int idEmpresa) {
        return matchService.houveMatch(idCandidato, idEmpresa)
    }

    List<Map> obterMatches(int idCandidato) {
        return matchService.obterMatchesCandidato(idCandidato)
    }
}