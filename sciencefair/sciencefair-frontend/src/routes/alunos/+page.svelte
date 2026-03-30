<svelte:head>
	<title>Alunos</title>
</svelte:head>

<script lang="ts">

	import { createAluno, deleteAluno, getAllAlunos } from '$lib/services/aluno.service';
	import { getAllProjetos } from '$lib/services/projeto.service';
	import { goto } from '$app/navigation';
	import { isLoggedIn } from '$lib/utils/session';

	let logado = $state(false);

	let alunos = $state<any[]>([]);
	let projetos = $state<any[]>([]);

	let nome = $state('');
	let email = $state('');
	let matricula = $state('');
	let turma = $state('');
	let projetoId = $state('');
	let mensagem = $state('');

	async function carregarDados() {
		alunos = await getAllAlunos();
		projetos = await getAllProjetos();
	}

	async function salvarAluno() {
		try {
			await createAluno({
				nome,
				email,
				matricula,
				turma,
				projeto: {
					id: Number(projetoId)
				}
			});

			nome = '';
			email = '';
			matricula = '';
			turma = '';
			projetoId = '';
			mensagem = 'Aluno cadastrado com sucesso.';
			await carregarDados();
		} catch (e) {
			console.error(e);
			mensagem = 'Erro ao cadastrar aluno.';
		}
	}

	async function excluirAluno(id: number) {
		try {
			await deleteAluno(id);
			mensagem = 'Aluno excluído com sucesso.';
			await carregarDados();
		} catch (e) {
			console.error(e);
			mensagem = 'Erro ao excluir aluno.';
		}
	}

	$effect(() => {
		logado = isLoggedIn();
		carregarDados();
	});
</script>


<h1>Cadastro de Alunos</h1>

{#if logado}
	<div>
		<input bind:value={nome} placeholder="Nome" />
		<input bind:value={email} placeholder="Email" type="email" />
		<input bind:value={matricula} placeholder="Matrícula" />
		<input bind:value={turma} placeholder="Turma" />

		<select bind:value={projetoId}>
			<option value="">Selecione um projeto</option>
			{#each projetos as projeto}
				<option value={projeto.id}>{projeto.titulo}</option>
			{/each}
		</select>

		<button onclick={salvarAluno}>Cadastrar</button>
	</div>
{:else}
	<p>Você precisa estar logado para cadastrar alunos.</p>
{/if}

{#if mensagem}
	<p>{mensagem}</p>
{/if}

<h2>Lista de Alunos</h2>

{#if alunos.length > 0}
	<ul>
		{#each alunos as aluno}
			<li>
				<strong>{aluno.nome}</strong>
				— {aluno.turma}
				— {aluno.email}
				— Projeto: {aluno.projeto?.titulo ?? aluno.projeto?.id}
				{#if logado}
					<button onclick={() => excluirAluno(aluno.id)}>Excluir</button>
				{/if}
			</li>
		{/each}
	</ul>
{:else}
	<p>Nenhum aluno cadastrado.</p>
{/if}
