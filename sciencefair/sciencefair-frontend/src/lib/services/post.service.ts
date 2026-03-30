import { get, post, put, del } from '$lib/utils/http-utils';

export type Post = {
  id?: number;
  titulo: string;
  conteudo: string;
  dataCriacao?: string;
  projeto: {
    id: number;
    titulo?: string;
  };
};

export async function getAllPosts(): Promise<Post[]> {
  return await get('/posts');
}

export async function getPostById(id: number): Promise<Post> {
  return await get(`/posts/${id}`);
}

export async function createPost(payload: Post): Promise<Post> {
  return await post('/posts', payload);
}

export async function updatePost(id: number, payload: Post): Promise<Post> {
  return await put(`/posts/${id}`, payload);
}

export async function deletePost(id: number): Promise<void> {
  return await del(`/posts/${id}`);
}