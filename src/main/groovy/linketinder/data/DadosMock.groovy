package linketinder.data

import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.model.Vaga

class DadosMock {

    static List<Candidato> candidatos() {
        [
                new Candidato("Ana Silva", "ana@email.com", "11111111111", 22, "SC", "88000-000", "Desenvolvedora iniciante", ["Java", "SQL"], "senha123"),
                new Candidato("Bruno Costa", "bruno@email.com", "22222222222", 25, "SP", "01000-000", "Back-end Node", ["Node", "MongoDB"], "senha123"),
                new Candidato("Carla Souza", "carla@email.com", "33333333333", 24, "RS", "90000-000", "Front-end", ["Angular", "CSS"], "senha123"),
                new Candidato("Diego Alves", "diego@email.com", "44444444444", 28, "PR", "80000-000", "Fullstack", ["React", "Node"], "senha123"),
                new Candidato("Elisa Rocha", "elisa@email.com", "55555555555", 21, "RJ", "20000-000", "Estudante de TI", ["Python", "Pandas"], "senha123")
        ]
    }

    static List<Empresa> empresas() {
        def techsul = new Empresa("TechSul", "rh@techsul.com", "11111111000101", "Brasil", "RS", "90000-100", "Software sob demanda", "corp123")
        techsul.vagas << new Vaga("Dev Java Pleno", "08h-17h", "Porto Alegre - RS", "R\$ 6.000", ["Java", "Spring"], techsul.id)
        techsul.vagas << new Vaga("Analista de Sistemas", "09h-18h", "Remoto", "R\$ 7.500", ["Java", "SQL"], techsul.id)

        def datawave = new Empresa("DataWave", "jobs@datawave.com", "22222222000102", "Brasil", "SP", "01000-100", "Dados e analytics", "corp123")
        datawave.vagas << new Vaga("Engenheiro de Dados", "Flexível", "São Paulo - SP", "R\$ 9.000", ["Python", "SQL"], datawave.id)

        def cloudnova = new Empresa("CloudNova", "vagas@cloudnova.com", "33333333000103", "Brasil", "SC", "88000-100", "Infraestrutura cloud", "corp123")
        cloudnova.vagas << new Vaga("DevOps Junior", "08h-17h", "Florianópolis - SC", "R\$ 5.500", ["Docker", "Linux"], cloudnova.id)

        def webprime = new Empresa("WebPrime", "contato@webprime.com", "44444444000104", "Brasil", "PR", "80000-100", "Desenvolvimento web", "corp123")
        webprime.vagas << new Vaga("Dev Front-end", "09h-18h", "Curitiba - PR", "R\$ 5.000", ["React", "CSS"], webprime.id)
        webprime.vagas << new Vaga("Dev Back-end Node", "Flexível", "Remoto", "R\$ 6.500", ["Node", "MongoDB"], webprime.id)

        def inovatech = new Empresa("InovaTech", "talentos@inovatech.com", "55555555000105", "Brasil", "RJ", "20000-100", "Soluções digitais", "corp123")
        inovatech.vagas << new Vaga("Dev Angular Sênior", "08h-17h", "Rio de Janeiro - RJ", "R\$ 10.000", ["Angular", "TypeScript"], inovatech.id)

        [techsul, datawave, cloudnova, webprime, inovatech]
    }
}