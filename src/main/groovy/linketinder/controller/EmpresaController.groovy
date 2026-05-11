package linketinder.controller

import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.service.CandidatoService
import linketinder.service.EmpresaService
import linketinder.service.MatchService

class EmpresaController {

    private final EmpresaService   empresaService
    private final CandidatoService candidatoService
    private final MatchService     matchService

    EmpresaController(EmpresaService empresaService,
                      CandidatoService candidatoService,
                      MatchService matchService) {
        this.empresaService   = empresaService
        this.candidatoService = candidatoService
        this.matchService     = matchService
    }

    Empresa cadastrar(Map dados) {
        Empresa nova = new Empresa.Builder()
                .nome(dados.nome)
                .email(dados.email)
                .cnpj(dados.cnpj)
                .pais(dados.pais)
                .estado(dados.estado)
                .cep(dados.cep)
                .descricao(dados.descricao)
                .senha(dados.senha)
                .build()
        empresaService.cadastrar(nova)
        return nova
    }

    void editarDados(Empresa empresa, Map dados) {
        empresaService.aplicarEdicao(empresa, dados)
    }

    List<Candidato> listarCandidatos() {
        return candidatoService.listar()
    }

    void registrarLikeEmpresa(int idEmpresa, int idCandidato) {
        matchService.registrarLikeEmpresa(idEmpresa, idCandidato)
    }

    boolean houveMatch(int idCandidato, int idEmpresa) {
        return matchService.houveMatch(idCandidato, idEmpresa)
    }

    List<Map> obterMatches(int idEmpresa) {
        return matchService.obterMatchesEmpresa(idEmpresa)
    }
}