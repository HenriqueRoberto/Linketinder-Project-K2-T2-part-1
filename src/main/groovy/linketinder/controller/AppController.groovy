package linketinder.controller

import linketinder.dao.CandidatoDAO
import linketinder.dao.CompetenciaDAO
import linketinder.dao.EmpresaDAO
import linketinder.dao.VagaDAO
import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.model.Pessoa
import linketinder.service.*
import linketinder.view.MenuView

class AppController {

    private final LoginService loginService
    private final CandidatoController candidatoController
    private final EmpresaController empresaController

    AppController(LoginService loginService,
                  CandidatoController candidatoController,
                  EmpresaController empresaController) {
        this.loginService        = loginService
        this.candidatoController = candidatoController
        this.empresaController   = empresaController
    }

    static AppController criar() {
        def candidatoDAO   = new CandidatoDAO()
        def empresaDAO     = new EmpresaDAO()
        def competenciaDAO = new CompetenciaDAO()
        def vagaDAO        = new VagaDAO()

        def candidatoService   = new CandidatoService(candidatoDAO)
        def empresaService     = new EmpresaService(empresaDAO, vagaDAO, competenciaDAO)
        def competenciaService = new CompetenciaService(competenciaDAO)
        def matchService       = new MatchService(empresaService, candidatoService)
        def loginService       = new LoginService(candidatoService, empresaService)

        def vagaController      = new VagaController(empresaService)
        def candidatoController = new CandidatoController(candidatoService, competenciaService, empresaService, matchService)
        def empresaController   = new EmpresaController(empresaService, candidatoService, matchService, vagaController)

        return new AppController(loginService, candidatoController, empresaController)
    }

    void iniciar() {
        while (true) {
            MenuView.mostrarMenuInicial()
            switch (MenuView.lerOpcao()) {
                case 1: fluxoLogin(); break
                case 2: candidatoController.fluxoCadastro(); break
                case 3: empresaController.fluxoCadastro(); break
                case 0: return
            }
        }
    }

    private void fluxoLogin() {
        def credenciais = MenuView.lerCredenciaisLogin()
        Pessoa usuario = loginService.realizarLogin(credenciais.email, credenciais.senha)

        if (usuario instanceof Candidato)  candidatoController.iniciarFluxo(usuario as Candidato)
        else if (usuario instanceof Empresa) empresaController.iniciarFluxo(usuario as Empresa)
        else MenuView.exibirMensagem("Erro: Credenciais inválidas.")
    }
}