package linketinder.service

import linketinder.model.Candidato
import linketinder.model.Empresa

class LoginService {

    private final CandidatoService candidatoService
    private final EmpresaService empresaService

    LoginService(CandidatoService candidatoService, EmpresaService empresaService) {
        this.candidatoService = candidatoService
        this.empresaService = empresaService
    }

    Object realizarLogin(String email, String senha) {
        Candidato candidato = candidatoService.listar().find { it.email == email && it.senha == senha }
        if (candidato) return candidato

        Empresa empresa = empresaService.listar().find { it.email == email && it.senha == senha }
        if (empresa) return empresa

        return null
    }
}