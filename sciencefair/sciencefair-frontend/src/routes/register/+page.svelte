<svelte:head>
	<title>Cadastro | IFBA Blog</title>
</svelte:head>

<script lang="ts">
	import type { ErrorResponse } from '$lib/types/error-response';
	import { createUser } from '$lib/services/user.service';
	import { ApiError } from '$lib/utils/http-utils';

	type FieldErrors = Record<string, string[]>;

	let name = $state('');
	let email = $state('');
	let password = $state('');
	let passwordConfirmation = $state('');
	let isSubmitting = $state(false);
	let successMessage = $state('');
	let formError = $state('');
	let fieldErrors = $state<FieldErrors>({});

	function setClientErrors(): boolean {
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

		if (!password) {
			errors.password = ['Informe a senha.'];
		} else if (password.length < 6 || password.length > 20) {
			errors.password = ['A senha deve ter entre 6 e 20 caracteres.'];
		}

		if (!passwordConfirmation) {
			errors.passwordConfirmation = ['Confirme a senha.'];
		} else if (password !== passwordConfirmation) {
			errors.passwordConfirmation = ['A confirmacao de senha nao confere.'];
		}

		fieldErrors = errors;
		return Object.keys(errors).length === 0;
	}

	function applyBackendErrors(errorBody?: ErrorResponse) {
		const backendFields = (errorBody as ErrorResponse & { fields?: FieldErrors } | undefined)?.fields;
		fieldErrors = errorBody?.fieldErrors ?? backendFields ?? {};
		formError = errorBody?.description ?? 'Nao foi possivel concluir o cadastro.';
	}

	async function submitForm(event: SubmitEvent) {
		event.preventDefault();
		successMessage = '';
		formError = '';

		if (!setClientErrors()) {
			return;
		}

		isSubmitting = true;

		try {
			await createUser({
				name: name.trim(),
				email: email.trim(),
				password
			});

			name = '';
			email = '';
			password = '';
			passwordConfirmation = '';
			fieldErrors = {};
			successMessage = 'Usuario cadastrado com sucesso.';
		} catch (error) {
			if (error instanceof ApiError) {
				applyBackendErrors(error.body);
			} else {
				formError = 'Nao foi possivel concluir o cadastro.';
			}
		} finally {
			isSubmitting = false;
		}
	}

	function errorsFor(field: string): string[] {
		return fieldErrors[field] ?? [];
	}
</script>

<section class="register-page">
	<div class="register-card">
		<div class="register-card__intro">
			<p class="register-card__eyebrow">Novo usuario</p>
			<h1 class="register-card__title">Crie sua conta</h1>
			<p class="register-card__description">
				Preencha seus dados para cadastrar um novo usuario no sistema. O perfil sera definido
				pelo backend.
			</p>
		</div>

		<form class="register-form" onsubmit={submitForm}>
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

			<div class="register-form__grid">
				<label>
					<span>Senha</span>
					<input
						bind:value={password}
						maxlength="20"
						minlength="6"
						placeholder="Entre 6 e 20 caracteres"
						type="password"
					/>
					{#each errorsFor('password') as error}
						<small class="field-error">{error}</small>
					{/each}
				</label>

				<label>
					<span>Confirmacao de senha</span>
					<input
						bind:value={passwordConfirmation}
						maxlength="20"
						minlength="6"
						placeholder="Repita a senha"
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

			<button class="register-form__submit" disabled={isSubmitting} type="submit">
				{isSubmitting ? 'Cadastrando...' : 'Cadastrar usuario'}
			</button>
		</form>
	</div>
</section>

<style>
	.register-page {
		display: grid;
		place-items: center;
	}

	.register-card {
		width: min(100%, 760px);
		padding: 2rem;
		border-radius: 28px;
		background: rgba(255, 255, 255, 0.94);
		border: 1px solid rgba(77, 109, 148, 0.14);
		box-shadow: 0 24px 60px rgba(37, 69, 109, 0.12);
	}

	.register-card__title {
		font-size: clamp(2rem, 4vw, 3rem);
		line-height: 1.05;
		letter-spacing: -0.04em;
	}

	.register-card__eyebrow {
		margin-bottom: 0.75rem;
		color: #347dd4;
		text-transform: uppercase;
		font-weight: 800;
		letter-spacing: 0.08em;
		font-size: 0.8rem;
	}

	.register-card__description {
		margin-top: 1rem;
		color: #667085;
		line-height: 1.7;
	}

	.register-form {
		display: grid;
		gap: 1rem;
		margin-top: 2rem;
	}

	.register-form__grid {
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

	.register-form__submit {
		border: 0;
		border-radius: 16px;
		padding: 1rem 1.2rem;
		background: linear-gradient(135deg, #347dd4 0%, #4b95e8 100%);
		color: #fff;
		font-weight: 800;
		cursor: pointer;
		box-shadow: 0 16px 30px rgba(52, 125, 212, 0.24);
	}

	.register-form__submit:disabled {
		opacity: 0.7;
		cursor: wait;
	}

	@media (max-width: 720px) {
		.register-card {
			padding: 1.4rem;
		}

		.register-form__grid {
			grid-template-columns: 1fr;
		}
	}
</style>
