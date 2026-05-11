package linketinder.controller

import linketinder.dao.CandidatoDAO
import linketinder.dao.CompetenciaDAO
import linketinder.dao.EmpresaDAO
import linketinder.dao.VagaDAO
import linketinder.model.Pessoa
import linketinder.service.*

class AppController {

    final CandidatoController candidatoController
    final EmpresaController   empresaController
    final VagaController      vagaController

    private final LoginService loginService

    AppController(LoginService loginService,
                  CandidatoController candidatoController,
                  EmpresaController empresaController,
                  VagaController vagaController) {
        this.loginService        = loginService
        this.candidatoController = candidatoController
        this.empresaController   = empresaController
        this.vagaController      = vagaController
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
        def empresaController   = new EmpresaController(empresaService, candidatoService, matchService)

        return new AppController(loginService, candidatoController, empresaController, vagaController)
    }

    Pessoa login(String email, String senha) {
        return loginService.realizarLogin(email, senha)
    }
}