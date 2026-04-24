import {
  Candidato,
  Empresa,
  LikeCandidatoVaga,
  LikeEmpresaCandidato,
  Match,
  Vaga,
} from "../types.js";
import { StorageService } from "./StorageService.js";

export interface MatchDoCandidato {
  vaga: Vaga;
  empresa: Empresa;
}

export interface MatchDaEmpresa {
  vaga: Vaga;
  candidatos: Candidato[];
}

export class MatchService {
  static registrarLikeCandidatoVaga(candidatoId: string, vagaId: string): void {
    StorageService.salvarLike({ candidatoId, vagaId });
  }

  static registrarLikeEmpresaCandidato(
    empresaId: string,
    candidatoId: string,
  ): void {
    StorageService.salvarLike({ empresaId, candidatoId });
  }

  static obterMatchesCompletosDoCandidato(
    candidatoId: string,
  ): MatchDoCandidato[] {
    return this.calcularMatches()
      .filter((m: Match) => m.candidatoId === candidatoId)
      .reduce((acc: MatchDoCandidato[], match: Match) => {
        const empresa = StorageService.obterEmpresaPorVaga(match.vagaId);
        const vaga = StorageService.obterTodasVagas().find(
          (v: Vaga) => v.id === match.vagaId,
        );
        if (empresa && vaga) acc.push({ vaga, empresa });
        return acc;
      }, []);
  }

  static obterMatchesCompletosDaEmpresa(empresaId: string): MatchDaEmpresa[] {
    const matchesDaEmpresa = this.calcularMatches().filter(
      (m: Match) => m.empresaId === empresaId,
    );
    const vagasUnicas = matchesDaEmpresa
      .map((m: Match) => m.vagaId)
      .filter(
        (id: string, indice: number, arr: string[]) =>
          arr.indexOf(id) === indice,
      );

    return vagasUnicas.reduce((acc: MatchDaEmpresa[], vagaId: string) => {
      const empresa = StorageService.obterEmpresaPorVaga(vagaId);
      const vaga = empresa?.vagas?.find((v: Vaga) => v.id === vagaId);
      if (!vaga) return acc;

      const candidatos = matchesDaEmpresa
        .filter((m: Match) => m.vagaId === vagaId)
        .reduce((lista: Candidato[], m: Match) => {
          const candidato = StorageService.obterUsuarios().find(
            (u): u is Candidato =>
              u.tipo === "candidato" && u.id === m.candidatoId,
          );
          if (candidato) lista.push(candidato);
          return lista;
        }, []);

      acc.push({ vaga, candidatos });
      return acc;
    }, []);
  }

  static obterVagasNaoVistasPeloCandidato(candidatoId: string): Vaga[] {
    const likes = StorageService.getLikes();
    return StorageService.obterTodasVagas().filter(
      (vaga: Vaga) =>
        !likes.some(
          (like): like is LikeCandidatoVaga =>
            "vagaId" in like &&
            like.candidatoId === candidatoId &&
            like.vagaId === vaga.id,
        ),
    );
  }

  static obterCandidatosParaSwipe(): Candidato[] {
    return StorageService.obterUsuarios().filter(
      (u): u is Candidato => u.tipo === "candidato",
    );
  }

  private static calcularMatches(): Match[] {
    const likes = StorageService.getLikes();

    const likesCandidatoVaga = likes.filter(
      (l): l is LikeCandidatoVaga => "vagaId" in l,
    );
    const likesEmpresaCandidato = likes.filter(
      (l): l is LikeEmpresaCandidato => "empresaId" in l && !("vagaId" in l),
    );

    const matches: Match[] = [];

    likesCandidatoVaga.forEach((likeCV: LikeCandidatoVaga) => {
      const empresa = StorageService.obterEmpresaPorVaga(likeCV.vagaId);
      if (!empresa) return;

      const empresaCurtiuCandidato = likesEmpresaCandidato.some(
        (likeEC: LikeEmpresaCandidato) =>
          likeEC.empresaId === empresa.id &&
          likeEC.candidatoId === likeCV.candidatoId,
      );
      if (!empresaCurtiuCandidato) return;

      const matchJaRegistrado = matches.some(
        (m: Match) =>
          m.candidatoId === likeCV.candidatoId && m.vagaId === likeCV.vagaId,
      );
      if (!matchJaRegistrado) {
        matches.push({
          candidatoId: likeCV.candidatoId,
          vagaId: likeCV.vagaId,
          empresaId: empresa.id,
        });
      }
    });

    return matches;
  }
}
