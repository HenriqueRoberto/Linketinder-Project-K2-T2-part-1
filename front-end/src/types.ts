export type UserType = "candidato" | "empresa";

export interface BaseUser {
  id: string;
  nome: string;
  email: string;
  senha: string;
  tipo: UserType;
}

export interface Candidato extends BaseUser {
  tipo: "candidato";
  foto?: string;
  cpf: string;
  idade: string;
  estado: string;
  cep: string;
  descricao: string;
  competencias: string[];
}

export interface Empresa extends BaseUser {
  tipo: "empresa";
  foto?: string;
  cnpj: string;
  pais: string;
  estado: string;
  cep: string;
  descricao: string;
  vagas?: Vaga[];
}

export type User = Candidato | Empresa;

export interface Vaga {
  id: string;
  empresaId: string;
  titulo: string;
  descricao: string;
  horario: string;
  localizacao: string;
  remuneracao: string;
  requisitos: string;
  competencias: string[];
}
export interface LikeEmpresaCandidato {
  empresaId: string;
  candidatoId: string;
}

export interface LikeCandidatoVaga {
  candidatoId: string;
  vagaId: string;
}

export type Like = LikeEmpresaCandidato | LikeCandidatoVaga;

export interface Match {
  candidatoId: string;
  vagaId: string;
  empresaId: string;
}
