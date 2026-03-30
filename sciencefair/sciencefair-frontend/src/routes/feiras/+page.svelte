<svelte:head>
	<title>Feiras</title>
</svelte:head>

<script lang="ts">

	import { createFeira, deleteFeira, getAllFeiras } from '$lib/services/feira.service';
	import { goto } from '$app/navigation';
	import { isLoggedIn } from '$lib/utils/session';

	let logado = $state(false);

	let feiras = $state<any[]>([]);
	let nome = $state('');
	let descricao = $state('');
	let dataInicio = $state('');
	let dataFim = $state('');
	let local = $state('');
	let mensagem = $state('');

	async function carregarFeiras() {
		feiras = await getAllFeiras();
	}

	async function salvarFeira() {
		try {
			await createFeira({
				nome,
				descricao,
				dataInicio,
				dataFim,
				local
			});

			nome = '';
			descricao = '';
			dataInicio = '';
			dataFim = '';
			local = '';
			mensagem = 'Feira cadastrada com sucesso.';
			await carregarFeiras();
		} catch (e) {
			console.error(e);
			mensagem = 'Erro ao cadastrar feira.';
		}
	}

	async function excluirFeira(id: number) {
		try {
			await deleteFeira(id);
			mensagem = 'Feira excluída com sucesso.';
			await carregarFeiras();
		} catch (e) {
			console.error(e);
			mensagem = 'Erro ao excluir feira.';
		}
	}

	$effect(() => {
		logado = isLoggedIn();
		carregarFeiras();
	});
</script>


<h1>Cadastro de Feiras</h1>

{#if logado}
	<div>
		<input bind:value={nome} placeholder="Nome" />
		<input bind:value={descricao} placeholder="Descrição" />
		<input bind:value={dataInicio} type="date" />
		<input bind:value={dataFim} type="date" />
		<input bind:value={local} placeholder="Local" />
		<button onclick={salvarFeira}>Cadastrar</button>
	</div>
{:else}
	<p>Você precisa estar logado para cadastrar feiras.</p>
{/if}

{#if mensagem}
	<p>{mensagem}</p>
{/if}

<h2>Lista de Feiras</h2>

{#if feiras.length > 0}
	<ul>
		{#each feiras as feira}
			<li>
				<strong>{feira.nome}</strong> — {feira.local}
				{#if logado}
					<button onclick={() => excluirFeira(feira.id)}>Excluir</button>
				{/if}
			</li>
		{/each}
	</ul>
{:else}
	<p>Nenhuma feira cadastrada.</p>
{/if}