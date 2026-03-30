import { get, post, put, del } from '$lib/utils/http-utils';

export type Feira = {
	id?: number;
	nome: string;
	descricao: string;
	dataInicio: string;
	dataFim: string;
	local: string;
};

export async function getAllFeiras(): Promise<Feira[]> {
	return await get('/feiras');
}

export async function createFeira(payload: Feira): Promise<Feira> {
	return await post('/feiras', payload);
}

export async function updateFeira(id: number, payload: Feira): Promise<Feira> {
	return await put(`/feiras/${id}`, payload);
}

export async function deleteFeira(id: number): Promise<void> {
	return await del(`/feiras/${id}`);
}