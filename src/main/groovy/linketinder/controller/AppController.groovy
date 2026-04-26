package linketinder.controller

import linketinder.dao.CandidatoDAO
import linketinder.dao.CompetenciaDAO
import linketinder.dao.EmpresaDAO
import linketinder.dao.VagaDAO
import linketinder.service.*
import linketinder.model.*
import linketinder.view.MenuView

class AppController {

    private final CandidatoService candidatoService
    private final EmpresaService empresaService
    private final CompetenciaService competenciaService
    private final MatchService matchService
    private final LoginService loginService

    private Object usuarioLogado = null

    // Construtor principal com injeção de dependência
    AppController(CandidatoService candidatoService,
                  EmpresaService empresaService,
                  CompetenciaService competenciaService,
                  MatchService matchService,
                  LoginService loginService) {
        this.candidatoService   = candidatoService
        this.empresaService     = empresaService
        this.competenciaService = competenciaService
        this.matchService       = matchService
        this.loginService       = loginService
    }

    // Factory method: monta todas as dependências reais
    static AppController criar() {
        def candidatoDAO    = new CandidatoDAO()
        def empresaDAO      = new EmpresaDAO()
        def competenciaDAO  = new CompetenciaDAO()
        def vagaDAO         = new VagaDAO()

        def candidatoService   = new CandidatoService(candidatoDAO)
        def empresaService     = new EmpresaService(empresaDAO, vagaDAO, competenciaDAO)
        def competenciaService = new CompetenciaService(competenciaDAO)
        def matchService       = new MatchService(empresaService, candidatoService)
        def loginService       = new LoginService(candidatoService, empresaService)

        return new AppController(candidatoService, empresaService, competenciaService, matchService, loginService)
    }

    void iniciar() {
        while (true) {
            MenuView.mostrarMenuInicial()
            switch (MenuView.lerOpcao()) {
                case 1: fluxoLogin(); break
                case 2: fluxoCadastroCandidato(); break
                case 3: fluxoCadastroEmpresa(); break
                case 0: return
            }
        }
    }

    private void fluxoLogin() {
        def credenciais = MenuView.lerCredenciaisLogin()
        usuarioLogado = loginService.realizarLogin(credenciais.email, credenciais.senha)

        if (usuarioLogado instanceof Candidato)  fluxoCandidato()
        else if (usuarioLogado instanceof Empresa) fluxoEmpresa()
        else MenuView.exibirMensagem("Erro: Credenciais inválidas.")
    }

    // ---- FLUXO CANDIDATO ----

    private void fluxoCandidato() {
        while (usuarioLogado != null) {
            MenuView.menuCandidato(usuarioLogado.nome)
            switch (MenuView.lerOpcao()) {
                case 1: MenuView.exibirPerfilLogado(usuarioLogado); break
                case 2: editarDadosCandidato(); break
                case 3: gerenciarCompetencias(usuarioLogado as Candidato); break
                case 4: explorarVagas(); break
                case 5: MenuView.exibirMatchesCandidato(matchService.obterMatchesCandidato(usuarioLogado.id)); break
                case 0: usuarioLogado = null; break
            }
        }
    }

    private void editarDadosCandidato() {
        def dados = MenuView.lerEdicaoCandidato(usuarioLogado as Candidato)
        candidatoService.aplicarEdicao(usuarioLogado as Candidato, dados)
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

    private void explorarVagas() {
        List<Vaga> vagas = empresaService.listarTodasVagas()
        if (vagas.isEmpty()) { MenuView.exibirMensagem("\nNenhuma vaga disponível no momento."); return }

        for (vaga in vagas) {
            MenuView.exibirVaga(vaga)
            MenuView.exibirOpcoesDeCurtir()
            String acao = MenuView.lerAcaoSwipe()

            if (acao == "L") {
                matchService.registrarLikeCandidato(usuarioLogado.id, vaga.id)
                Empresa empresa = empresaService.buscarPorId(vaga.idEmpresa)
                if (empresa && matchService.houveMatch(usuarioLogado.id, empresa.id)) {
                    MenuView.exibirMensagem("MATCH com a empresa ${empresa.nome}!")
                }
            } else if (acao == "S") break
        }
    }

    // ---- FLUXO EMPRESA ----

    private void fluxoEmpresa() {
        while (usuarioLogado != null) {
            MenuView.menuEmpresa(usuarioLogado.nome)
            switch (MenuView.lerOpcao()) {
                case 1: MenuView.exibirPerfilLogado(usuarioLogado); break
                case 2: editarDadosEmpresa(); break
                case 3: explorarCandidatos(); break
                case 4: MenuView.exibirMatchesEmpresa(matchService.obterMatchesEmpresa(usuarioLogado.id)); break
                case 5: fluxoGerenciarVagas(); break
                case 0: usuarioLogado = null; break
            }
        }
    }

    private void editarDadosEmpresa() {
        def dados = MenuView.lerEdicaoEmpresa(usuarioLogado as Empresa)
        empresaService.aplicarEdicao(usuarioLogado as Empresa, dados)
        MenuView.exibirMensagem("Sucesso: Dados atualizados!")
    }

    private void explorarCandidatos() {
        for (candidato in candidatoService.listar()) {
            MenuView.exibirCandidatoRestrito(candidato)
            MenuView.exibirOpcoesDeCurtir()
            String acao = MenuView.lerAcaoSwipe()

            if (acao == "L") {
                matchService.registrarLikeEmpresa(usuarioLogado.id, candidato.id)
                if (matchService.houveMatch(candidato.id, usuarioLogado.id)) {
                    MenuView.exibirMensagem("MATCH!")
                }
            } else if (acao == "S") break
        }
    }

    private void fluxoGerenciarVagas() {
        while (true) {
            MenuView.menuGerenciarVagas()
            switch (MenuView.lerOpcao()) {
                case 1: criarVaga(); break
                case 2: editarVaga(); break
                case 3: excluirVaga(); break
                case 4: listarVagasDaEmpresa(); break
                case 0: return
            }
        }
    }

    private void criarVaga() {
        def dados = MenuView.lerDadosNovaVaga()
        if ([dados.nome, dados.descricao, dados.horario, dados.localizacao, dados.remuneracao].any { it.isEmpty() }) {
            MenuView.exibirMensagem("Erro: Todos os campos são obrigatórios.")
            return
        }

        List<Competencia> competencias = []
        gerenciarCompetenciasVaga(competencias)

        try {
            empresaService.criarVaga(usuarioLogado.id, new Vaga(dados.nome, dados.descricao, dados.horario, dados.localizacao, dados.remuneracao, competencias, usuarioLogado.id))
            MenuView.exibirMensagem("Sucesso: Vaga criada!")
        } catch (IllegalArgumentException e) {
            MenuView.exibirMensagem("Erro: ${e.message}")
        }
    }

    private void editarVaga() {
        List<Vaga> vagas = empresaService.listarVagasDaEmpresa(usuarioLogado.id)
        if (vagas.isEmpty()) { MenuView.exibirMensagem("\nVocê não possui vagas cadastradas."); return }

        MenuView.exibirListaDeVagas(vagas)
        int indice = MenuView.lerNumeroVaga()
        if (indiceInvalido(indice, vagas.size())) return

        Vaga vaga = vagas[indice]
        def dados = MenuView.lerEdicaoVaga(vaga)

        List<Competencia> competencias = new ArrayList<>(vaga.competencias)
        gerenciarCompetenciasVaga(competencias)

        try {
            Vaga atualizada = new Vaga(
                    dados.nome        ?: vaga.nome,
                    dados.descricao   ?: vaga.descricao,
                    dados.horario     ?: vaga.horario,
                    dados.localizacao ?: vaga.localizacao,
                    dados.remuneracao ?: vaga.remuneracao,
                    competencias,
                    usuarioLogado.id
            )
            empresaService.editarVaga(usuarioLogado.id, indice, atualizada)
            MenuView.exibirMensagem("Sucesso: Vaga atualizada!")
        } catch (IllegalArgumentException e) {
            MenuView.exibirMensagem("Erro: ${e.message}")
        }
    }

    private void gerenciarCompetenciasVaga(List<Competencia> competencias) {
        while (true) {
            MenuView.menuCompetenciasVaga()
            switch (MenuView.lerOpcao()) {
                case 1:
                    String entrada = MenuView.lerNovaCompetencia()
                    if (!entrada.isEmpty()) {
                        competencias.add(new Competencia(entrada))
                        MenuView.exibirMensagem("Sucesso: '${entrada}' adicionada!")
                    }
                    break
                case 2:
                    if (competencias.isEmpty()) { MenuView.exibirMensagem("Nenhuma competência para excluir."); break }
                    int indice = MenuView.lerNumeroCompetencia()
                    if (!indiceInvalido(indice, competencias.size())) {
                        MenuView.exibirMensagem("Sucesso: '${competencias.remove(indice).nome}' removida!")
                    }
                    break
                case 3:
                    MenuView.exibirCompetencias(competencias)
                    break
                case 0: return
            }
        }
    }

    private void excluirVaga() {
        List<Vaga> vagas = empresaService.listarVagasDaEmpresa(usuarioLogado.id)
        if (vagas.isEmpty()) { MenuView.exibirMensagem("\nVocê não possui vagas cadastradas."); return }

        MenuView.exibirListaDeVagas(vagas)
        int indice = MenuView.lerNumeroVaga()

        try {
            empresaService.excluirVaga(usuarioLogado.id, indice)
            MenuView.exibirMensagem("Sucesso: Vaga excluída!")
        } catch (IllegalArgumentException e) {
            MenuView.exibirMensagem("Erro: ${e.message}")
        }
    }

    private void listarVagasDaEmpresa() {
        List<Vaga> vagas = empresaService.listarVagasDaEmpresa(usuarioLogado.id)
        MenuView.exibirMensagem("\n--- SUAS VAGAS ---")
        if (vagas.isEmpty()) { MenuView.exibirMensagem("Nenhuma vaga cadastrada."); return }
        vagas.eachWithIndex { v, i -> MenuView.exibirMensagem("\n[${i + 1}]\n${v}") }
    }

    private void fluxoCadastroCandidato() {
        def dados = MenuView.lerDadosCadastroCandidato()
        if ([dados.nome, dados.email, dados.cpf, dados.estado, dados.cep, dados.senha, dados.descricao].any { it.toString().isEmpty() }) {
            MenuView.exibirMensagem("Erro: Todos os campos são obrigatórios.")
            return
        }
        try {
            Candidato novo = new Candidato(dados.nome, dados.email, dados.cpf, dados.idade as int, dados.estado, dados.cep, dados.descricao, [], dados.senha)
            candidatoService.cadastrar(novo)
            gerenciarCompetencias(novo)
            MenuView.exibirMensagem("Sucesso: Cadastrado com sucesso!")
        } catch (IllegalArgumentException e) {
            MenuView.exibirMensagem("Erro: ${e.message}")
        }
    }

    private void fluxoCadastroEmpresa() {
        def dados = MenuView.lerDadosCadastroEmpresa()
        if ([dados.nome, dados.email, dados.cnpj, dados.pais, dados.estado, dados.cep, dados.senha, dados.descricao].any { it.isEmpty() }) {
            MenuView.exibirMensagem("Erro: Todos os campos são obrigatórios.")
            return
        }
        try {
            empresaService.cadastrar(new Empresa(dados.nome, dados.email, dados.cnpj, dados.pais, dados.estado, dados.cep, dados.descricao, dados.senha))
            MenuView.exibirMensagem("Sucesso: Cadastrada com sucesso!")
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