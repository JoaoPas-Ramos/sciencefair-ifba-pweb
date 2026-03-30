import { get, post, put, del } from '$lib/utils/http-utils';

export type Aluno = {
	id?: number;
	nome: string;
	email: string;
	matricula: string;
	turma: string;
	projeto: {
		id: number;
	};
};

export async function getAllAlunos(): Promise<Aluno[]> {
	return await get('/alunos');
}

export async function createAluno(payload: Aluno): Promise<Aluno> {
	return await post('/alunos', payload);
}

export async function updateAluno(id: number, payload: Aluno): Promise<Aluno> {
	return await put(`/alunos/${id}`, payload);
}

export async function deleteAluno(id: number): Promise<void> {
	return await del(`/alunos/${id}`);
}
