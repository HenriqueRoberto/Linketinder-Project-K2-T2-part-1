import { User } from "../types.js";
import { StorageService } from "../services/StorageService.js";

export class AuthService {
  register(user: User): void {
    const users = StorageService.getUsers();

    const exists = users.some((u) => u.email === user.email);

    if (exists) {
      alert("Email já cadastrado");
      return;
    }

    users.push(user);
    StorageService.saveUsers(users);

    alert("Cadastro realizado!");
    document.dispatchEvent(new CustomEvent("goToLogin"));
  }

  login(email: string, senha: string): void {
    const users = StorageService.getUsers();

    const user = users.find((u) => u.email === email && u.senha === senha);

    if (!user) {
      alert("Login inválido");
      return;
    }

    StorageService.saveCurrentUser(user);
    window.location.href = "app.html";
  }

  logout(): void {
    StorageService.clearCurrentUser();
    window.location.href = "auth.html";
  }
}
