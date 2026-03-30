<svelte:head>
	<title>Projetos</title>
</svelte:head>

<script lang="ts">
	import { createProjeto, deleteProjeto, getAllProjetos } from '$lib/services/projeto.service';
	import { getAllFeiras } from '$lib/services/feira.service';
	import { getAllProfessores } from '$lib/services/professor.service';

	let projetos = $state<any[]>([]);
	let feiras = $state<any[]>([]);
	let professores = $state<any[]>([]);

	let titulo = $state('');
	let descricao = $state('');
	let areaTematica = $state('');
	let status = $state('PENDENTE');
	let feiraId = $state('');
	let professorId = $state('');
	let mensagem = $state('');

	async function carregarDados() {
		projetos = await getAllProjetos();
		feiras = await getAllFeiras();
		professores = await getAllProfessores();
	}

	async function salvarProjeto() {
		try {
			await createProjeto({
				titulo,
				descricao,
				areaTematica,
				status,
				feira: {
					id: Number(feiraId)
				},
				professor: {
					id: Number(professorId)
				}
			});

			titulo = '';
			descricao = '';
			areaTematica = '';
			status = 'PENDENTE';
			feiraId = '';
			professorId = '';
			mensagem = 'Projeto cadastrado com sucesso.';
			await carregarDados();
		} catch (e) {
			console.error(e);
			mensagem = 'Erro ao cadastrar projeto.';
		}
	}

	async function excluirProjeto(id: number) {
		try {
			await deleteProjeto(id);
			mensagem = 'Projeto excluído com sucesso.';
			await carregarDados();
		} catch (e) {
			console.error(e);
			mensagem = 'Erro ao excluir projeto.';
		}
	}

	$effect(() => {
		carregarDados();
	});
</script>

<h1>Cadastro de Projetos</h1>

<div>
	<input bind:value={titulo} placeholder="Título" />
	<input bind:value={descricao} placeholder="Descrição" />
	<input bind:value={areaTematica} placeholder="Área temática" />

	<select bind:value={status}>
		<option value="PENDENTE">PENDENTE</option>
		<option value="APROVADO">APROVADO</option>
	</select>

	<select bind:value={feiraId}>
		<option value="">Selecione uma feira</option>
		{#each feiras as feira}
			<option value={feira.id}>{feira.nome}</option>
		{/each}
	</select>

	<select bind:value={professorId}>
		<option value="">Selecione um professor</option>
		{#each professores as professor}
			<option value={professor.id}>{professor.nome}</option>
		{/each}
	</select>

	<button onclick={salvarProjeto}>Cadastrar</button>
</div>

{#if mensagem}
	<p>{mensagem}</p>
{/if}

<h2>Lista de Projetos</h2>

{#if projetos.length > 0}
	<ul>
		{#each projetos as projeto}
			<li>
				<strong>{projeto.titulo}</strong>
				— {projeto.areaTematica}
				— {projeto.status}
				— Feira: {projeto.feira?.nome ?? projeto.feira?.id}
				— Professor: {projeto.professor?.nome ?? projeto.professor?.id}
				<button onclick={() => excluirProjeto(projeto.id)}>Excluir</button>
			</li>
		{/each}
	</ul>
{:else}
	<p>Nenhum projeto cadastrado.</p>
{/if}
