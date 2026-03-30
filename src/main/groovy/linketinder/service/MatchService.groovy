package linketinder.service

import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.model.Vaga

class MatchService {

    // Likes do candidato em vagas: [idCandidato: Set<idVaga>]
    private static Map<Integer, Set<Integer>> likesCandidatos = [:]

    // Likes da empresa em candidatos: [idEmpresa: Set<idCandidato>]
    private static Map<Integer, Set<Integer>> likesEmpresas = [:]

    static void registrarLikeCandidato(int idCandidato, int idVaga) {
        if (!likesCandidatos[idCandidato]) likesCandidatos[idCandidato] = [] as Set
        likesCandidatos[idCandidato].add(idVaga)
    }

    static void registrarLikeEmpresa(int idEmpresa, int idCandidato) {
        if (!likesEmpresas[idEmpresa]) likesEmpresas[idEmpresa] = [] as Set
        likesEmpresas[idEmpresa].add(idCandidato)
    }

    // Match ocorre se: empresa curtiu o candidato E candidato curtiu uma vaga que ainda existe dessa empresa
    static boolean houveMatch(int idCandidato, int idEmpresa) {
        boolean empresaCurtiu = likesEmpresas[idEmpresa]?.contains(idCandidato) ?: false
        if (!empresaCurtiu) return false

        Empresa empresa = EmpresaService.buscarPorId(idEmpresa)
        if (empresa == null) return false

        Set<Integer> vagasCurtidas = likesCandidatos[idCandidato] ?: ([] as Set)

        // Verifica se alguma vaga curtida ainda existe na empresa
        return empresa.vagas.any { vaga -> vagasCurtidas.contains(vaga.id) }
    }

    // Retorna lista de maps com [vaga: Vaga, empresa: Empresa]
    static List<Map> obterMatchesCandidato(int idCandidato) {
        List<Map> resultado = []

        Set<Integer> vagasCurtidas = likesCandidatos[idCandidato] ?: ([] as Set)

        vagasCurtidas.each { idVaga ->
            // Busca a vaga pelo id em todas as empresas
            Empresa empresa = EmpresaService.listar().find { e -> e.vagas.any { v -> v.id == idVaga } }
            if (empresa == null) return  // Vaga não existe mais — match some

            Vaga vaga = empresa.vagas.find { v -> v.id == idVaga }
            if (vaga == null) return

            // Só inclui se a empresa também curtiu o candidato
            if (likesEmpresas[empresa.id]?.contains(idCandidato)) {
                resultado << [vaga: vaga, empresa: empresa]
            }
        }

        return resultado
    }

    // Retorna lista de maps com [vaga: Vaga, candidatos: List<Candidato>]
    static List<Map> obterMatchesEmpresa(int idEmpresa) {
        List<Map> resultado = []

        Empresa empresa = EmpresaService.buscarPorId(idEmpresa)
        if (empresa == null) return resultado

        empresa.vagas.each { vaga ->
            List<Candidato> candidatosComMatch = CandidatoService.listar().findAll { candidato ->
                likesEmpresas[idEmpresa]?.contains(candidato.id) &&
                        likesCandidatos[candidato.id]?.contains(vaga.id)
            }

            if (!candidatosComMatch.isEmpty()) {
                resultado << [vaga: vaga, candidatos: candidatosComMatch]
            }
        }

        return resultado
    }
}