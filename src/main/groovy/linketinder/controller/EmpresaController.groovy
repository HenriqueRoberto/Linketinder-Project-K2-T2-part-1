package linketinder.controller

import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.service.CandidatoService
import linketinder.service.EmpresaService
import linketinder.service.MatchService
import linketinder.view.MenuView

class EmpresaController {

    private final EmpresaService empresaService
    private final CandidatoService candidatoService
    private final MatchService matchService
    private final VagaController vagaController

    EmpresaController(EmpresaService empresaService,
                      CandidatoService candidatoService,
                      MatchService matchService,
                      VagaController vagaController) {
        this.empresaService   = empresaService
        this.candidatoService = candidatoService
        this.matchService     = matchService
        this.vagaController   = vagaController
    }

    void iniciarFluxo(Empresa empresa) {
        while (empresa != null) {
            MenuView.menuEmpresa(empresa.nome)
            switch (MenuView.lerOpcao()) {
                case 1: MenuView.exibirPerfilLogado(empresa); break
                case 2: editarDados(empresa); break
                case 3: explorarCandidatos(empresa); break
                case 4: MenuView.exibirMatchesEmpresa(matchService.obterMatchesEmpresa(empresa.id)); break
                case 5: vagaController.iniciarFluxo(empresa.id); break
                case 0: return
            }
        }
    }

    private void editarDados(Empresa empresa) {
        def dados = MenuView.lerEdicaoEmpresa(empresa)
        empresaService.aplicarEdicao(empresa, dados)
        MenuView.exibirMensagem("Sucesso: Dados atualizados!")
    }

    private void explorarCandidatos(Empresa empresa) {
        for (candidato in candidatoService.listar()) {
            MenuView.exibirCandidatoRestrito(candidato)
            MenuView.exibirOpcoesDeCurtir()
            String acao = MenuView.lerAcaoSwipe()

            if (acao == "L") {
                matchService.registrarLikeEmpresa(empresa.id, candidato.id)
                if (matchService.houveMatch(candidato.id, empresa.id)) {
                    MenuView.exibirMensagem("MATCH!")
                }
            } else if (acao == "S") break
        }
    }

    void fluxoCadastro() {
        def dados = MenuView.lerDadosCadastroEmpresa()
        if ([dados.nome, dados.email, dados.cnpj, dados.pais, dados.estado, dados.cep, dados.senha, dados.descricao].any { it.isEmpty() }) {
            MenuView.exibirMensagem("Erro: Todos os campos são obrigatórios.")
            return
        }
        try {
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
            MenuView.exibirMensagem("Sucesso: Cadastrada com sucesso!")
        } catch (IllegalArgumentException e) {
            MenuView.exibirMensagem("Erro: ${e.message}")
        }
    }
}