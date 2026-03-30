import { get, post, put, del } from '$lib/utils/http-utils';

export type Projeto = {
	id?: number;
	titulo: string;
	descricao: string;
	areaTematica: string;
	status: string;
	feira: {
		id: number;
	};
	professor: {
		id: number;
	};
};

export async function getAllProjetos(): Promise<Projeto[]> {
	return await get('/projetos');
}

export async function createProjeto(payload: Projeto): Promise<Projeto> {
	return await post('/projetos', payload);
}

export async function updateProjeto(id: number, payload: Projeto): Promise<Projeto> {
	return await put(`/projetos/${id}`, payload);
}

export async function deleteProjeto(id: number): Promise<void> {
	return await del(`/projetos/${id}`);
}
