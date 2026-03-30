<svelte:head>
	<title>Professores</title>
</svelte:head>

<script lang="ts">

	import {
		createProfessor,
		deleteProfessor,
		getAllProfessores
	} from '$lib/services/professor.service';
	import { goto } from '$app/navigation';
	import { isLoggedIn } from '$lib/utils/session';

	let logado = $state(false);

	let professores = $state<any[]>([]);
	let nome = $state('');
	let email = $state('');
	let departamento = $state('');
	let matricula = $state('');
	let mensagem = $state('');

	async function carregarProfessores() {
		professores = await getAllProfessores();
	}

	async function salvarProfessor() {
		try {
			await createProfessor({
				nome,
				email,
				departamento,
				matricula
			});

			nome = '';
			email = '';
			departamento = '';
			matricula = '';
			mensagem = 'Professor cadastrado com sucesso.';
			await carregarProfessores();
		} catch (e) {
			console.error(e);
			mensagem = 'Erro ao cadastrar professor.';
		}
	}

	async function excluirProfessor(id: number) {
		try {
			await deleteProfessor(id);
			mensagem = 'Professor excluído com sucesso.';
			await carregarProfessores();
		} catch (e) {
			console.error(e);
			mensagem = 'Erro ao excluir professor.';
		}
	}

	$effect(() => {
		logado = isLoggedIn();
		carregarProfessores();
	});
</script>


<h1>Cadastro de Professores</h1>

{#if logado}
	<div>
		<input bind:value={nome} placeholder="Nome" />
		<input bind:value={email} placeholder="Email" type="email" />
		<input bind:value={departamento} placeholder="Departamento" />
		<input bind:value={matricula} placeholder="Matrícula" />
		<button onclick={salvarProfessor}>Cadastrar</button>
	</div>
{:else}
	<p>Você precisa estar logado para cadastrar professores.</p>
{/if}

{#if mensagem}
	<p>{mensagem}</p>
{/if}

<h2>Lista de Professores</h2>

{#if professores.length > 0}
	<ul>
		{#each professores as professor}
			<li>
				<strong>{professor.nome}</strong> — {professor.departamento} — {professor.email}
				{#if logado}
					<button onclick={() => excluirProfessor(professor.id)}>Excluir</button>
				{/if}
			</li>
		{/each}
	</ul>
{:else}
	<p>Nenhum professor cadastrado.</p>
{/if}