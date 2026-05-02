package linketinder.controller

import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.model.Vaga
import linketinder.service.CandidatoService
import linketinder.service.CompetenciaService
import linketinder.service.EmpresaService
import linketinder.service.MatchService
import linketinder.view.MenuView

class CandidatoController {

    private final CandidatoService candidatoService
    private final CompetenciaService competenciaService
    private final EmpresaService empresaService
    private final MatchService matchService

    CandidatoController(CandidatoService candidatoService,
                        CompetenciaService competenciaService,
                        EmpresaService empresaService,
                        MatchService matchService) {
        this.candidatoService   = candidatoService
        this.competenciaService = competenciaService
        this.empresaService     = empresaService
        this.matchService       = matchService
    }

    void iniciarFluxo(Candidato candidato) {
        while (candidato != null) {
            MenuView.menuCandidato(candidato.nome)
            switch (MenuView.lerOpcao()) {
                case 1: MenuView.exibirPerfilLogado(candidato); break
                case 2: editarDados(candidato); break
                case 3: gerenciarCompetencias(candidato); break
                case 4: explorarVagas(candidato); break
                case 5: MenuView.exibirMatchesCandidato(matchService.obterMatchesCandidato(candidato.id)); break
                case 0: return
            }
        }
    }

    private void editarDados(Candidato candidato) {
        def dados = MenuView.lerEdicaoCandidato(candidato)
        candidatoService.aplicarEdicao(candidato, dados)
        MenuView.exibirMensagem("Sucesso: Dados atualizados!")
    }

    private void gerenciarCompetencias(Candidato candidato) {
        while (true) {
            MenuView.menuCompetencias()
            switch (MenuView.lerOpcao()) {
                case 1: adicionarCompetencia(candidato); break
                case 2: editarCompetencia(candidato); break
                case 3: excluirCompetencia(candidato); break
                case 4: MenuView.exibirCompetencias(candidato.competencias); break
                case 0: return
            }
        }
    }

    private void adicionarCompetencia(Candidato candidato) {
        String entrada = MenuView.lerNovaCompetencia()
        if (entrada.isEmpty()) return
        competenciaService.adicionarAoCandidato(candidato, entrada)
        MenuView.exibirMensagem("Sucesso: '${entrada}' adicionada!")
    }

    private void editarCompetencia(Candidato candidato) {
        if (candidato.competencias.isEmpty()) { MenuView.exibirMensagem("Nenhuma competência para editar."); return }
        int indice = MenuView.lerNumeroCompetencia()
        if (indiceInvalido(indice, candidato.competencias.size())) return
        String novoNome = MenuView.lerEdicaoCompetencia(candidato.competencias[indice].nome)
        if (novoNome.isEmpty()) return
        competenciaService.editarDoCandidato(candidato, indice, novoNome)
        MenuView.exibirMensagem("Sucesso: competência atualizada!")
    }

    private void excluirCompetencia(Candidato candidato) {
        if (candidato.competencias.isEmpty()) { MenuView.exibirMensagem("Nenhuma competência para excluir."); return }
        int indice = MenuView.lerNumeroCompetencia()
        if (indiceInvalido(indice, candidato.competencias.size())) return
        String removida = competenciaService.removerDoCandidato(candidato, indice)
        MenuView.exibirMensagem("Sucesso: '${removida}' removida!")
    }

    private void explorarVagas(Candidato candidato) {
        List<Vaga> vagas = empresaService.listarTodasVagas()
        if (vagas.isEmpty()) { MenuView.exibirMensagem("\nNenhuma vaga disponível no momento."); return }

        for (vaga in vagas) {
            MenuView.exibirVaga(vaga)
            MenuView.exibirOpcoesDeCurtir()
            String acao = MenuView.lerAcaoSwipe()

            if (acao == "L") {
                matchService.registrarLikeCandidato(candidato.id, vaga.id)
                Empresa empresa = empresaService.buscarPorId(vaga.idEmpresa)
                if (empresa && matchService.houveMatch(candidato.id, empresa.id)) {
                    MenuView.exibirMensagem("MATCH com a empresa ${empresa.nome}!")
                }
            } else if (acao == "S") break
        }
    }

    void fluxoCadastro() {
        def dados = MenuView.lerDadosCadastroCandidato()
        if ([dados.nome, dados.email, dados.cpf, dados.estado, dados.cep, dados.senha, dados.descricao].any { it.toString().isEmpty() }) {
            MenuView.exibirMensagem("Erro: Todos os campos são obrigatórios.")
            return
        }
        try {
            Candidato novo = new Candidato.Builder()
                    .nome(dados.nome)
                    .email(dados.email)
                    .cpf(dados.cpf)
                    .idade(dados.idade as int)
                    .estado(dados.estado)
                    .cep(dados.cep)
                    .descricao(dados.descricao)
                    .competencias([])
                    .senha(dados.senha)
                    .build()
            candidatoService.cadastrar(novo)
            gerenciarCompetencias(novo)
            MenuView.exibirMensagem("Sucesso: Cadastrado com sucesso!")
        } catch (IllegalArgumentException e) {
            MenuView.exibirMensagem("Erro: ${e.message}")
        }
    }

    private static boolean indiceInvalido(int indice, int tamanho) {
        if (indice < 0 || indice >= tamanho) {
            MenuView.exibirMensagem("Erro: Número inválido.")
            return true
        }
        return false
    }
}