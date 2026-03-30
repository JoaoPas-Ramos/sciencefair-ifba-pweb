<svelte:head>
	<title>Gerenciador de Feira de Ciências</title>
</svelte:head>

<script lang="ts">
	import { goto } from '$app/navigation';

	let user = $state<any>(null);

	function carregarUsuario() {
		if (typeof localStorage !== 'undefined') {
			const storedUser = localStorage.getItem('user');
			user = storedUser ? JSON.parse(storedUser) : null;
		}
	}

	function logout() {
		localStorage.removeItem('user');
		user = null;
		goto('/login');
	}

	$effect(() => {
		carregarUsuario();
	});
</script>

<div class="app-shell">
	<header class="topbar">
		<div class="brand">
			<a href="/">ScienceFair</a>
		</div>

		<nav class="menu">
			<a href="/">Início</a>
			<a href="/feiras">Feiras</a>
			<a href="/professores">Professores</a>
			<a href="/projetos">Projetos</a>
			<a href="/alunos">Alunos</a>
			<a href="/posts">Posts</a>
		</nav>

		<div class="user-area">
			   {#if user}
				   <span class="user-name">Olá, {user.nome}</span>
				   <button class="logout-btn" onclick={logout}>Sair</button>
			   {:else}
				   <div class="auth-links">
					   <a href="/login">Login</a>
					   <a href="/register">Cadastro</a>
				   </div>
			   {/if}
		</div>
	</header>

	<main class="content">
		<slot />
	</main>
</div>

<style>
	:global(body) {
		margin: 0;
		font-family: Arial, Helvetica, sans-serif;
		background: #f5f7fb;
		color: #1f2937;
	}

	:global(a) {
		text-decoration: none;
		color: inherit;
	}

	.app-shell {
		min-height: 100vh;
	}

	.topbar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 1rem;
		padding: 1rem 1.5rem;
		background: #2563eb;
		border-bottom: 1px solid #e5e7eb;
		flex-wrap: wrap;
	}

	.brand a {
		font-size: 1.2rem;
		font-weight: 800;
		color: #ffffff;
	}

	.menu {
		display: flex;
		gap: 1rem;
		flex-wrap: wrap;
	}

	.menu a {
		padding: 0.5rem 0.75rem;
		border-radius: 10px;
		font-weight: 600;
	}

	.menu a:hover {
		background: #eff6ff;
		color: #2563eb;
	}

	.user-area {
		display: flex;
		align-items: center;
		gap: 0.75rem;
	}


	   .user-name {
		   font-weight: 600;
	   }

	   .auth-links {
		   display: flex;
		   gap: 0.75rem;
		   align-items: center;
	   }

	.logout-btn {
		border: none;
		background: #dc2626;
		color: white;
		padding: 0.55rem 0.9rem;
		border-radius: 10px;
		cursor: pointer;
		font-weight: 700;
	}

	.logout-btn:hover {
		background: #b91c1c;
	}

	.content {
		padding: 1.5rem;
	}

	@media (max-width: 768px) {
		.topbar {
			align-items: flex-start;
		}

		.menu {
			width: 100%;
		}

		.user-area {
			width: 100%;
			justify-content: flex-start;
		}
	}
</style>