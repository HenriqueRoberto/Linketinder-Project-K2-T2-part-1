package linketinder.view

import linketinder.controller.AppController
import linketinder.controller.CandidatoController
import linketinder.controller.EmpresaController
import linketinder.controller.VagaController
import linketinder.model.*

class MenuView {

    private final AppController       app
    private final CandidatoController candidatoCtrl
    private final EmpresaController   empresaCtrl
    private final VagaController      vagaCtrl
    private static Scanner scanner = new Scanner(System.in)

    MenuView(AppController app) {
        this.app          = app
        this.candidatoCtrl = app.candidatoController
        this.empresaCtrl  = app.empresaController
        this.vagaCtrl     = app.vagaController
    }

    // =========================================================
    // PONTO DE ENTRADA
    // =========================================================

    void iniciar() {
        while (true) {
            mostrarMenuInicial()
            switch (lerOpcao()) {
                case 1: fluxoLogin(); break
                case 2: fluxoCadastroCandidato(); break
                case 3: fluxoCadastroEmpresa(); break
                case 0: return
            }
        }
    }

    // =========================================================
    // FLUXOS PRINCIPAIS
    // =========================================================

    private void fluxoLogin() {
        def creds = lerCredenciaisLogin()
        Pessoa usuario = app.login(creds.email, creds.senha)

        if (usuario instanceof Candidato)  fluxoCandidato(usuario as Candidato)
        else if (usuario instanceof Empresa) fluxoEmpresa(usuario as Empresa)
        else exibirMensagem("Erro: Credenciais inválidas.")
    }

    private void fluxoCadastroCandidato() {
        def dados = lerDadosCadastroCandidato()
        if ([dados.nome, dados.email, dados.cpf, dados.estado, dados.cep, dados.senha, dados.descricao].any { it.toString().isEmpty() }) {
            exibirMensagem("Erro: Todos os campos são obrigatórios.")
            return
        }
        try {
            Candidato novo = candidatoCtrl.cadastrar(dados)
            fluxoCompetenciasCandidato(novo)
            exibirMensagem("Sucesso: Cadastrado com sucesso!")
        } catch (IllegalArgumentException e) {
            exibirMensagem("Erro: ${e.message}")
        }
    }

    private void fluxoCadastroEmpresa() {
        def dados = lerDadosCadastroEmpresa()
        if ([dados.nome, dados.email, dados.cnpj, dados.pais, dados.estado, dados.cep, dados.senha, dados.descricao].any { it.isEmpty() }) {
            exibirMensagem("Erro: Todos os campos são obrigatórios.")
            return
        }
        try {
            empresaCtrl.cadastrar(dados)
            exibirMensagem("Sucesso: Cadastrada com sucesso!")
        } catch (IllegalArgumentException e) {
            exibirMensagem("Erro: ${e.message}")
        }
    }

    // =========================================================
    // FLUXO CANDIDATO
    // =========================================================

    private void fluxoCandidato(Candidato candidato) {
        while (true) {
            menuCandidato(candidato.nome)
            switch (lerOpcao()) {
                case 1: exibirPerfilLogado(candidato); break
                case 2: fluxoEditarCandidato(candidato); break
                case 3: fluxoCompetenciasCandidato(candidato); break
                case 4: fluxoExplorarVagas(candidato); break
                case 5: exibirMatchesCandidato(candidatoCtrl.obterMatches(candidato.id)); break
                case 0: return
            }
        }
    }

    private void fluxoEditarCandidato(Candidato candidato) {
        def dados = lerEdicaoCandidato(candidato)
        candidatoCtrl.editarDados(candidato, dados)
        exibirMensagem("Sucesso: Dados atualizados!")
    }

    private void fluxoCompetenciasCandidato(Candidato candidato) {
        while (true) {
            menuCompetencias()
            switch (lerOpcao()) {
                case 1: fluxoAdicionarCompetencia(candidato); break
                case 2: fluxoEditarCompetencia(candidato); break
                case 3: fluxoExcluirCompetencia(candidato); break
                case 4: exibirCompetencias(candidato.competencias); break
                case 0: return
            }
        }
    }

    private void fluxoAdicionarCompetencia(Candidato candidato) {
        String entrada = lerNovaCompetencia()
        if (entrada.isEmpty()) return
        candidatoCtrl.adicionarCompetencia(candidato, entrada)
        exibirMensagem("Sucesso: '${entrada}' adicionada!")
    }

    private void fluxoEditarCompetencia(Candidato candidato) {
        if (candidato.competencias.isEmpty()) { exibirMensagem("Nenhuma competência para editar."); return }
        int indice = lerNumeroCompetencia()
        if (indiceInvalido(indice, candidato.competencias.size())) return
        String novoNome = lerEdicaoCompetencia(candidato.competencias[indice].nome)
        if (novoNome.isEmpty()) return
        candidatoCtrl.editarCompetencia(candidato, indice, novoNome)
        exibirMensagem("Sucesso: competência atualizada!")
    }

    private void fluxoExcluirCompetencia(Candidato candidato) {
        if (candidato.competencias.isEmpty()) { exibirMensagem("Nenhuma competência para excluir."); return }
        int indice = lerNumeroCompetencia()
        if (indiceInvalido(indice, candidato.competencias.size())) return
        String removida = candidatoCtrl.excluirCompetencia(candidato, indice)
        exibirMensagem("Sucesso: '${removida}' removida!")
    }

    private void fluxoExplorarVagas(Candidato candidato) {
        List<Vaga> vagas = candidatoCtrl.listarTodasVagas()
        if (vagas.isEmpty()) { exibirMensagem("\nNenhuma vaga disponível no momento."); return }

        for (vaga in vagas) {
            exibirVaga(vaga)
            exibirOpcoesDeCurtir()
            String acao = lerAcaoSwipe()

            if (acao == "L") {
                candidatoCtrl.registrarLikeCandidato(candidato.id, vaga.id)
                Empresa empresa = candidatoCtrl.buscarEmpresaPorId(vaga.idEmpresa)
                if (empresa && candidatoCtrl.houveMatch(candidato.id, empresa.id)) {
                    exibirMensagem("MATCH com a empresa ${empresa.nome}!")
                }
            } else if (acao == "S") break
        }
    }

    // =========================================================
    // FLUXO EMPRESA
    // =========================================================

    private void fluxoEmpresa(Empresa empresa) {
        while (true) {
            menuEmpresa(empresa.nome)
            switch (lerOpcao()) {
                case 1: exibirPerfilLogado(empresa); break
                case 2: fluxoEditarEmpresa(empresa); break
                case 3: fluxoExplorarCandidatos(empresa); break
                case 4: exibirMatchesEmpresa(empresaCtrl.obterMatches(empresa.id)); break
                case 5: fluxoGerenciarVagas(empresa.id); break
                case 0: return
            }
        }
    }

    private void fluxoEditarEmpresa(Empresa empresa) {
        def dados = lerEdicaoEmpresa(empresa)
        empresaCtrl.editarDados(empresa, dados)
        exibirMensagem("Sucesso: Dados atualizados!")
    }

    private void fluxoExplorarCandidatos(Empresa empresa) {
        for (candidato in empresaCtrl.listarCandidatos()) {
            exibirCandidatoRestrito(candidato)
            exibirOpcoesDeCurtir()
            String acao = lerAcaoSwipe()

            if (acao == "L") {
                empresaCtrl.registrarLikeEmpresa(empresa.id, candidato.id)
                if (empresaCtrl.houveMatch(candidato.id, empresa.id)) {
                    exibirMensagem("MATCH!")
                }
            } else if (acao == "S") break
        }
    }

    // =========================================================
    // FLUXO VAGAS
    // =========================================================

    private void fluxoGerenciarVagas(int idEmpresa) {
        while (true) {
            menuGerenciarVagas()
            switch (lerOpcao()) {
                case 1: fluxoCriarVaga(idEmpresa); break
                case 2: fluxoEditarVaga(idEmpresa); break
                case 3: fluxoExcluirVaga(idEmpresa); break
                case 4: fluxoListarVagas(idEmpresa); break
                case 0: return
            }
        }
    }

    private void fluxoCriarVaga(int idEmpresa) {
        def dados = lerDadosNovaVaga()
        if ([dados.nome, dados.descricao, dados.horario, dados.localizacao, dados.remuneracao].any { it.isEmpty() }) {
            exibirMensagem("Erro: Todos os campos são obrigatórios.")
            return
        }
        List<Competencia> competencias = []
        fluxoCompetenciasVaga(competencias)
        try {
            vagaCtrl.criar(idEmpresa, dados, competencias)
            exibirMensagem("Sucesso: Vaga criada!")
        } catch (IllegalArgumentException e) {
            exibirMensagem("Erro: ${e.message}")
        }
    }

    private void fluxoEditarVaga(int idEmpresa) {
        List<Vaga> vagas = vagaCtrl.listar(idEmpresa)
        if (vagas.isEmpty()) { exibirMensagem("\nVocê não possui vagas cadastradas."); return }

        exibirListaDeVagas(vagas)
        int indice = lerNumeroVaga()
        if (indiceInvalido(indice, vagas.size())) return

        def dados = lerEdicaoVaga(vagas[indice])
        List<Competencia> competencias = new ArrayList<>(vagas[indice].competencias)
        fluxoCompetenciasVaga(competencias)
        try {
            vagaCtrl.editar(idEmpresa, indice, dados, competencias)
            exibirMensagem("Sucesso: Vaga atualizada!")
        } catch (IllegalArgumentException e) {
            exibirMensagem("Erro: ${e.message}")
        }
    }

    private void fluxoExcluirVaga(int idEmpresa) {
        List<Vaga> vagas = vagaCtrl.listar(idEmpresa)
        if (vagas.isEmpty()) { exibirMensagem("\nVocê não possui vagas cadastradas."); return }

        exibirListaDeVagas(vagas)
        int indice = lerNumeroVaga()
        try {
            vagaCtrl.excluir(idEmpresa, indice)
            exibirMensagem("Sucesso: Vaga excluída!")
        } catch (IllegalArgumentException e) {
            exibirMensagem("Erro: ${e.message}")
        }
    }

    private void fluxoListarVagas(int idEmpresa) {
        List<Vaga> vagas = vagaCtrl.listar(idEmpresa)
        exibirMensagem("\n--- SUAS VAGAS ---")
        if (vagas.isEmpty()) { exibirMensagem("Nenhuma vaga cadastrada."); return }
        vagas.eachWithIndex { v, i -> exibirMensagem("\n[${i + 1}]\n${v}") }
    }

    private void fluxoCompetenciasVaga(List<Competencia> competencias) {
        while (true) {
            menuCompetenciasVaga()
            switch (lerOpcao()) {
                case 1:
                    String entrada = lerNovaCompetencia()
                    if (!entrada.isEmpty()) {
                        competencias.add(new Competencia(entrada))
                        exibirMensagem("Sucesso: '${entrada}' adicionada!")
                    }
                    break
                case 2:
                    if (competencias.isEmpty()) { exibirMensagem("Nenhuma competência para excluir."); break }
                    int indice = lerNumeroCompetencia()
                    if (!indiceInvalido(indice, competencias.size())) {
                        exibirMensagem("Sucesso: '${competencias.remove(indice).nome}' removida!")
                    }
                    break
                case 3:
                    exibirCompetencias(competencias)
                    break
                case 0: return
            }
        }
    }

    // =========================================================
    // MENUS (exibição)
    // =========================================================

    private static void mostrarMenuInicial() {
        println "\n=== LINKETINDER ==="
        println "1 - Login"
        println "2 - Cadastrar Candidato"
        println "3 - Cadastrar Empresa"
        println "0 - Sair"
        print "Escolha uma opção: "
    }

    private static void menuCandidato(String nome) {
        println "\n--- MENU CANDIDATO: ${nome} ---"
        println "1 - Ver Meus Dados"
        println "2 - Editar Meus Dados"
        println "3 - Gerenciar Competências"
        println "4 - Explorar Vagas"
        println "5 - Ver Meus Matches"
        println "0 - Logout"
        print "Escolha uma opção: "
    }

    private static void menuCompetencias() {
        println "\n--- COMPETÊNCIAS ---"
        println "1 - Adicionar Competência"
        println "2 - Editar Competência"
        println "3 - Excluir Competência"
        println "4 - Listar Competências"
        println "0 - Voltar"
        print "Escolha uma opção: "
    }

    private static void menuCompetenciasVaga() {
        println "\n--- COMPETÊNCIAS DA VAGA ---"
        println "1 - Adicionar Competência"
        println "2 - Excluir Competência"
        println "3 - Listar Competências"
        println "0 - Voltar"
        print "Escolha uma opção: "
    }

    private static void menuEmpresa(String nome) {
        println "\n--- MENU EMPRESA: ${nome} ---"
        println "1 - Ver Meus Dados"
        println "2 - Editar Meus Dados"
        println "3 - Explorar Candidatos"
        println "4 - Ver Meus Matches"
        println "5 - Gerenciar Vagas"
        println "0 - Logout"
        print "Escolha uma opção: "
    }

    private static void menuGerenciarVagas() {
        println "\n--- GERENCIAR VAGAS ---"
        println "1 - Criar Vaga"
        println "2 - Editar Vaga"
        println "3 - Excluir Vaga"
        println "4 - Listar Minhas Vagas"
        println "0 - Voltar"
        print "Escolha uma opção: "
    }

    // =========================================================
    // LEITURA DE DADOS
    // =========================================================

    private static int lerOpcao() {
        int op = scanner.nextInt()
        scanner.nextLine()
        return op
    }

    private static Map<String, String> lerCredenciaisLogin() {
        println "\n--- LOGIN ---"
        print "Email: "; String email = scanner.nextLine().trim()
        print "Senha: "; String senha = scanner.nextLine().trim()
        return [email: email, senha: senha]
    }

    private static Map<String, Object> lerDadosCadastroCandidato() {
        println "\n--- NOVO CANDIDATO ---"
        print "Nome: ";      String nome   = scanner.nextLine().trim()
        print "Email: ";     String email  = scanner.nextLine().trim()
        print "CPF: ";       String cpf    = scanner.nextLine().trim()
        print "Idade: ";     int    idade  = scanner.nextInt(); scanner.nextLine()
        print "Estado: ";    String estado = scanner.nextLine().trim()
        print "CEP: ";       String cep    = scanner.nextLine().trim()
        print "Senha: ";     String senha  = scanner.nextLine().trim()
        print "Descrição: "; String desc   = scanner.nextLine().trim()
        return [nome: nome, email: email, cpf: cpf, idade: idade, estado: estado, cep: cep, senha: senha, descricao: desc]
    }

    private static Map<String, String> lerDadosCadastroEmpresa() {
        println "\n--- NOVA EMPRESA ---"
        print "Nome: ";      String nome   = scanner.nextLine().trim()
        print "Email: ";     String email  = scanner.nextLine().trim()
        print "CNPJ: ";      String cnpj   = scanner.nextLine().trim()
        print "País: ";      String pais   = scanner.nextLine().trim()
        print "Estado: ";    String estado = scanner.nextLine().trim()
        print "CEP: ";       String cep    = scanner.nextLine().trim()
        print "Senha: ";     String senha  = scanner.nextLine().trim()
        print "Descrição: "; String desc   = scanner.nextLine().trim()
        return [nome: nome, email: email, cnpj: cnpj, pais: pais, estado: estado, cep: cep, senha: senha, descricao: desc]
    }

    private static Map<String, String> lerEdicaoCandidato(Candidato candidato) {
        println "\n--- EDITAR MEUS DADOS ---"
        println "Deixe em branco para manter o valor atual."
        return [
                nome:      lerCampoOpcional("Nome",      candidato.nome),
                cpf:       lerCampoOpcional("CPF",       candidato.cpf),
                idade:     lerCampoOpcional("Idade",     String.valueOf(candidato.idade)),
                estado:    lerCampoOpcional("Estado",    candidato.estado),
                cep:       lerCampoOpcional("CEP",       candidato.cep),
                descricao: lerCampoOpcional("Descrição", candidato.descricao),
                senha:     lerCampoSenhaOpcional()
        ]
    }

    private static Map<String, String> lerEdicaoEmpresa(Empresa empresa) {
        println "\n--- EDITAR MEUS DADOS ---"
        println "Deixe em branco para manter o valor atual."
        return [
                nome:      lerCampoOpcional("Nome",      empresa.nome),
                cnpj:      lerCampoOpcional("CNPJ",      empresa.cnpj),
                pais:      lerCampoOpcional("País",      empresa.pais),
                estado:    lerCampoOpcional("Estado",    empresa.estado),
                cep:       lerCampoOpcional("CEP",       empresa.cep),
                descricao: lerCampoOpcional("Descrição", empresa.descricao),
                senha:     lerCampoSenhaOpcional()
        ]
    }

    private static Map<String, String> lerEdicaoVaga(Vaga vaga) {
        println "\n--- EDITANDO: ${vaga.nome} ---"
        println "Deixe em branco para manter o valor atual."
        return [
                nome:        lerCampoOpcional("Nome",        vaga.nome),
                descricao:   lerCampoOpcional("Descrição",   vaga.descricao),
                horario:     lerCampoOpcional("Horário",     vaga.horario),
                localizacao: lerCampoOpcional("Localização", vaga.localizacao),
                remuneracao: lerCampoOpcional("Remuneração", vaga.remuneracao)
        ]
    }

    private static Map<String, String> lerDadosNovaVaga() {
        println "\n--- NOVA VAGA ---"
        print "Nome: ";        String nome    = scanner.nextLine().trim()
        print "Descrição: ";   String desc    = scanner.nextLine().trim()
        print "Horário: ";     String horario = scanner.nextLine().trim()
        print "Localização: "; String local   = scanner.nextLine().trim()
        print "Remuneração: "; String remun   = scanner.nextLine().trim()
        return [nome: nome, descricao: desc, horario: horario, localizacao: local, remuneracao: remun]
    }

    private static String lerNovaCompetencia() {
        print "Nova competência: "
        return scanner.nextLine().trim()
    }

    private static int lerNumeroCompetencia() {
        print "Número da competência: "
        int num = scanner.nextInt()
        scanner.nextLine()
        return num - 1
    }

    private static String lerEdicaoCompetencia(String nomeAtual) {
        print "Novo valor [${nomeAtual}]: "
        return scanner.nextLine().trim()
    }

    private static String lerAcaoSwipe() {
        return scanner.nextLine().toUpperCase()
    }

    private static int lerNumeroVaga() {
        int num = scanner.nextInt()
        scanner.nextLine()
        return num - 1
    }

    // =========================================================
    // EXIBIÇÃO
    // =========================================================

    private static void exibirPerfilLogado(Pessoa usuario) {
        println "\n--- SEU PERFIL ---"
        println usuario.toString()
    }

    private static void exibirOpcoesDeCurtir() {
        print "\n[L] Curtir | [P] Próximo | [S] Sair: "
    }

    private static void exibirVaga(Vaga vaga) {
        println "\n------------------------"
        println vaga.toString()
    }

    private static void exibirListaDeVagas(List<Vaga> vagas) {
        println "\n--- SUAS VAGAS ---"
        vagas.eachWithIndex { v, i -> println "${i + 1}. ${v.nome} | ${v.localizacao}" }
    }

    private static void exibirCompetencias(List<Competencia> competencias) {
        println "\n--- COMPETÊNCIAS ---"
        if (competencias.isEmpty()) {
            println "Nenhuma competência cadastrada."
        } else {
            competencias.eachWithIndex { comp, i -> println "${i + 1}. ${comp.nome}" }
        }
    }

    private static void exibirCandidatoRestrito(Candidato candidato) {
        println "\n------------------------"
        println "Descrição: ${candidato.descricao ?: 'Sem descrição'}"
        println "Competências: ${formatarCompetencias(candidato.competencias)}"
        println "Estado: ${candidato.estado}"
        println "CEP: ${candidato.cep}"
    }

    private static void exibirMatchesCandidato(List<Map> matches) {
        println "\n--- SEUS MATCHES ---"
        if (matches.isEmpty()) { println "Nenhum match encontrado até o momento."; return }

        matches.each { m ->
            Vaga vaga = m.vaga
            Empresa empresa = m.empresa
            println "\n============================"
            println "[ VAGA ]"
            println vaga.toString()
            println "\n[ EMPRESA ]"
            println "Nome: ${empresa.nome}"
            println "Email: ${empresa.email}"
            println "CNPJ: ${empresa.cnpj}"
            println "Descrição: ${empresa.descricao ?: 'Sem descrição'}"
            println "País: ${empresa.pais}"
            println "Estado: ${empresa.estado}"
            println "CEP: ${empresa.cep}"
        }
    }

    private static void exibirMatchesEmpresa(List<Map> matches) {
        println "\n--- SEUS MATCHES ---"
        if (matches.isEmpty()) { println "Nenhum match encontrado até o momento."; return }

        matches.each { m ->
            println "\n============================"
            println "[ VAGA ] ${m.vaga.descricao}"
            println "Candidatos com match:"
            (m.candidatos as List<Candidato>).each { c ->
                println "---"
                exibirCandidatoCompleto(c)
            }
        }
    }

    private static void exibirMensagem(String mensagem) {
        println mensagem
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private boolean indiceInvalido(int indice, int tamanho) {
        if (indice < 0 || indice >= tamanho) {
            exibirMensagem("Erro: Número inválido.")
            return true
        }
        return false
    }

    private static void exibirCandidatoCompleto(Candidato candidato) {
        println "\n  Nome: ${candidato.nome}"
        println "  CPF: ${candidato.cpf}"
        println "  Idade: ${candidato.idade}"
        println "  Estado: ${candidato.estado}"
        println "  CEP: ${candidato.cep}"
        println "  Descrição: ${candidato.descricao ?: 'Sem descrição'}"
        println "  Competências: ${formatarCompetencias(candidato.competencias)}"
    }

    private static String formatarCompetencias(List<Competencia> competencias) {
        if (!competencias) return "sem competências cadastradas"
        return competencias.collect { it.nome }.join(", ")
    }

    private static String lerCampoOpcional(String label, String valorAtual) {
        print "${label} [${valorAtual}]: "
        return scanner.nextLine().trim()
    }

    private static String lerCampoSenhaOpcional() {
        print "Senha [****]: "
        return scanner.nextLine().trim()
    }
}