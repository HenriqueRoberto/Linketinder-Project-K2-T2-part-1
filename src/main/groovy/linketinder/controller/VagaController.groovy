package linketinder.controller

import linketinder.model.Competencia
import linketinder.model.Vaga
import linketinder.service.EmpresaService
import linketinder.view.MenuView

class VagaController {

    private final EmpresaService empresaService

    VagaController(EmpresaService empresaService) {
        this.empresaService = empresaService
    }

    void iniciarFluxo(int idEmpresa) {
        while (true) {
            MenuView.menuGerenciarVagas()
            switch (MenuView.lerOpcao()) {
                case 1: criarVaga(idEmpresa); break
                case 2: editarVaga(idEmpresa); break
                case 3: excluirVaga(idEmpresa); break
                case 4: listarVagas(idEmpresa); break
                case 0: return
            }
        }
    }

    private void criarVaga(int idEmpresa) {
        def dados = MenuView.lerDadosNovaVaga()
        if ([dados.nome, dados.descricao, dados.horario, dados.localizacao, dados.remuneracao].any { it.isEmpty() }) {
            MenuView.exibirMensagem("Erro: Todos os campos são obrigatórios.")
            return
        }

        List<Competencia> competencias = []
        gerenciarCompetenciasVaga(competencias)

        try {
            Vaga vaga = new Vaga.Builder()
                    .nome(dados.nome)
                    .descricao(dados.descricao)
                    .horario(dados.horario)
                    .localizacao(dados.localizacao)
                    .remuneracao(dados.remuneracao)
                    .competencias(competencias)
                    .idEmpresa(idEmpresa)
                    .build()
            empresaService.criarVaga(idEmpresa, vaga)
            MenuView.exibirMensagem("Sucesso: Vaga criada!")
        } catch (IllegalArgumentException e) {
            MenuView.exibirMensagem("Erro: ${e.message}")
        }
    }

    private void editarVaga(int idEmpresa) {
        List<Vaga> vagas = empresaService.listarVagasDaEmpresa(idEmpresa)
        if (vagas.isEmpty()) { MenuView.exibirMensagem("\nVocê não possui vagas cadastradas."); return }

        MenuView.exibirListaDeVagas(vagas)
        int indice = MenuView.lerNumeroVaga()
        if (indiceInvalido(indice, vagas.size())) return

        Vaga vaga = vagas[indice]
        def dados = MenuView.lerEdicaoVaga(vaga)

        List<Competencia> competencias = new ArrayList<>(vaga.competencias)
        gerenciarCompetenciasVaga(competencias)

        try {
            Vaga atualizada = new Vaga.Builder()
                    .nome(dados.nome ?: vaga.nome)
                    .descricao(dados.descricao ?: vaga.descricao)
                    .horario(dados.horario ?: vaga.horario)
                    .localizacao(dados.localizacao ?: vaga.localizacao)
                    .remuneracao(dados.remuneracao ?: vaga.remuneracao)
                    .competencias(competencias)
                    .idEmpresa(idEmpresa)
                    .build()
            empresaService.editarVaga(idEmpresa, indice, atualizada)
            MenuView.exibirMensagem("Sucesso: Vaga atualizada!")
        } catch (IllegalArgumentException e) {
            MenuView.exibirMensagem("Erro: ${e.message}")
        }
    }

    private void excluirVaga(int idEmpresa) {
        List<Vaga> vagas = empresaService.listarVagasDaEmpresa(idEmpresa)
        if (vagas.isEmpty()) { MenuView.exibirMensagem("\nVocê não possui vagas cadastradas."); return }

        MenuView.exibirListaDeVagas(vagas)
        int indice = MenuView.lerNumeroVaga()

        try {
            empresaService.excluirVaga(idEmpresa, indice)
            MenuView.exibirMensagem("Sucesso: Vaga excluída!")
        } catch (IllegalArgumentException e) {
            MenuView.exibirMensagem("Erro: ${e.message}")
        }
    }

    private void listarVagas(int idEmpresa) {
        List<Vaga> vagas = empresaService.listarVagasDaEmpresa(idEmpresa)
        MenuView.exibirMensagem("\n--- SUAS VAGAS ---")
        if (vagas.isEmpty()) { MenuView.exibirMensagem("Nenhuma vaga cadastrada."); return }
        vagas.eachWithIndex { v, i -> MenuView.exibirMensagem("\n[${i + 1}]\n${v}") }
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

    private static boolean indiceInvalido(int indice, int tamanho) {
        if (indice < 0 || indice >= tamanho) {
            MenuView.exibirMensagem("Erro: Número inválido.")
            return true
        }
        return false
    }
}