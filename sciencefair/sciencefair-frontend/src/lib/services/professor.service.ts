import { get, post, put, del } from '$lib/utils/http-utils';

export type Professor = {
	id?: number;
	nome: string;
	email: string;
	departamento: string;
	matricula: string;
};

export async function getAllProfessores(): Promise<Professor[]> {
	return await get('/professores');
}

export async function createProfessor(payload: Professor): Promise<Professor> {
	return await post('/professores', payload);
}

export async function updateProfessor(id: number, payload: Professor): Promise<Professor> {
	return await put(`/professores/${id}`, payload);
}

export async function deleteProfessor(id: number): Promise<void> {
	return await del(`/professores/${id}`);
}