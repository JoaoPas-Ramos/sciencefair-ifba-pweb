<svelte:head>
	<title>Entrar | Gerenciador de Feira de Ciências</title>
</svelte:head>

<script lang="ts">
	import { goto } from '$app/navigation';
	import { loginUser } from '$lib/services/user.service';

	type FieldErrors = Record<string, string[]>;

	let email = '';
	let password = '';
	let isSubmitting = false;
	let formError = '';
	let successMessage = '';
	let fieldErrors: FieldErrors = {};

	function errorsFor(field: string): string[] {
		return fieldErrors[field] ?? [];
	}

	function validate(): boolean {
		const errors: FieldErrors = {};

		if (!email.trim()) {
			errors.email = ['Informe o email.'];
		}

		if (!password) {
			errors.password = ['Informe a senha.'];
		}

		fieldErrors = errors;
		return Object.keys(errors).length === 0;
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
			const user = await loginUser({
				email: email.trim(),
				password
			});

			localStorage.setItem('user', JSON.stringify(user));
			fieldErrors = {};
			successMessage = 'Login realizado com sucesso.';
			await goto('/');
		} catch (error: any) {
			console.error(error);
			formError = error?.body?.message ?? error?.message ?? 'Não foi possível realizar o login.';
		} finally {
			isSubmitting = false;
		}
	}
</script>

<section class="login-page">
	<div class="login-card">
		<p class="login-card__eyebrow">Acesso</p>
		<h1 class="login-card__title">Entrar no sistema</h1>
		<p class="login-card__description">
			Faça login para acessar o gerenciador da feira de ciências.
		</p>

		<form class="login-form" on:submit|preventDefault={submitForm}>
			<label>
				<span>Email</span>
				<input bind:value={email} type="email" placeholder="seuemail@ifba.edu.br" />
				{#each errorsFor('email') as error}
					<small class="field-error">{error}</small>
				{/each}
			</label>

			<label>
				<span>Senha</span>
				<input bind:value={password} type="password" placeholder="Digite sua senha" />
				{#each errorsFor('password') as error}
					<small class="field-error">{error}</small>
				{/each}
			</label>

			{#if formError}
				<p class="form-feedback form-feedback--error">{formError}</p>
			{/if}

			{#if successMessage}
				<p class="form-feedback form-feedback--success">{successMessage}</p>
			{/if}

			<button class="login-form__submit" disabled={isSubmitting} type="submit">
				{isSubmitting ? 'Entrando...' : 'Entrar'}
			</button>
		</form>
	</div>
</section>

<style>
	.login-page {
		display: grid;
		place-items: center;
	}

	.login-card {
		width: min(100%, 560px);
		padding: 2rem;
		border-radius: 28px;
		background:
			linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(246, 250, 255, 0.94)),
			radial-gradient(circle at top right, rgba(52, 125, 212, 0.16), transparent 36%);
		border: 1px solid rgba(77, 109, 148, 0.14);
		box-shadow: 0 24px 60px rgba(37, 69, 109, 0.12);
	}

	.login-card__eyebrow {
		margin-bottom: 0.75rem;
		color: #347dd4;
		text-transform: uppercase;
		font-weight: 800;
		letter-spacing: 0.08em;
		font-size: 0.8rem;
	}

	.login-card__title {
		font-size: clamp(2rem, 4vw, 3rem);
		line-height: 1.05;
		letter-spacing: -0.04em;
	}

	.login-card__description {
		margin-top: 1rem;
		color: #667085;
		line-height: 1.7;
	}

	.login-form {
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

	.login-form__submit {
		border: 0;
		border-radius: 16px;
		padding: 1rem 1.2rem;
		background: linear-gradient(135deg, #347dd4 0%, #4b95e8 100%);
		color: #fff;
		font-weight: 800;
		cursor: pointer;
		box-shadow: 0 16px 30px rgba(52, 125, 212, 0.24);
	}

	.login-form__submit:disabled {
		opacity: 0.7;
		cursor: wait;
	}

	@media (max-width: 720px) {
		.login-card {
			padding: 1.4rem;
		}
	}
</style>