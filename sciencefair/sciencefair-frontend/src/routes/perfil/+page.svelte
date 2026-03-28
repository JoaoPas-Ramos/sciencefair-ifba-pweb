<svelte:head>
	<title>Meu Perfil | IFBA Blog</title>
</svelte:head>

<script lang="ts">
	import { goto } from '$app/navigation';
	import { updateLoggedUserProfile } from '$lib/services/user.service';
	import type { ErrorResponse } from '$lib/types/error-response';
	import { ApiError } from '$lib/utils/http-utils';
	import {
		getAuthenticatedUserEmail,
		getAuthenticatedUserName,
		isAuthenticated,
		updateAuthenticatedUserProfile
	} from '$lib/utils/auth';

	type FieldErrors = Record<string, string[]>;

	let name = $state(getAuthenticatedUserName() ?? '');
	let email = $state(getAuthenticatedUserEmail() ?? '');
	let isSubmitting = $state(false);
	let formError = $state('');
	let successMessage = $state('');
	let fieldErrors = $state<FieldErrors>({});

	$effect(() => {
		if (!isAuthenticated()) {
			goto('/login');
		}
	});

	function errorsFor(field: string): string[] {
		return fieldErrors[field] ?? [];
	}

	function validate(): boolean {
		const errors: FieldErrors = {};

		if (!name.trim()) {
			errors.name = ['Informe o nome.'];
		} else if (name.trim().length > 150) {
			errors.name = ['O nome deve ter no maximo 150 caracteres.'];
		}

		if (!email.trim()) {
			errors.email = ['Informe o email.'];
		} else if (email.trim().length > 150) {
			errors.email = ['O email deve ter no maximo 150 caracteres.'];
		}

		fieldErrors = errors;
		return Object.keys(errors).length === 0;
	}

	function applyBackendErrors(errorBody?: ErrorResponse) {
		const backendFields = (errorBody as ErrorResponse & { fields?: FieldErrors } | undefined)?.fields;
		fieldErrors = errorBody?.fieldErrors ?? backendFields ?? {};
		formError = errorBody?.description ?? 'Nao foi possivel atualizar o perfil.';
	}

	async function submitForm(event: SubmitEvent) {
		event.preventDefault();
		formError = '';
		successMessage = '';

		if (!validate()) {
			return;
		}

		isSubmitting = true;

		try {
			const updatedUser = await updateLoggedUserProfile({
				name: name.trim(),
				email: email.trim()
			});

			name = updatedUser.name;
			email = updatedUser.email;
			fieldErrors = {};
			updateAuthenticatedUserProfile(updatedUser.name, updatedUser.email);
			successMessage = 'Perfil atualizado com sucesso.';
		} catch (error) {
			if (error instanceof ApiError) {
				applyBackendErrors(error.body);
			} else {
				formError = 'Nao foi possivel atualizar o perfil.';
			}
		} finally {
			isSubmitting = false;
		}
	}
</script>

<section class="profile-page">
	<div class="profile-card">
		<div class="profile-card__intro">
			<p class="profile-card__eyebrow">Meu perfil</p>
			<h1 class="profile-card__title">Atualize seu nome e email</h1>
			<p class="profile-card__description">
				Mantenha seus dados principais atualizados. As alteracoes refletidas no backend tambem
				sao sincronizadas no header.
			</p>
		</div>

		<form class="profile-form" onsubmit={submitForm}>
			<label>
				<span>Nome</span>
				<input bind:value={name} maxlength="150" placeholder="Seu nome completo" type="text" />
				{#each errorsFor('name') as error}
					<small class="field-error">{error}</small>
				{/each}
			</label>

			<label>
				<span>Email</span>
				<input bind:value={email} maxlength="150" placeholder="voce@email.com" type="email" />
				{#each errorsFor('email') as error}
					<small class="field-error">{error}</small>
				{/each}
			</label>

			{#if formError}
				<p class="form-feedback form-feedback--error">{formError}</p>
			{/if}

			{#if successMessage}
				<p class="form-feedback form-feedback--success">{successMessage}</p>
			{/if}

			<button class="profile-form__submit" disabled={isSubmitting} type="submit">
				{isSubmitting ? 'Salvando...' : 'Salvar perfil'}
			</button>
		</form>
	</div>
</section>

<style>
	.profile-page {
		display: grid;
		place-items: center;
	}

	.profile-card {
		width: min(100%, 680px);
		padding: 2rem;
		border-radius: 28px;
		background: rgba(255, 255, 255, 0.94);
		border: 1px solid rgba(77, 109, 148, 0.14);
		box-shadow: 0 24px 60px rgba(37, 69, 109, 0.12);
	}

	.profile-card__eyebrow {
		margin-bottom: 0.75rem;
		color: #347dd4;
		text-transform: uppercase;
		font-weight: 800;
		letter-spacing: 0.08em;
		font-size: 0.8rem;
	}

	.profile-card__title {
		font-size: clamp(2rem, 4vw, 3rem);
		line-height: 1.05;
		letter-spacing: -0.04em;
	}

	.profile-card__description {
		margin-top: 1rem;
		color: #667085;
		line-height: 1.7;
	}

	.profile-form {
		display: grid;
		gap: 1rem;
		margin-top: 2rem;
	}

	label {
		display: grid;
		gap: 0.45rem;
		color: #344054;
		font-weight: 700;
	}

	span {
		font-size: 0.95rem;
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

	.profile-form__submit {
		border: 0;
		border-radius: 16px;
		padding: 1rem 1.2rem;
		background: linear-gradient(135deg, #347dd4 0%, #4b95e8 100%);
		color: #fff;
		font-weight: 800;
		cursor: pointer;
		box-shadow: 0 16px 30px rgba(52, 125, 212, 0.24);
	}

	.profile-form__submit:disabled {
		opacity: 0.7;
		cursor: wait;
	}

	@media (max-width: 720px) {
		.profile-card {
			padding: 1.4rem;
		}
	}
</style>
