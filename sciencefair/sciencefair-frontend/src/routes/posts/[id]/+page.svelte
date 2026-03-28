<svelte:head>
	<title>{data.post.title} | IFBA Blog</title>
</svelte:head>

<script lang="ts">
	import { goto, invalidateAll } from '$app/navigation';
	import { createComment, deleteComment } from '$lib/services/comment.service';
	import { deletePost, updatePost } from '$lib/services/post.service';
	import type { ErrorResponse } from '$lib/types/error-response';
	import type { PostPayload } from '$lib/types/post';
	import { getAuthenticatedUserId, isAdmin, isAuthenticated } from '$lib/utils/auth';
	import { ApiError } from '$lib/utils/http-utils';
	import type { PageProps } from './$types';

	type FieldErrors = Record<string, string[]>;

	let { data }: PageProps = $props();

	let body = $state('');
	let userId = $state(getAuthenticatedUserId() ?? 0);
	let rating = $state(5);
	let isSubmitting = $state(false);
	let errorMessage = $state('');
	let deletingCommentKey = $state('');
	let authenticated = $state(false);
	let admin = $state(false);

	let isEditing = $state(false);
	let isSavingPost = $state(false);
	let isDeletingPost = $state(false);
	let postFormError = $state('');
	let postFieldErrors = $state<FieldErrors>({});
	let postTitle = $state('');
	let postBody = $state('');
	let postCategoryId = $state<number | null>(null);
	let postUserId = $state(0);

	$effect(() => {
		authenticated = isAuthenticated();
		admin = isAdmin();
		userId = getAuthenticatedUserId() ?? 0;
	});

	$effect(() => {
		if (!isEditing) {
			postTitle = data.post.title;
			postBody = data.post.body;
			postCategoryId = data.post.categoryId;
			postUserId = getAuthenticatedUserId() ?? data.post.userId;
		}
	});

	function formatDate(date: string): string {
		return new Date(date).toLocaleDateString('pt-BR');
	}

	function postErrorsFor(field: string): string[] {
		return postFieldErrors[field] ?? [];
	}

	function resetEditForm() {
		postTitle = data.post.title;
		postBody = data.post.body;
		postCategoryId = data.post.categoryId;
		postUserId = data.post.userId;
		postFieldErrors = {};
		postFormError = '';
	}

	function applyBackendErrors(errorBody?: ErrorResponse) {
		const backendFields = (errorBody as ErrorResponse & { fields?: FieldErrors } | undefined)?.fields;
		postFieldErrors = errorBody?.fieldErrors ?? backendFields ?? {};
		postFormError = errorBody?.description ?? 'Nao foi possivel salvar o post.';
	}

	function validatePost(): boolean {
		const errors: FieldErrors = {};

		if (!postTitle.trim()) {
			errors.title = ['Informe o titulo do post.'];
		}

		if (!postBody.trim()) {
			errors.body = ['Informe o conteudo do post.'];
		}

		if (!postCategoryId) {
			errors.categoryId = ['Selecione uma categoria.'];
		}

		if (!postUserId || postUserId < 1) {
			errors.userId = ['Informe um usuario valido.'];
		}

		postFieldErrors = errors;
		return Object.keys(errors).length === 0;
	}

	async function savePost(event: SubmitEvent) {
		event.preventDefault();
		postFormError = '';

		if (!admin) {
			await goto('/');
			return;
		}

		if (!validatePost()) {
			return;
		}

		isSavingPost = true;

		const payload: PostPayload = {
			title: postTitle.trim(),
			body: postBody.trim(),
			categoryId: postCategoryId!,
			userId: postUserId
		};

		try {
			await updatePost(data.post.id, payload);
			isEditing = false;
			await invalidateAll();
		} catch (error) {
			if (error instanceof ApiError) {
				applyBackendErrors(error.body);
			} else {
				postFormError = 'Nao foi possivel salvar o post.';
			}
		} finally {
			isSavingPost = false;
		}
	}

	async function removePost() {
		if (!admin) {
			await goto('/');
			return;
		}

		const confirmed = confirm('Tem certeza que deseja excluir este post?');

		if (!confirmed) {
			return;
		}

		postFormError = '';
		isDeletingPost = true;

		try {
			await deletePost(data.post.id);
			await goto('/');
		} catch (error) {
			if (error instanceof ApiError) {
				postFormError = error.body?.description ?? 'Nao foi possivel excluir o post.';
			} else {
				postFormError = 'Nao foi possivel excluir o post.';
			}
		} finally {
			isDeletingPost = false;
		}
	}

	async function submitComment(event: SubmitEvent) {
		event.preventDefault();
		errorMessage = '';

		if (!authenticated || !userId) {
			errorMessage = 'Faca login para comentar neste post.';
			await goto('/login');
			return;
		}

		const payload = {
			body: body.trim(),
			rating,
			postId: data.post.id,
			userId
		};

		if (!payload.body) {
			errorMessage = 'Escreva um comentario antes de enviar.';
			return;
		}

		isSubmitting = true;

		try {
			await createComment(payload);
			body = '';
			rating = 5;
			await invalidateAll();
		} catch (error) {
			errorMessage = 'Nao foi possivel enviar o comentario.';
		} finally {
			isSubmitting = false;
		}
	}

	async function removeComment(commentId: number | undefined, commentKey: string) {
		if (!admin) {
			await goto('/');
			return;
		}

		if (!commentId) {
			errorMessage = 'Nao foi possivel identificar o comentario para exclusao.';
			return;
		}

		const confirmed = confirm('Tem certeza que deseja excluir este comentario?');

		if (!confirmed) {
			return;
		}

		errorMessage = '';
		deletingCommentKey = commentKey;

		try {
			await deleteComment(commentId);
			await invalidateAll();
		} catch (error) {
			errorMessage = 'Nao foi possivel excluir o comentario.';
		} finally {
			deletingCommentKey = '';
		}
	}
</script>

<article class="post-detail">
	<div class="post-detail__header">
		<div class="post-detail__headline">
			<div>
				<p class="post-detail__category">{data.post.category.name}</p>
				<h1>{data.post.title}</h1>
			</div>

			<div class="post-detail__actions">
				{#if admin}
					<button class="post-detail__edit" onclick={() => (isEditing = !isEditing)} type="button">
						{isEditing ? 'Fechar edicao' : 'Editar post'}
					</button>
					<button
						class="post-detail__delete"
						disabled={isDeletingPost}
						onclick={removePost}
						type="button"
					>
						{isDeletingPost ? 'Excluindo...' : 'Excluir post'}
					</button>
				{/if}
			</div>
		</div>

		<div class="post-detail__meta">
			<p>Criado em {formatDate(data.post.creationDate)}</p>
			<p>Atualizado em {formatDate(data.post.updateDate)}</p>
			<p>Por {data.post.user.name}</p>
		</div>
	</div>

	<div class="post-detail__body">
		<p>{@html data.post.body}</p>
	</div>
</article>

{#if isEditing && admin}
	<section class="post-edit-card">
		<h2>Editar post</h2>

		<form class="post-edit-form" onsubmit={savePost}>
			<label>
				<span>Titulo</span>
				<input bind:value={postTitle} type="text" />
				{#each postErrorsFor('title') as error}
					<small class="form-error">{error}</small>
				{/each}
			</label>

			<div class="post-edit-form__grid">
				<label>
					<span>Categoria</span>
					<select bind:value={postCategoryId}>
						<option value={null}>Selecione uma categoria</option>
						{#each data.categories as category}
							<option value={category.id}>{category.name}</option>
						{/each}
					</select>
					{#each postErrorsFor('categoryId') as error}
						<small class="form-error">{error}</small>
					{/each}
				</label>

				<label>
					<span>Usuario ID</span>
					<input bind:value={postUserId} min="1" type="number" />
					{#each postErrorsFor('userId') as error}
						<small class="form-error">{error}</small>
					{/each}
				</label>
			</div>

			<label>
				<span>Conteudo</span>
				<textarea bind:value={postBody} rows="8"></textarea>
				{#each postErrorsFor('body') as error}
					<small class="form-error">{error}</small>
				{/each}
			</label>

			{#if postFormError}
				<p class="comment-form__error">{postFormError}</p>
			{/if}

			<div class="post-edit-form__actions">
				<button class="comment-form__submit" disabled={isSavingPost} type="submit">
					{isSavingPost ? 'Salvando...' : 'Salvar post'}
				</button>
				<button
					class="post-edit-form__cancel"
					onclick={() => {
						resetEditForm();
						isEditing = false;
					}}
					type="button"
				>
					Cancelar
				</button>
			</div>
		</form>
	</section>
{/if}

<section class="comments-section" aria-labelledby="comments-title">
	<div class="comments-section__header">
		<h2 id="comments-title">Comentarios</h2>
		<p>{data.comments.length} comentario(s) neste post</p>
	</div>

	{#if authenticated}
		<form class="comment-form" onsubmit={submitComment}>
			<div class="comment-form__grid">
				<label>
					<span>Usuario ID</span>
					<input bind:value={userId} disabled type="number" />
				</label>

				<label>
					<span>Avaliacao</span>
					<select bind:value={rating}>
						<option value={1}>1</option>
						<option value={2}>2</option>
						<option value={3}>3</option>
						<option value={4}>4</option>
						<option value={5}>5</option>
					</select>
				</label>
			</div>

			<label class="comment-form__message">
				<span>Comentario</span>
				<textarea
					bind:value={body}
					rows="4"
					placeholder="Escreva seu comentario sobre o post"
				></textarea>
			</label>

			{#if errorMessage}
				<p class="comment-form__error">{errorMessage}</p>
			{/if}

			<button class="comment-form__submit" disabled={isSubmitting} type="submit">
				{isSubmitting ? 'Enviando...' : 'Publicar comentario'}
			</button>
		</form>
	{:else}
		<p class="comment-login-hint">
			Faca <a href="/login">login</a> para publicar comentarios neste post.
		</p>
	{/if}

	<div class="comment-list">
		{#if data.comments.length > 0}
			{#each data.comments as comment, index (`${comment.userId}-${index}`)}
				<article class="comment-card">
					<div class="comment-card__header">
						<div>
							<p class="comment-card__author">{comment.user?.name ?? 'Usuario'}</p>
							<p class="comment-card__email">{comment.user?.email ?? 'Email indisponivel'}</p>
						</div>

						<div class="comment-card__tools">
							<span class="comment-card__rating">Nota {comment.rating}/5</span>
							{#if admin}
								<button
									aria-label="Apagar comentario"
									class="comment-card__delete"
									disabled={deletingCommentKey === `${comment.userId}-${index}`}
									onclick={() => removeComment(comment.id, `${comment.userId}-${index}`)}
									type="button"
								>
									{deletingCommentKey === `${comment.userId}-${index}` ? '...' : '🗑'}
								</button>
							{/if}
						</div>
					</div>

					<p class="comment-card__body">{comment.body}</p>
				</article>
			{/each}
		{:else}
			<p class="comment-list__empty">Ainda nao existem comentarios para este post.</p>
		{/if}
	</div>
</section>

<style>
	.post-detail,
	.post-edit-card,
	.comments-section {
		max-width: 860px;
		margin: 0 auto;
	}

	.post-detail,
	.post-edit-card {
		padding: 2rem;
		border-radius: 24px;
		background: rgba(255, 255, 255, 0.94);
		border: 1px solid rgba(77, 109, 148, 0.14);
		box-shadow: 0 24px 60px rgba(37, 69, 109, 0.12);
	}

	.post-edit-card {
		margin-top: 2rem;
	}

	.post-detail__headline,
	.post-detail__actions,
	.post-edit-form__actions {
		display: flex;
	}

	.post-detail__headline {
		justify-content: space-between;
		gap: 1rem;
		align-items: start;
	}

	.post-detail__category {
		margin-bottom: 0.75rem;
		color: #5d95cf;
		font-weight: 800;
		text-transform: uppercase;
		letter-spacing: 0.08em;
		font-size: 0.8rem;
	}

	.post-detail h1 {
		font-size: clamp(2.2rem, 4vw, 3.4rem);
		line-height: 1.04;
		letter-spacing: -0.04em;
	}

	.post-detail__meta {
		display: flex;
		flex-wrap: wrap;
		gap: 1rem 1.5rem;
		margin-top: 1.25rem;
		color: #667085;
		font-size: 0.95rem;
	}

	.post-detail__body {
		margin-top: 2rem;
		font-size: 1.08rem;
		line-height: 1.9;
		color: #253142;
	}

	.post-detail__actions,
	.post-edit-form__actions {
		gap: 0.75rem;
		flex-wrap: wrap;
	}

	.post-detail__edit,
	.post-detail__delete,
	.post-edit-form__cancel {
		border: 0;
		border-radius: 14px;
		padding: 0.9rem 1rem;
		font-weight: 800;
		cursor: pointer;
	}

	.post-detail__edit,
	.post-edit-form__cancel {
		background: #edf4fb;
		color: #446f9a;
	}

	.post-detail__delete {
		background: rgba(192, 52, 43, 0.1);
		color: #c0342b;
	}

	.post-edit-form {
		display: grid;
		gap: 1rem;
		margin-top: 1rem;
	}

	.post-edit-form__grid {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 1rem;
	}

	.comments-section {
		margin-top: 2rem;
	}

	.comments-section__header {
		display: flex;
		justify-content: space-between;
		gap: 1rem;
		align-items: end;
		margin-bottom: 1rem;
	}

	.comments-section__header h2 {
		font-size: 1.7rem;
	}

	.comments-section__header p {
		color: #667085;
	}

	.comment-form {
		padding: 1.5rem;
		border-radius: 22px;
		background: rgba(255, 255, 255, 0.94);
		border: 1px solid rgba(77, 109, 148, 0.14);
		box-shadow: 0 24px 60px rgba(37, 69, 109, 0.12);
	}

	.comment-form__grid {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 1rem;
	}

	label {
		display: grid;
		gap: 0.45rem;
		font-weight: 700;
		color: #344054;
	}

	label span {
		font-size: 0.92rem;
	}

	input,
	select,
	textarea {
		width: 100%;
		padding: 0.9rem 1rem;
		border-radius: 14px;
		border: 1px solid rgba(77, 109, 148, 0.2);
		background: #f9fbff;
		font: inherit;
		color: #1d2433;
	}

	textarea {
		resize: vertical;
		min-height: 120px;
	}

	.comment-form__message {
		margin-top: 1rem;
	}

	.comment-form__submit {
		margin-top: 1rem;
		border: 0;
		border-radius: 14px;
		padding: 0.95rem 1.2rem;
		background: linear-gradient(135deg, #347dd4 0%, #4b95e8 100%);
		color: #fff;
		font-weight: 800;
		cursor: pointer;
	}

	.comment-form__submit:disabled,
	.post-detail__delete:disabled {
		opacity: 0.7;
		cursor: wait;
	}

	.comment-login-hint {
		padding: 1.25rem 1.4rem;
		border-radius: 22px;
		background: rgba(255, 255, 255, 0.94);
		border: 1px solid rgba(77, 109, 148, 0.14);
		box-shadow: 0 24px 60px rgba(37, 69, 109, 0.12);
		color: #667085;
	}

	.comment-login-hint a {
		color: #347dd4;
		font-weight: 800;
	}

	.comment-form__error,
	.form-error {
		margin-top: 0.75rem;
		color: #c0342b;
		font-weight: 700;
	}

	.form-error {
		font-size: 0.84rem;
	}

	.comment-list {
		display: grid;
		gap: 1rem;
		margin-top: 1.5rem;
	}

	.comment-card {
		padding: 1.35rem 1.5rem;
		border-radius: 20px;
		background: rgba(255, 255, 255, 0.9);
		border: 1px solid rgba(77, 109, 148, 0.14);
		box-shadow: 0 18px 40px rgba(37, 69, 109, 0.08);
	}

	.comment-card__header {
		display: flex;
		justify-content: space-between;
		align-items: start;
		gap: 1rem;
		margin-bottom: 0.9rem;
	}

	.comment-card__author {
		font-weight: 800;
	}

	.comment-card__email {
		color: #667085;
		font-size: 0.92rem;
	}

	.comment-card__tools {
		display: flex;
		align-items: center;
		gap: 0.6rem;
	}

	.comment-card__rating {
		padding: 0.45rem 0.7rem;
		border-radius: 999px;
		background: #d7e9fb;
		color: #446f9a;
		font-weight: 800;
		font-size: 0.84rem;
	}

	.comment-card__delete {
		width: 2.2rem;
		height: 2.2rem;
		border: 0;
		border-radius: 999px;
		background: rgba(192, 52, 43, 0.1);
		color: #c0342b;
		font-size: 1rem;
		font-weight: 800;
		cursor: pointer;
	}

	.comment-card__delete:disabled {
		opacity: 0.7;
		cursor: wait;
	}

	.comment-card__body,
	.comment-list__empty {
		color: #344054;
		line-height: 1.7;
	}

	@media (max-width: 720px) {
		.post-detail,
		.post-edit-card {
			padding: 1.4rem;
		}

		.post-detail__headline,
		.post-edit-form__grid,
		.comment-form__grid,
		.comments-section__header {
			grid-template-columns: 1fr;
			display: grid;
		}

		.comment-card__header {
			flex-direction: column;
		}
	}
</style>
