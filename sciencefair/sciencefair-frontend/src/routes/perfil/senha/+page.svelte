<svelte:head>
	<title>Alterar Senha | IFBA Blog</title>
</svelte:head>

<script lang="ts">
	import { goto } from '$app/navigation';
	import { updateLoggedUserPassword } from '$lib/services/user.service';
	import type { ErrorResponse } from '$lib/types/error-response';
	import { ApiError } from '$lib/utils/http-utils';
	import { isAuthenticated } from '$lib/utils/auth';

	type FieldErrors = Record<string, string[]>;

	let currentPassword = $state('');
	let newPassword = $state('');
	let passwordConfirmation = $state('');
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

		if (!currentPassword) {
			errors.currentPassword = ['Informe a senha atual.'];
		}

		if (!newPassword) {
			errors.newPassword = ['Informe a nova senha.'];
		} else if (newPassword.length < 6 || newPassword.length > 20) {
			errors.newPassword = ['A nova senha deve ter entre 6 e 20 caracteres.'];
		}

		if (!passwordConfirmation) {
			errors.passwordConfirmation = ['Confirme a nova senha.'];
		} else if (newPassword !== passwordConfirmation) {
			errors.passwordConfirmation = ['A confirmacao de senha nao confere.'];
		}

		fieldErrors = errors;
		return Object.keys(errors).length === 0;
	}

	function applyBackendErrors(errorBody?: ErrorResponse) {
		const backendFields = (errorBody as ErrorResponse & { fields?: FieldErrors } | undefined)?.fields;
		fieldErrors = errorBody?.fieldErrors ?? backendFields ?? {};
		formError = errorBody?.description ?? 'Nao foi possivel atualizar a senha.';
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
			await updateLoggedUserPassword({
				currentPassword,
				newPassword
			});

			currentPassword = '';
			newPassword = '';
			passwordConfirmation = '';
			fieldErrors = {};
			successMessage = 'Senha atualizada com sucesso.';
		} catch (error) {
			if (error instanceof ApiError) {
				applyBackendErrors(error.body);
			} else {
				formError = 'Nao foi possivel atualizar a senha.';
			}
		} finally {
			isSubmitting = false;
		}
	}
</script>

<section class="password-page">
	<div class="password-card">
		<div class="password-card__intro">
			<p class="password-card__eyebrow">Seguranca</p>
			<h1 class="password-card__title">Altere sua senha</h1>
			<p class="password-card__description">
				Informe sua senha atual e escolha uma nova senha para continuar protegendo sua conta.
			</p>
		</div>

		<form class="password-form" onsubmit={submitForm}>
			<label>
				<span>Senha atual</span>
				<input bind:value={currentPassword} autocomplete="current-password" type="password" />
				{#each errorsFor('currentPassword') as error}
					<small class="field-error">{error}</small>
				{/each}
			</label>

			<div class="password-form__grid">
				<label>
					<span>Nova senha</span>
					<input
						bind:value={newPassword}
						autocomplete="new-password"
						maxlength="20"
						minlength="6"
						type="password"
					/>
					{#each errorsFor('newPassword') as error}
						<small class="field-error">{error}</small>
					{/each}
				</label>

				<label>
					<span>Confirmacao da nova senha</span>
					<input
						bind:value={passwordConfirmation}
						autocomplete="new-password"
						maxlength="20"
						minlength="6"
						type="password"
					/>
					{#each errorsFor('passwordConfirmation') as error}
						<small class="field-error">{error}</small>
					{/each}
				</label>
			</div>

			{#if formError}
				<p class="form-feedback form-feedback--error">{formError}</p>
			{/if}

			{#if successMessage}
				<p class="form-feedback form-feedback--success">{successMessage}</p>
			{/if}

			<button class="password-form__submit" disabled={isSubmitting} type="submit">
				{isSubmitting ? 'Atualizando...' : 'Atualizar senha'}
			</button>
		</form>
	</div>
</section>

<style>
	.password-page {
		display: grid;
		place-items: center;
	}

	.password-card {
		width: min(100%, 760px);
		padding: 2rem;
		border-radius: 28px;
		background: rgba(255, 255, 255, 0.94);
		border: 1px solid rgba(77, 109, 148, 0.14);
		box-shadow: 0 24px 60px rgba(37, 69, 109, 0.12);
	}

	.password-card__eyebrow {
		margin-bottom: 0.75rem;
		color: #347dd4;
		text-transform: uppercase;
		font-weight: 800;
		letter-spacing: 0.08em;
		font-size: 0.8rem;
	}

	.password-card__title {
		font-size: clamp(2rem, 4vw, 3rem);
		line-height: 1.05;
		letter-spacing: -0.04em;
	}

	.password-card__description {
		margin-top: 1rem;
		color: #667085;
		line-height: 1.7;
	}

	.password-form {
		display: grid;
		gap: 1rem;
		margin-top: 2rem;
	}

	.password-form__grid {
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

	.password-form__submit {
		border: 0;
		border-radius: 16px;
		padding: 1rem 1.2rem;
		background: linear-gradient(135deg, #347dd4 0%, #4b95e8 100%);
		color: #fff;
		font-weight: 800;
		cursor: pointer;
		box-shadow: 0 16px 30px rgba(52, 125, 212, 0.24);
	}

	.password-form__submit:disabled {
		opacity: 0.7;
		cursor: wait;
	}

	@media (max-width: 720px) {
		.password-card {
			padding: 1.4rem;
		}

		.password-form__grid {
			grid-template-columns: 1fr;
		}
	}
</style>
