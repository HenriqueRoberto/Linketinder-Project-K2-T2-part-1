package linketinder.service

import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.model.Match
import linketinder.model.Vaga

class MatchService {

    private final EmpresaService empresaService
    private final CandidatoService candidatoService

    private Map<Integer, Set<Integer>> likesPorCandidato = [:]
    private Map<Integer, Set<Integer>> likesPorEmpresa   = [:]
    private List<Match> matches = []

    MatchService(EmpresaService empresaService, CandidatoService candidatoService) {
        this.empresaService = empresaService
        this.candidatoService = candidatoService
    }

    void registrarLikeCandidato(int idCandidato, int idVaga) {
        adicionarLike(likesPorCandidato, idCandidato, idVaga)
    }

    void registrarLikeEmpresa(int idEmpresa, int idCandidato) {
        adicionarLike(likesPorEmpresa, idEmpresa, idCandidato)
    }

    boolean houveMatch(int idCandidato, int idEmpresa) {
        boolean empresaCurtiuCandidato = likesPorEmpresa[idEmpresa]?.contains(idCandidato) ?: false
        if (!empresaCurtiuCandidato) return false

        Empresa empresa = empresaService.buscarPorId(idEmpresa)
        if (empresa == null) return false

        List<Vaga> vagasDaEmpresa = empresaService.listarVagasDaEmpresa(idEmpresa)
        Set<Integer> vagasCurtidasPeloCandidato = likesPorCandidato[idCandidato] ?: ([] as Set)
        List<Vaga> vagasEmComum = vagasDaEmpresa.findAll { vaga -> vagasCurtidasPeloCandidato.contains(vaga.id) }

        if (vagasEmComum.isEmpty()) return false

        vagasEmComum.each { vaga ->
            boolean matchJaRegistrado = matches.any { it.idCandidato == idCandidato && it.idVaga == vaga.id }
            if (!matchJaRegistrado) matches.add(new Match(idCandidato, vaga.id))
        }

        return true
    }

    List<Map> obterMatchesCandidato(int idCandidato) {
        List<Map> resultado = []

        matches.findAll { it.idCandidato == idCandidato }.each { match ->
            List<Empresa> todasEmpresas = empresaService.listar()
            List<Vaga> todasVagas = todasEmpresas.collectMany { empresaService.listarVagasDaEmpresa(it.id) }

            Vaga vaga = todasVagas.find { it.id == match.idVaga }
            if (vaga == null) return

            Empresa empresa = todasEmpresas.find { e -> empresaService.listarVagasDaEmpresa(e.id).any { v -> v.id == match.idVaga } }
            if (empresa == null) return

            resultado << [vaga: vaga, empresa: empresa]
        }

        return resultado
    }

    List<Map> obterMatchesEmpresa(int idEmpresa) {
        List<Map> resultado = []

        Empresa empresa = empresaService.buscarPorId(idEmpresa)
        if (empresa == null) return resultado

        empresaService.listarVagasDaEmpresa(idEmpresa).each { vaga ->
            List<Candidato> candidatosComMatch = matches
                    .findAll { it.idVaga == vaga.id }
                    .collect { match -> candidatoService.listar().find { c -> c.id == match.idCandidato } }
                    .findAll { it != null }

            if (!candidatosComMatch.isEmpty()) {
                resultado << [vaga: vaga, candidatos: candidatosComMatch]
            }
        }

        return resultado
    }

    private static void adicionarLike(Map<Integer, Set<Integer>> mapa, int chave, int valor) {
        if (!mapa[chave]) mapa[chave] = [] as Set
        mapa[chave].add(valor)
    }
}