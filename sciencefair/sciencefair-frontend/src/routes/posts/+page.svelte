<svelte:head>
	<title>Posts</title>
</svelte:head>

<script lang="ts">
	import { createPost, deletePost, getAllPosts } from '$lib/services/post.service';
	import { getAllProjetos } from '$lib/services/projeto.service';

	let posts = $state<any[]>([]);
	let projetos = $state<any[]>([]);

	let titulo = $state('');
	let conteudo = $state('');
	let projetoId = $state('');
	let mensagem = $state('');

	function formatDate(date: string): string {
		return new Date(date).toLocaleDateString('pt-BR');
	}

	async function carregarDados() {
		posts = await getAllPosts();
		projetos = await getAllProjetos();
	}

	async function salvarPost() {
		try {
			await createPost({
				titulo,
				conteudo,
				projeto: {
					id: Number(projetoId)
				}
			});

			titulo = '';
			conteudo = '';
			projetoId = '';
			mensagem = 'Post cadastrado com sucesso.';
			await carregarDados();
		} catch (e) {
			console.error(e);
			mensagem = 'Erro ao cadastrar post.';
		}
	}

	async function excluirPost(id: number) {
		try {
			await deletePost(id);
			mensagem = 'Post excluído com sucesso.';
			await carregarDados();
		} catch (e) {
			console.error(e);
			mensagem = 'Erro ao excluir post.';
		}
	}

	$effect(() => {
		carregarDados();
	});
</script>

<h1>Cadastro de Posts</h1>

<div>
	<input bind:value={titulo} placeholder="Título" />
	<textarea bind:value={conteudo} placeholder="Conteúdo"></textarea>

	<select bind:value={projetoId}>
		<option value="">Selecione um projeto</option>
		{#each projetos as projeto}
			<option value={projeto.id}>{projeto.titulo}</option>
		{/each}
	</select>

	<button onclick={salvarPost}>Cadastrar</button>
</div>

{#if mensagem}
	<p>{mensagem}</p>
{/if}

<h2>Lista de Posts</h2>

{#if posts.length > 0}
	<ul>
		{#each posts as post}
			<li>
				<strong>{post.titulo}</strong>
				— Projeto: {post.projeto?.titulo ?? post.projeto?.id}
				{#if post.dataCriacao}
					— {formatDate(post.dataCriacao)}
				{/if}
				<button onclick={() => excluirPost(post.id)}>Excluir</button>
			</li>
		{/each}
	</ul>
{:else}
	<p>Nenhum post cadastrado.</p>
{/if}
