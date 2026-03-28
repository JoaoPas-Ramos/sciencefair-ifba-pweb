<svelte:head>
	<title>Gerenciador de Feira de Ciências</title>
</svelte:head>

<script lang="ts">
	let { data }: any = $props();

	function formatDate(date: string): string {
		return new Date(date).toLocaleDateString('pt-BR');
	}
</script>

<section class="hero">
	<h1>Posts dos Projetos</h1>
	<p class="hero__description">
		Acompanhe as atualizações dos projetos da feira de ciências.
	</p>
</section>

<section class="post-grid" aria-label="Posts dos projetos">
	{#if data.posts?.length > 0}
		{#each data.posts as post (post.id)}
			<article class="post-card">
				<div class="post-card__content">
					<h2>{post.titulo}</h2>
					<p class="post-card__excerpt">{post.conteudo}</p>

					<div class="post-card__author">
						<div>
							<p class="post-card__author-name">
								Projeto: {post.projeto?.titulo ?? 'Sem projeto'}
							</p>
							<p class="post-card__date">
								{formatDate(post.dataCriacao)}
							</p>
						</div>
					</div>
				</div>
			</article>
		{/each}
	{:else}
		<p>Nenhum post encontrado.</p>
	{/if}
</section>