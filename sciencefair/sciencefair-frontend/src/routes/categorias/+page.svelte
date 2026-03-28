<svelte:head>
	<title>Categorias | IFBA Blog</title>
</svelte:head>

<script lang="ts">
	import { goto, invalidateAll } from '$app/navigation';
	import { ApiError } from '$lib/utils/http-utils';
	import {
		createCategory,
		deleteCategory,
		updateCategory
	} from '$lib/services/category.service';
	import { isAdmin } from '$lib/utils/auth';
	import type { Category } from '$lib/types/category';
	import type { ErrorResponse } from '$lib/types/error-response';
	import type { PageProps } from './$types';

	type FieldErrors = Record<string, string[]>;

	let { data }: PageProps = $props();

	let editingId = $state<number | null>(null);
	let name = $state('');
	let isSubmitting = $state(false);
	let deletingId = $state<number | null>(null);
	let formError = $state('');
	let successMessage = $state('');
	let fieldErrors = $state<FieldErrors>({});
	let admin = $state(false);

	$effect(() => {
		admin = isAdmin();
		if (!admin) {
			goto('/');
		}
	});

	function errorsFor(field: string): string[] {
		return fieldErrors[field] ?? [];
	}

	function resetForm() {
		editingId = null;
		name = '';
		formError = '';
		successMessage = '';
		fieldErrors = {};
	}

	function startEdit(category: { id?: number; name: string }) {
		editingId = category.id ?? null;
		name = category.name;
		formError = '';
		successMessage = '';
		fieldErrors = {};
	}

	function applyBackendErrors(errorBody?: ErrorResponse) {
		const backendFields = (errorBody as ErrorResponse & { fields?: FieldErrors } | undefined)?.fields;
		fieldErrors = errorBody?.fieldErrors ?? backendFields ?? {};
		formError = errorBody?.description ?? 'Nao foi possivel salvar a categoria.';
	}

	function validate(): boolean {
		const errors: FieldErrors = {};

		if (!name.trim()) {
			errors.name = ['Informe o nome da categoria.'];
		}

		fieldErrors = errors;
		return Object.keys(errors).length === 0;
	}

	async function submitForm(event: SubmitEvent) {
		event.preventDefault();
		formError = '';
		successMessage = '';

		if (!admin) {
			await goto('/');
			return;
		}

		if (!validate()) {
			return;
		}

		isSubmitting = true;

		const payload: Category = {
			name: name.trim()
		};

		try {
			if (editingId) {
				await updateCategory(editingId, payload);
				successMessage = 'Categoria atualizada com sucesso.';
			} else {
				await createCategory(payload);
				successMessage = 'Categoria criada com sucesso.';
			}

			await invalidateAll();
			resetForm();
		} catch (error) {
			if (error instanceof ApiError) {
				applyBackendErrors(error.body);
			} else {
				formError = 'Nao foi possivel salvar a categoria.';
			}
		} finally {
			isSubmitting = false;
		}
	}

	async function removeCategory(id: number) {
		if (!admin) {
			await goto('/');
			return;
		}

		const confirmed = confirm('Tem certeza que deseja remover esta categoria?');

		if (!confirmed) {
			return;
		}

		formError = '';
		successMessage = '';
		deletingId = id;

		try {
			await deleteCategory(id);
			if (editingId === id) {
				resetForm();
			}
			successMessage = 'Categoria removida com sucesso.';
			await invalidateAll();
		} catch (error) {
			if (error instanceof ApiError) {
				formError = error.body?.description ?? 'Nao foi possivel remover a categoria.';
			} else {
				formError = 'Nao foi possivel remover a categoria.';
			}
		} finally {
			deletingId = null;
		}
	}
</script>

<section class="categories-page">
	<div class="categories-page__header">
		<div>
			<p class="categories-page__eyebrow">Categorias</p>
			<h1>Gerencie as categorias do blog</h1>
			<p class="categories-page__description">
				Cadastre novas categorias, atualize as existentes ou remova itens que nao serao mais
				utilizados.
			</p>
		</div>
	</div>

	<div class="categories-layout">
		{#if admin}
			<section class="category-form-card">
				<h2>{editingId ? 'Editar categoria' : 'Nova categoria'}</h2>

				<form class="category-form" onsubmit={submitForm}>
					<label>
						<span>Nome</span>
						<input bind:value={name} placeholder="Ex.: Tecnologia" type="text" />
						{#each errorsFor('name') as error}
							<small class="field-error">{error}</small>
						{/each}
					</label>

					{#if formError}
						<p class="form-feedback form-feedback--error">{formError}</p>
					{/if}

					{#if successMessage}
						<p class="form-feedback form-feedback--success">{successMessage}</p>
					{/if}

					<div class="category-form__actions">
						<button class="category-form__submit" disabled={isSubmitting} type="submit">
							{isSubmitting
								? editingId
									? 'Salvando...'
									: 'Cadastrando...'
								: editingId
									? 'Salvar alteracoes'
									: 'Cadastrar categoria'}
						</button>

						{#if editingId}
							<button class="category-form__cancel" onclick={resetForm} type="button">
								Cancelar edicao
							</button>
						{/if}
					</div>
				</form>
			</section>

			<section class="category-list-card">
				<div class="category-list-card__header">
					<h2>Lista de categorias</h2>
					<span>{data.categories.length} item(ns)</span>
				</div>

				<div class="category-list">
					{#if data.categories.length > 0}
						{#each data.categories as category (category.id)}
							<article class="category-item">
								<div class="category-item__content">
									<h3>{category.name}</h3>
								</div>

								<div class="category-item__actions">
									<button type="button" onclick={() => startEdit(category)}>Editar</button>
									<button
										class="category-item__delete"
										disabled={deletingId === category.id}
										type="button"
										onclick={() => removeCategory(category.id!)}
									>
										{deletingId === category.id ? 'Removendo...' : 'Remover'}
									</button>
								</div>
							</article>
						{/each}
					{:else}
						<p class="category-list__empty">Nenhuma categoria cadastrada ate o momento.</p>
					{/if}
				</div>
			</section>
		{/if}
	</div>
</section>

<style>
	.categories-page__header {
		margin-bottom: 2rem;
	}

	.categories-page__eyebrow {
		margin-bottom: 0.7rem;
		color: #347dd4;
		text-transform: uppercase;
		font-weight: 800;
		letter-spacing: 0.08em;
		font-size: 0.8rem;
	}

	.categories-page h1 {
		font-size: clamp(2rem, 4vw, 3.2rem);
		line-height: 1.05;
		letter-spacing: -0.04em;
	}

	.categories-page__description {
		margin-top: 1rem;
		color: #667085;
		line-height: 1.7;
		max-width: 720px;
	}

	.categories-layout {
		display: grid;
		grid-template-columns: minmax(280px, 380px) minmax(0, 1fr);
		gap: 1.5rem;
		align-items: start;
	}

	.category-form-card,
	.category-list-card {
		padding: 1.6rem;
		border-radius: 24px;
		background: rgba(255, 255, 255, 0.94);
		border: 1px solid rgba(77, 109, 148, 0.14);
		box-shadow: 0 24px 60px rgba(37, 69, 109, 0.12);
	}

	.category-form,
	.category-list {
		display: grid;
		gap: 1rem;
		margin-top: 1rem;
	}

	label {
		display: grid;
		gap: 0.45rem;
		color: #344054;
		font-weight: 700;
	}

	label span {
		font-size: 0.94rem;
	}

	input {
		width: 100%;
		padding: 0.95rem 1rem;
		border-radius: 16px;
		border: 1px solid rgba(77, 109, 148, 0.2);
		background: #f9fbff;
		font: inherit;
		color: #1d2433;
	}

	.field-error {
		color: #c0342b;
		font-size: 0.84rem;
		font-weight: 700;
	}

	.form-feedback {
		padding: 0.9rem 1rem;
		border-radius: 16px;
		font-weight: 700;
	}

	.form-feedback--error {
		background: rgba(192, 52, 43, 0.08);
		color: #c0342b;
	}

	.form-feedback--success {
		background: rgba(34, 163, 74, 0.12);
		color: #1c7c3a;
	}

	.category-form__actions {
		display: flex;
		flex-wrap: wrap;
		gap: 0.8rem;
	}

	.category-form__submit,
	.category-form__cancel,
	.category-item__actions button {
		border: 0;
		border-radius: 14px;
		padding: 0.9rem 1rem;
		font-weight: 800;
		cursor: pointer;
	}

	.category-form__submit {
		background: linear-gradient(135deg, #347dd4 0%, #4b95e8 100%);
		color: #fff;
		box-shadow: 0 16px 30px rgba(52, 125, 212, 0.24);
	}

	.category-form__cancel {
		background: #edf4fb;
		color: #446f9a;
	}

	.category-form__submit:disabled,
	.category-item__actions button:disabled {
		opacity: 0.7;
		cursor: wait;
	}

	.category-list-card__header,
	.category-item,
	.category-item__actions {
		display: flex;
	}

	.category-list-card__header {
		justify-content: space-between;
		align-items: center;
		gap: 1rem;
	}

	.category-list-card__header span {
		color: #667085;
		font-weight: 700;
	}

	.category-item {
		justify-content: space-between;
		gap: 1rem;
		padding: 1.2rem;
		border-radius: 20px;
		background: #f9fbff;
		border: 1px solid rgba(77, 109, 148, 0.1);
	}

	.category-item__content h3 {
		font-size: 1.1rem;
	}

	.category-item__actions {
		gap: 0.65rem;
		align-items: start;
	}

	.category-item__actions button {
		background: #d7e9fb;
		color: #446f9a;
	}

	.category-item__delete {
		background: rgba(192, 52, 43, 0.1) !important;
		color: #c0342b !important;
	}

	.category-list__empty {
		color: #667085;
	}

	@media (max-width: 860px) {
		.categories-layout {
			grid-template-columns: 1fr;
		}
	}

	@media (max-width: 640px) {
		.category-item,
		.category-item__actions {
			flex-direction: column;
		}
	}
</style>
