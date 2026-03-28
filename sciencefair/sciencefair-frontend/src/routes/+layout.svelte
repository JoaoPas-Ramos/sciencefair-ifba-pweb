<script lang="ts">
	import { afterNavigate, goto } from '$app/navigation';
	import favicon from '$lib/assets/favicon.ico';
	import { clearAuthSession, getAuthenticatedUserName, isAdmin, isAuthenticated } from '$lib/utils/auth';
	import './+layout.css';
	import { onMount } from 'svelte';
	import type { Snippet } from 'svelte';
	import type { LayoutProps } from './$types';

	let { children, data }: LayoutProps = $props();
	let authenticatedUserName = $state<string | null>(null);
	let authenticated = $state(false);
	let admin = $state(false);

	function syncAuthenticatedUser() {
		authenticatedUserName = getAuthenticatedUserName();
		authenticated = isAuthenticated();
		admin = isAdmin();
	}

	afterNavigate(() => {
		syncAuthenticatedUser();
	});

	onMount(() => {
		syncAuthenticatedUser();
		const handleAuthChange = () => syncAuthenticatedUser();
		window.addEventListener('authchange', handleAuthChange);

		return () => {
			window.removeEventListener('authchange', handleAuthChange);
		};
	});

	async function logout() {
		clearAuthSession();
		syncAuthenticatedUser();
		await goto('/');
	}
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
	<title>IFBA Blog</title>
</svelte:head>

<div class="app-shell">
	<header class="topbar">
		<div class="topbar__inner">
			<a class="brand" href="/" aria-label="IFBA Blog home">
				<span class="brand__text">IFBA BLOG APP</span>
			</a>

			<form class="topbar__search" role="search" action="/">
				<label class="sr-only" for="site-search">Buscar posts</label>
				<input
					id="site-search"
					name="keyword"
					type="search"
					value={data.searchTerm}
					placeholder="Buscar posts"
				/>
				<button type="submit">Buscar</button>
			</form>

			<nav class="topbar__actions" aria-label="Authentication">
				<a class="button button--ghost" href="/register">Cadastrar</a>
				{#if admin}
					<a class="button button--ghost" href="/posts/cadastro">Novo post</a>
					<a class="button button--ghost" href="/categorias">Categorias</a>
				{/if}
				{#if authenticated}
					<a class="button button--ghost" href="/perfil">Perfil</a>
					<a class="button button--ghost" href="/perfil/senha">Senha</a>
					<span class="button button--ghost button--static">{authenticatedUserName}</span>
					<button class="button button--ghost button--danger" onclick={logout} type="button">
						Logout
					</button>
				{:else}
					<a class="button button--ghost" href="/login">Entrar</a>
				{/if}
			</nav>
		</div>
	</header>

	<main class="page-content">
		{@render children()}
	</main>
</div>
