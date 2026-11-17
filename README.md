# ScienceFair · Gerenciador de feira de ciências

Projeto acadêmico de Programação Web no IFBA. A aplicação reúne cadastros de feiras, projetos, alunos, professores e publicações em uma interface web.

## Arquitetura

- `sciencefair/sciencefair-backend/` · API REST com Java 17, Spring Boot, Spring Data JPA e banco de desenvolvimento H2 em arquivo.
- `sciencefair/sciencefair-frontend/` · Interface com SvelteKit, TypeScript e Vite. O frontend consulta a API local na porta 8080.

A API inclui `/auth/register`, `/auth/login`, `/feiras`, `/projetos`, `/alunos`, `/professores` e `/posts`. A interface oferece telas para cadastro, login e navegação pelos recursos da feira.

## Executar no Windows

Com JDK 17 e Node.js 22 instalado, abra dois terminais na raiz do repositório.

Backend:

```powershell
cd sciencefair/sciencefair-backend
.\mvnw.cmd spring-boot:run
```

Frontend:

```powershell
cd sciencefair/sciencefair-frontend
npm ci
npm run dev
```

Abra `http://localhost:5173`. Em macOS/Linux use `./mvnw spring-boot:run` no backend. Se o shell pedir permissão de execução do wrapper, execute `bash mvnw spring-boot:run`.

## Escopo

O H2 e a configuração de autenticação atendem ao ambiente acadêmico local. Uma publicação exigiria revisar permissões de acesso, configurações de ambiente e dados de demonstração.
