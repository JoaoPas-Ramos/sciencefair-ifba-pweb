<svelte:head>
	<title>Novo Post | IFBA Blog</title>
</svelte:head>

<script lang="ts">
	import { goto } from '$app/navigation';
	import { createPost } from '$lib/services/post.service';
	import { getAuthenticatedUserId, isAdmin } from '$lib/utils/auth';
	import type { ErrorResponse } from '$lib/types/error-response';
	import type { PageProps } from './$types';
	import { ApiError } from '$lib/utils/http-utils';

	type FieldErrors = Record<string, string[]>;

	let { data }: PageProps = $props();

	let title = $state('');
	let body = $state('');
	let categoryId = $state<number | null>(null);
	let userId = $state(getAuthenticatedUserId() ?? 0);
	let isSubmitting = $state(false);
	let formError = $state('');
	let fieldErrors = $state<FieldErrors>({});
	let admin = $state(false);

	$effect(() => {
		admin = isAdmin();
		if (!admin) {
			goto('/');
			return;
		}

		if (categoryId === null) {
			categoryId = data.categories[0]?.id ?? null;
		}
	});

	function errorsFor(field: string): string[] {
		return fieldErrors[field] ?? [];
	}

	function applyBackendErrors(errorBody?: ErrorResponse) {
		const backendFields = (errorBody as ErrorResponse & { fields?: FieldErrors } | undefined)?.fields;
		fieldErrors = errorBody?.fieldErrors ?? backendFields ?? {};
		formError = errorBody?.description ?? 'Nao foi possivel cadastrar o post.';
	}

	function validate(): boolean {
		const errors: FieldErrors = {};

		if (!title.trim()) {
			errors.title = ['Informe o titulo do post.'];
		}

		if (!body.trim()) {
			errors.body = ['Informe o conteudo do post.'];
		}

		if (!categoryId) {
			errors.categoryId = ['Selecione uma categoria.'];
		}

		if (!userId || userId < 1) {
			errors.userId = ['Informe um usuario valido.'];
		}

		fieldErrors = errors;
		return Object.keys(errors).length === 0;
	}

	async function submitForm(event: SubmitEvent) {
		event.preventDefault();
		formError = '';

		if (!admin) {
			await goto('/');
			return;
		}

		if (!validate()) {
			return;
		}

		isSubmitting = true;

		try {
			const createdPost = await createPost({
				title: title.trim(),
				body: body.trim(),
				categoryId: categoryId!,
				userId
			});

			await goto(`/posts/${createdPost.id}`);
		} catch (error) {
			if (error instanceof ApiError) {
				applyBackendErrors(error.body);
			} else {
				formError = 'Nao foi possivel cadastrar o post.';
			}
		} finally {
			isSubmitting = false;
		}
	}
</script>

<section class="post-form-page">
	<div class="post-form-card">
		{#if admin}
			<div class="post-form-card__intro">
				<p class="post-form-card__eyebrow">Novo post</p>
				<h1 class="post-form-card__title">Cadastre um novo post</h1>
				<p class="post-form-card__description">
					Preencha os campos abaixo para publicar um novo conteudo no blog.
				</p>
			</div>

			<form class="post-form" onsubmit={submitForm}>
				<label>
					<span>Titulo</span>
					<input bind:value={title} placeholder="Ex.: Guia rapido de Spring Boot" type="text" />
					{#each errorsFor('title') as error}
						<small class="field-error">{error}</small>
					{/each}
				</label>

				<div class="post-form__grid">
					<label>
						<span>Categoria</span>
						<select bind:value={categoryId}>
							<option value={null}>Selecione uma categoria</option>
							{#each data.categories as category}
								<option value={category.id}>{category.name}</option>
							{/each}
						</select>
						{#each errorsFor('categoryId') as error}
							<small class="field-error">{error}</small>
						{/each}
					</label>

					<label>
						<span>Usuario ID</span>
						<input bind:value={userId} min="1" type="number" />
						{#each errorsFor('userId') as error}
							<small class="field-error">{error}</small>
						{/each}
					</label>
				</div>

				<label>
					<span>Conteudo</span>
					<textarea bind:value={body} placeholder="Escreva o conteudo completo do post" rows="10"></textarea>
					{#each errorsFor('body') as error}
						<small class="field-error">{error}</small>
					{/each}
				</label>

				{#if formError}
					<p class="form-feedback form-feedback--error">{formError}</p>
				{/if}

				<button class="post-form__submit" disabled={isSubmitting} type="submit">
					{isSubmitting ? 'Publicando...' : 'Publicar post'}
				</button>
			</form>
		{/if}
	</div>
</section>

<style>
	.post-form-page {
		display: grid;
		place-items: center;
	}

	.post-form-card {
		width: min(100%, 860px);
		padding: 2rem;
		border-radius: 28px;
		background: rgba(255, 255, 255, 0.94);
		border: 1px solid rgba(77, 109, 148, 0.14);
		box-shadow: 0 24px 60px rgba(37, 69, 109, 0.12);
	}

	.post-form-card__eyebrow {
		margin-bottom: 0.75rem;
		color: #347dd4;
		text-transform: uppercase;
		font-weight: 800;
		letter-spacing: 0.08em;
		font-size: 0.8rem;
	}

	.post-form-card__title {
		font-size: clamp(2rem, 4vw, 3rem);
		line-height: 1.05;
		letter-spacing: -0.04em;
	}

	.post-form-card__description {
		margin-top: 1rem;
		color: #667085;
		line-height: 1.7;
	}

	.post-form {
		display: grid;
		gap: 1rem;
		margin-top: 2rem;
	}

	.post-form__grid {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 1rem;
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

	input,
	select,
	textarea {
		width: 100%;
		padding: 0.95rem 1rem;
		border-radius: 16px;
		border: 1px solid rgba(77, 109, 148, 0.2);
		background: #f9fbff;
		font: inherit;
		color: #1d2433;
	}

	textarea {
		resize: vertical;
		min-height: 220px;
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

	.post-form__submit {
		border: 0;
		border-radius: 16px;
		padding: 1rem 1.2rem;
		background: linear-gradient(135deg, #347dd4 0%, #4b95e8 100%);
		color: #fff;
		font-weight: 800;
		cursor: pointer;
		box-shadow: 0 16px 30px rgba(52, 125, 212, 0.24);
	}

	.post-form__submit:disabled {
		opacity: 0.7;
		cursor: wait;
	}

	@media (max-width: 720px) {
		.post-form-card {
			padding: 1.4rem;
		}

		.post-form__grid {
			grid-template-columns: 1fr;
		}
	}
</style>
